package com.pdmanager.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Created by george on 17/11/2015.
 */
public class DBHandler extends SQLiteOpenHelper {


    /* private static DBHandler sInstance;



     public static synchronized DBHandler getInstance(Context context) {

         // Use the application context, which will ensure that you
         // don't accidentally leak an Activity's context.
         // See this article for more information: http://bit.ly/6LRzfx
         if (sInstance == null) {
             sInstance = new DBHandler(context.getApplicationContext());
         }
         return sInstance;
     }
     */
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_LOGS = "logs";
    public static final String TABLE_JREQUESTS = "jrequests";
    public static final String TABLE_ALERTS = "alerts";
    public static final String TABLE_SYNC = "synctables";
    public static final String TABLE_MEDICATIONS = "meds";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SETTING = "setting";
    public static final String COLUMN_TABLE = "synctable";
    public static final String COLUMN_URI = "uri";
    public static final String COLUMN_JSON = "json";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_LOGTYPE = "type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LOGMESSAGE = "message";
    public static final String COLUMN_ALERT = "alert";
    public static final String COLUMN_ALERTCREATED = "created";
    public static final String COLUMN_ALERTACTIVE = "active";
    public static final String COLUMN_ALERTSOURCE = "source";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_EXPIRATION = "expiration";
    public static final String COLUMN_LASTALERTTIMESTAMP = "lastalerttimestap";
    public static final String COLUMN_DRUG = "drug";
    public static final String COLUMN_DOSE = "dose";
    public static final String COLUMN_INTAKEDESC = "description";
    public static final String COLUMN_ALERTTYPE = "type";
    public static final String COLUMN_ALERTMESSAGE = "message";
    public static final Uri URI_TABLE_USERS = Uri.parse("sqlite://com.medlab.pdmanager/table/" + TABLE_LOGS);
    public static final Uri URI_TABLE_ALERTS = Uri.parse("sqlite://com.medlab.pdmanager/table/" + TABLE_ALERTS);
    public static final Uri URI_TABLE_MEDS = Uri.parse("sqlite://com.medlab.pdmanager/table/" + TABLE_MEDICATIONS);
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pdManagerDB.db";
    private DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHandler getInstance(Context context) {

        return new DBHandler(context.getApplicationContext());
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        InitLogTable(sqLiteDatabase);
        InitCommunicatonTable(sqLiteDatabase);
        InitAlertTable(sqLiteDatabase);
        InitMedicationTimeTable(sqLiteDatabase);
        InitSyncTable(sqLiteDatabase);
    }

    private void InitLogTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_LOGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP
                + " INTEGER," + COLUMN_LOGTYPE
                + " TEXT," + COLUMN_LOGMESSAGE + " TEXT)";
        db.execSQL(CREATE_TABLE);


    }

    private void InitMedicationTimeTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_MEDICATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP
                + " INTEGER," + COLUMN_TIME
                + " INTEGER," + COLUMN_LASTALERTTIMESTAMP

                + " INTEGER," + COLUMN_DRUG + " TEXT," + COLUMN_DOSE + " TEXT," + COLUMN_INTAKEDESC + " TEXT)";
        db.execSQL(CREATE_TABLE);


    }

    private void InitSyncTable(SQLiteDatabase db) {


        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_SYNC + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP
                + " INTEGER," + COLUMN_TABLE
                + " TEXT)";
        db.execSQL(CREATE_TABLE);


    }

    private void InitAlertTable(SQLiteDatabase db) {


        String CREATE_TABLE = "CREATE TABLE " +
                TABLE_ALERTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TIMESTAMP
                + " INTEGER," + COLUMN_EXPIRATION
                + " TEXT," + COLUMN_ALERTTYPE
                + " TEXT," + COLUMN_ALERTMESSAGE
                + " TEXT," + COLUMN_ALERTSOURCE
                + " TEXT," + COLUMN_ALERTCREATED
                + " INTEGER," + COLUMN_ALERTACTIVE
                + " INTEGER," + COLUMN_ALERT + " TEXT)";
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

    public boolean clearTable(String table) {

        SQLiteDatabase mDb = null;
        boolean ret = false;
        try {

            mDb = this.getWritableDatabase();
            mDb.delete(table, null, null);
            ret = true;

        } catch (Exception ex) {
            Log.d("DBHandler", ex.getMessage());
            ret = false;
        } finally {
            if (mDb != null)
                mDb.close();
        }

        return ret;


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
