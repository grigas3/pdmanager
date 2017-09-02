package com.pdmanager.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.pdmanager.persistence.DBHandler;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

public class WatchdogService extends IntentService {
    public static final String TAG = "WATCHDOG";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public WatchdogService() {
        super("WatchdogService");
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
        // Check if recording service is running and put it on Log

        if (isServiceRunning(RecordingService.class)) {
            ProcessLog("INFO", "Recording service is running");

        } else {

            ProcessLog("ERROR", "Recording service is not running");
        }


        // Release the wake lock provided by the BroadcastReceiver.
        WatchdogAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }


    public void ProcessLog(String logType, String message) {


        //REMOVED FOR PILOT

        Log.v(TAG, message);

        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {

            long unixTime = System.currentTimeMillis() / 1000L;

            handler = DBHandler.getInstance(this);

            sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(DBHandler.COLUMN_LOGTYPE, logType);
            values.put(DBHandler.COLUMN_LOGMESSAGE, message);
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            sqlDB.insert(DBHandler.TABLE_LOGS, null, values);


        } catch (Exception ex) {

            Log.e(TAG, ex.getMessage(), ex.getCause());
            //LogError("Process Log", ex.getCause());

        } finally {

            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();


        }


    }


}
