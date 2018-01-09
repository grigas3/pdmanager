package com.pdmanager.alerting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.pdmanager.communication.JsonStorage;
import com.pdmanager.models.UserAlert;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.settings.RecordingSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by george on 30/11/2015.
 */
public class UserAlertManager implements IUserAlertManager {


    private final  static String TAG="USERALERTMANAGER";
    private Context mContext;

    public UserAlertManager(Context context) {

        this.mContext = context;


    }

    public static IUserAlertManager newInstance(Context context) {

        UserAlertManager fragment = new UserAlertManager(context);

        return fragment;
    }


    /**
     * Any unnotified
     *
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
        //long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000L;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " < ?  and " + DBHandler.COLUMN_ALERTCREATED + " = ?  and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";

        String[] whereArgs = new String[]{
                Long.toString(unixTime),
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
     *
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
        long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000L;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " < ? and " /*+ DBHandler.COLUMN_EXPIRATION + " <  ? and "*/ + DBHandler.COLUMN_ALERTACTIVE + " = ? ";

        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                // Long.toString(exunixTime),
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
     *
     * @return
     */
    @Override
    public UserAlert getFirstActive() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000L;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " > ? and " + DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";

        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                Long.toString(exunixTime),
                "1"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION, DBHandler.COLUMN_ALERTSOURCE}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                int id = cursor.getInt(0);
                result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4), cursor.getLong(5), cursor.getString(6));
                return result;
            }

        } catch (Exception ex) {

            Log.d("AL.MG. getFirstActive", ex.getMessage());

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



    @Override
    public List<UserAlert> getAlerts() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        ArrayList<UserAlert> alerts=new ArrayList<UserAlert>();

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000L;
        String whereClause =  DBHandler.COLUMN_ALERTACTIVE + " = ? ";

        String[] whereArgs = new String[]{
                "1"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION, DBHandler.COLUMN_ALERTSOURCE}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "50");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {



                do {


                    int id = cursor.getInt(0);
                    result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4), cursor.getLong(5), cursor.getString(6));
                    alerts.add(result);

                }while(cursor.moveToNext());



            }

        } catch (Exception ex) {

            Log.d("AL.MG. getFirstActive", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return alerts;
    }

    @Override
    public List<UserAlert> getActive() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        ArrayList<UserAlert> alerts=new ArrayList<UserAlert>();

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000L;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " < ? and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";

        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                "1"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION, DBHandler.COLUMN_ALERTSOURCE}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "50");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {



                do {


                    int id = cursor.getInt(0);
                    result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4), cursor.getLong(5), cursor.getString(6));
                  alerts.add(result);

                }while(cursor.moveToNext());



            }

        } catch (Exception ex) {

            Log.d("AL.MG. getFirstActive", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return alerts;
    }


    /**
     * Get Un notified
     *
     * @return
     */
    @Override
    public UserAlert getUnNotified() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() ;
        String whereClause = DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTCREATED + " = ? ";

        String[] whereArgs = new String[]{
                Long.toString(exunixTime),
                "0"

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION, DBHandler.COLUMN_ALERTSOURCE}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                int id = cursor.getInt(0);
                result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4), cursor.getLong(5), cursor.getString(6));
                return result;
            }

        } catch (Exception ex) {

            Log.d("AL.MG. getFirstActive", ex.getMessage());

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
     *
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

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                int id = cursor.getInt(0);
                result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4), cursor.getLong(5), null);
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


    private void doSetNotifiedIssue(String code) {
        if (RecordingSettings.GetRecordingSettings(mContext).getRemoteLogging()) {

            Bugfender.sendIssue(code, UserTaskTrackingCodes.NOTIFICATION);

        }

    }

    /**
     * Set Notified
     *
     * @param alert
     * @return
     */

    private boolean doSetNotified(UserAlert alert) {

        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put(DBHandler.COLUMN_ALERTCREATED, 1);


            db.update(DBHandler.TABLE_ALERTS, data, DBHandler.COLUMN_ID + "=" + alert.getId(), null);

            mContext.getContentResolver().notifyChange(DBHandler.URI_TABLE_ALERTS, null);
            ret = true;

        }
        catch (Exception e)
        {

            Log.e(TAG,e.getMessage(),e.getCause());

        }
        finally {

            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }
        return ret;


    }


    /**
     * Users may see their mobile phone after a significant period of time (or days for the specific type of users)
     * Therefore some alerts may be obsolete and should be discarded
     * This is accomplished by the updateExpired method.
     *
     * @return
     */
    @Override
    public boolean updateExpired() {

        Cursor cursor = null;
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;
        long unixTime = System.currentTimeMillis();
        String whereClause = DBHandler.COLUMN_EXPIRATION + " <  ? and " + DBHandler.COLUMN_ALERTACTIVE + " = ? ";

        String[] whereArgs = new String[]{
                Long.toString(unixTime),
                "1"
        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();
            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "50");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                do {

                    ContentValues data = new ContentValues();
                    int id = cursor.getInt(0);
                    long exp = cursor.getLong(5);
                    String code = cursor.getString(3);

                    long expDate = getMaxActiveTimeDate(exp, code);
                    if (unixTime > expDate) {
                        data.put(DBHandler.COLUMN_EXPIRATION, getNewExpirationDate(exp, code));
                        data.put(DBHandler.COLUMN_ALERTCREATED, 0);
                        db.update(DBHandler.TABLE_ALERTS, data, DBHandler.COLUMN_ID + " = " + id, null);
                    }

                } while (cursor.moveToNext());


            }


        } catch (Exception ex) {

            Log.e(TAG, ex.getMessage());
            throw ex;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }
        return ret;


    }


    private void updateAlertIssue(String code) {
        if (RecordingSettings.GetRecordingSettings(mContext).getRemoteLogging()) {

            Bugfender.sendIssue(code, UserTaskTrackingCodes.START);

        }
    }

    private boolean updateAlertInDB(String code)
    {
        Cursor cursor = null;
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;
        String whereClause = DBHandler.COLUMN_ALERTTYPE + " = ? ";

        String[] whereArgs = new String[]{
                code.toLowerCase()

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();
            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERT, DBHandler.COLUMN_ALERTMESSAGE, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "50");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {


                do {

                    ContentValues data = new ContentValues();
                    int id = cursor.getInt(0);
                    long exp=cursor.getLong(5);
                    data.put(DBHandler.COLUMN_EXPIRATION,getNewExpirationDate(exp,code));
                    data.put(DBHandler.COLUMN_ALERTCREATED, 0);
                    db.update(DBHandler.TABLE_ALERTS, data, DBHandler.COLUMN_ID + " = " + id, null);

                }while(cursor.moveToNext());



            }


        }
        catch (Exception ex)
        {

            Log.e(TAG,ex.getMessage());
            throw  ex;

        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();

            ret = false;
        }
        return ret;


    }

    @Override
    public boolean updateAlerts(String code) {


        //Create an Issue in BugFender
        updateAlertIssue(code);
        //Update Database
        return updateAlertInDB(code);

    }

    /***
     * Get new expiration date based on old expiration date and alert type
     * @param oldExpDate Old expiration date
     * @param currentType Alert type
     * @return
     */
    public long getNewExpirationDate(long oldExpDate,String currentType) {

        long expDate= 1000 * 24 * 60 * 60;


        if (currentType != null) {
            if (currentType.toLowerCase().equals("med") || currentType.toLowerCase().equals( "mood")) {

                //Add a day
                expDate =  1000 * 24 * 60 * 60;
            } else if (currentType.toLowerCase().startsWith("cogn")) {

                //Add 7 days and substract 12 hours
                expDate = 1000 * 24 * 60 * 60 * 7 - 1000 * 12 * 60 * 60;
            } else if (currentType.toLowerCase().startsWith("diary")) {

                ///Every day (increment by one hour)
                expDate = 1000 * 24 * 60 * 60 * 1;

                Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

                Date date = new Date(oldExpDate);
                cal1.setTime(date);
                int hour=cal1.get(Calendar.HOUR_OF_DAY)+1;
                int startHour = RecordingSettings.GetRecordingSettings(mContext).getStartHour();
                int stopHour = RecordingSettings.GetRecordingSettings(mContext).getStopHour();
                if(hour>stopHour)
                    expDate+=(startHour-hour+1)*1000 *  60 * 60;
                else
                    expDate+=1000 *  60 * 60;



            }


        }


        return expDate+oldExpDate;


    }


    /***
     * Get max active time per alert type
     * Alerts regarding medications,
     * @param oldExpDate Old expiration date
     * @param currentType Alert type
     * @return
     */
    public long getMaxActiveTimeDate(long oldExpDate, String currentType) {

        long defaultActiveTime = 1000 * 12 * 60 * 60;


        /*
        if (currentType != null) {
            if (currentType.toLowerCase().equals("med") || currentType.toLowerCase().equals( "mood")) {

                //Add a day
                defaultActiveTime =  1000 * 24 * 60 * 60;
            } else if (currentType.toLowerCase().startsWith("diary")) {

                ///Every day (increment by one hour)
                defaultActiveTime = 1000 * 6 * 60 * 60 * 1;

            }


        }
        */

        return defaultActiveTime + oldExpDate;


    }


    /***
     * Get New expiration date
     * @param alert Alert
     * @return
     */
    private long getNewExpirationDate(UserAlert alert) {

                long expDate=0;
                String currentType = alert.getAlertType();
                long currentExpDate = alert.getExpiration();

                if (currentType != null) {
                    if (currentType.toLowerCase() == "med" || currentType.toLowerCase() == "mood") {

                        //Add a day
                        expDate = currentExpDate + 1000 * 24 * 60 * 60;
                    } else if (currentType.toLowerCase().startsWith("cogn")) {

                        //Add 7 days and substract 12 hours
                        expDate = currentExpDate + 1000 * 24 * 60 * 60 * 7 - 1000 * 12 * 60 * 60;
                    } else if (currentType.toLowerCase().startsWith("diary")) {

                        ///Every 3 days
                        expDate = currentExpDate + 1000 * 24 * 60 * 60 * 3;

                    }


                }


        return expDate;


    }


    /**
     * Set Not Active
     *
     * @param id Id
     * @return
     */

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

            mContext.getContentResolver().notifyChange(DBHandler.URI_TABLE_ALERTS, null);

            ret = true;

        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage(),e.getCause());
        }
        finally {

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
     *
     * @return
     */
    @Override
    public boolean clearAll() {

        DBHandler helper = null;

        try {
            helper = DBHandler.getInstance(mContext);
            helper.clearTable(DBHandler.TABLE_ALERTS);

        } catch (Exception ex) {

            Log.e(TAG, ex.getMessage());

        } finally {

            if (helper != null)
                helper.close();
        }
        return false;
    }

    /**
     * Add alert
     *
     * @param alert      Alert Title
     * @param message    Alert Message
     * @param type       Alert Type
     * @param source     Alert Source
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
            values.put(DBHandler.COLUMN_ALERTACTIVE, 1);
            values.put(DBHandler.COLUMN_ALERTCREATED, 0);

            sqlDB.insert(DBHandler.TABLE_ALERTS, null, values);


        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());

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
     *
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
            values.put(DBHandler.COLUMN_ALERTSOURCE, alert.getSource());
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            values.put(DBHandler.COLUMN_EXPIRATION, alert.getExpiration());
            values.put(DBHandler.COLUMN_ALERTCREATED, 0);
            values.put(DBHandler.COLUMN_ALERTACTIVE, 1);
            sqlDB.insert(DBHandler.TABLE_ALERTS, null, values);


            mContext.getContentResolver().notifyChange(DBHandler.URI_TABLE_ALERTS, null);


        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());

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
     * When a notification has been created for a notification then set this notification as notified
     * Otherwise multiple alerts for the same task could be created
     *
     * @param alert
     */
    @Override
    public void setNotified(UserAlert alert) {

        try {

            doSetNotifiedIssue(alert.getAlertType());

            doSetNotified(alert);


        } catch (Exception ex) {

            Log.d("Alert Manager", ex.getMessage());

        }

    }





    @Override
    public void setNotified(String id) {

        try {

            UserAlert alert=getAlert(id);
            if(alert!=null) {
                doSetNotified(alert);

            }


        } catch (Exception ex) {

            Log.d("Alert Manager", ex.getMessage());

        }

    }
}
