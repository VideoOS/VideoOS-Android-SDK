package cn.com.venvy.common.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import cn.com.venvy.common.utils.VenvyLog;

public class DBHelper extends SQLiteOpenHelper {

    private List<String> databaseCreate;

    private static DBHelper dbHelper;

    private DBHelper(Context context, String name, int version, List<String> databaseCreate) {
        this(context, name, version, databaseCreate, null);
    }

    private DBHelper(Context context, String name, int version, List<String> databaseCreate, CursorFactory factory) {
        super(context, name, factory, version);
        this.databaseCreate = databaseCreate;
    }

    public static synchronized DBHelper getInstance(Context context, String name, int version, List<String> databaseCreate, CursorFactory factory) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context, name, version, databaseCreate, factory);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (databaseCreate != null && db != null) {
            execSql(db, databaseCreate);
        } else {
            throw new IllegalArgumentException(getClass().getSimpleName()
                    + "\tonCreate()");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        VenvyLog.i("oldVersion=" + oldVersion + ",newVersion" + newVersion);
        if (newVersion <= oldVersion || db == null) {
            return;
        }
        execSql(db, databaseCreate);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 针对降级处理 ，如果版本小于上一个版本 ，先删除原先的表在创建新的表
        db.beginTransaction();
        try {
            for (String tableName : DBConstants.TABLE_NAMES) {
                db.execSQL("DROP TABLE " + tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        onCreate(db);
    }

    private void execSql(SQLiteDatabase db, List<String> sqls) {
        db.beginTransaction();
        try {
            for (String sql : sqls) {
                db.execSQL(sql);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

}

