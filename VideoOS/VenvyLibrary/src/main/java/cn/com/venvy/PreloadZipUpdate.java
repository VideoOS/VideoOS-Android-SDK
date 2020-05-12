package cn.com.venvy;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.venvy.common.download.DownloadTask;
import cn.com.venvy.common.download.DownloadTaskRunner;
import cn.com.venvy.common.download.TaskListener;
import cn.com.venvy.common.report.Report;
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
    private static final String DOWN_ZIP_PATH = "/lua/os/down/zip";
    private static final String UN_ZIP_PATH = "/lua/os/cache/unzip";
    private final String PARSE_LOCAL_ZIP = "parse_local_zip";
    private final String PARSE_UNZIP = "unzip";
    private static String NEED_DOWN_URL_KEY = "need_urls";
    private static String ALL_DOWN_URL_KEY = "all_urls";
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
            VenvyLog.i(TAG, "down zip json failure，because down zip urls is null");
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
                Map<String, List<String>>>() {
            @Override
            public Map<String, List<String>> doAsyncTask(JSONArray... zips) throws Exception {
                if (zips == null || zips.length == 0) {
                    return null;
                }
                Map<String, List<String>> mapOfUrls = new HashMap<>();
                List<String> listOfNeedUrls = new ArrayList<>();
                List<String> listOfAllUrls = new ArrayList<>();
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
                        listOfAllUrls.add(url);
                        String cacheMd5 = getFileZipEncoderByMd5(Uri.parse(url).getLastPathSegment());
                        if (!TextUtils.equals(md5, cacheMd5)) {
                            listOfNeedUrls.add(url);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    VenvyLog.i(TAG, "VideoPlusZipUpdate ——> checkDownZipUrls error：" + e.getMessage());
                }
                mapOfUrls.put(NEED_DOWN_URL_KEY, listOfNeedUrls);
                mapOfUrls.put(ALL_DOWN_URL_KEY, listOfAllUrls);
                return mapOfUrls;
            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<Map<String, List<String>>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(Map<String, List<String>> urls) {
                CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                if (urls == null) {
                    if (callback != null) {
                        callback.updateError(new Exception("update zip error,发生未知错误"));
                    }
                    return;
                }
                List<String> listOfNeedUrls = urls.get(NEED_DOWN_URL_KEY);
                List<String> listOfAllUrls = urls.get(ALL_DOWN_URL_KEY);
                if (listOfNeedUrls.size() == 0) {
                    List<File> zipFiles = getZipFilesWithUrl(listOfAllUrls);
                    if (zipFiles == null || zipFiles.size() <= 0) {
                        startDownloadZipFile(listOfAllUrls, listOfAllUrls);
                    } else {
                        unZipAndReadData(zipFiles);
                    }
                    return;
                }
                startDownloadZipFile(listOfAllUrls, listOfNeedUrls);
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
        VenvyAsyncTaskUtil.doAsyncTask(PARSE_UNZIP, new VenvyAsyncTaskUtil.IDoAsyncTask<File, JSONArray>() {

            @Override
            public JSONArray doAsyncTask(File... files) throws Exception {
                final JSONArray queryArray = new JSONArray();
                final String unZipPath = getUnZipAbsolutePath();
                VenvyFileUtil.createDir(unZipPath);
                for (File file : files) {
                    final String cacheUrlPath = file.getAbsolutePath();
                    VenvyGzipUtil.unzipFile(cacheUrlPath, unZipPath, false);
                }
                List<String> listOfUnZipJson = VenvyFileUtil.getFileName(unZipPath);
                for (String cacheUrlPath : listOfUnZipJson) {
                    String fileName = Uri.parse(cacheUrlPath).getLastPathSegment();
                    if (TextUtils.isEmpty(fileName)) {
                        continue;
                    }
                    File file = new File(unZipPath, fileName);
                    if (!file.exists() || !file.isFile()) {
                        continue;
                    }
                    String queryChainData = VenvyFileUtil.readFormFile(App.getContext(), unZipPath + File.separator + fileName);
                    if (TextUtils.isEmpty(queryChainData)) {
                        continue;
                    }
                    try {
                        queryArray.put(new JSONObject(queryChainData));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                VenvyFileUtil.delFolder(unZipPath);
                return queryArray;

            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<JSONArray>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(JSONArray queryData) {
                CacheZipUpdateCallback callback = getCacheLuaUpdateCallback();
                if (callback == null) {
                    VenvyLog.i(TAG, "unZip call is Null ");
                    return;
                }
                if (queryData == null) {
                    callback.updateError(new NullPointerException("read unZip data is error,because read json is null"));
                } else {
                    callback.updateComplete(queryData);
                }
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
            DownloadTask task = new DownloadTask(App.getContext(), string, getZipAbsolutePath() + File.separator + Uri.parse(string).getLastPathSegment(), true);
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
                        Report.report(Report.ReportLevel.w, PreloadZipUpdate.class.getName(), buildReportString(failedTasks));
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
            String path = getZipAbsolutePath() + File.separator + Uri.parse(url).getLastPathSegment();
            File hasDownFile = new File(path);
            if (!hasDownFile.exists() || !TextUtils.equals("zip", VenvyFileUtil.getExtension(path).toLowerCase())) {
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
        return VenvyMD5Util.EncoderByMd5(new File(getZipAbsolutePath() + File.separator + fileName));
    }

    /***
     * 获取下载Zip的绝对路径
     * @return
     */
    private String getZipAbsolutePath() {
        return VenvyFileUtil.getCachePath(App.getContext()) + DOWN_ZIP_PATH;
    }

    /***
     * 获取解压Zip的绝对路径
     * @return
     */
    private String getUnZipAbsolutePath() {
        return VenvyFileUtil.getCachePath(App.getContext()) + UN_ZIP_PATH;
    }
    private static String buildReportString(List<DownloadTask> failedTasks) {

        StringBuilder builder = new StringBuilder();
        builder.append("[download zip failed],");
        builder.append("\\n");
        if (failedTasks != null) {
            for (DownloadTask downloadTask : failedTasks) {
                builder.append("url = ").append(downloadTask.getDownloadUrl());
                builder.append("\\n");
            }
        }
        return builder.toString();
    }
}
