package com.pdmanager.alerting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pdmanager.communication.JsonStorage;
import com.pdmanager.models.UserAlert;
import com.pdmanager.notification.LocalNotificationTask;
import com.pdmanager.persistence.DBHandler;

/**
 * Created by george on 30/11/2015.
 */
public class UserAlertManager implements IUserAlertManager {

    private Context mContext;

    public UserAlertManager(Context context) {

        this.mContext = context;


    }
    public   static IUserAlertManager newInstance(Context context) {


        UserAlertManager fragment = new UserAlertManager(context);

        return fragment;
    }


    /**
     * Any unnotified
     * @return
     */
    @Override
    public boolean anyUnNotified() {

        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        boolean hasActiveAlert = false;

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis()  + 60 * 60*1000L;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " > ? and " + DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTCREATED + " = ?  and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";


        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                Long.toString(exunixTime),
                "0",
                "1"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {
                hasActiveAlert = true;
            }

        } catch (Exception ex) {

            Log.d("anyActive", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return hasActiveAlert;
    }


    /**
     * Any Active
     * @return
     */
    @Override
    public boolean anyActive() {

        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        boolean hasActiveAlert = false;

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis()  + 60 * 60*1000L;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " > ? and " + DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";


        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                Long.toString(exunixTime),
               "1"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {
                hasActiveAlert = true;
            }

        } catch (Exception ex) {

            Log.d("anyActive", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return hasActiveAlert;
    }


    /**
     * Get Active
     * @return
     */
    @Override
    public UserAlert getActive() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        long unixTime = System.currentTimeMillis() ;
        long exunixTime = System.currentTimeMillis() + 60 * 60*1000L ;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " > ? and " + DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";


        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                Long.toString(exunixTime),
                "1"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE,DBHandler.COLUMN_ALERTTYPE,DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION,DBHandler.COLUMN_ALERTSOURCE}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {


            } else {

                int id = cursor.getInt(0);
                result = new UserAlert(Integer.toString(id),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getLong(4), cursor.getLong(5), cursor.getString(6));
                return result;
            }

        } catch (Exception ex) {

            Log.d("AL.MG. getActive", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return result;
    }


    /**
     * Get Un notified
     * @return
     */
    @Override
    public UserAlert getUnNotified() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        long unixTime = System.currentTimeMillis() ;
        long exunixTime = System.currentTimeMillis() + 60 * 60*1000L ;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " > ? and " + DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTCREATED + " = ? ";


        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                Long.toString(exunixTime),
                "0"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE,DBHandler.COLUMN_ALERTTYPE,DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION,DBHandler.COLUMN_ALERTSOURCE}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {


            } else {

                int id = cursor.getInt(0);
                result = new UserAlert(Integer.toString(id),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getLong(4), cursor.getLong(5), cursor.getString(6));
                return result;
            }

        } catch (Exception ex) {

            Log.d("AL.MG. getActive", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return result;
    }

    /**
     * Delete Expired Alerts
     */
    public void deleteExpired() {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;
        long unixTime = System.currentTimeMillis();

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();

            //Delete Expired alerts
            db.delete(DBHandler.TABLE_ALERTS, DBHandler.COLUMN_EXPIRATION + "<" + unixTime, null);

            ret = true;

        } finally {


            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }

    }



    /**
     * Delete Expired Alerts
     */
    public void deleteExpiredTest(long unixTime) {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();

            //Delete Expired alerts
            db.delete(DBHandler.TABLE_ALERTS, DBHandler.COLUMN_EXPIRATION + "<" + unixTime, null);

            ret = true;

        } finally {


            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }

    }

    /**
     * Get Alert By Id
     * @param alertId ID of the alert
     * @return An alert item
     */
    @Override
    public UserAlert getAlert(String alertId) {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_ID + " = ? ";


        String[] whereArgs = new String[]{
               alertId

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE,DBHandler.COLUMN_ALERTTYPE,DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {


            } else {

                int id = cursor.getInt(0);
                result = new UserAlert(Integer.toString(id),cursor.getString(1), cursor.getString(2),cursor.getString(3), cursor.getLong(4), cursor.getLong(5), null);
                return result;
            }

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return result;
    }


    /**
     * Set Notified
     * @param id
     * @return
     */
    @Override
    public boolean setNotified(String id) {


        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put(DBHandler.COLUMN_ALERTCREATED, 1);


            db.update(DBHandler.TABLE_ALERTS, data, DBHandler.COLUMN_ID + "=" + id, null);

            ret = true;

        } finally {


            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }
        return ret;


    }


    /**
     * Set Not Active
     * @param id Id
     * @return
     */
    @Override
    public boolean setNotActive(String id) {


        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put(DBHandler.COLUMN_ALERTACTIVE, 0);


            db.update(DBHandler.TABLE_ALERTS, data, DBHandler.COLUMN_ID + "=" + id, null);

            ret = true;

        } finally {


            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }
        return ret;


    }






    /**
     * Clear all alerts
     * @return
     */
    @Override
    public boolean clearAll() {

        DBHandler helper = null;

        try {
            helper = DBHandler.getInstance(mContext);
            helper.clearTable(DBHandler.TABLE_ALERTS);

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {


            if (helper != null)
                helper.close();
        }
        return false;
    }

    /**
     * Add alert
     * @param alert Alert Title
     * @param message Alert Message
     * @param type Alert Type
     * @param source Alert Source
     * @param expiration Alert Expiration
     */
    @Override
    public void add(String alert, String message, String type, String source, long expiration) {

        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {


            long unixTime = System.currentTimeMillis();

            handler = DBHandler.getInstance(mContext);

            sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ALERT, alert);
            values.put(DBHandler.COLUMN_ALERTMESSAGE, message);
            values.put(DBHandler.COLUMN_ALERTTYPE, type);
            values.put(DBHandler.COLUMN_ALERTSOURCE, source);
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            values.put(DBHandler.COLUMN_EXPIRATION, expiration);
            values.put(DBHandler.COLUMN_ALERTACTIVE,1);
            values.put(DBHandler.COLUMN_ALERTCREATED,0);

            sqlDB.insert(DBHandler.TABLE_ALERTS, null, values);



        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());

        } finally {


            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();
        }

    /*
        try {

            new LocalNotificationTask(mContext).execute(alert);
        } catch (Exception ex) {


            Log.d("Alert Manager", ex.getMessage());

        }
    */
    }

    /**
     * Add user alert
     * @param alert
     */
    @Override
    public void add(UserAlert alert) {

        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {


            long unixTime = System.currentTimeMillis();

            handler = DBHandler.getInstance(mContext);

            sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_ALERT, alert.getTitle());
            values.put(DBHandler.COLUMN_ALERTMESSAGE, alert.getMessage());
            values.put(DBHandler.COLUMN_ALERTTYPE, alert.getAlertType());
            values.put(DBHandler.COLUMN_ALERTSOURCE,alert.getSource());
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            values.put(DBHandler.COLUMN_EXPIRATION, alert.getExpiration());
            values.put(DBHandler.COLUMN_ALERTCREATED,0);
            values.put(DBHandler.COLUMN_ALERTACTIVE,1);
            sqlDB.insert(DBHandler.TABLE_ALERTS, null, values);



        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());

        } finally {


            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();
        }

    /*
        try {

            new LocalNotificationTask(mContext).execute(alert);
        } catch (Exception ex) {


            Log.d("Alert Manager", ex.getMessage());

        }
    */
    }


    /**
     * User alert
     * @param alert
     */
    @Override
    public void setNotified(UserAlert alert) {


        try {
            setNotified(alert.getId());
            new LocalNotificationTask(mContext).execute(alert);

        } catch (Exception ex) {


            Log.d("Alert Manager", ex.getMessage());

        }

    }
}
