package com.pdmanager.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.JsonStorage;
import com.pdmanager.models.Alert;
import com.pdmanager.models.UserAlert;
import com.pdmanager.settings.RecordingSettings;

import java.net.URLEncoder;
import java.util.List;

/**
 * Created by george on 14/4/2017.
 */

public class SyncManager  {

    private final  static String TAG="SYNCMANAGER";



    private Context mContext;
    private final Gson gson;
    public SyncManager(Context context)
    {
        gson = new Gson();
        this.mContext=context;


    }

    private long syncAlerts(String accessToken,String userid,long timestamp,boolean addSync)
    {

        UserAlertManager alertManager=new UserAlertManager(mContext);

        String param = "";
        try {
            param = "/find?take=0&skip=0&filter=" + URLEncoder.encode("{'userid':'" + userid + "'}", "UTF-8") + "&sort=&sortdir=false&lastmodified="+timestamp;
        } catch (Exception ex) {



        }

        CommunicationManager commManager = new CommunicationManager(accessToken);
        String jsonResponse = commManager.Get("Alert", param);

        try {
            List<Alert> alerts = gson.fromJson(jsonResponse, new TypeToken<List<Alert>>() {
            }.getType());



            long maxTimestamp=-1;
            for(Alert e:alerts)
            {
                alertManager.add(new UserAlert(e.getTitle(),e.getMessage(),e.getAlertType(),e.getTimestamp(),e.getExpiration(),"PD_Manager"));
                if(e.getTimestamp()>maxTimestamp)
                    maxTimestamp=e.getTimestamp();

            }

            if(maxTimestamp>-1) {
                if (addSync) {
                    addLastTimestamp(DBHandler.TABLE_ALERTS, maxTimestamp);
                } else
                    updateLastTimestamp(DBHandler.TABLE_ALERTS, maxTimestamp);

            }
            return maxTimestamp;

        } catch (Exception ex) {


        }


        return -1;


    }
    /**
     * Sync Table
     * @param table The table to sync
     */
    public long syncTable(String table)
    {

        long ret=-1;
        long timestamp=getLastSyncTimestamp(table);
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(mContext);

        if(timestamp<=0)
        {



            if(table.toLowerCase().equals(DBHandler.TABLE_ALERTS))
            {

                ret=syncAlerts(settings.getToken(),settings.getUserID(),timestamp,true);

            }


        }
        else
        {




        }

        return ret;

    }

    /**
     * Get Last Sync Timestamp for table
     * @param table
     * @return
     */
    private long getLastSyncTimestamp(String table)
    {
        SQLiteDatabase db = null;
        UserAlert result = null;
        DBHandler helper = null;
        Cursor cursor0 = null;
        Cursor cursor = null;
        String whereClause = DBHandler.COLUMN_TABLE + " = ? ";
        String[] whereArgs = new String[]{
              table

        };

        long lastSyncTimestamp = 0;


        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getReadableDatabase();

            cursor0 = db.query(DBHandler.TABLE_SYNC, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_TIMESTAMP}, null, null, null, null, DBHandler.COLUMN_TIMESTAMP + " desc", "1");
            cursor0.moveToFirst();
            if (cursor0.getCount() == 0) {

                lastSyncTimestamp= 0;

            } else

            {
                lastSyncTimestamp = cursor0.getLong(1);


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

        return lastSyncTimestamp;


    }


    private void addLastTimestamp(String table,long timestamp)
    {


        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {

            long unixTime = System.currentTimeMillis();

            handler = DBHandler.getInstance(mContext);

            sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DBHandler.COLUMN_TABLE, table);
            values.put(DBHandler.COLUMN_TIMESTAMP, timestamp);

            sqlDB.insert(DBHandler.TABLE_SYNC, null, values);


        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());

        } finally {

            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();
        }




    }

    private void updateLastTimestamp(String table, long timestamp)
    {

        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;


        try {
            helper = DBHandler.getInstance(mContext);

            db = helper.getWritableDatabase();

            ContentValues data = new ContentValues();
            data.put(DBHandler.COLUMN_TIMESTAMP, timestamp);


            db.update(DBHandler.TABLE_SYNC, data, DBHandler.COLUMN_ID + "=" + table, null);


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


        }

    }



}
