package cn.com.venvy;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.common.bean.LuaFileInfo;
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
    public static final String LUA_CACHE_PATH = "/lua/os/cache/demo";
    private DownloadTaskRunner mDownloadTaskRunner;
    private CacheLuaUpdateCallback mUpdateCallback;
    private Platform mPlatform;
    private int preloadType;
    private String taskTag = "";

    public PreloadLuaUpdate(int preloadType, Platform platform, CacheLuaUpdateCallback callback) {
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
        VenvyAsyncTaskUtil.cancel(getTaskTag());
        mUpdateCallback = null;
    }

    /***
     * 开启下载Lua文件
     * @param listOfLuaInfo Lua文件列表
     */
    public void startDownloadLuaFile(List<LuaFileInfo> listOfLuaInfo) {
        if (listOfLuaInfo == null || listOfLuaInfo.size() <= 0) {
            CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
            if (callback != null) {
                callback.updateError(new Exception("update Lua error,because down urls is null"));
            }
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (LuaFileInfo info : listOfLuaInfo) {
            builder.append(info.getMiniAppId());
        }
        setTaskTag(builder.toString());
        checkUpdateLuaInfos(listOfLuaInfo,getTaskTag());
    }

    /***
     * 检测列表需要下载的Lua文件
     * @param taskTag
     */
    private void checkUpdateLuaInfos(final List<LuaFileInfo> listOfLuaInfo, String taskTag) {
        VenvyAsyncTaskUtil.doAsyncTask(taskTag, new VenvyAsyncTaskUtil.IDoAsyncTask<List<LuaFileInfo>,
                Map<String, Set<LuaFileInfo.LuaListBean>>>() {
            @Override
            public Map<String, Set<LuaFileInfo.LuaListBean>> doAsyncTask(List<LuaFileInfo>... listOfInfo) throws Exception {
                if (listOfInfo == null || listOfInfo.length == 0) {
                    return null;
                }
                List<LuaFileInfo> listOfLuaFileInfo = listOfInfo[0];
                Map<String, Set<LuaFileInfo.LuaListBean>> needDownMap = new HashMap<>();
                for (LuaFileInfo fileInfo : listOfLuaFileInfo) {
                    List<LuaFileInfo.LuaListBean> listOfLuaBean = fileInfo.getLuaList();
                    Set<LuaFileInfo.LuaListBean> listOfNeedDownLua = new LinkedHashSet();
                    for (LuaFileInfo.LuaListBean luaBean : listOfLuaBean) {
                        String fileUrl = luaBean.getLuaFileUrl();
                        String fileMd5 = luaBean.getLuaFileMd5();
                        if (TextUtils.isEmpty(fileUrl)) {
                            continue;
                        }
                        String cacheMd5 = getFileLuaEncoderByMd5(Uri.parse(fileUrl).getLastPathSegment(), fileInfo.getMiniAppId());
                        if (!TextUtils.equals(fileMd5, cacheMd5)) {
                            listOfNeedDownLua.add(luaBean);
                        }
                    }
                    needDownMap.put(fileInfo.getMiniAppId(), listOfNeedDownLua);
                }
                return needDownMap;
            }
        }, new VenvyAsyncTaskUtil.IAsyncCallback<Map<String, Set<LuaFileInfo.LuaListBean>>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(Map<String, Set<LuaFileInfo.LuaListBean>> mapOfLuaBean) {
                if (mapOfLuaBean == null) {
                    return;
                }
                Set<LuaFileInfo.LuaListBean> needDownLuaCount = new LinkedHashSet<>();
                for (Set<LuaFileInfo.LuaListBean> value : mapOfLuaBean.values()) {
                    needDownLuaCount.addAll(value);
                }
                if (needDownLuaCount.size() == 0) {
                    //本地存在 无需下载直接返回成功回调
                    CacheLuaUpdateCallback callback = getCacheLuaUpdateCallback();
                    if (callback != null) {
                        callback.updateComplete(false);
                    }
                    return;
                }
                startDownloadLuaFile(mapOfLuaBean);
            }

            @Override
            public void onCancelled() {
                VenvyLog.e("cancel");
            }

            @Override
            public void onException(Exception ie) {
            }
        }, listOfLuaInfo);

    }

    private void startDownloadLuaFile(Map<String, Set<LuaFileInfo.LuaListBean>> mapInfo) {
        if (mDownloadTaskRunner == null) {
            mDownloadTaskRunner = new DownloadTaskRunner(mPlatform);
        }
        Iterator<Map.Entry<String, Set<LuaFileInfo.LuaListBean>>> entries = mapInfo.entrySet().iterator();

        ArrayList<DownloadTask> arrayList = new ArrayList<>();

        while(entries.hasNext()){
            Map.Entry<String, Set<LuaFileInfo.LuaListBean>> entry = entries.next();
            String key = entry.getKey();
            Set<LuaFileInfo.LuaListBean> value = entry.getValue();
            for (LuaFileInfo.LuaListBean luaBean:value){
                String downUrl=luaBean.getLuaFileUrl();
                String downPath = TextUtils.isEmpty(key) ? VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + Uri.parse(downUrl).getLastPathSegment() : VenvyFileUtil.getCachePath(App.getContext()) + LUA_CACHE_PATH + File.separator + key + File.separator + Uri.parse(downUrl).getLastPathSegment();
                DownloadTask task = new DownloadTask(App.getContext(), downUrl, downPath, true);
                arrayList.add(task);
            }
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

    private CacheLuaUpdateCallback getCacheLuaUpdateCallback() {
        return mUpdateCallback;
    }

    private String getTaskTag() {
        return taskTag;
    }

    private void setTaskTag(String taskTag) {
        this.taskTag = taskTag;
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
