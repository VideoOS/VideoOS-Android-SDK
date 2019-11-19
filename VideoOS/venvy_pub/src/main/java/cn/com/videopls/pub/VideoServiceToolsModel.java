package cn.com.videopls.pub;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.venvy.AppSecret;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.PreloadLuaUpdate;
import cn.com.venvy.common.bean.LuaFileInfo;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;
import cn.com.videopls.pub.exception.DownloadException;
import cn.com.videopls.pub.view.VideoOSLuaView;

/**
 * Created by Lucas on 2019/11/19.
 * <p>
 * 请求视联网小工具的相关信息
 * <p>
 * wiki 地址 ： http://wiki.videojj.com/pages/viewpage.action?pageId=3244401
 */
public class VideoServiceToolsModel extends VideoPlusBaseModel {

    private static final String API = Config.HOST_VIDEO_OS
            + "/api/getMiniAppInfo";

    private Map<String, String> mParams;
    private PreloadLuaUpdate mDownLuaUpdate;
    private VisionProgramToolsCallback callback;

    public VideoServiceToolsModel(@NonNull Platform platform, Map<String, String> params, VisionProgramToolsCallback callback) {
        super(platform);
        mParams = params;
        this.callback = callback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(API, createBody(mParams));
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
                        if (callback != null) {
                            callback.downError(new Exception("download lua script error"));
                        }
                        return;
                    }

                    // 解密返回数据
                    final JSONObject value = new JSONObject(response.getResult());
                    String encryptData = value.optString("encryptData");
                    if (TextUtils.isEmpty(encryptData)) {
                        if (callback != null) {
                            callback.downError(new NullPointerException("response lua script is null"));
                        }
                        return;
                    }

                    String jsonStr = VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform()));
                    final JSONObject decryptData = new JSONObject(jsonStr);

                    final JSONObject miniAppInfoObj = decryptData.optJSONObject("miniAppInfo");

                    final String miniAppId = miniAppInfoObj.optString("miniAppId");
                    final String template = miniAppInfoObj.optString("template");
                    JSONArray fileListArray = miniAppInfoObj.optJSONArray("luaList");
//                    final String developerUserId = miniAppInfoObj.optString("developerUserId");


                    String resCode = decryptData.optString("resCode"); //  应答码  00-成功  01-失败

                    if (resCode.equalsIgnoreCase("00")) {
                        //LuaArray --> JavaBean
                        List<LuaFileInfo> luaFileInfoList = new ArrayList<>();
                        LuaFileInfo luaFileInfo = new LuaFileInfo();
                        luaFileInfo.setMiniAppId(miniAppId);
                        List<LuaFileInfo.LuaListBean> luaList = new ArrayList<>();
                        for (int i = 0; i < fileListArray.length(); i++) {
                            JSONObject luaFileObj = fileListArray.optJSONObject(i);
                            if (luaFileObj == null) {
                                continue;
                            }
                            String luaMD5 = luaFileObj.optString("md5");
                            String luaUrl = luaFileObj.optString("url");

                            if (TextUtils.isEmpty(luaMD5) || TextUtils.isEmpty(luaUrl)) {
                                continue;
                            }
                            LuaFileInfo.LuaListBean luaListBean = new LuaFileInfo.LuaListBean();
                            luaListBean.setLuaFileMd5(luaMD5);
                            luaListBean.setLuaFileUrl(luaUrl);
                            luaList.add(luaListBean);
                        }
                        if (luaList.size() <= 0) {
                            if (callback != null) {
                                callback.downError(new NullPointerException("response lua script is null"));
                            }
                            return;
                        }
                        luaFileInfo.setLuaList(luaList);
                        luaFileInfoList.add(luaFileInfo);

                        if (mDownLuaUpdate == null) {
                            mDownLuaUpdate = new PreloadLuaUpdate(Platform.STATISTICS_DOWNLOAD_STAGE_REALPLAY, getPlatform(), new PreloadLuaUpdate.CacheLuaUpdateCallback() {
                                @Override
                                public void updateComplete(boolean isUpdateByNetWork) {
                                    if (isUpdateByNetWork) {
                                        VideoOSLuaView.destroyLuaScript();
                                    }
                                    if (callback != null) {
                                        callback.downComplete(template, miniAppInfoObj.toString());
                                    }
                                }

                                @Override
                                public void updateError(Throwable t) {
                                    if (callback != null) {
                                        callback.downError(new DownloadException());
                                    }
                                }
                            });
                        }
                        mDownLuaUpdate.startDownloadLuaFile(luaFileInfoList);
                    } else {
                        VenvyLog.e(decryptData.optString("resMsg")); //  应答信息
                    }

                } catch (Exception e) {
                    VenvyLog.e(VideoServiceToolsModel.class.getName(), e);
                    if (callback != null) {
                        callback.downError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VisionProgramConfigModel.class.getName(), "视联网小工具加载失败 " + (e != null ? e.getMessage() : ""));
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

    private Map<String, String> createBody(Map<String, String> params) {
        Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("commonParam", LVCommonParamPlugin.getCommonParamJson());
        if (params != null) {
            bodyParams.put("miniAppId", params.get("miniAppId"));
        }
        HashMap<String, String> dataParams = new HashMap<>();
        dataParams.put("data", VenvyAesUtil.encrypt(AppSecret.getAppSecret(getPlatform()),
                AppSecret.getAppSecret(getPlatform()), new JSONObject(bodyParams).toString()));
        return dataParams;
    }

    public interface VisionProgramToolsCallback {
        void downComplete(String entranceLua, String miniAppInfo);

        void downError(Throwable t);
    }
}
