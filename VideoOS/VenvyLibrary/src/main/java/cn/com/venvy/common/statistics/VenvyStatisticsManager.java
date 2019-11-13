package cn.com.venvy.common.statistics;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.Platform;
import cn.com.venvy.common.download.DownloadDbHelper;
import cn.com.venvy.common.download.DownloadImageTask;
import cn.com.venvy.common.download.DownloadTask;

/**
 * Created by videopls on 2019/8/19.
 */

public final class VenvyStatisticsManager {
    public static final int AB_APPLET_TRACK = 1;
    public static final int USER_ACTION = 2;
    public static final int VISUAL_SWITCH_COUNT = 3;
    public static final int PLAY_CONFIRM = 4;
    public static final int PRELOAD_FLOW = 5;

    private Platform platform;
    private VenvyStatisticsManager() {

    }

    private static class Holder {
        private static VenvyStatisticsManager instance = new VenvyStatisticsManager();
    }

    public static VenvyStatisticsManager getInstance() {
        return Holder.instance;
    }

    public void init(Platform platform) {
        this.platform = platform;
    }

    public void submitCommonTrack(int type, String dataJson) {
        if(TextUtils.isEmpty(dataJson)){
            return;
        }
        if (VenvyStatisticsManager.AB_APPLET_TRACK == type) {
            submitABAppletTrackStatisticsInfo(dataJson);
        } else if (VenvyStatisticsManager.USER_ACTION == type) {

        }else if (VenvyStatisticsManager.VISUAL_SWITCH_COUNT == type) {

        }else if (VenvyStatisticsManager.PLAY_CONFIRM == type) {

        }else if (VenvyStatisticsManager.PRELOAD_FLOW == type) {

        }
    }

    private void submitABAppletTrackStatisticsInfo(String dataJson) {
        try {
            JSONObject dataObj = new JSONObject(dataJson);
            if(dataObj == null){
                return;
            }
            String originMiniAppId = dataObj.optString("originMiniAppId");
            String miniAppId = dataObj.optString("miniAppId");
            if(TextUtils.isEmpty(originMiniAppId) || TextUtils.isEmpty(miniAppId)){
                return;
            }
            StatisticsInfoBean statisticsInfoBean = new StatisticsInfoBean();
            statisticsInfoBean.type = VenvyStatisticsManager.AB_APPLET_TRACK;
            statisticsInfoBean.originMiniAppId = originMiniAppId;
            statisticsInfoBean.miniAppId = miniAppId;
            executeThread(statisticsInfoBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void executeThread(StatisticsInfoBean statisticsInfoBean) {
        if(statisticsInfoBean != null && platform != null){
            ThreadManager.getInstance().createLongPool().execute(new AsyncStatisticsRunnable(platform,statisticsInfoBean));
        }
    }

    public void submitVisualSwitchStatisticsInfo(String onOrOff) {
        if(TextUtils.isEmpty(onOrOff) || platform == null){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = new StatisticsInfoBean();
        statisticsInfoBean.type = VenvyStatisticsManager.VISUAL_SWITCH_COUNT;
        statisticsInfoBean.onOrOff = onOrOff;

        executeThread(statisticsInfoBean);
    }

    public void submitFileStatisticsInfo(List<DownloadTask> downloadTaskList ,int downLoadStage) {
        if(downloadTaskList == null || downloadTaskList.size() <= 0 || platform == null){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = convertDownloadTaskList2StatisticsInfoBean(downloadTaskList, downLoadStage);
        executeThread(statisticsInfoBean);
    }

    public void submitImageStatisticsInfo(List<DownloadImageTask> downloadTaskList , int downLoadStage) {
        if(downloadTaskList == null || downloadTaskList.size() <= 0 || platform == null){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = convertDownloadImageTaskList2StatisticsInfoBean(downloadTaskList, downLoadStage);
        executeThread(statisticsInfoBean);
    }

    private StatisticsInfoBean convertDownloadImageTaskList2StatisticsInfoBean(List<DownloadImageTask> downloadImageTaskList, int downLoadStage) {
        if(downloadImageTaskList == null || downloadImageTaskList.size() <= 0){
            return null;
        }
        StatisticsInfoBean statisticsInfoBean = new StatisticsInfoBean();
        List<StatisticsInfoBean.FileInfoBean> fileInfoBeans = new ArrayList<>();
        StatisticsInfoBean.FileInfoBean fileInfoBean = null;

        for (DownloadImageTask task : downloadImageTaskList){
            String imageUrl = task.getImageUrl();
            if(imageUrl == null){
                continue;
            }
            fileInfoBean = new StatisticsInfoBean.FileInfoBean();

            String url = imageUrl;
            String fileName = "";
            if(!TextUtils.isEmpty(url.trim())){
                fileName = url.substring(url.lastIndexOf("/")+1);
            }
            long downloadSize = 0;

            fileInfoBean.fileName = fileName;
            fileInfoBean.filePath = url;
            fileInfoBean.fileSize = downloadSize;
            fileInfoBeans.add(fileInfoBean);
        }
        statisticsInfoBean.downLoadStage = downLoadStage;
        statisticsInfoBean.fileInfoBeans = fileInfoBeans;
        statisticsInfoBean.type = VenvyStatisticsManager.PRELOAD_FLOW;
        return statisticsInfoBean;
    }

    public void submitFileStatisticsInfo(StatisticsInfoBean.FileInfoBean fileInfoBean , int downLoadStage) {
        if(fileInfoBean == null || platform == null){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = new StatisticsInfoBean();
        List<StatisticsInfoBean.FileInfoBean> fileInfoBeanList = new ArrayList<>();
        fileInfoBeanList.add(fileInfoBean);
        statisticsInfoBean.fileInfoBeans = fileInfoBeanList;
        statisticsInfoBean.downLoadStage = downLoadStage;
        statisticsInfoBean.type = VenvyStatisticsManager.PRELOAD_FLOW;
        executeThread(statisticsInfoBean);
    }

    public void submitFileStatisticsInfo(DownloadTask downloadTask ,int downLoadStage) {
        if(downloadTask == null || platform == null){
            return;
        }
        List<DownloadTask> downloadTaskList = new ArrayList<>();
        downloadTaskList.add(downloadTask);
        StatisticsInfoBean statisticsInfoBean = convertDownloadTaskList2StatisticsInfoBean(downloadTaskList, downLoadStage);
        executeThread(statisticsInfoBean);
    }

    private StatisticsInfoBean convertDownloadTaskList2StatisticsInfoBean(List<DownloadTask> downloadTaskList, int downLoadStage) {
        if(downloadTaskList == null || downloadTaskList.size() <= 0){
            return null;
        }

        StatisticsInfoBean statisticsInfoBean = new StatisticsInfoBean();
        List<StatisticsInfoBean.FileInfoBean> fileInfoBeans = new ArrayList<>();
        StatisticsInfoBean.FileInfoBean fileInfoBean = null;

        for (DownloadTask task : downloadTaskList){
            DownloadDbHelper.DownloadInfo downloadInfo = task.getmDownloadInfo();
            if(downloadInfo == null){
                continue;
            }
            fileInfoBean = new StatisticsInfoBean.FileInfoBean();

            String url = downloadInfo.url;
            String fileName = "";
            if(!TextUtils.isEmpty(url.trim())){
                fileName = url.substring(url.lastIndexOf("/")+1);
            }
            long downloadSize = downloadInfo.downloadSize;

            fileInfoBean.fileName = fileName;
            fileInfoBean.filePath = url;
            fileInfoBean.fileSize = downloadSize;
            fileInfoBeans.add(fileInfoBean);
        }
        statisticsInfoBean.downLoadStage = downLoadStage;
        statisticsInfoBean.fileInfoBeans = fileInfoBeans;
        statisticsInfoBean.type = VenvyStatisticsManager.PRELOAD_FLOW;
        return statisticsInfoBean;
    }
}
