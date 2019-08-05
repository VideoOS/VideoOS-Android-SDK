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
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyGzipUtil;
import cn.com.venvy.common.utils.VenvyMD5Util;

/**
 * Created by videojj_pls on 2019/7/25.
 * 结构下载Zip 以及解压
 */

public class VideoPlusZipUpdate {
    private static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private static final String LUA_ZIP = "/lua/os/chain.zip";
    private DownloadTaskRunner mDownloadTaskRunner;
    private CacheZipUpdateCallback mUpdateCallback;
    private Platform mPlatform;

    public VideoPlusZipUpdate(Platform platform, VideoPlusZipUpdate.CacheZipUpdateCallback callback) {
        this.mPlatform = platform;
        this.mUpdateCallback = callback;
    }

    public interface CacheZipUpdateCallback {
        void updateComplete(JSONArray zipJsonDataArray);

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
    public void startDownloadZipFile(JSONArray luaUrls) {
        if (luaUrls == null || luaUrls.length() <= 0) {
            CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("update Lua error,because down urls is null"));
            }
            return;
        }
        List<String> needDownZipUrls = checkUpdateZip(luaUrls);
        if (needDownZipUrls.size() == 0) {
            //本地存在 无需下载直接返回成功回调
            CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                List<File> zipFiles = getZipFilesWithUrl(needDownZipUrls);
                if (zipFiles == null || zipFiles.size() <= 0) {
                    if (callback != null) {
                        callback.updateError(new Exception("update Lua error,because down urls is failed"));
                    }
                    return;
                }
                for (File file : zipFiles) {
                    final String cacheUrlPath = file.getAbsolutePath();
                    VenvyAsyncTaskUtil.doAsyncTask("unzip_lua", new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Boolean>() {

                        @Override
                        public Boolean doAsyncTask(Void... voids) throws Exception {
                            long value = VenvyGzipUtil.unzipFile(cacheUrlPath, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, false);
                            File file = new File(cacheUrlPath);
                            file.delete();
                            return value > 0;

                        }
                    }, new VenvyAsyncTaskUtil.CommonAsyncCallback<Boolean>() {
                        @Override
                        public void onPostExecute(Boolean aBoolean) {
//                            if (!aBoolean) {
//                                VideoServiceQueryChainModel.ServiceQueryChainCallback callback = getQueryChainCallback();
//                                if (callback != null) {
//                                    callback.queryError(new Exception("unzip error"));
//                                }
//                                return;
//                            }
                            final JSONArray queryArray = new JSONArray();
                            CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                            if (callback != null) {
                                String fileName = Uri.parse(cacheUrlPath).getLastPathSegment();
                                if (TextUtils.isEmpty(fileName)) {
                                    callback.updateError(new Exception(""));
                                    return;
                                }
                                File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, fileName.replace(".zip", ".json"));
                                if (!file.exists() || !file.isFile()) {
                                    callback.updateError(new Exception(""));
                                    return;
                                }
                                String queryChainData = VenvyFileUtil.readFormFile(App.getContext(), file.getAbsolutePath());
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
                    });
                }
            }
            return;
        }
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(mPlatform);
        }
        final ArrayList<DownloadTask> arrayList = new ArrayList<>();
        for (String string : needDownZipUrls) {
            DownloadTask task = new DownloadTask(App.getContext(), string, VenvyFileUtil.getCachePath(App.getContext()) + LUA_ZIP + File.separator + Uri.parse(string).getLastPathSegment());
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
                if (failedTasks != null && failedTasks.size() > 0) {
                    CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update error,because downloadTask error"));
                    }
                    return;
                }
                List<File> zipFiles = getZipFiles(arrayList);
                if (zipFiles == null || zipFiles.size() <= 0) {
                    CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateError(new Exception("update Lua error,because down urls is failed"));
                    }
                    return;
                }
                for (File file : zipFiles) {
                    final String cacheUrlPath = file.getAbsolutePath();
                    VenvyAsyncTaskUtil.doAsyncTask("unzip_lua", new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Boolean>() {

                        @Override
                        public Boolean doAsyncTask(Void... voids) throws Exception {
                            long value = VenvyGzipUtil.unzipFile(cacheUrlPath, VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, false);
                            File file = new File(cacheUrlPath);
                            file.delete();
                            return value > 0;

                        }
                    }, new VenvyAsyncTaskUtil.CommonAsyncCallback<Boolean>() {
                        @Override
                        public void onPostExecute(Boolean aBoolean) {
//                            if (!aBoolean) {
//                                VideoServiceQueryChainModel.ServiceQueryChainCallback callback = getQueryChainCallback();
//                                if (callback != null) {
//                                    callback.queryError(new Exception("unzip error"));
//                                }
//                                return;
//                            }
                            final JSONArray queryArray = new JSONArray();
                            CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                            if (callback != null) {
                                String fileName = Uri.parse(cacheUrlPath).getLastPathSegment();
                                if (TextUtils.isEmpty(fileName)) {
                                    callback.updateError(new Exception(""));
                                    return;
                                }
                                File file = new File(VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH, fileName.replace(".zip", ".json"));
                                if (!file.exists() || !file.isFile()) {
                                    callback.updateError(new Exception(""));
                                    return;
                                }
                                String queryChainData = VenvyFileUtil.readFormFile(App.getContext(), file.getAbsolutePath());
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
                    });
                }
            }
        });
    }

    /***
     * 检测列表需要下载的Lua文件
     * @param luaUrls
     * @return
     */
    private List<String> checkUpdateZip(JSONArray luaUrls) {
        List<String> needDownUrls = new ArrayList<>();
        int len = luaUrls.length();
        for (int i = 0; i < len; i++) {
            JSONObject obj = luaUrls.optJSONObject(i);
            if (obj == null) {
                break;
            }
            String url = obj.optString("url");
            String md5 = obj.optString("md5");
            String cacheMd5 = getFileZipEncoderByMd5(Uri.parse(url).getLastPathSegment());
            if (!TextUtils.equals(md5, cacheMd5)) {
                needDownUrls.add(url);
            }
        }
        return needDownUrls;
    }

    private VideoPlusZipUpdate.CacheZipUpdateCallback getCacheLuaUpdateCallback() {
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

    private List<File> getZipFiles(ArrayList<DownloadTask> downloadTasks) {
        List<File> paths = new ArrayList<>();
        if (downloadTasks == null || downloadTasks.size() <= 0) {
            return paths;
        }
        for (DownloadTask downloadTask : downloadTasks) {
            File hasDownFile = new File(downloadTask.getDownloadCacheUrl());
            if (!hasDownFile.exists() || !TextUtils.equals("zip", VenvyFileUtil.getExtension(downloadTask.getDownloadCacheUrl()))) {
                continue;
            }
            paths.add(hasDownFile);
        }
        return paths;
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
