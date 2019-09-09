package cn.com.videopls.pub;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
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
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by Lucas on 2019/9/9.
 */
public class VideoRecentlyModel extends VideoPlusBaseModel {

    private static final String API = "/vision/addRecentMiniApp";

    private String miniAppId;

    public VideoRecentlyModel(@NonNull Platform platform, String miniAppId) {
        super(platform);
        this.miniAppId = miniAppId;
    }


    @Override
    public boolean needCheckResponseValid() {
        return false;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(Config.HOST_VIDEO_OS + API, createBody());
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
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                if (response.isSuccess()) {
                    try {
                        // 解密返回数据
                        final JSONObject value = new JSONObject(response.getResult());
                        String encryptData = value.optString("encryptData");
                        final JSONObject decryptData = new JSONObject(VenvyAesUtil.decrypt(encryptData, AppSecret.getAppSecret(getPlatform()), AppSecret.getAppSecret(getPlatform())));
                        String resCode = decryptData.optString("resCode"); //  应答码  00-成功  01-失败
                        if(resCode.equals("00")){
                            VenvyLog.d("refresh success");
                        }else{
                            String resMsg = decryptData.optString("resMsg");
                            VenvyLog.d("refresh faile : "+resMsg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    VenvyLog.d("refresh failed");
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VenvyLog.e(VisionProgramConfigModel.class.getName(), "视联网小程序refresh failed " + (e != null ? e.getMessage() : ""));
            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        };
    }
}
