package cn.com.venvy.common.download;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.com.venvy.common.db.DBConstants;
import cn.com.venvy.common.db.VenvyDBController;
import cn.com.venvy.common.exception.DBException;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2017/6/21.
 */

public class DownloadDbHelper {

    private Context mContext;

    public DownloadDbHelper(Context context) {
        mContext = context;
    }


    public DownloadInfo queryDownloadInfo(String url) {
        DownloadInfo info = null;
        VenvyDBController controller = getDBController();
        Cursor cursor = controller.query(DBConstants.TABLE_NAMES[DBConstants.TABLE_DOWN_CACHE], DBConstants.DownloadDB.COLUMNS[DBConstants.DownloadDB.URL], url);
        try {
            if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                info = new DownloadInfo();
                info.Id = cursor.getInt(DBConstants.DownloadDB.DOWNLOAD_ID);
                String downloadedSize = cursor.getString(DBConstants.DownloadDB.DOWNLOAD_SIZE);
                info.downloadSize = TextUtils.isEmpty(downloadedSize) ? 0 : Long.valueOf(downloadedSize);
                String totalSize = cursor.getString(DBConstants.DownloadDB.TOTAL_SIZE);
                info.totalSize = TextUtils.isEmpty(totalSize) ? 0 : Long.valueOf(totalSize);
                info.filePath = cursor.getString(DBConstants.DownloadDB.FILE_PATH);
                info.url = cursor.getString(DBConstants.DownloadDB.URL);
                String downloadStatus = cursor.getString(DBConstants.DownloadDB.DOWNLOAD_STATUS);
                info.status = !TextUtils.isEmpty(downloadStatus) ? DownloadStatus.getStatusByType(downloadStatus) : DownloadStatus.NONE;
                cursor.close();
            }
        } catch (Exception e) {
            VenvyLog.i("queryDownInfo:" + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return info;
    }

    public void updateDownloadInfoForDB(DownloadInfo info) {
        try {
            getDBController().update(DBConstants.TABLE_NAMES[DBConstants.TABLE_DOWN_CACHE], DBConstants.DownloadDB.COLUMNS, downloadInfoForArray(info), DBConstants.DownloadDB.COLUMNS[DBConstants.DownloadDB.URL], info.url, 1);
        } catch (DBException e) {
            VenvyLog.e(getClass().getName(), e);
        }
    }

    public void insertDownloadInfoForDB(DownloadInfo info) {
        try {
            getDBController().insert(DBConstants.TABLE_NAMES[DBConstants.TABLE_DOWN_CACHE], DBConstants.DownloadDB.COLUMNS, downloadInfoForArray(info), 1);
        } catch (DBException e) {
            VenvyLog.e(getClass().getName(), e);
        }
    }

    public void deleteDownloadingInfo() {
        try {
            getDBController().delete(DBConstants.TABLE_NAMES[DBConstants.TABLE_DOWN_CACHE], DBConstants.DownloadDB.COLUMNS[DBConstants.DownloadDB.DOWNLOAD_STATUS], DownloadStatus.DOWNLOADING.getType());
        } catch (DBException e) {
            VenvyLog.e(getClass().getName(), e);
        }
    }

    public void delete(DownloadInfo info) {
        try {
            getDBController().delete(DBConstants.TABLE_NAMES[DBConstants.TABLE_DOWN_CACHE], DBConstants.DownloadDB.COLUMNS[DBConstants.DownloadDB.DOWNLOAD_ID], String.valueOf(info.Id));
        } catch (DBException e) {
            VenvyLog.e(getClass().getName(), e);
        }
    }

    private String[] downloadInfoForArray(DownloadInfo info) {
        return new String[]{String.valueOf(info.Id), info.url, String.valueOf(info.totalSize), String.valueOf(info.downloadSize), info.status.getType(), info.filePath};
    }

    private VenvyDBController getDBController() {
        return VenvyDBController.getInstance(mContext);
    }

    public static class DownloadInfo {
        public String url;
        public int Id;
        public long totalSize;
        public long downloadSize;
        public DownloadStatus status;
        String filePath;
    }

    public enum DownloadStatus {
        DOWNLOAD_FAILED("1"),
        DOWNLOAD_SUCCESS("2"),
        DOWNLOADING("3"),
        NONE("0");

        String mType = "0";

        DownloadStatus(String type) {
            mType = type;
        }

        public static DownloadStatus getStatusByType(@NonNull String type) {
            DownloadStatus status;
            switch (type) {
                case "1":
                    status = DOWNLOAD_FAILED;
                    break;

                case "2":
                    status = DOWNLOAD_SUCCESS;
                    break;

                case "3":
                    status = DOWNLOADING;
                    break;

                default:
                    status = NONE;
                    break;
            }
            return status;
        }

        public String getType() {
            return mType;
        }

    }
}
