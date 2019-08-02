package cn.com.videopls.pub;

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
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by Lucas on 2019/8/1.
 * <p>
 * 视联网小程序配置接口
 */
public class MiniAppConfigModel extends VideoPlusBaseModel {


    private static final String CONFIG = "/vision/getMiniAppConf";

    private MiniAppConfigCallback callback;
    private VideoPlusLuaUpdate mDownLuaUpdate;
    private String miniAppId;

    public MiniAppConfigModel(@NonNull Platform platform, String miniAppId, MiniAppConfigCallback configCallback) {
        super(platform);
        this.callback = configCallback;
        this.miniAppId = miniAppId;
    }


    public MiniAppConfigCallback getCallback() {
        return callback;
    }

    @Override
    public Request createRequest() {
//        return HttpRequest.get(Config.HOST_VIDEO_OS + CONFIG, createBody());
        return HttpRequest.get("http://mock.videojj.com/mock/5d42ae7eb4383d45dfd0367c/vision/getMiniAppConf");
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
                        MiniAppConfigModel.MiniAppConfigCallback callback = getCallback();
                        if (callback != null) {
                            callback.downError(new Exception("download lua script error"));
                        }
                    }
                    // 解密返回数据
                   final  JSONObject decryptData = new JSONObject(response.getResult());
//                    String encryptData = value.optString("encryptData");
//                    if (TextUtils.isEmpty(encryptData)) {
//                        MiniAppConfigModel.MiniAppConfigCallback callback = getCallback();
//                        if (callback != null) {
//                            callback.downError(new NullPointerException("response lua script is null"));
//                        }
//                        return;
//                    }
//                    final JSONObject decryptData = new JSONObject(VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform())));


                    JSONArray fileListArray = decryptData.optJSONArray("luaList");// lua文件列表  sample : [{url:xxx, md5:xxx}, {url:xxx, md5:xxx} , ...]
                    String template = decryptData.optString("template"); //  入口lua文件名称
                    String resCode = decryptData.optString("resCode"); //  应答码  00-成功  01-失败
                    String resMsg = decryptData.optString("resMsg"); //  应答信息


                    if (mDownLuaUpdate == null) {
                        mDownLuaUpdate = new VideoPlusLuaUpdate(getPlatform(), new VideoPlusLuaUpdate.CacheLuaUpdateCallback() {
                            @Override
                            public void updateComplete(boolean isUpdateByNetWork) {
                                MiniAppConfigModel.MiniAppConfigCallback callback = getCallback();
                                if (callback != null) {
//                                    Map<String, String> params = getQueryAdsParams();
//                                    String adsType = params != null ? params.get(VenvySchemeUtil.QUERY_PARAMETER_ADS_TYPE) : "";
//                                    ServiceQueryAdsInfo queryAdsInfo = new ServiceQueryAdsInfo.Builder().setQueryAdsTemplate(queryAdsTemplate).setQueryAdsId(queryAdsId).setQueryAdsType(!TextUtils.isEmpty(adsType) ? Integer.valueOf(adsType) : 0).build();
                                    callback.downComplete(decryptData.toString());
                                }
                            }

                            @Override
                            public void updateError(Throwable t) {
                                MiniAppConfigModel.MiniAppConfigCallback callback = getCallback();
                                if (callback != null) {
                                    callback.downError(new Exception("download mini app  down lua failed"));
                                }
                            }
                        });
                    }
                    mDownLuaUpdate.startDownloadLuaFile(fileListArray);


                } catch (Exception e) {
                    VenvyLog.e(MiniAppConfigModel.class.getName(), e);
                    MiniAppConfigModel.MiniAppConfigCallback callback = getCallback();
                    if (callback != null) {
                        callback.downError(e);
                    }
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(MiniAppConfigModel.class.getName(), "视联网小程序加载失败 " + (e != null ? e.getMessage() : ""));
                MiniAppConfigModel.MiniAppConfigCallback callback = getCallback();
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

    public interface MiniAppConfigCallback {
        void downComplete(String originData);

        void downError(Throwable t);
    }
}
