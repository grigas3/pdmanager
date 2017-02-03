package com.pdmanager.core.logging;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.pdmanager.core.persistence.DBHandler;

/**
 * Created by george on 20/11/2015.
 */


public class LogAdapter {


    private static final String TAG = "LogDbAdapter";
    private final Context mCtx;
    private DBHandler mDbHelper;
    private SQLiteDatabase mDb;
    private boolean dbOpen = false;


    public LogAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public LogAdapter open() throws SQLException {


        mDbHelper = DBHandler.getInstance(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        dbOpen = true;
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
            dbOpen = false;
        }
    }

    public void clearAlerts() {

        mDbHelper = DBHandler.getInstance(mCtx);
        mDbHelper.clearTable(DBHandler.TABLE_ALERTS);
        mDbHelper.close();
        //mCtx.getContentResolver().notifyChange(DBHandler.URI_TABLE_USERS, null);

    }

    public void clearLog() {

        mDbHelper = DBHandler.getInstance(mCtx);
        mDbHelper.clearTable(DBHandler.TABLE_LOGS);
        mDbHelper.close();


        mCtx.getContentResolver().notifyChange(DBHandler.URI_TABLE_USERS, null);

    }

    public void clearCommQueue() {


        mDbHelper = DBHandler.getInstance(mCtx);
        mDbHelper.clearTable(DBHandler.TABLE_JREQUESTS);
        mDbHelper.close();
       /*
        mCtx.getContentResolver().notifyChange(DBHandler.URI_TABLE_USERS, null);
        */

    }
    public Cursor fetchLogs() {


        if (!dbOpen)
            open();

        Cursor mCursor = mDb.query(DBHandler.TABLE_LOGS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_TIMESTAMP,
                        DBHandler.COLUMN_LOGTYPE, DBHandler.COLUMN_LOGMESSAGE},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        close();
        return mCursor;
    }


}


