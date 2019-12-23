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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.taobao.luaview.util.ToastUtil;

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
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by Lucas on 2019/12/18.
 * 第三方广告内容对接
 */
public class VideoAdsModel extends VideoPlusBaseModel {

    private static final String ADS_URL = "http://mock.videojj.com/mock/5b029ad88e21c409b29a2114/api/getDownloadUrl#!method=get";
    private static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private DownloadTaskRunner mDownloadTaskRunner;
    private int notificationIconRes;
    private Notification notification;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private Notification.Builder builderO;
private String fileProviderAuthorities;

    public VideoAdsModel(@NonNull Platform platform, int notificationIconRes,String fileProviderAuthorities) {
        super(platform);
        this.notificationIconRes = notificationIconRes;
        this.fileProviderAuthorities = fileProviderAuthorities;
    }

    @Override
    public Request createRequest() {
        return HttpRequest.get(ADS_URL);
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
                    String downloadUrl = dataJson.getString("downloadUrl");
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


        notificationManager.notify(1, notification);
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
                initNotification();
            }

            @Override
            public void onTaskProgress(DownloadTask downloadTask, int progress) {
                VenvyLog.d("onTaskProgress : " + progress);
                if (progress <= 1) return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builderO.setProgress(100, progress, false);
                    builderO.setContentText("下载完成度："+progress+"%");
                    notification = builderO.build();
                } else {
                    builder.setProgress(100, progress, false);
                    builder.setContentText("下载完成度："+progress+"%");
                    notification = builder.build();

                }
                notificationManager.notify(1, notification);
            }

            @Override
            public void onTaskFailed(DownloadTask downloadTask, @Nullable Throwable throwable) {

            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {
                VenvyLog.d("onTaskSuccess");
                ToastUtil.showToast(getPlatform().getContentViewGroup().getContext(), "下载完成");

                // todo : 前往安装APK 页面
                Intent resultIntent = new Intent(Intent.ACTION_VIEW);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Uri data;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    // 7.0 通过FileProvider的方式访问
                    data = FileProvider.getUriForFile(getPlatform().getContentViewGroup().getContext(),fileProviderAuthorities,new File(filePath));
                    resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 赋予临时权限
                }else{
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

                notificationManager.notify(1, notification);
            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                VenvyLog.d("onTasksComplete");

            }
        });
    }
}
