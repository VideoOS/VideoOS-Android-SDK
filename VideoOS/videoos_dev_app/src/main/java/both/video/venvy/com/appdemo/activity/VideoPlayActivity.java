package both.video.venvy.com.appdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.AssetsUtil;
import both.video.venvy.com.appdemo.utils.FileUtil;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyFileUtil;

import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;

public class VideoPlayActivity extends BasePlayerActivity {

    public final static int TYPE_PROGRAM_A_LOCAL = 0x0001;
    public final static int TYPE_PROGRAM_A_ONLINE = 0x0002;
    public final static int TYPE_PROGRAM_B = 0x0003;

    //A
    private String luaName;
    private String jsonName;

    //B
    private String miniAppId;

    public static void newIntent(Context context, Bundle bundle){
        Intent videoPlayIntent = new Intent();
        videoPlayIntent.putExtras(bundle);
        videoPlayIntent.setClass(context, VideoPlayActivity.class);
        context.startActivity(videoPlayIntent);
    }


    //获取传送过来的数据
    @Override
    protected void initLuaData() {
        Bundle bundle = this.getIntent().getExtras();
        programMode = bundle.getInt("program_type");
        if(programMode == VideoPlayActivity.TYPE_PROGRAM_A_LOCAL){
            String luaPath = bundle.getString("luaPath");
            String jsonPath = bundle.getString("jsonPath");
            luaName = FileUtil.getFileName(luaPath);
            jsonName = FileUtil.getFileName(jsonPath);
        }else if(programMode == VideoPlayActivity.TYPE_PROGRAM_A_ONLINE){
            String luaPath = bundle.getString("luaPath");
            String jsonPath = bundle.getString("jsonPath");
            miniAppId = bundle.getString("miniAppId");
            luaName = luaPath.substring(0,luaPath.lastIndexOf("."));
            jsonName = jsonPath.substring(0,jsonPath.lastIndexOf("."));
        }else if(programMode == VideoPlayActivity.TYPE_PROGRAM_B){
            miniAppId = bundle.getString("miniAppId");

            mRootView.findViewById(R.id.iv_launch_video_mode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    programBDebug();
                }
            });
        }
    }

    @Override
    public boolean isDevMode() {
        return true;
    }

    //根据传送过来的数据加载Lua
    @Override
    public void startLua() {
        if (mVideoPlusView == null)
            return;
        if(programMode == VideoPlayActivity.TYPE_PROGRAM_A_LOCAL){
            programALocalDebug();
        }else if(programMode == VideoPlayActivity.TYPE_PROGRAM_A_ONLINE){
            try {
                programAOnlineDebug();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(programMode == VideoPlayActivity.TYPE_PROGRAM_B){
            programBDebug();
        }
    }

    private void programALocalDebug() {
        Uri uri = Uri.parse("LuaView://defaultLuaView?template=" + luaName + ".lua&id=" + luaName);
        HashMap<String, String> params = new HashMap<>();
        params.put("data", AssetsUtil.readFileAssets("alocal/" + jsonName + ".json", VideoPlayActivity.this));

        mVideoPlusView.navigation(uri, params, new IRouterCallback() {
            @Override
            public void arrived() {

            }

            @Override
            public void lost() {

            }
        });
    }

    private void programAOnlineDebug() throws JSONException {

        Uri uri = Uri.parse("LuaView://defaultLuaView?template=" + luaName + ".lua&id=" + luaName +"&miniAppId=" + miniAppId);
        HashMap<String, String> params = new HashMap<>();

        String serviceConfigJson = VenvyFileUtil.readFormFile(VideoPlayActivity.this, VenvyFileUtil.getCachePath(VideoPlayActivity.this) + LUA_CACHE_PATH + File.separator + miniAppId + File.separator + jsonName + ".json");

        String commonConfigJson = FileUtil.getFromAssets(this, "common/luaConfig.json");
        JSONObject commonJsonObject = new JSONObject(commonConfigJson);
        commonJsonObject.putOpt("data", new JSONObject(serviceConfigJson));
        commonJsonObject.putOpt("template", luaName + ".lua");
        JSONObject miniAppInfoObj = commonJsonObject.optJSONObject("miniAppInfo");
        miniAppInfoObj.put("miniAppId", miniAppId);
        Log.i("zhangjunling", "commonConfigJson:" + commonJsonObject.toString());

        params.put("data", commonJsonObject.toString());

        mVideoPlusView.navigation(uri, params, new IRouterCallback() {
            @Override
            public void arrived() {
            }

            @Override
            public void lost() {

            }
        });
    }

    private void programBDebug() {
        Bundle bundle = new Bundle();
        bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, miniAppId);
        bundle.putString(VenvyObservableTarget.KEY_ORIENTATION_TYPE, "1");
        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_APP_TYPE, "1");
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, bundle);
    }
}
