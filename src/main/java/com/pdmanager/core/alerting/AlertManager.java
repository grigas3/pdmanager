package com.pdmanager.core.alerting;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pdmanager.core.notification.LocalNotificationTask;
import com.pdmanager.core.persistence.DBHandler;

/**
 * Created by george on 30/11/2015.
 */
public class AlertManager implements IAlertManager {

    private Context mContext;

    public AlertManager(Context context) {

        this.mContext = context;


    }

    @Override
    public void AddAlert(String alert) {


        try {


            long unixTime = System.currentTimeMillis() / 1000L;

            DBHandler handler = new DBHandler(mContext);

            SQLiteDatabase sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ALERT, alert);
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            sqlDB.insert(DBHandler.TABLE_ALERTS, null, values);
            sqlDB.close();

        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());

        }


        try {

            new LocalNotificationTask(mContext).execute(alert);
        } catch (Exception ex) {


            Log.d("Alert Manager", ex.getMessage());

        }

    }


    @Override
    public void UserAlert(String alert) {


        try {

            new LocalNotificationTask(mContext).execute(alert);
        } catch (Exception ex) {


            Log.d("Alert Manager", ex.getMessage());

        }

    }
}
