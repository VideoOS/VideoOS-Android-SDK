package cn.com.venvy.common.db;

import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.common.utils.VenvyLog;

public class DBConstants {
    private interface ITable {
        String produceCreateSQL();
    }

    public static final String DATABASE_NAME = "venvy_video.db";
    public static final int DATABASE_VERSION = 5;


    public static final String[] TABLE_NAMES = {"report_os_cache", "download_cache_db"};

    public static final int TABLE_OS_REPORT = 0;
    public static final int TABLE_DOWN_CACHE = 1;

    public static List<String> DB_CREATE_SQL;

    static {
        ReportDB report = new ReportDB(TABLE_NAMES[TABLE_OS_REPORT]);
        DownloadDB downloadDB = new DownloadDB(TABLE_NAMES[TABLE_DOWN_CACHE]);
        DB_CREATE_SQL = new ArrayList<>(TABLE_NAMES.length);
        DB_CREATE_SQL.add(report.produceCreateSQL());
        DB_CREATE_SQL.add(downloadDB.produceCreateSQL());
    }

    public static class ReportDB implements ITable {
        public static final String[] COLUMNS = {"report_id", "leavel", "create_time", "tag", "message"};
        public static final int REPORT_ID = 0;// report_Id
        public static final int REPORT_LEVEL = 1;
        public static final int REPORT_CREATE_TIME = 2;
        public static final int REPORT_TAG = 3;
        public static final int REPORT_MESSAGE = 4;
        private String mTableName;

        public ReportDB(String tableName) {
            mTableName = tableName;
        }

        @Override
        public String produceCreateSQL() {
            final String sql = "CREATE TABLE IF NOT EXISTS "
                    + mTableName + "(" + COLUMNS[REPORT_ID]
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMNS[REPORT_LEVEL]
                    + " INTEGER NOT NULL,"
                    + COLUMNS[REPORT_CREATE_TIME]
                    + " TEXT NOT NULL,"
                    + COLUMNS[REPORT_TAG]
                    + " TEXT NOT NULL,"
                    + COLUMNS[REPORT_MESSAGE]
                    + " TEXT NOT NULL)";
            VenvyLog.d(getClass().getSimpleName(), " createSQL = " + sql);
            return sql;
        }
    }

    public static class DownloadDB implements ITable {
        public static final String[] COLUMNS = {"download_id", "url", "total_size", "download_size", "download_status", "file_path"};
        public static final int DOWNLOAD_ID = 0;// report_Id
        public static final int URL = 1;
        public static final int TOTAL_SIZE = 2;
        public static final int DOWNLOAD_SIZE = 3;
        public static final int DOWNLOAD_STATUS = 4;
        public static final int FILE_PATH = 5;
        private String mTableName;

        public DownloadDB(String tableName) {
            mTableName = tableName;
        }

        @Override
        public String produceCreateSQL() {
            final String sql = "CREATE TABLE IF NOT EXISTS "
                    + mTableName + "(" + COLUMNS[DOWNLOAD_ID]
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMNS[URL]
                    + " INTEGER NOT NULL,"
                    + COLUMNS[TOTAL_SIZE]
                    + " TEXT NOT NULL,"
                    + COLUMNS[DOWNLOAD_SIZE]
                    + " TEXT NOT NULL,"
                    + COLUMNS[DOWNLOAD_STATUS]
                    + " TEXT NOT NULL,"
                    + COLUMNS[FILE_PATH]
                    + " TEXT NOT NULL)";
            VenvyLog.d(getClass().getSimpleName(), " createSQL = " + sql);
            return sql;
        }
    }

}
