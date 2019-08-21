package cn.com.videopls.pub;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Platform;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by videojj_pls on 2019/8/19.
 * App启动预加载lua文件接口(架构优化版本)
 */

public class VideoPlusPreloadLuaFileInfo extends VideoPlusBaseModel {
    private static final String PRE_LOAD_LUA_URL = "/api/preloadLuaFileInfo";
    private static final String PRE_LOAD_LUA_URL_MOCK = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/preloadLuaFileInfo";
    private PreloadLuaCallback mPreloadLuaCallback;
    private PreloadLuaUpdate mDownLuaUpdate;

    public VideoPlusPreloadLuaFileInfo(Platform platform, PreloadLuaCallback callback) {
        super(platform);
        this.mPreloadLuaCallback = callback;
    }

    @Override
    public boolean needCheckResponseValid() {
        return false;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                try {
                    if (!response.isSuccess()) {
                        PreloadLuaCallback callback = getPreloadLuaCallback();
                        if (callback != null) {
                            callback.updateError(new Exception("preloadLuaFile info data error"));
                        }
                        return;
                    }
                    JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    if (TextUtils.isEmpty(encryptData)) {
                        PreloadLuaCallback callback = getPreloadLuaCallback();
                        if (callback != null) {
                            callback.updateError(new NullPointerException());
                        }
                        return;
                    }
                    JSONObject needValue = new JSONObject(VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform())));
                    if (needValue == null) {
                        PreloadLuaCallback callback = getPreloadLuaCallback();
                        if (callback != null) {
                            callback.updateError(new NullPointerException());
                        }
                        return;
                    }
                    final String resCode = needValue.optString("resCode");
                    if (!TextUtils.equals(resCode, "00")) {
                        PreloadLuaCallback callback = getPreloadLuaCallback();
                        if (callback != null) {
                            final String resMsg = needValue.optString("resMsg");
                            if (!TextUtils.isEmpty(resMsg)) {
                                callback.updateError(new Exception(resMsg));
                            } else {
                                callback.updateError(new NullPointerException());
                            }
                        }
                        return;
                    }
                    JSONArray preloadLuaArray = needValue.optJSONArray("luaList");
                    if (preloadLuaArray == null || preloadLuaArray.length() <= 0) {
                        PreloadLuaCallback callback = getPreloadLuaCallback();
                        if (callback != null) {
                            callback.updateError(new NullPointerException());
                        }
                        return;
                    }
                    if (mDownLuaUpdate == null) {
                        mDownLuaUpdate = new PreloadLuaUpdate(getPlatform(), new PreloadLuaUpdate.CacheLuaUpdateCallback() {
                            @Override
                            public void updateComplete(boolean isUpdateByNetWork) {
                                if (isUpdateByNetWork) {
                                    VideoOSLuaView.destroyLuaScript();
                                }
                                PreloadLuaCallback callback = getPreloadLuaCallback();
                                if (callback != null) {
                                    callback.updateComplete(isUpdateByNetWork);
                                }
                            }

                            @Override
                            public void updateError(Throwable t) {
                                PreloadLuaCallback callback = getPreloadLuaCallback();
                                if (callback != null) {
                                    callback.updateError(t);
                                }
                            }
                        });
                    }
                    mDownLuaUpdate.startDownloadLuaFile(preloadLuaArray);
                } catch (Exception e) {
                    VenvyLog.e(VideoPlusPreloadLuaFileInfo.class.getName(), e);
                    PreloadLuaCallback callback = getPreloadLuaCallback();
                    if (callback != null) {
                        callback.updateError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VideoPlusPreloadLuaFileInfo.class.getName(), "App启动预加载lua文件接口访问失败 " + (e != null ? e.getMessage() : ""));
                PreloadLuaCallback callback = getPreloadLuaCallback();
                if (callback != null) {
                    callback.updateError(e);
                }
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        };
    }

    @Override
    public Request createRequest() {
//        return HttpRequest.post(Config.HOST_VIDEO_OS + PRE_LOAD_LUA_URL, createBody());
        return HttpRequest.post(PRE_LOAD_LUA_URL_MOCK, createBody());
    }

    @Override
    public void destroy() {
        super.destroy();
        if (mDownLuaUpdate != null) {
            mDownLuaUpdate.destroy();
        }
        mPreloadLuaCallback = null;
    }

    public interface PreloadLuaCallback {
        void updateComplete(boolean isUpdateByNetWork);

        void updateError(Throwable t);
    }

    private PreloadLuaCallback getPreloadLuaCallback() {
        return mPreloadLuaCallback;
    }

    private Map<String, String> createBody() {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        paramBody.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
        return paramBody;
    }
}
