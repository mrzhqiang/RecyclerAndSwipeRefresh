package cn.qiang.zhang.recyclerandswiperefresh.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static cn.qiang.zhang.recyclerandswiperefresh.db.HellGateDBHelper.AccountTable.COL_EX_UID;
import static cn.qiang.zhang.recyclerandswiperefresh.db.HellGateDBHelper.AccountTable.COL_PASSWORD;
import static cn.qiang.zhang.recyclerandswiperefresh.db.HellGateDBHelper.AccountTable.COL_USERNAME;

/**
 * <p>
 * Created by mrZQ on 2017/3/22.
 */
public class HellGateDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "hell_gate.db";
    public static final int DB_VERSION = 1;

    public HellGateDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + AccountTable.TABLE_NAME + " (" +
                AccountTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_USERNAME + " TEXT NOT NULL," +
                COL_PASSWORD + " TEXT NOT NULL," +
                COL_EX_UID + " INTEGER);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class AccountTable implements BaseColumns {
        public static final String TABLE_NAME = "hg_account";

        public static final String COL_USERNAME = "username";
        public static final String COL_PASSWORD = "password";
        public static final String COL_EX_UID = "ex_uid";

        public String username;
        public String password;
        public long exUid;

        public static ContentValues toContentValues(String username, String password, long exUid) {
            ContentValues out = new ContentValues();
            out.put(COL_USERNAME, username);
            out.put(COL_PASSWORD, password);
            out.put(COL_EX_UID, exUid);
            return out;
        }
    }

}
