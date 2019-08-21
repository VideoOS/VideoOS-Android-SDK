package cn.com.videopls.pub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.exception.DownloadException;

import static cn.com.venvy.App.getContext;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_MSG;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_NEED_RETRY;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_TITLE;

/**
 * Created by Lucas on 2019/8/1.
 * <p>
 * 视联网小程序配置接口
 */
public class VisionProgramConfigModel extends VideoPlusBaseModel {


    private static final String CONFIG = "/vision/getMiniAppConf";

    private VisionProgramConfigCallback callback;
    private VideoPlusLuaUpdate mDownLuaUpdate;
    private String miniAppId;

    public VisionProgramConfigModel(@NonNull Platform platform, String miniAppId, VisionProgramConfigCallback configCallback) {
        super(platform);
        this.callback = configCallback;
        this.miniAppId = miniAppId;
    }


    public VisionProgramConfigCallback getCallback() {
        return callback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(Config.HOST_VIDEO_OS + CONFIG, createBody());
//        return HttpRequest.get("http://mock.videojj.com/mock/5d42ae7eb4383d45dfd0367c/vision/getMiniAppConf");
    }


    private Map<String, String> createBody() {
        Map<String, String> paramBody = new HashMap<>();
        paramBody.put("miniAppId", miniAppId);
        paramBody.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        HashMap<String, String> data = new HashMap<>();
        data.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()), new JSONObject(paramBody).toString()));
        return data;
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
                        VisionProgramConfigCallback callback = getCallback();
                        if (callback != null) {
                            callback.downError(new Exception("download lua script error"));
                        }
                    }
                    // 解密返回数据
                    final JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    if (TextUtils.isEmpty(encryptData)) {
                        VisionProgramConfigModel.VisionProgramConfigCallback callback = getCallback();
                        if (callback != null) {
                            callback.downError(new NullPointerException("response lua script is null"));
                        }
                        return;
                    }
                    final JSONObject decryptData = new JSONObject(VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform())));


                    JSONArray fileListArray = decryptData.optJSONArray("luaList");// lua文件列表  sample : [{url:xxx, md5:xxx}, {url:xxx, md5:xxx} , ...]
                    final String template = decryptData.optString("template"); //  入口lua文件名称
                    String resCode = decryptData.optString("resCode"); //  应答码  00-成功  01-失败
                    JSONObject displayObj = decryptData.optJSONObject("display");

                    if (displayObj != null) {
                        final String nativeTitle = displayObj.optString("navTitle");
                        updateVisionTitle(nativeTitle);
                    }


                    if (resCode.equalsIgnoreCase("00")) {
                        if (mDownLuaUpdate == null) {
                            mDownLuaUpdate = new VideoPlusLuaUpdate(getPlatform(), new VideoPlusLuaUpdate.CacheLuaUpdateCallback() {
                                @Override
                                public void updateComplete(boolean isUpdateByNetWork) {
                                    VisionProgramConfigCallback callback = getCallback();
                                    if (callback != null) {
                                        callback.downComplete(template, isUpdateByNetWork);
                                    }
                                }

                                @Override
                                public void updateError(Throwable t) {
                                    VisionProgramConfigCallback callback = getCallback();
                                    if (callback != null) {
                                        callback.downError(new DownloadException());
                                    }
                                }
                            });
                        }
                        mDownLuaUpdate.startDownloadLuaFile(fileListArray);
                    } else if (resCode.equalsIgnoreCase("01")) {
                        // 小程序下架不可用
                        Bundle bundle = new Bundle();
                        bundle.putString(CONSTANT_MSG, getContext().getString(
                                VenvyResourceUtil.getStringId(getContext(), "errorDesc")));
                        bundle.putBoolean(CONSTANT_NEED_RETRY, false);
                        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_SHOW_VISION_ERROR_LOGIC, bundle);
                    } else {
                        VenvyLog.e(decryptData.optString("resMsg")); //  应答信息
                    }

                } catch (Exception e) {
                    VenvyLog.e(VisionProgramConfigModel.class.getName(), e);
                    VisionProgramConfigCallback callback = getCallback();
                    if (callback != null) {
                        callback.downError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VisionProgramConfigModel.class.getName(), "视联网小程序加载失败 " + (e != null ? e.getMessage() : ""));
                VisionProgramConfigCallback callback = getCallback();
                if (callback != null) {
                    callback.downError(e);
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

    private void updateVisionTitle(String title) {
        if (TextUtils.isEmpty(title)) return;
        Bundle bundle = new Bundle();
        bundle.putString(CONSTANT_TITLE, title);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_UPDATE_VISION_TITLE, bundle);
    }


    public interface VisionProgramConfigCallback {
        void downComplete(String entranceLua, boolean isUpdateByNet);

        void downError(Throwable t);
    }
}
