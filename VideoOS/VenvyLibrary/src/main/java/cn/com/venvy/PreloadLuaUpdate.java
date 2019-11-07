package cn.com.venvy;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyMD5Util;

/**
 * Created by videojj_pls on 2019/7/25.
 * 结构下载Lua
 */

public class PreloadLuaUpdate {
    private final static String TAG = PreloadLuaUpdate.class.getName();
    private final String PARSE_LOCAL_LUA = "parse_local_luas";
    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private DownloadTaskRunner mDownloadTaskRunner;
    private CacheLuaUpdateCallback mUpdateCallback;
    private Platform mPlatform;
    private int preloadType;

    public PreloadLuaUpdate(int preloadType, Platform platform, PreloadLuaUpdate.CacheLuaUpdateCallback callback) {
        this.preloadType = preloadType;
        this.mPlatform = platform;
        this.mUpdateCallback = callback;
    }

    public interface CacheLuaUpdateCallback {
        void updateComplete(boolean isUpdateByNetWork);

        void updateError(Throwable t);
    }

    public void destroy() {
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }
        VenvyAsyncTaskUtil.cancel(PARSE_LOCAL_LUA);
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
        checkUpdateLua(luaUrls, null);
    }

    /***
     *
     * @param luaUrls
     * @param miniAppId
     */
    public void startDownloadLuaFile(JSONArray luaUrls, String miniAppId) {
        if (luaUrls == null || luaUrls.length() <= 0) {
            CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("update Lua error,because down urls is null"));
            }
            return;
        }
        checkUpdateLua(luaUrls, miniAppId);
    }

    /***
     * 检测列表需要下载的Lua文件
     * @param luaUrls
     */
    private void checkUpdateLua(final JSONArray luaUrls, final String miniAppId) {
        VenvyAsyncTaskUtil.doAsyncTask(PARSE_LOCAL_LUA, new VenvyAsyncTaskUtil.IDoAsyncTask<JSONArray,
                Set<String>>() {
            @Override
            public Set<String> doAsyncTask(JSONArray... urls) throws Exception {
                if (urls == null || urls.length == 0) {
                    return null;
                }
                Set<String> needDownUrls = new LinkedHashSet();
                try {
                    JSONArray jsonArray = urls[0];
                    int len = luaUrls.length();
                    for (int i = 0; i < len; i++) {
                        JSONObject obj = jsonArray.optJSONObject(i);
                        if (obj == null) {
                            break;
                        }
                        String url = obj.optString("url");
                        String md5 = obj.optString("md5");
                        if (TextUtils.isEmpty(url)) {
                            continue;
                        }
                        String cacheMd5 = getFileLuaEncoderByMd5(Uri.parse(url).getLastPathSegment(), miniAppId);
                        if (!TextUtils.equals(md5, cacheMd5)) {
                            needDownUrls.add(url);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VenvyLog.i(TAG, "VideoPlusLuaUpdate ——> checkDownLuaUrls error：" + e.getMessage());
                }

                return needDownUrls;
            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<Set<String>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(Set<String> urls) {
                if (urls == null) {
                    return;
                }
                if (urls.size() == 0) {
                    //本地存在 无需下载直接返回成功回调
                    CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateComplete(false);
                    }
                    return;
                }
                startDownloadLuaFile(urls, miniAppId);
            }

            @Override
            public void onCancelled() {
                VenvyLog.e("cancel");
            }

            @Override
            public void onException(Exception ie) {
            }
        }, luaUrls);

    }

    private void startDownloadLuaFile(Set<String> urls, String miniAppId) {
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(mPlatform);
        }
        ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (String string : urls) {
            String downPath = TextUtils.isEmpty(miniAppId) ? VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + Uri.parse(string).getLastPathSegment() : VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + miniAppId + File.separator + Uri.parse(string).getLastPathSegment();
            DownloadTask task = new DownloadTask(App.getContext(), string, downPath, true);
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
                VenvyStatisticsManager.getInstance().submitFileStatisticsInfo(successfulTasks, preloadType);
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

    private PreloadLuaUpdate.CacheLuaUpdateCallback getCacheLuaUpdateCallback() {
        return mUpdateCallback;
    }

    /***
     * 获取本地Lua文件内容MD5值
     * @param fileName
     * @return
     */
    private String getFileLuaEncoderByMd5(String fileName, String miniAppId) {
        if (!TextUtils.isEmpty(miniAppId)) {
            return VenvyMD5Util.EncoderByMd5(new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + miniAppId + File.separator + fileName));
        }
        return VenvyMD5Util.EncoderByMd5(new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + fileName));
    }
}
