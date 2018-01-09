package com.pdmanager.medication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pdmanager.R;
import com.pdmanager.communication.JsonStorage;
import com.pdmanager.models.MedTiming;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.PendingMedication;
import com.pdmanager.models.UserAlert;
import com.pdmanager.persistence.DBHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by george on 30/11/2015.
 */
public class MedManager implements IMedManager {

    private Context mContext;

    public MedManager(Context context) {

        this.mContext = context;


    }


    @Override
    public boolean addMedicationOrders(List<MedicationOrder> medOrders) {

        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {

            long unixTime = System.currentTimeMillis();

            handler = DBHandler.getInstance(mContext);

            sqlDB = handler.getWritableDatabase();

            for (MedicationOrder medOrder : medOrders) {
                for (MedTiming timing : medOrder.getTimings()) {
                    ContentValues values = new ContentValues();
                    values.put(DBHandler.COLUMN_DRUG, medOrder.MedicationId);
                    values.put(DBHandler.COLUMN_INTAKEDESC, medOrder.Instructions);
                    values.put(DBHandler.COLUMN_DOSE, timing.Dose);
                    values.put(DBHandler.COLUMN_TIME, timing.Time);
                    values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
                    values.put(DBHandler.COLUMN_LASTALERTTIMESTAMP, 0);

                    sqlDB.insert(DBHandler.TABLE_MEDICATIONS, null, values);

                }
            }


        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());

            return false;

        } finally {

            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();
        }

        return true;


    }

    /**
     * Add Medication Order
     *
     * @param medOrder A med order to add
     * @return
     */
    @Override
    public boolean addMedicationOrder(MedicationOrder medOrder) {

        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {

            long unixTime = System.currentTimeMillis();

            handler = DBHandler.getInstance(mContext);

            sqlDB = handler.getWritableDatabase();

            for (MedTiming timing : medOrder.getTimings()) {
                ContentValues values = new ContentValues();
                values.put(DBHandler.COLUMN_DRUG, medOrder.MedicationId);
                values.put(DBHandler.COLUMN_INTAKEDESC, medOrder.Instructions);
                values.put(DBHandler.COLUMN_DOSE, timing.Dose);
                values.put(DBHandler.COLUMN_TIME, timing.Time);
                values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
                values.put(DBHandler.COLUMN_LASTALERTTIMESTAMP, 0);

                sqlDB.insert(DBHandler.TABLE_MEDICATIONS, null, values);

            }


        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());

        } finally {

            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();
        }

        return true;


    }


    @Override
    public boolean clearAll() {

        DBHandler helper = null;

        try {
            helper = DBHandler.getInstance(mContext);
            helper.clearTable(DBHandler.TABLE_MEDICATIONS);
            return true;

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {

            if (helper != null)
                helper.close();
        }
        return false;
    }


    /**
     * Check if an alert already exists for the specific medication
     *
     * @param db
     * @param medId
     * @return
     */
    private boolean alertExists(SQLiteDatabase db, String medId) {

        Cursor cursor = null;

        boolean exists = false;
        String whereClause = DBHandler.COLUMN_ALERTSOURCE + " = ? and " + DBHandler.COLUMN_ALERTTYPE + " = ? and " + DBHandler.COLUMN_TIMESTAMP + " > ?";
        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() - 12 * 60 * 60 * 1000;

        ///Check for the same med and that has been created at least 12 hours before
        String[] whereArgs = new String[]{
                medId
                , "MED"
                , Long.toString(exunixTime)

        };
        try {

            cursor = db.query(DBHandler.TABLE_ALERTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_ALERTTYPE, DBHandler.COLUMN_ALERTSOURCE, DBHandler.COLUMN_EXPIRATION}, whereClause, whereArgs, null, null, DBHandler.COLUMN_EXPIRATION + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                exists = true;
            }

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }

        return exists;
    }

    /**
     * Set Not Active
     *
     * @param id Id
     * @return
     */
    @Override
    public boolean setLastMessage(String id) {

        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        boolean ret = false;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();
            long t = System.currentTimeMillis();
            ContentValues data = new ContentValues();
            data.put(DBHandler.COLUMN_LASTALERTTIMESTAMP, t);

            db.update(DBHandler.TABLE_MEDICATIONS, data, DBHandler.COLUMN_ID + " = ?", new String[]{id});

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

    @Override
    public List<UserAlert> getAlerts() {
        return new ArrayList<UserAlert>();
    }

    private boolean shouldTake(long time) {

        Date date1 = new Date(time);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Date date2 = new Date();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int time2 = cal2.get(Calendar.HOUR_OF_DAY) * 60 + cal2.get(Calendar.MINUTE);
        int tmi = cal1.get(Calendar.HOUR_OF_DAY) * 60 + cal1.get(Calendar.MINUTE);
        return time2 > tmi - 10 && time2 < tmi + 30;
//return true;

    }

    @Override
    public int pendingMedication() {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000;

        int ret = -1;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_TIME}, null, null, null, null, DBHandler.COLUMN_ID + " asc", "50");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                do {

                    int id = cursor.getInt(0);
                    long time = cursor.getLong(2);

                    //First check if timing is ok
                    if (shouldTake(time)) {

                        //Then if we already have an alert created
                        if (!alertExists(db, Integer.toString(id))) {

                            ret = id;
                            break;
                        }
                    }


                } while (cursor.moveToNext());


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

        return ret;
    }


    public int pendingMedicationTest(long testtime) {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        Cursor cursor = null;

        long unixTime = testtime;
        long exunixTime = testtime + 60 * 60 * 1000;

        int ret = -1;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_TIME}, null, null, null, null, DBHandler.COLUMN_ID + " asc", "50");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                do {

                    int id = cursor.getInt(0);
                    long time = cursor.getLong(2);

                    //First check if timing is ok
                    if (shouldTake(time)) {

                        //Then if we already have an alert created
                        if (!alertExists(db, Integer.toString(id))) {

                            ret = id;
                            break;
                        }
                    }


                } while (cursor.moveToNext());


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

        return ret;
    }


    private String getMessage(String s, long time) {

        String message = s;

        try {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTimeInMillis(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String shortTimeStr = sdf.format(cal1.getTime());
            message = mContext.getString(R.string.medalertpart1) + " " + s + " " + mContext.getString(R.string.medalertpart2) + " " + shortTimeStr;

        } catch (Exception e) {

        }
        return message;
    }

    @Override
    public UserAlert getPendingMedAlert(int medid) {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_ID + " = ? ";
        long unixTime = System.currentTimeMillis();

        String[] whereArgs = new String[]{
                Integer.toString(medid)

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_INTAKEDESC, DBHandler.COLUMN_TIME}, whereClause, whereArgs, null, null, DBHandler.COLUMN_ID + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                int id = cursor.getInt(0);
                long time = cursor.getLong(3);
                String s = cursor.getString(1);
                getMessage(s, time);
                result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), "MED", unixTime, cursor.getLong(3), Integer.toString(medid));
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


    private boolean isNext(long time) {

        Date date1 = new Date(time);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Date date2 = new Date();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int time2 = cal2.get(Calendar.HOUR_OF_DAY) * 60 + cal2.get(Calendar.MINUTE);
        int tmi = cal1.get(Calendar.HOUR_OF_DAY) * 60 + cal1.get(Calendar.MINUTE);
        return time2 < tmi;
//return true;

    }


    private long convertToDay(long time) {
        Date date1 = new Date(time);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Date date2 = new Date();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        long t2 = cal2.getTimeInMillis();
        int time2 = (cal2.get(Calendar.HOUR_OF_DAY) * 60 + cal2.get(Calendar.MINUTE)) * 60;
        int tmi = (cal1.get(Calendar.HOUR_OF_DAY) * 60 + cal1.get(Calendar.MINUTE)) * 60;

        return t2 - (time2 - tmi) * 1000;


    }

    public UserAlert getNextMedicationTest(long unixTime) {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor0 = null;
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_LASTALERTTIMESTAMP + " < ? ";

        long exunixTime = unixTime - 12 * 60 * 60 * 1000;
        String[] whereArgs = new String[]{
                Long.toString(exunixTime)

        };

        long lastAlertTimestamp = 0;
        long lastIntake = 0;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor0 = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_DOSE, DBHandler.COLUMN_INTAKEDESC, DBHandler.COLUMN_TIME, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_LASTALERTTIMESTAMP}, null, null, null, null, DBHandler.COLUMN_LASTALERTTIMESTAMP + " desc", "1");
            cursor0.moveToFirst();
            if (cursor0.getCount() == 0) {

            } else

            {
                lastAlertTimestamp = cursor0.getLong(6);
                lastIntake = convertToDay(cursor0.getLong(4));


             /*   do {

                    long t2=(cursor0.getLong(6));
                    Log.d("TEST",Long.toString(t2));
                }while(cursor0.moveToNext());
                */
            }

            cursor = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_DOSE, DBHandler.COLUMN_INTAKEDESC, DBHandler.COLUMN_TIME, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_LASTALERTTIMESTAMP}, whereClause, whereArgs, null, null, DBHandler.COLUMN_TIME + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

                return null;

            } else {

                int id = cursor.getInt(0);
                Calendar cal1 = Calendar.getInstance();
                long te = convertToDay(cursor.getLong(4));
                cal1.setTimeInMillis(te);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String shortTimeStr = sdf.format(cal1.getTime());

                if (lastAlertTimestamp == 0 || (lastAlertTimestamp > 0 && (te - 30 * 60 * 1000 < lastIntake || unixTime > lastIntake))) {

                    String message = "";// mContext.getString(R.string.medalertpart3) + " " + cursor.getString(1) + " " + mContext.getString(R.string.medalertpart4) + " " + shortTimeStr;

                    result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), "MED", unixTime, te, Integer.toString(id));
                }


            }

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {

            if (cursor0 != null) {
                cursor0.close();
            }
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

    public UserAlert getNextMedication() {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor0 = null;
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_LASTALERTTIMESTAMP + " < ? ";
        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() - 12 * 60 * 60 * 1000;
        String[] whereArgs = new String[]{
                Long.toString(exunixTime)

        };

        long lastAlertTimestamp = 0;
        long lastIntake = 0;

        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor0 = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_DOSE, DBHandler.COLUMN_INTAKEDESC, DBHandler.COLUMN_TIME, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_LASTALERTTIMESTAMP}, null, null, null, null, DBHandler.COLUMN_LASTALERTTIMESTAMP + " desc", "1");
            cursor0.moveToFirst();
            if (cursor0.getCount() == 0) {

            } else

            {
                lastAlertTimestamp = cursor0.getLong(6);
                lastIntake = convertToDay(cursor0.getLong(4));


             /*   do {

                    long t2=(cursor0.getLong(6));
                    Log.d("TEST",Long.toString(t2));
                }while(cursor0.moveToNext());
                */
            }

            cursor = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_DOSE, DBHandler.COLUMN_INTAKEDESC, DBHandler.COLUMN_TIME, DBHandler.COLUMN_TIMESTAMP, DBHandler.COLUMN_LASTALERTTIMESTAMP}, whereClause, whereArgs, null, null, DBHandler.COLUMN_TIME + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

                return null;

            } else {

                int id = cursor.getInt(0);
                Calendar cal1 = Calendar.getInstance();
                long te = convertToDay(cursor.getLong(4));
                cal1.setTimeInMillis(te);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String shortTimeStr = sdf.format(cal1.getTime());

                if (lastAlertTimestamp == 0 || (lastAlertTimestamp > 0 && (te - 30 * 60 * 1000 < lastIntake || unixTime > lastIntake))) {

                    String message = "";// mContext.getString(R.string.medalertpart3) + " " + cursor.getString(1) + " " + mContext.getString(R.string.medalertpart4) + " " + shortTimeStr;

                    result = new UserAlert(Integer.toString(id), cursor.getString(1), cursor.getString(2), "MED", unixTime, te, Integer.toString(id));
                }


            }

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {

            if (cursor0 != null) {
                cursor0.close();
            }
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


    public PendingMedication getPendingMedication(String medid) {
        SQLiteDatabase db = null;
        PendingMedication result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_ID + " = ? ";
        long unixTime = System.currentTimeMillis();
        long exunixTime = System.currentTimeMillis() + 60 * 60 * 1000;
        String[] whereArgs = new String[]{
                medid

        };
        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor = db.query(DBHandler.TABLE_MEDICATIONS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_DRUG, DBHandler.COLUMN_DOSE, DBHandler.COLUMN_INTAKEDESC, DBHandler.COLUMN_TIME, DBHandler.COLUMN_TIMESTAMP}, whereClause, whereArgs, null, null, DBHandler.COLUMN_TIMESTAMP + " asc", "1");
            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {

                int id = cursor.getInt(0);
                result = new PendingMedication(Integer.toString(id), null, cursor.getString(1), cursor.getLong(4), cursor.getString(2), cursor.getString(3), null);

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
}
