package both.video.venvy.com.appdemo.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import both.video.venvy.com.appdemo.UrlContent;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.lua.plugin.LVCommonParamPlugin;

/**
 * Created by videopls on 2019/10/14.
 */

public class DevelopDebugConfigInfoModel extends IConfigModel{

    private int programId;
    private String submitId;
    private AppDebugInfoCallback appDebugInfoCallback;

    public DevelopDebugConfigInfoModel(int programId, String submitId, AppDebugInfoCallback appDebugInfoCallback) {
        this.programId = programId;
        this.submitId = submitId;
        this.appDebugInfoCallback = appDebugInfoCallback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(UrlContent.getUrlDevDebugConfigInfo(),createHead(),createBody());
    }

    private Map<String, String> createHead() {
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put("appKey", ConfigUtil.getAppKey());
        return headMap;
    }

    private Map<String,String> createBody(){
        HashMap<String, String> bodyMap = new HashMap<>();
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appletType", programId + "");
        paramMap.put("submitId", submitId);
        paramMap.put("commonParam", LVCommonParamPlugin.getCommonParamJson());

        bodyMap.put("data", VenvyAesUtil.encrypt(ConfigUtil.getAppSecret(), ConfigUtil.getAppSecret(), new JSONObject(paramMap).toString()));
        return bodyMap;
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                if (!response.isSuccess()) {
                    if (appDebugInfoCallback != null) {
                        appDebugInfoCallback.updateError(new Exception("get app config error"));
                    }
                }
                if (appDebugInfoCallback == null) {
                    return;
                }
                String result = response.getResult();
                if (!TextUtils.isEmpty(result)) {
                    appDebugInfoCallback.updateComplete(result);
                } else {
                    appDebugInfoCallback.updateError(new NullPointerException("get app config NullPointerException"));
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                if (appDebugInfoCallback != null) {
                    appDebugInfoCallback.updateError(e);
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

    public interface AppDebugInfoCallback {
        void updateComplete(String result);

        void updateError(Throwable t);
    }
}
