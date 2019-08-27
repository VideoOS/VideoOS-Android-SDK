package cn.com.venvy.common.statistics;

import android.text.TextUtils;

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

    public void submitFileStatisticsInfo(List<DownloadTask> downloadTaskList ,int downLoadStage) {
        if(downloadTaskList == null || downloadTaskList.size() <= 0){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = convertDownloadTaskList2StatisticsInfoBean(downloadTaskList, downLoadStage);
        if(statisticsInfoBean != null){
            ThreadManager.getInstance().createLongPool().execute(new AsyncStatisticsRunnable(platform,statisticsInfoBean));
        }
    }

    public void submitImageStatisticsInfo(List<DownloadImageTask> downloadTaskList , int downLoadStage) {
        if(downloadTaskList == null || downloadTaskList.size() <= 0){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = convertDownloadImageTaskList2StatisticsInfoBean(downloadTaskList, downLoadStage);
        if(statisticsInfoBean != null){
            ThreadManager.getInstance().createLongPool().execute(new AsyncStatisticsRunnable(platform,statisticsInfoBean));
        }
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
        return statisticsInfoBean;

    }

    public void submitFileStatisticsInfo(StatisticsInfoBean.FileInfoBean fileInfoBean , int downLoadStage) {
        if(fileInfoBean == null){
            return;
        }
        StatisticsInfoBean statisticsInfoBean = new StatisticsInfoBean();
        List<StatisticsInfoBean.FileInfoBean> fileInfoBeanList = new ArrayList<>();
        fileInfoBeanList.add(fileInfoBean);
        statisticsInfoBean.fileInfoBeans = fileInfoBeanList;
        statisticsInfoBean.downLoadStage = downLoadStage;
        if(statisticsInfoBean != null){
            ThreadManager.getInstance().createLongPool().execute(new AsyncStatisticsRunnable(platform,statisticsInfoBean));
        }
    }

    public void submitFileStatisticsInfo(DownloadTask downloadTask ,int downLoadStage) {
        if(downloadTask == null){
            return;
        }
        List<DownloadTask> downloadTaskList = new ArrayList<>();
        downloadTaskList.add(downloadTask);
        StatisticsInfoBean statisticsInfoBean = convertDownloadTaskList2StatisticsInfoBean(downloadTaskList, downLoadStage);
        if(statisticsInfoBean != null){
            ThreadManager.getInstance().createLongPool().execute(new AsyncStatisticsRunnable(platform,statisticsInfoBean));
        }
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
        return statisticsInfoBean;
    }
}
