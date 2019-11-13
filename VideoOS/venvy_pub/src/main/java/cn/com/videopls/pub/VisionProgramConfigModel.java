package cn.com.videopls.pub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.App;
import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.exception.DownloadException;
import cn.com.videopls.pub.view.VideoOSLuaView;

import static cn.com.venvy.App.getContext;
import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_H5_URL;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_MSG;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_NEED_RETRY;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_NVG_SHOW;
import static cn.com.venvy.common.observer.VenvyObservableTarget.Constant.CONSTANT_TITLE;

/**
 * Created by Lucas on 2019/8/1.
 * <p>
 * 视联网小程序配置接口
 */
public class VisionProgramConfigModel extends VideoPlusBaseModel {


    private static final String CONFIG = "/vision/getMiniAppConf";

    private VisionProgramConfigCallback callback;
    private PreloadLuaUpdate mDownLuaUpdate;
    private String miniAppId;
    private boolean isH5Type;
    private boolean nvgShow = true;

    public VisionProgramConfigModel(@NonNull Platform platform, String miniAppId, boolean isH5Type, VisionProgramConfigCallback configCallback) {
        super(platform);
        this.callback = configCallback;
        this.miniAppId = miniAppId;
        this.isH5Type = isH5Type;
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

                    String jsonStr = "";
                    if (App.isIsDevMode()) {
                        VenvyLog.d("devMode is open");
                        String jsonFilePath = VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + "dev_config.json";
                        File file = new File(jsonFilePath);
                        if (file.exists()) {
                            jsonStr = VenvyFileUtil.readFile(App.getContext(), jsonFilePath, null);
                        } else {
                            VenvyLog.e("The dev mode is open,but json file not found");
                            return;
                        }

                    } else {
                        VenvyLog.d("devMode is close");
                        jsonStr = VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()));
                    }

                    final JSONObject decryptData = new JSONObject(jsonStr);

                    if (isH5Type) {
                        final String h5Url = decryptData.optString("h5Url");
                        if (TextUtils.isEmpty(h5Url)) {
                            VenvyLog.e("appType is H5,but url is null");
                        } else {
                            // 拉起一个H5容器
                            Bundle bundle = new Bundle();
                            bundle.putString(CONSTANT_H5_URL, h5Url);
                            ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_H5_VISION_PROGRAM, bundle);
                        }

                        return;
                    }
                    // lua文件列表  sample : [{url:xxx, md5:xxx}, {url:xxx, md5:xxx} , ...]
                    // 开发者模式下 则是：[{url:本地filePath}, {url:本地filePath} , ...]
                    JSONArray fileListArray = decryptData.optJSONArray("luaList");
                    final String template = decryptData.optString("template"); //  入口lua文件名称
                    String resCode = App.isIsDevMode() ? "-1" : decryptData.optString("resCode"); //  应答码  00-成功  01-失败
                    JSONObject displayObj = decryptData.optJSONObject("display");
                    if (displayObj != null) {
                        final String nativeTitle = displayObj.optString("navTitle");
                        nvgShow = displayObj.optBoolean("navShow", true);
                        updateVisionTitle(nativeTitle, nvgShow);
                    }


                    if (resCode.equalsIgnoreCase("00")) {
                        if (mDownLuaUpdate == null) {
                            mDownLuaUpdate = new PreloadLuaUpdate(Platform.STATISTICS_DOWNLOAD_STAGE_REALPLAY, getPlatform(), new PreloadLuaUpdate.CacheLuaUpdateCallback() {
                                @Override
                                public void updateComplete(boolean isUpdateByNetWork) {
                                    if (isUpdateByNetWork) {
                                        VideoOSLuaView.destroyLuaScript();
                                    }
                                    VisionProgramConfigCallback callback = getCallback();
                                    if (callback != null) {
                                        callback.downComplete(template, isUpdateByNetWork, nvgShow);
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
                    } else if (resCode.equalsIgnoreCase("-1")) {
                        // 开发者模式
                        VisionProgramConfigCallback callback = getCallback();
                        if (callback != null) {
                            callback.downComplete(template, false, nvgShow);
                        }
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

    private void updateVisionTitle(String title, boolean nvgShow) {
        if (TextUtils.isEmpty(title)) return;
        Bundle bundle = new Bundle();
        bundle.putString(CONSTANT_TITLE, title);
        bundle.putBoolean(CONSTANT_NVG_SHOW, nvgShow);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_UPDATE_VISION_TITLE, bundle);
    }


    public interface VisionProgramConfigCallback {
        void downComplete(String entranceLua, boolean isUpdateByNet, boolean nvgShow);

        void downError(Throwable t);
    }
}
