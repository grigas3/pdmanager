package com.pdmanager.medication;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.pdmanager.persistence.DBHandler;

/**
 * Created by george on 20/11/2015.
 */


public class MedAdapter {


    private static final String TAG = "MedDbAdapter";
    private final Context mCtx;
    private DBHandler mDbHelper;
    private SQLiteDatabase mDb;
    private boolean dbOpen = false;


    public MedAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public MedAdapter open() throws SQLException {


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


    public Cursor fetchMeds() {


        if (!dbOpen)
            open();

        Cursor mCursor = mDb.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_TIME,
                        DBHandler.COLUMN_DRUG, DBHandler.COLUMN_DOSE},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        close();
        return mCursor;
    }


}


