package cn.com.videopls.pub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.com.venvy.Platform;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.receiver.AppStatusObserver;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by Lucas on 2019/12/18.
 * 第三方广告内容对接
 */
public class VideoAdsModel extends VideoPlusBaseModel {

    private static final String ADS_URL = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/getDownloadUrl#!method=get";
    private static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final int NOTIFICATION_ID = 1;
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

    public VideoAdsModel(@NonNull Platform platform, Bundle bundle, int notificationIconRes, String fileProviderAuthorities) {
        super(platform);
        // TODO : notificationIconRes 可能乱传，这里要讨论一下，非法数据的情况
        this.notificationIconRes = notificationIconRes;
        this.fileProviderAuthorities = fileProviderAuthorities;
        initData(bundle);
    }

    private void initData(Bundle bundle) {
        downloadAPI = bundle.getString(VenvyObservableTarget.Constant.CONSTANT_DOWNLOAD_API);
        dsTrackLinks = bundle.getStringArray("dsTrackLinks"); // 下载开始
        dfTrackLinks = bundle.getStringArray("dfTrackLinks"); // 下载完成
        isTrackLinks = bundle.getStringArray("isTrackLinks"); // 安装开始
        instTrackLinks = bundle.getStringArray("instTrackLinks"); // 安装完成
    }

    @Override
    public Request createRequest() {
        // TODO : should replace official api
//        return HttpRequest.get(ADS_URL);
        return HttpRequest.get(downloadAPI);
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
                        VenvyLog.e("response was failed.");
                        return;
                    }
                    final JSONObject data = new JSONObject(response.getResult());
                    JSONObject dataJson = data.getJSONObject("data");
                    String clickId = dataJson.getString("clickid");
                    String downloadUrl = dataJson.getString("dslink");
                    VenvyLog.d("downloadUrl : " + downloadUrl);
                    execDownloadTask(downloadUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestError(Request request, @Nullable Exception e) {

            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        };
    }


    private void initNotification() {
        Context context = getPlatform().getContentViewGroup().getContext();
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

    private void execDownloadTask(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            VenvyLog.e("download url is empty, so abort next logic");
            return;
        }
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(getPlatform());
        }
        final String filePath = VenvyFileUtil.getCachePath(getPlatform().getContentViewGroup().getContext()) + LUA_CACHE_PATH + File.separator + Uri.parse(downloadUrl).getLastPathSegment();
        VenvyLog.d("download to : " + filePath);
        DownloadTask downloadTask = new DownloadTask(getPlatform().getContentViewGroup().getContext(), downloadUrl, filePath, true);
        mDownloadTaskRunner.startTask(downloadTask, new TaskListener<DownloadTask, Boolean>() {
            @Override
            public boolean isFinishing() {
                return false;
            }

            @Override
            public void onTaskStart(DownloadTask downloadTask) {
                VenvyLog.d("onTaskStart");
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

                // todo : 前往安装APK 页面
                Intent resultIntent = new Intent(Intent.ACTION_VIEW);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Uri data;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 7.0 通过FileProvider的方式访问
                    data = FileProvider.getUriForFile(getPlatform().getContentViewGroup().getContext(), fileProviderAuthorities, new File(filePath));
                    resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 赋予临时权限
                } else {
                    data = Uri.fromFile(new File(downloadTask.getDownloadCacheUrl()));
                }
                resultIntent.setDataAndType(data, "application/vnd.android.package-archive");

                PendingIntent pendingIntent = PendingIntent.getActivity(getPlatform().getContentViewGroup().getContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                // todo : 如果链接中有替换字符 _CLICK_ID_ 需要使用clickId替换之后再请求
                new AdsTrackModel(getPlatform(), links[i]).startRequest();
            }
        }
    }

    private void registerAppReceiver(String filePath) {
        String filePackageName = VenvyFileUtil.getPackageNameByApkFile(getPlatform().getContentViewGroup().getContext(), filePath);
        if (appStatusObserver == null) {
            appStatusObserver = new AppStatusObserver(getPlatform().getContentViewGroup().getContext());
        }
        appStatusObserver.registerReceiver(filePackageName, new AppStatusObserver.AppStatusChangeListener() {
            @Override
            public void onAppInstall(String packageName) {
                // 安装完成
                uploadTrack(instTrackLinks);
                VenvyLog.d("onAppInstall track: " + packageName);
            }

            @Override
            public void onAppUninstall(String packageName) {
                VenvyLog.d("onAppUninstall track: " + packageName);
            }
        });
    }


}
