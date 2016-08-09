package com.pdmanager.core.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by george on 17/11/2015.
 */
public class DBHandler extends SQLiteOpenHelper {


    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_LOGS = "logs";
    public static final String TABLE_JREQUESTS = "jrequests";
    public static final String TABLE_ALERTS = "alerts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SETTING = "setting";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_JSON = "json";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_LOGTYPE = "type";
    public static final String COLUMN_LOGMESSAGE = "message";
    public static final String COLUMN_ALERT = "alert";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final Uri URI_TABLE_USERS = Uri.parse("sqlite://com.medlab.pdmanager/table/" + TABLE_LOGS);
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pdManagerDB.db";

    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        InitLogTable(sqLiteDatabase);

        InitCommunicatonTable(sqLiteDatabase);

    }

    private void InitLogTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_LOGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP
                + " INTEGER," + COLUMN_LOGTYPE
                + " TEXT," + COLUMN_LOGMESSAGE + " TEXT)";
        db.execSQL(CREATE_TABLE);


    }


    private void InitAlertTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_ALERTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP
                + " INTEGER," + COLUMN_ALERT + ")";
        db.execSQL(CREATE_TABLE);


    }

    private void InitSettingsTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_SETTINGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_SETTING
                + " TEXT," + COLUMN_VALUE + " TEXT)";
        db.execSQL(CREATE_TABLE);


    }


    private void InitCommunicatonTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_JREQUESTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_URI
                + " TEXT," + COLUMN_JSON + " TEXT)";
        db.execSQL(CREATE_TABLE);


    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
