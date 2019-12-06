package cn.com.videopls.pub.view;

import android.content.Context;
import android.text.TextUtils;

import com.taobao.luaview.global.LuaScriptLoader;
import com.taobao.luaview.global.LuaView;
import com.taobao.luaview.scriptbundle.ScriptBundle;
import com.taobao.luaview.scriptbundle.ScriptFile;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.luaj.vm2.LuaValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import cn.com.venvy.Platform;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.interf.ActionType;
import cn.com.venvy.common.router.RouteType;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvySchemeUtil;
import cn.com.venvy.lua.LuaCallback;
import cn.com.venvy.lua.LuaHelper;
import cn.com.venvy.processor.annotation.VenvyAutoData;
import cn.com.venvy.processor.annotation.VenvyAutoRun;
import cn.com.venvy.processor.annotation.VenvyRouter;

/*
 * Created by yanjiangbo on 2018/1/18.
 */
@VenvyRouter(name = VenvySchemeUtil.SCHEME_LUA_VIEW, type = RouteType.TYPE_VIEW)
public class VideoOSLuaView extends VideoOSBaseView {
    /**
     * A类小程序 LuaView://defaultLuaView?template=xxx.lua&id=xxx
     * 跳转B类小程序     LuaView://applets?appletId=xxxx&type=x(type: 1横屏,2竖屏)
     * <p>
     * B类小程序容器内部跳转   LuaView://applets?appletId=xxxx&template=xxxx.lua&id=xxxx&(priority=x)
     */

    private static final String INIT_SCRIPT = "Init_ScriptBundle";
    private static final String LOCAL_LUA_PATH = "lua/";
    private static final String KEY_MINIAPPID = "miniAppId";
    private static final String KEY_APPLETID = "appletId";
    private static final String KEY_DATA = "data";
    private volatile LuaView mLuaView;
    private boolean hasCallShowFunction = false;
    public static ScriptBundle sScriptBundle;

    @VenvyAutoData(name = "data")
    private Object data;

    @VenvyAutoData(name = "template")
    private String luaName;

    @VenvyAutoData(name = "id")
    private String id;

    @VenvyAutoData(name = "platform")
    private Platform mPlatform;

    @VenvyAutoData(name = "priority")
    private String priority;

    @VenvyAutoData(name = "event")
    private String eventData;

    @VenvyAutoData(name = "appletId")
    private String appletId;

    public VideoOSLuaView(Context context) {
        super(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        VenvyAsyncTaskUtil.cancel(INIT_SCRIPT);
        if (mLuaView != null) {
            mLuaView.removeAllViews();
            mLuaView.onDestroy();
        }
        super.onDetachedFromWindow();
    }

    public static void destroyLuaScript() {
        if (sScriptBundle != null) {
            sScriptBundle = null;
        }
    }

    public String getPriority() {
        return priority;
    }

    @VenvyAutoRun
    private void showTargetView() {
        VenvyLog.d("Router",
                "LuaView run and template is " + luaName + ", time is " + System.currentTimeMillis());
        if (TextUtils.isEmpty(luaName)) {
            removeFromSuper(this);
            return;
        }
        if (!TextUtils.isEmpty(id)) {
            this.setTag(id);
        }
        hasCallShowFunction = false;
        if (mLuaView == null) {
            // 可能mLuaView 正在异步创建中，此时数据delay执行，等待LuaView 的创建完毕
            LuaHelper.createLuaViewAsync(getContext(), mPlatform, this,
                    new LuaView.CreatedCallback() {
                        @Override
                        public void onCreated(LuaView luaView) {
                            mLuaView = luaView;
                            VideoOSLuaView.this.addView(luaView);
                            runLuaFile(luaView, luaName, data);
                        }
                    });
        } else {
            callLuaFunction(mLuaView, data);

        }
    }

    public void callLuaFunction(String functionName, HashMap<String, String> map) {
        if (mLuaView != null) {
            LuaValue dataTable = null;
            String key = "data";
            Object dataValue = map.get(key);
            if (dataValue != null && dataValue instanceof String) {
                dataTable = JsonUtil.toLuaTable((String) dataValue);
                if (dataTable != null && dataTable.istable()) {
                    map.remove(key);
                }
            }
            LuaValue table = LuaUtil.toTable(map);
            if (dataTable != null && table != null && table.istable()) {
                table.set(key, dataTable);
            }
            mLuaView.callLuaFunction(functionName, table);
        }
    }

    @VenvyAutoRun
    private void reResumeService() {
        if (mLuaView == null) {
            return;
        }
        if (TextUtils.isEmpty(eventData)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(eventData);
            String eventActionPause = obj.optString(VenvySchemeUtil.QUERY_PARAMETER_ACTION_TYPE);
            if (!TextUtils.equals(eventActionPause,
                    String.valueOf(ActionType.EventTypeResume.getId()))) {
                return;
            }
            mLuaView.getGlobals().callLuaFunction("event", JsonUtil.toLuaTable(eventData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @VenvyAutoRun
    private void pauseService() {
        if (mLuaView == null) {
            return;
        }
        if (TextUtils.isEmpty(eventData)) {
            return;
        }
        try {
            JSONObject obj = new JSONObject(eventData);
            String eventActionPause = obj.optString(VenvySchemeUtil.QUERY_PARAMETER_ACTION_TYPE);
            if (!TextUtils.equals(eventActionPause,
                    String.valueOf(ActionType.EventTypePause.getId()))) {
                return;
            }
            mLuaView.getGlobals().callLuaFunction("event", JsonUtil.toLuaTable(eventData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callLuaFunction(LuaView luaView, Object object) {
        LuaValue table = null;
        LuaValue dataTable = null;
        String key = "data";
        if (object != null && object instanceof HashMap) {
            HashMap map = (HashMap) object;
            Object dataValue = map.get(key);
            if (dataValue != null && dataValue instanceof String) {
                dataTable = JsonUtil.toLuaTable((String) dataValue);
            }
            table = LuaUtil.toTable(map);
            if (dataTable != null && table != null && table.istable()) {
                table.set(key, dataTable);
            }
        } else {
            if (object != null && object instanceof LuaValue) {
                table = (LuaValue) object;
            }
        }
        luaView.callLuaFunction("show", table);
    }

    private void runLuaFile(final LuaView luaView, final String luaName, final Object valueData) {
        if (TextUtils.isEmpty(luaName)) {
            return;
        }
        String miniAppId = "";
        if (valueData instanceof HashMap) {
            HashMap params = (HashMap) valueData;
            if (params != null && params.size() >= 0) {
                if (params.containsKey(KEY_MINIAPPID)) {
                    miniAppId = params.get(KEY_MINIAPPID) == null ? "" : String.valueOf(params.get(KEY_MINIAPPID));
                } else if (params.containsKey(KEY_APPLETID)) {
                    miniAppId = params.get(KEY_APPLETID) == null ? "" : String.valueOf(params.get(KEY_APPLETID));
                }
                if (TextUtils.isEmpty(miniAppId)) {
                    // 尝试去data中找miniAppId
                    if (params.containsKey(KEY_DATA)) {
                        try {
                            JSONObject jsonObject = new JSONObject((String) params.get(KEY_DATA));
                            JSONObject miniAppInfo = jsonObject.optJSONObject("miniAppInfo");
                            if (miniAppInfo != null) {
                                miniAppId = TextUtils.isEmpty(miniAppInfo.optString("miniAppId")) ? "" : miniAppInfo.optString("miniAppId");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }


        if (sScriptBundle == null) {
            sScriptBundle = initScriptBundle(VenvyFileUtil.getCachePath(VideoOSLuaView.this.getContext()) + PreloadLuaUpdate.LUA_CACHE_PATH);
            if (sScriptBundle != null) {
                luaView.loadScriptBundle(sScriptBundle, TextUtils.isEmpty(miniAppId) ? luaName : miniAppId + File.separator + luaName,
                        new LuaCallbackImpl(valueData));
            } else {
                runLua(luaView, luaName, valueData);
            }
        } else {
            luaView.loadScriptBundle(sScriptBundle, TextUtils.isEmpty(miniAppId) ? luaName : miniAppId + File.separator + luaName, new LuaCallbackImpl(valueData));
        }
    }

    private void runLua(final LuaView luaView, final String luaName, final Object valueData) {
        if (TextUtils.isEmpty(luaName)) {
            return;
        }
        LuaValue table = null;
        LuaValue dataTable = null;
        String key = "data";
        if (valueData != null && valueData instanceof HashMap) {
            HashMap map = (HashMap) valueData;
            Object dataValue = map.get(key);
            if (dataValue != null && dataValue instanceof String) {
                dataTable = JsonUtil.toLuaTable((String) dataValue);
            }
            table = LuaUtil.toTable(map);
            if (dataTable != null && table != null && table.istable()) {
                table.set(key, dataTable);
            }
        } else {
            if (valueData != null && valueData instanceof LuaValue) {
                table = (LuaValue) valueData;
            }
        }
        final LuaValue needValue = table;
        luaView.load(LOCAL_LUA_PATH + luaName, new LuaScriptLoader.ScriptExecuteCallback() {
            @Override
            public boolean onScriptPrepared(ScriptBundle bundle) {
                return false;
            }

            @Override
            public boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view) {
                return false;
            }

            @Override
            public void onScriptExecuted(String uri, boolean executedSuccess) {
                if (executedSuccess) {
                    luaView.getGlobals().callLuaFunction("show", needValue);
                }
            }
        });
    }

    private ScriptBundle initScriptBundle(String luaPath) {
        File luaFilePackage = new File(luaPath);
        if (!luaFilePackage.exists()) {
            return null;
        }
        if (!luaFilePackage.isDirectory()) {
            return null;
        }
        ScriptBundle scriptBundle = null;
        for (File file : luaFilePackage.listFiles()) {
            if (scriptBundle == null) {
                scriptBundle = new ScriptBundle();
                scriptBundle.setBaseFilePath(luaFilePackage.getAbsolutePath());
            }
            if (TextUtils.equals("lua", VenvyFileUtil.getExtension(file.getName()))) {
                byte[] data = getBytes(file.getPath());
                ScriptFile scriptFile = new ScriptFile(null,
                        luaFilePackage.getAbsolutePath(), file.getName(), data);
                scriptBundle.addScript(scriptFile);
                VenvyLog.i("fileName = " + file.getName() + "  " + file.length());
            }
            if (file.exists() && file.isDirectory()) {
                for (File childFile : file.listFiles()) {
                    if (TextUtils.equals("lua", VenvyFileUtil.getExtension(childFile.getName()))) {
                        byte[] data = getBytes(childFile.getPath());
                        String fileAbsolutePath = file.getAbsolutePath();
                        ScriptFile scriptFile = new ScriptFile(null,
                                fileAbsolutePath, fileAbsolutePath.substring(fileAbsolutePath.lastIndexOf("/") + 1, fileAbsolutePath.length()) + File.separator + childFile.getName(), data);
                        scriptBundle.addScript(scriptFile);
                    }
                }
            }
        }
        return scriptBundle;
    }

    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            VenvyLog.e(VideoOSLuaView.class.getName(), e);
            VenvyIOUtils.close(fis);
            VenvyIOUtils.close(bos);
        }
        return buffer;
    }

    private class LuaCallbackImpl extends LuaCallback {

        private Object mValueData;

        LuaCallbackImpl(Object value) {
            this.mValueData = value;
        }

        @Override
        public boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view) {
            return super.onScriptCompiled(value, context, view);
        }

        @Override
        public void onScriptExecuted(String uri, boolean executedSuccess) {
            super.onScriptExecuted(uri, executedSuccess);
            if (executedSuccess) {
                if (mLuaView != null && !hasCallShowFunction) {
                    callLuaFunction(mLuaView, mValueData);
                    hasCallShowFunction = true;
                }
            } else {

            }
        }

        @Override
        public boolean onScriptPrepared(ScriptBundle bundle) {
            return super.onScriptPrepared(bundle);
        }
    }
}
