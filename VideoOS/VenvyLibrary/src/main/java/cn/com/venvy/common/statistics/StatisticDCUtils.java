package cn.com.venvy.common.statistics;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

import cn.com.venvy.common.download.DownloadDbHelper;
import cn.com.venvy.common.download.DownloadTask;

/**
 * Created by videopls on 2019/11/13.
 */

public class StatisticDCUtils {
    /**
     * 创建 PreLoadFlow Statistic Json
     * @param type
     * @param jsonObj
     * @return
     * @throws JSONException
     */
    public static String obtainPreLoadFlowStatisticsJson(int type, JSONObject jsonObj) throws JSONException {
        String videoId = jsonObj.optString("videoId");
        String fileInfo = jsonObj.optString("fileInfo");
        String downLoadStage = jsonObj.optString("downLoadStage");
        return createCommonObj(type,createDataObj(Integer.valueOf(downLoadStage),videoId,new JSONArray(fileInfo))).toString();
    }

    /**
     * 创建 PlayConfirm Statistic Json
     * @param type
     * @param jsonObj
     * @return
     * @throws JSONException
     */
    public static String obtainPlayConfirmStatisticsJson(int type, JSONObject jsonObj) throws JSONException {
        return createCommonObj(type, jsonObj).toString();
    }

    /**
     * 创建 VisualSwitch Statistic Json
     * @param type
     * @param jsonObj
     * @return
     * @throws JSONException
     */
    public static String obtainVisualSwitchStatisticsJson(int type, JSONObject jsonObj) throws JSONException {
        return createCommonObj(type, jsonObj).toString();
    }


    /**
     * 创建 UserAction Statistic Json
     * @param type
     * @param jsonObj
     * @return
     * @throws JSONException
     */
    public static String obtainUserActionStatisticsJson(int type, JSONObject jsonObj) throws JSONException {
        return createCommonObj(type, jsonObj).toString();
    }

    /**
     * 创建 ABApplet Statistic Json
     * @param type
     * @param jsonObj
     * @return
     * @throws JSONException
     */
    public static String obtainABAppletTrackStatisticsJson(int type, JSONObject jsonObj) throws JSONException {
        return createCommonObj(type, jsonObj).toString();
    }

    /**
     * 创建视联网开关次数统计Json
     * @param type
     * @param onOrOff
     * @return
     * @throws JSONException
     */
    public static String obtainVisualSwitchStatisticsJson(int type, String onOrOff) throws JSONException {
        JSONObject dataObj = new JSONObject();
        dataObj.put("onOrOff", onOrOff);
        return createCommonObj(type, dataObj).toString();
    }


    public static String obtainFlowStatisticJson(int type, StatisticsInfoBean.FileInfoBean fileInfoBean , int downLoadStage) throws JSONException {
        JSONArray fileInfoArray = new JSONArray();
        JSONObject tmpObj = new JSONObject();
        tmpObj.put("fileName" , fileInfoBean.fileName);
        tmpObj.put("filePath", fileInfoBean.filePath);
        tmpObj.put("fileSize", fileInfoBean.fileSize);
        fileInfoArray.put(tmpObj);
        return createCommonObj(type,createDataObj(downLoadStage,"",fileInfoArray)).toString();
    }


    public static String obtainFlowStatisticJson(int type, List<DownloadTask> downloadTaskList, int downLoadStage) throws JSONException {
        JSONArray fileInfoArray = new JSONArray();
        JSONObject tmpObj = null;
        for (DownloadTask task : downloadTaskList){
            DownloadDbHelper.DownloadInfo downloadInfo = task.getmDownloadInfo();
            if(downloadInfo == null){
                continue;
            }

            String url = downloadInfo.url;
            String fileName = "";
            if(!TextUtils.isEmpty(url.trim())){
                fileName = url.substring(url.lastIndexOf("/")+1);
            }
            long downloadSize = downloadInfo.downloadSize;

            tmpObj = new JSONObject();
            tmpObj.put("fileName" , fileName);
            tmpObj.put("filePath", url);
            tmpObj.put("fileSize", downloadSize);
            fileInfoArray.put(tmpObj);
            tmpObj = null;
        }
        return createCommonObj(type,createDataObj(downLoadStage,"",fileInfoArray)).toString();
    }

    /**
     * 通用的封装
     * @param type
     * @param dataObj
     * @return
     * @throws JSONException
     */
    private static JSONObject createCommonObj(int type, JSONObject dataObj) throws JSONException {
        JSONObject encryptDataObj = new JSONObject();
        encryptDataObj.put("type", type);
        encryptDataObj.put("data", dataObj);
        encryptDataObj.put("commonParam", createCommonParamJsonObj());
        return encryptDataObj;
    }

    /**
     * 流量统计
     * @return
     * @throws JSONException
     */
    private static JSONObject createDataObj(int downLoadStage, String videoId, JSONArray fileInfoArray) throws JSONException {
        JSONObject dataObj = new JSONObject();
        dataObj.put("videoId", videoId);
        dataObj.put("fileInfo", fileInfoArray);
        dataObj.put("downLoadStage", downLoadStage);
        return dataObj;
    }

    /**
     * 获取通用参数封装的JSONObject
     * @return
     * @throws JSONException
     */
    private static JSONObject createCommonParamJsonObj() throws JSONException {
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
}
