package cn.com.videopls.pub;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.App;
import cn.com.venvy.Platform;
import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyMD5Util;

/**
 * Created by videojj_pls on 2019/7/25.
 * 结构下载Lua
 */

public class VideoPlusLuaUpdate {
    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private DownloadTaskRunner mDownloadTaskRunner;
    private CacheLuaUpdateCallback mUpdateCallback;
    private Platform mPlatform;

    public VideoPlusLuaUpdate(Platform platform, VideoPlusLuaUpdate.CacheLuaUpdateCallback callback) {
        this.mPlatform = platform;
        this.mUpdateCallback = callback;
    }

    public interface CacheLuaUpdateCallback {
        void updateComplete(boolean isUpdateByNetWork);

        void updateError(Throwable t);
    }

    public void destroy() {
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner.destroy();
        }
        mUpdateCallback = null;
    }

    /***
     * 开启下载Lua文件
     * @param luaUrls Lua文件列表
     */
    public void startDownloadLuaFile(JSONArray luaUrls) {
        if (luaUrls == null || luaUrls.length() <= 0) {
            CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("update Lua error,because down urls is null"));
            }
            return;
        }
        List<String> needDownLuaUrls = checkUpdateLua(luaUrls);
        if (needDownLuaUrls.size() == 0) {
            //本地存在 无需下载直接返回成功回调
            CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateComplete(false);
            }
            return;
        }
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(mPlatform);
        }
        ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (String string : needDownLuaUrls) {
            DownloadTask task = new DownloadTask(App.getContext(), string, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + Uri.parse(string).getLastPathSegment());
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
                downloadTask.failed();
            }

            @Override
            public void onTaskSuccess(DownloadTask downloadTask, Boolean aBoolean) {

            }

            @Override
            public void onTasksComplete(@Nullable List<DownloadTask> successfulTasks, @Nullable List<DownloadTask> failedTasks) {
                CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
                if (callback != null) {
                    if (failedTasks != null && failedTasks.size() > 0) {
                        callback.updateError(new Exception("update Lua error,because down urls is failed"));
                    } else {
                        callback.updateComplete(true);
                    }
                }
            }
        });
    }

    /***
     * 检测列表需要下载的Lua文件
     * @param luaUrls
     * @return
     */
    private List<String> checkUpdateLua(JSONArray luaUrls) {
        List<String> needDownUrls = new ArrayList<>();
        int len = luaUrls.length();
        for (int i = 0; i < len; i++) {
            JSONObject obj = luaUrls.optJSONObject(i);
            if (obj == null) {
                break;
            }
            String url = obj.optString("url");
            String md5 = obj.optString("md5");
            String cacheMd5 = getFileLuaEncoderByMd5(Uri.parse(url).getLastPathSegment());
            if (!TextUtils.equals(md5, cacheMd5)) {
                needDownUrls.add(url);
            }
        }
        return needDownUrls;
    }

    private VideoPlusLuaUpdate.CacheLuaUpdateCallback getCacheLuaUpdateCallback() {
        return mUpdateCallback;
    }

    /***
     * 获取本地Lua文件内容MD5值
     * @param fileName
     * @return
     */
    private String getFileLuaEncoderByMd5(String fileName) {
        return VenvyMD5Util.EncoderByMd5(new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + fileName));
    }
}
