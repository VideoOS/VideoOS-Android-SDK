package cn.com.videopls.pub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.com.venvy.Platform;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.receiver.AppStatusObserver;
import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;

/**
 * Created by Lucas on 2019/12/18.
 * 第三方广告内容对接
 */
public class VideoAdsHandler extends BroadcastReceiver implements VenvyObserver {

    private static final String ADS_URL = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/getDownloadUrl#!method=get"; // for test
    private static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final int NOTIFICATION_ID = 9527;
    private DownloadTaskRunner mDownloadTaskRunner;
    private int notificationIconRes;
    private Notification notification;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification.Builder builderO;
    private String fileProviderAuthorities;

    private AppStatusObserver appStatusObserver;

    private String downloadAPI;
    private String[] isTrackLinks;
    private String[] dsTrackLinks;
    private String[] dfTrackLinks;
    private String[] instTrackLinks;
    private Platform platform;
    private String launchPlanId;

    public VideoAdsHandler() {
    }

    public VideoAdsHandler(@NonNull Platform platform) {
        this.platform = platform;
        this.notificationIconRes = VenvyResourceUtil.getDrawableId(platform.getContentViewGroup().getContext(), "ic_launcher");
        this.fileProviderAuthorities = fileProviderAuthorities;
    }


    public void initData(Bundle bundle, String fileProviderAuthorities) {
        downloadAPI = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DOWNLOAD_API);
        dsTrackLinks = bundle.getStringArray("dsTrackLinks"); // 下载开始
        dfTrackLinks = bundle.getStringArray("dfTrackLinks"); // 下载完成
        isTrackLinks = bundle.getStringArray("isTrackLinks"); // 安装开始
        instTrackLinks = bundle.getStringArray("instTrackLinks"); // 安装完成
        launchPlanId = bundle.getString("launchPlanId"); // 投放计划id
        this.fileProviderAuthorities = fileProviderAuthorities;

        VenvyStatisticsManager.getInstance().init(platform);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_INSTALL_START, this);
    }


    private void initNotification() {
        Context context = platform.getContentViewGroup().getContext();
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "VideoOS";
            NotificationChannel mChannel = new NotificationChannel(channelId, "video_os_channel_name", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            if (builderO == null) {
                builderO = new Notification.Builder(context, channelId)
                        .setSmallIcon(notificationIconRes)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), notificationIconRes))
                        .setAutoCancel(false)
                        .setContentTitle("开始下载")
                        .setContentText("准备下载")
                        .setProgress(100, 0, false);
                notification = builderO.build();
            }
        } else {
            if (builder == null) {
                builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(notificationIconRes)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), notificationIconRes))
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(false)
                        .setContentTitle("开始下载")
                        .setContentText("准备下载")
                        .setProgress(100, 0, false);
                notification = builder.build();
            }
        }


        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void execDownloadTask() {
        if (TextUtils.isEmpty(downloadAPI)) {
            VenvyLog.e("download url is empty, so abort next logic");
            return;
        }
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(platform);
        }
        final String filePath = VenvyFileUtil.getCachePath(platform.getContentViewGroup().getContext()) + LUA_CACHE_PATH + File.separator + Uri.parse(downloadAPI).getLastPathSegment();
        VenvyLog.d("download to : " + filePath);
        DownloadTask downloadTask = new DownloadTask(platform.getContentViewGroup().getContext(), downloadAPI, filePath, true);
        mDownloadTaskRunner.startTask(downloadTask, new TaskListener<DownloadTask, Boolean>() {
            @Override
            public boolean isFinishing() {
                return false;
            }

            @Override
            public void onTaskStart(DownloadTask downloadTask) {
                VenvyLog.d("onTaskStart");
                trackToVideoOS("4");
                uploadTrack(dsTrackLinks); //  开始下载上报track
                initNotification();
            }

            @Override
            public void onTaskProgress(DownloadTask downloadTask, int progress) {
                VenvyLog.d("onTaskProgress : " + progress);
                if (progress <= 1) return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builderO.setProgress(100, progress, false);
                    builderO.setContentText("下载完成度：" + progress + "%");
                    notification = builderO.build();
                } else {
                    builder.setProgress(100, progress, false);
                    builder.setContentText("下载完成度：" + progress + "%");
                    notification = builder.build();

                }
                notificationManager.notify(NOTIFICATION_ID, notification);
            }

            @Override
            public void onTaskFailed(DownloadTask downloadTask, @Nullable Throwable throwable) {

            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {
                VenvyLog.d("onTaskSuccess");

                registerAppReceiver(filePath);// 开启对第三方APP的安装监听


                // 要监听通知的点击事件，所以这里发送一个广播
                Intent notifyIntent = new Intent(platform.getContentViewGroup().getContext(), VideoAdsHandler.class);
                notifyIntent.putExtra("filePath", filePath);
                notifyIntent.putExtra("fileProvider", fileProviderAuthorities);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(platform.getContentViewGroup().getContext(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = builderO.setContentTitle("下载完成")
                            .setContentText("点击安装")
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent).build();
                } else {
                    notification = builder.setContentTitle("下载完成")
                            .setContentText("点击安装")
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent).build();
                }

                notificationManager.notify(NOTIFICATION_ID, notification);
            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                VenvyLog.d("onTasksComplete");
                uploadTrack(dfTrackLinks); // 下载完成上报track
                trackToVideoOS("5");
            }
        });
    }


    /**
     * 批量上报track data
     *
     * @param links
     */
    private void uploadTrack(String[] links) {
        if (links != null && links.length > 0) {
            for (int i = 0, len = links.length; i < len; i++) {
                new AdsTrackModel(platform, links[i]).startRequest();
            }
        }
    }


    /**
     * 上传到我们自己的后台，eventType : 1:展示曝光 2:点击曝光 3:点击事件  4:app开始下载 5:下载完成 6:开始安装 7: 安装完成
     *
     * @param eventType
     */
    private void trackToVideoOS(String eventType) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("videoId", platform.getPlatformInfo().getVideoId());
            jsonObject.put("type", "3");// 1:信息层 2:热点 3:下载
            jsonObject.put("eventType", eventType);
            jsonObject.put("launchPlanId", launchPlanId);
            VenvyStatisticsManager.getInstance().submitCommonTrack(2, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void registerAppReceiver(String filePath) {
        String filePackageName = VenvyFileUtil.getPackageNameByApkFile(platform.getContentViewGroup().getContext(), filePath);

        if (TextUtils.isEmpty(filePackageName)) return;

        if (appStatusObserver == null) {
            appStatusObserver = new AppStatusObserver(platform.getContentViewGroup().getContext());
        }
        appStatusObserver.registerReceiver(filePackageName, new AppStatusObserver.AppStatusChangeListener() {
            @Override
            public void onAppInstall(String packageName) {
                // 安装完成os
                uploadTrack(instTrackLinks);
                trackToVideoOS("7");
                VenvyLog.d("onAppInstall track: " + packageName);
            }

            @Override
            public void onAppUninstall(String packageName) {
                VenvyLog.d("onAppUninstall track: " + packageName);
            }
        });
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String filePath = intent.getStringExtra("filePath");
        String fileProvider = intent.getStringExtra("fileProvider");
        // 前往安装APK页面，在此时算作开始安装
        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0 通过FileProvider的方式访问
            data = FileProvider.getUriForFile(context, fileProvider, new File(filePath));
            resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 赋予临时权限
        } else {
            data = Uri.fromFile(new File(filePath));
        }
        resultIntent.setDataAndType(data, "application/vnd.android.package-archive");

        context.startActivity(resultIntent);


        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_INSTALL_START);

    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        // track [开始安装]的数据
        uploadTrack(isTrackLinks);
        trackToVideoOS("6");
    }

    public void release() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_INSTALL_START, this);
        if (appStatusObserver != null) {
            appStatusObserver.unRegisterReceiver();
        }
    }
}
