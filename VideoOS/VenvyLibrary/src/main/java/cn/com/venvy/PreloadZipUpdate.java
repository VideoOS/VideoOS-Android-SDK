package cn.com.venvy;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.statistics.VenvyStatisticsManager;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyGzipUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyMD5Util;

/**
 * Created by videojj_pls on 2019/7/25.
 * 结构下载Zip 以及解压
 */

public class PreloadZipUpdate {
    private final static String TAG = PreloadZipUpdate.class.getName();
    private static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final String LUA_ZIP = "/lua/os/chain.zip";
    private static final String LUA_ZIP_CACHE = "/lua/os/cache/demo/zip";
    private final String PARSE_LOCAL_ZIP = "parse_local_zip";
    private final String PARSE_UNZIP = "unzip";
    private DownloadTaskRunner mDownloadTaskRunner;
    private CacheZipUpdateCallback mUpdateCallback;
    private Platform mPlatform;
    private int preloadType;

    public PreloadZipUpdate(int preloadType, Platform platform, PreloadZipUpdate.CacheZipUpdateCallback callback) {
        this.preloadType = preloadType;
        this.mPlatform = platform;
        this.mUpdateCallback = callback;
    }

    public interface CacheZipUpdateCallback {
        void updateComplete(JSONArray zipJsonDataArray);

        void updateError(Throwable t);
    }

    public void destroy() {
        VenvyAsyncTaskUtil.cancel(PARSE_LOCAL_ZIP);
        VenvyAsyncTaskUtil.cancel(PARSE_UNZIP);
        if (mDownloadTaskRunner != null) {
            mDownloadTaskRunner.destroy();
        }
        mUpdateCallback = null;
    }

    /***
     * 开启下载Lua文件
     * @param luaUrls Lua文件列表
     */
    public void startDownloadZipFile(JSONArray luaUrls) {
        if (luaUrls == null || luaUrls.length() <= 0) {
            CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateComplete(new JSONArray());
            }
            return;
        }
        //检查 需要下载的Url
        checkDownZipUrls(luaUrls);
    }

    /***
     * 检查获取需要下载的Zip文件
     * @param zipUrls
     */
    private void checkDownZipUrls(final JSONArray zipUrls) {
        VenvyAsyncTaskUtil.doAsyncTask(PARSE_LOCAL_ZIP, new VenvyAsyncTaskUtil.IDoAsyncTask<JSONArray,
                List<String>>() {
            @Override
            public List<String> doAsyncTask(JSONArray... zips) throws Exception {
                if (zips == null || zips.length == 0) {
                    return null;
                }
                List<String> needDownUrls = new ArrayList<>();
                try {
                    JSONArray jsonArray = zips[0];
                    int len = jsonArray.length();
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
                        String cacheMd5 = getFileZipEncoderByMd5(Uri.parse(url).getLastPathSegment());
                        if (!TextUtils.equals(md5, cacheMd5)) {
                            needDownUrls.add(url);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VenvyLog.i(TAG, "VideoPlusZipUpdate ——> checkDownZipUrls error：" + e.getMessage());
                }
                return needDownUrls;
            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<List<String>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(List<String> urls) {
                if (urls == null) {
                    return;
                }
                List<String> zipUrlArray = getAllZipUrls(zipUrls);
                if (urls.size() == 0) {
                    CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                    List<File> zipFiles = getZipFilesWithUrl(zipUrlArray);
                    if (zipFiles == null || zipFiles.size() <= 0) {
                        if (callback != null) {
                            callback.updateError(new Exception("update zip error,because down urls is failed"));
                        }
                        return;
                    }
                    unZipAndReadData(zipFiles);
                    return;
                }
                startDownloadZipFile(zipUrlArray, urls);
            }

            @Override
            public void onCancelled() {
                VenvyLog.e("cancel");
            }

            @Override
            public void onException(Exception ie) {
            }
        }, zipUrls);
    }

    /***
     * 解压Zip并读取文件
     * @param zipFiles
     */
    private void unZipAndReadData(final List<File> zipFiles) {
        if (zipFiles == null || zipFiles.size() <= 0) {
            CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("update zip data error,because down urls is failed"));
            }
            return;
        }
        VenvyAsyncTaskUtil.doAsyncTask(PARSE_UNZIP, new VenvyAsyncTaskUtil.IDoAsyncTask<File, List<String>>() {

            @Override
            public List<String> doAsyncTask(File... files) throws Exception {
                List<String> cacheUrls = new ArrayList<>();
                VenvyFileUtil.createDir(VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP_CACHE);
                for (File file : files) {
                    final String cacheUrlPath = file.getAbsolutePath();
                    VenvyGzipUtil.unzipFile(cacheUrlPath, VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP_CACHE, false);
                }
                cacheUrls.addAll(VenvyFileUtil.getFileName(VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP_CACHE));
                VenvyFileUtil.copyDir(VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP_CACHE, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH);
                return cacheUrls;

            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<List<String>>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(List<String> cacheUrls) {
                VenvyFileUtil.delFolder(VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP_CACHE);
                final JSONArray queryArray = new JSONArray();
                CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                for (String cacheUrlPath : cacheUrls) {
                    if (callback != null) {
                        String fileName = Uri.parse(cacheUrlPath).getLastPathSegment();
                        if (TextUtils.isEmpty(fileName)) {
                            callback.updateError(new Exception(""));
                            return;
                        }
                        File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, fileName);
                        if (!file.exists() || !file.isFile()) {
                            callback.updateError(new Exception(""));
                            return;
                        }
                        String queryChainData = VenvyFileUtil.readFormFile(App.getContext(), VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + fileName);
                        if (TextUtils.isEmpty(queryChainData)) {
                            callback.updateError(new Exception(""));
                            return;
                        }
                        try {
                            queryArray.put(new JSONObject(queryChainData));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                callback.updateComplete(queryArray);
            }

            @Override
            public void onCancelled() {
                CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                if (callback != null) {
                    callback.updateError(new Exception("unzip error"));
                }
            }

            @Override
            public void onException(Exception ie) {

            }
        }, zipFiles.toArray(new File[zipFiles.size()]));
    }

    /***
     * 下载Zip
     * @param downZipUrls 全部的Url
     * @param needDownZipUrls 需要下载的Url
     */
    private void startDownloadZipFile(final List<String> downZipUrls, final List<String> needDownZipUrls) {
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(mPlatform);
        }
        final ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (String string : needDownZipUrls) {
            DownloadTask task = new DownloadTask(App.getContext(), string, VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP + File.separator + Uri.parse(string).getLastPathSegment(), true);
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
                if (failedTasks != null && failedTasks.size() > 0) {
                    CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update error,because downloadTask error"));
                    }
                    return;
                }
                List<File> zipFiles = getZipFilesWithUrl(downZipUrls);
                if (zipFiles == null || zipFiles.size() <= 0) {
                    CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update zip data error,because down urls is failed"));
                    }
                    return;
                }
                unZipAndReadData(zipFiles);
            }
        });
    }

    /***
     * 获取所有Zip Down urls
     * @param zipUrls
     * @return
     */
    private List<String> getAllZipUrls(JSONArray zipUrls) {
        List<String> zipUrlArray = new ArrayList<>();
        if (zipUrls == null || zipUrls.length() <= 0) {
            return zipUrlArray;
        }
        int len = zipUrls.length();
        for (int i = 0; i < len; i++) {
            JSONObject obj = zipUrls.optJSONObject(i);
            if (obj == null) {
                break;
            }
            String url = obj.optString("url");
            if (!TextUtils.isEmpty(url)) {
                zipUrlArray.add(url);
            }
        }
        return zipUrlArray;
    }

    /***
     * 获取回调
     * @return
     */
    private PreloadZipUpdate.CacheZipUpdateCallback getCacheLuaUpdateCallback() {
        return mUpdateCallback;
    }

    private List<File> getZipFilesWithUrl(List<String> urls) {
        List<File> zipFlies = new ArrayList<>();
        if (urls == null || urls.size() <= 0) {
            return zipFlies;
        }
        for (String url : urls) {
            String path = VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP + File.separator + Uri.parse(url).getLastPathSegment();
            File hasDownFile = new File(path);
            if (!hasDownFile.exists() || !TextUtils.equals("zip", VenvyFileUtil.getExtension(path))) {
                continue;
            }
            zipFlies.add(hasDownFile);
        }
        return zipFlies;
    }

    /***
     * 获取本地Zip文件内容MD5值
     * @param fileName
     * @return
     */
    private String getFileZipEncoderByMd5(String fileName) {
        return VenvyMD5Util.EncoderByMd5(new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP + File.separator + fileName));
    }
}
