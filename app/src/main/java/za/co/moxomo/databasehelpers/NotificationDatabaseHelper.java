package za.co.moxomo.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Paballo Ditshego on 7/31/15.
 */
public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTIFICATIONS = "notification_items";
    public static final String COLUMN_ID = "_id";
    public static final String STATUS = "status";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String ACTION_STRING = "action_string";
    public static final String IMAGE_URL = "image_url";
    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTIFICATIONS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + TITLE + " text not null," + BODY + " text not null,"
            + TYPE + " text not null," + ACTION_STRING + " text not null," + IMAGE_URL + " text not null," + STATUS
            + " text not null," + "time INTEGER NOT NULL DEFAULT (strftime('%s','now'))" + ")";
    private SQLiteDatabase db;

    public NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public void open() throws SQLException {
        db = this.getWritableDatabase();

    }

    public synchronized void close() {
        db.close();
    }

    public Cursor getNotification(long rowId) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(true, TABLE_NOTIFICATIONS, new String[]{
                        COLUMN_ID, TYPE, ACTION_STRING}, COLUMN_ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void updateNotification(long rowId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(STATUS, "read");
        db.update(TABLE_NOTIFICATIONS, args, COLUMN_ID + "=" + rowId, null);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NotificationDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

}