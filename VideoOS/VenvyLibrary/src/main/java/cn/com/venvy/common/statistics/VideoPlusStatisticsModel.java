package cn.com.venvy.common.statistics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyAesUtil;

/**
 * Created by videopls on 2019/8/22.
 */
public class VideoPlusStatisticsModel extends VideoPlusStatisticsBaseModel {
    private static final String SERVICE_STATISTICS_URL = Config.HOST_VIDEO_OS + "/commonStats";
    private StatisticsInfoBean statisticsInfoBean;
    private VideoPlusStatisticsCallback videoPlusStatisticsCallback;
    public VideoPlusStatisticsModel(@NonNull Platform platform,StatisticsInfoBean statisticsInfoBean,VideoPlusStatisticsCallback videoPlusStatisticsCallback) {
        super(platform);
        this.statisticsInfoBean = statisticsInfoBean;
        this.videoPlusStatisticsCallback = videoPlusStatisticsCallback;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.post(SERVICE_STATISTICS_URL,createHead(),createBody());
    }

    private Map<String, String> createHead() {
        HashMap<String, String> headMap = new HashMap<>();
        if(getPlatform() != null && getPlatform().getPlatformInfo() != null && getPlatform().getPlatformInfo().getAppKey() != null){
            headMap.put("appKey", getPlatform().getPlatformInfo().getAppKey());
        }
        return headMap;
    }

    private Map<String, String> createBody() {
        Map<String, String> bodyMap = new HashMap<>();
        try{
            JSONObject encryptDataObj = new JSONObject();
            encryptDataObj.put("type", getType());
            encryptDataObj.put("data", createParamDataJsonObj());
            encryptDataObj.put("commonParam", createCommonParamJsonObj());

            bodyMap.put("data", VenvyAesUtil.encrypt(getPlatform().getPlatformInfo().getAppSecret(), getPlatform().getPlatformInfo().getAppSecret(), encryptDataObj.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bodyMap;
    }

    private JSONObject createParamDataJsonObj() throws JSONException {
        JSONObject paramData = null;
        switch (getType()) {
            case VenvyStatisticsManager.AB_APPLET_TRACK:
                paramData = abAppletTrackStatistics();
                break;
            case VenvyStatisticsManager.USER_ACTION:

                break;
            case VenvyStatisticsManager.VISUAL_SWITCH_COUNT:
                paramData = visualSwitchCountStatistic();
                break;
            case VenvyStatisticsManager.PLAY_CONFIRM:

                break;
            case VenvyStatisticsManager.PRELOAD_FLOW:
                paramData = preLoadFlowStatistic();
                break;
        }
        return paramData;
    }

    private JSONObject abAppletTrackStatistics() throws JSONException {
        JSONObject dataObj = new JSONObject();
        dataObj.put("originMiniAppId", statisticsInfoBean.originMiniAppId);
        dataObj.put("miniAppId", statisticsInfoBean.miniAppId);
        return dataObj;
    }

    private JSONObject visualSwitchCountStatistic() throws JSONException {
        JSONObject dataObj = new JSONObject();
        dataObj.put("onOrOff", getOnOrOff());
        return dataObj;
    }

    private JSONObject preLoadFlowStatistic() throws JSONException {
        JSONObject dataObj = new JSONObject();
        dataObj.put("downLoadStage", getDownLoadStage());
        dataObj.put("videoId", getVideoId());
        dataObj.put("fileInfo", getFileInfoJson());
        return dataObj;
    }

    private String getOnOrOff() {
        String onOrOff = "0";
        if(statisticsInfoBean != null){
            onOrOff = statisticsInfoBean.onOrOff;
        }
        return onOrOff;
    }

    private String getVideoId() {
        String videoId = "";
        if(statisticsInfoBean != null){
            videoId = statisticsInfoBean.videoId;
        }
        return videoId;
    }

    private int getType() {
        int type = 0;
        if(statisticsInfoBean != null){
            type = statisticsInfoBean.type;
        }
        return type;
    }

    private int getDownLoadStage() {
        int downLoadStage = 0;
        if(statisticsInfoBean != null){
            downLoadStage = statisticsInfoBean.downLoadStage;
        }
        return downLoadStage;
    }

    private JSONArray getFileInfoJson() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject tmpObj = null;
        if(statisticsInfoBean != null && statisticsInfoBean.fileInfoBeans.size() > 0){
            for(int i = 0; i < statisticsInfoBean.fileInfoBeans.size(); i++){
                tmpObj = new JSONObject();
                tmpObj.put("fileName" , statisticsInfoBean.fileInfoBeans.get(i).fileName);
                tmpObj.put("filePath", statisticsInfoBean.fileInfoBeans.get(i).filePath);
                tmpObj.put("fileSize", statisticsInfoBean.fileInfoBeans.get(i).fileSize);
                jsonArray.put(tmpObj);
                tmpObj = null;
            }
        }
        return jsonArray;
    }

    private JSONObject createCommonParamJsonObj() throws JSONException {
        String commonParamJson = "";
        try {
            Class<?> clazz = Class.forName("cn.com.venvy.lua.plugin.LVCommonParamPlugin");
            Method method = clazz.getMethod("getCommonParamJson");
            commonParamJson = (String)method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(commonParamJson);
    }

    @Override
    public IRequestHandler createRequestHandler() {
        return new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                if (!response.isSuccess()) {
                    VideoPlusStatisticsCallback configCallback = getAppConfigCallback();
                    if (configCallback != null) {
                        configCallback.updateError(new Exception("get app config error"));
                    }
                }
                VideoPlusStatisticsCallback configCallback = getAppConfigCallback();
                if (configCallback == null) {
                    return;
                }
                String result = response.getResult();
                if (!TextUtils.isEmpty(result)) {
                    configCallback.updateComplete(result);
                } else {
                    configCallback.updateError(new NullPointerException("get app config NullPointerException"));
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {
                VideoPlusStatisticsCallback configCallback = getAppConfigCallback();
                if (configCallback != null) {
                    configCallback.updateError(e);
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

    public interface VideoPlusStatisticsCallback {
        void updateComplete(String result);

        void updateError(Throwable t);
    }

    @Override
    public void destroy() {
        super.destroy();
        videoPlusStatisticsCallback = null;
        statisticsInfoBean = null;
    }

    private VideoPlusStatisticsCallback getAppConfigCallback() {
        return videoPlusStatisticsCallback;
    }
}
