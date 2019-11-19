package both.video.venvy.com.appdemo.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.Platform;
import cn.com.venvy.PlatformInfo;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;
import cn.com.venvy.common.utils.VenvyLog;

import static cn.com.venvy.PreloadLuaUpdate.LUA_CACHE_PATH;

/**
 * Created by videopls on 2019/10/12.
 */

public class FileUtil {

    public static void writeToFile(Context context, @NonNull String filePath, String content) {
        File file = new File(filePath);
        OutputStreamWriter osw = null;
        try {
            if (!file.exists()) {
                VenvyLog.i("---createNewFile---");
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            if (!file.exists()) {
                VenvyLog.d("Error: file is not exists!");
                return ;
            }
            osw = new OutputStreamWriter(new FileOutputStream(filePath, false), "utf-8");
            osw.write(content);
            osw.flush();
            osw.close();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            VenvyIOUtils.close(osw);
        }
    }

    public static String getFileName(String filePath) {
        int start = filePath.lastIndexOf("/");
        int end = filePath.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return filePath.substring(start + 1, end);
        } else {
            return null;
        }
    }

    public static String getDebugALocalPath() {
        File debugLocalFilePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test/alocal/");
        if(debugLocalFilePath != null && !debugLocalFilePath.exists()){
            debugLocalFilePath.mkdirs();
        }
        return debugLocalFilePath.getAbsolutePath();
    }

    public static String getDebugBLocalPath() {
        File debugLocalFilePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test/blocal/");
        if(debugLocalFilePath != null && !debugLocalFilePath.exists()){
            debugLocalFilePath.mkdirs();
        }
        return debugLocalFilePath.getAbsolutePath();
    }


    public static void copyDir(String sourcePath, String newPath) throws IOException {

        File file = new File(sourcePath);

        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {

            (new File(newPath)).mkdirs();

        }

        for (int i = 0; i < filePath.length; i++) {

            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {

                copyDir(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);

            }

            if (new File(sourcePath + file.separator + filePath[i]).isFile()) {

                copyFile(sourcePath + file.separator + filePath[i], newPath + file.separator + filePath[i]);

            }

        }
    }

    public static void copyFile(String oldPath, String newPath) throws IOException {

        File oldFile = new File(oldPath);

        File file = new File(newPath);

        FileInputStream in = new FileInputStream(oldFile);

        FileOutputStream out = new FileOutputStream(file);

        byte[] buffer = new byte[2097152];
        int readByte = 0;
        while ((readByte = in.read(buffer)) != -1) {
            out.write(buffer, 0, readByte);
        }
    }

    public static void startDownloadLuaFile(Context context, List<String> urls, final DownloadFileCallback downloadFileCallback) {
        DownloadTaskRunner mDownloadTaskRunner = new DownloadTaskRunner(new Platform(new PlatformInfo.Builder().setAppKey(ConfigUtil.getAppKey()).setAppSecret(ConfigUtil.getAppSecret()).builder()));
        ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (String string : urls) {
            DownloadTask task = new DownloadTask(context, string, VenvyFileUtil.getCachePath(context) + LUA_CACHE_PATH + File.separator + Uri.parse(string).getLastPathSegment(),true);
            arrayList.add(task);
        }
        mDownloadTaskRunner.startTasks(arrayList, new TaskListener<DownloadTask, Boolean>() {
            @Override
            public boolean isFinishing() {
                return false;
            }

            @Override
            public void onTaskStart(DownloadTask downloadTask) {

            }

            @Override
            public void onTaskProgress(DownloadTask downloadTask, int progress) {

            }

            @Override
            public void onTaskFailed(DownloadTask downloadTask, @Nullable Throwable throwable) {

            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {

            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                if (downloadFileCallback != null) {
                    downloadFileCallback.onSuccess(successfulTasks);
                }
            }
        });
    }

    public interface DownloadFileCallback{
        public void onFailed(String errorMessage);

        public void onSuccess(List<DownloadTask> successfulTasks);
    }
}
