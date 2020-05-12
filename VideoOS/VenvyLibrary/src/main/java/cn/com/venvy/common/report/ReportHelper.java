package cn.com.venvy.common.report;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.venvy.App;
import cn.com.venvy.Config;
import cn.com.venvy.Platform;
import cn.com.venvy.common.db.DBConstants;
import cn.com.venvy.common.db.VenvyDBController;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.exception.DBException;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.RequestFactory;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.http.base.RequestConnectStatus;
import cn.com.venvy.common.priority.Priority;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.common.utils.VenvyAsyncTaskUtil;
import cn.com.venvy.common.utils.VenvyGzipUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

import static cn.com.venvy.common.report.Report.ReportLevel.e;

/**
 * 手动打点预报
 * Created by yanjiangbo on 2017/5/4.
 */

class ReportHelper {

    private static final String REPORT_AES_KEY = "8lgK5fr5yatOfHio";
    private static final String REPORT_AES_IV = "lx7eZhVoBEnKXELF";
    private static final String REPORT_SERVER_KEY = "info";
    private static final String KEY_ASYNC_TASK = "Report_report";
    private static final String REPORT_URL = "https://os-saas.videojj.com/os-report-log/api/log";

    //最大缓存条数
    private static final int MAX_CACHE_NUM = 5;
    //数据库最大保存条数
    private static final int MAX_DB_CACHE_NUM = 300;
    //轮询时间间隔
    private static final int POLLING_TIME = 1000 * 60 * 5;

    protected Context mContext;

    private String mCacheTableName;

    private BaseRequestConnect mConnect;

    private boolean enable = Config.REPORT_ABLE;


    ReportHelper(Platform platform) {
        mContext = App.getContext();
        mCacheTableName = DBConstants.TABLE_NAMES[DBConstants.TABLE_OS_REPORT];
        mConnect = RequestFactory.initConnect(platform);
        startPolling();
    }

    boolean isEnable() {
        return !DebugStatus.isPreView() && enable;
    }

    public void report(@NonNull final ReportInfo reportInfo) {
        if (!isEnable()) {
            VenvyLog.w("Report has closed");
            return;
        }
        reportInfo.createTime = String.valueOf(System.currentTimeMillis());
        cacheReportInfo(reportInfo);
        long count = getTotalCacheNum();
        checkReportStatus(count);

        if (reportInfo.level == e || count > MAX_CACHE_NUM) {
            startReport();
        }
        if (count > MAX_DB_CACHE_NUM) {
            setReportEnable(false);
        }
    }

    private void reportCache() {
        startReport();
    }

    void startReport() {

        VenvyAsyncTaskUtil.doAsyncTask(KEY_ASYNC_TASK, new VenvyAsyncTaskUtil.IDoAsyncTask<Void, Void>() {
            @Override
            public Void doAsyncTask(Void... strings) throws Exception {
                // 避免重复发送，正在发送的时候所有的请求只入到cache而等待上次投递结果
                if (mConnect == null) {
                    VenvyLog.e("connect is null,do you call init method?");
                    return null;
                }
                if (mConnect.getConnectStatus() == RequestConnectStatus.ACTIVE) {
                    return null;
                }
                final List<ReportInfo> list = getReportInfoListAndDeleteDB();
                try {
                    String reportString = reportInfoListToString(list);
                    String gzipString = VenvyGzipUtil.compress(reportString);
                    if (gzipString == null) {
                        return null;
                    }
                    HashMap<String, String> params = new HashMap<>();
                    String signParams = VenvyAesUtil.encrypt(REPORT_AES_KEY, REPORT_AES_IV, gzipString);
                    params.put(REPORT_SERVER_KEY, signParams);
                    Request request = HttpRequest.post(REPORT_URL, params);
                    request.setPriority(Priority.LOW);
                    mConnect.connect(request, new IRequestHandler() {
                        @Override
                        public void requestFinish(Request request, IResponse response) {
                            if (!response.isSuccess()) {
                                requestError(request, new Exception("http error"));
                            } else {
                                try {
                                    String body = response.getResult();
                                    if (TextUtils.isEmpty(body)) {
                                        return;
                                    }
                                    JSONObject jsonObject = new JSONObject(body);
                                    if (jsonObject.has("data")) {
                                        int reportNum = jsonObject.optInt("data");
                                        Report.ReportLevel.buildLevelAble(reportNum);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void requestError(Request request, Exception e) {
                            cacheReportList(list);
                        }

                        @Override
                        public void startRequest(Request request) {

                        }

                        @Override
                        public void requestProgress(Request request, int progress) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    VenvyLog.e("report error : ", e);
                }
                return null;
            }
        }, null);
    }

    void cacheReportInfo(ReportInfo info) {
        try {
            getDbController().insert(mCacheTableName, DBConstants.ReportDB.COLUMNS, getReportDbContent(info), 1);
        } catch (DBException e) {
            VenvyLog.e("DBException : ", e);
        }
    }

    private String[] getReportDbContent(ReportInfo info) {
        return new String[]{String.valueOf(info.id), String.valueOf(info.level.getValue()), info.createTime, info.tag, info.message};
    }

    private void cacheReportList(List<ReportInfo> list) {
        try {
            if (list == null || list.size() == 0) {
                return;
            }
            ArrayList<String[]> dbContents = new ArrayList<>();
            for (ReportInfo info : list) {
                dbContents.add(getReportDbContent(info));
            }
            getDbController().insert(mCacheTableName, DBConstants.ReportDB.COLUMNS, dbContents, 1);
        } catch (DBException e) {
            VenvyLog.e("DBException : ", e);
        }
    }

    private long getTotalCacheNum() {
        Cursor cursor = null;
        try {
            cursor = getDbController().queryCount(mCacheTableName);
            if (cursor != null && !cursor.isClosed() && isEnable()) {
                cursor.moveToFirst();
                long count = cursor.getLong(0);
                cursor.close();
                return count;
            }
        } catch (Exception e) {
            VenvyLog.i("=ReportHelper=" + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0;
    }


    protected List<ReportInfo> getReportInfoListAndDeleteDB() {
        Cursor cursor = null;
        List<ReportInfo> list = new ArrayList<>();
        try {
            cursor = getDbController().queryAll(mCacheTableName);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ReportInfo info = new ReportInfo();
                    info.id = cursor.getInt(DBConstants.ReportDB.REPORT_ID);
                    info.level = Report.ReportLevel.getLevel(cursor.getInt(DBConstants.ReportDB.REPORT_LEVEL));
                    info.message = cursor.getString(DBConstants.ReportDB.REPORT_MESSAGE);
                    info.createTime = cursor.getString(DBConstants.ReportDB.REPORT_CREATE_TIME);
                    info.tag = cursor.getString(DBConstants.ReportDB.REPORT_TAG);
                    if (info.level.isEnable()) {
                        list.add(info);
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            deleteCache();
            return list;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void checkReportStatus(long length) {
        if (length >= MAX_DB_CACHE_NUM) {
            setReportEnable(false);
        }
    }

    protected void setReportEnable(boolean able) {
        this.enable = able;
    }


    protected void deleteCache() {
        try {
            getDbController().deleteAll(mCacheTableName);
        } catch (DBException e) {
            e.printStackTrace();
            VenvyLog.e("DB delete error : ", e);
        }
    }


    protected String reportInfoListToString(List<ReportInfo> infoList) {
        if (infoList == null || infoList.size() == 0) {
            return "";
        }
        JSONArray jsonArray = new JSONArray();
        try {
            for (ReportInfo reportInfo : infoList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("level", reportInfo.level.getName());
                jsonObject.put("tag", reportInfo.tag);
                jsonObject.put("message", reportInfo.message);
                jsonObject.put("create_time", reportInfo.createTime);
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            VenvyLog.e("JSON error : ", e);
        }
        return jsonArray.toString();
    }

    /**
     * 每隔一段时间主动上抛一次数据
     */
    private void startPolling() {
        final WeakReference<ReportHelper> reference = new WeakReference<>(this);
        if (!isEnable()) {
            return;
        }
        reportCache();
        VenvyUIUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                ReportHelper reportHelper = reference.get();
                if (reportHelper != null) {
                    reportHelper.startPolling();
                    VenvyLog.i("start poll Report");
                }
            }
        }, POLLING_TIME);
    }

    VenvyDBController getDbController() {
        return VenvyDBController.getInstance(mContext);
    }

    public void onDestroy() {
        VenvyAsyncTaskUtil.cancel(KEY_ASYNC_TASK);
        if (getDbController() != null) {
            getDbController().onDestroy();
        }
    }
}
