package com.pdmanager.alerting;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.pdmanager.persistence.DBHandler;

/**
 * Created by george on 20/11/2015.
 */


public class AlertAdapter {


    private static final String TAG = "AlertDbAdapter";
    private final Context mCtx;
    private DBHandler mDbHelper;
    private SQLiteDatabase mDb;
    private boolean dbOpen = false;


    public AlertAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public AlertAdapter open() throws SQLException {


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


    public void clearAlert() {

        mDbHelper =  DBHandler.getInstance(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        mDb.delete(DBHandler.TABLE_ALERTS, null, null);
        mDbHelper.close();
        mCtx.getContentResolver().notifyChange(DBHandler.URI_TABLE_USERS, null);

    }


    public Cursor fetchAlerts() {


        if (!dbOpen)
            open();

        Cursor mCursor = mDb.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_TIMESTAMP,
                        DBHandler.COLUMN_ALERT},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        close();
        return mCursor;
    }


}


