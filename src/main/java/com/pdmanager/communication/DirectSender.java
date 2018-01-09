package com.pdmanager.communication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.pdmanager.interfaces.IJsonRequestHandler;
import com.pdmanager.persistence.DBHandler;

/**
 * Created by george on 15/6/2016.
 */
public class DirectSender implements IJsonRequestHandler {


    private final String accessToken;
    private final Context mContext;

    public DirectSender(String a) {
        accessToken = a;
        this.mContext=null;
    }
    public DirectSender(String a,Context context) {
        accessToken = a;
        this.mContext=context;
    }
    @Override
    public void addRequest(JsonStorage request) {


        if(mContext!=null)
        {

            if(!NetworkStatus.IsNetworkConnected(mContext))
            {

                persist(request);
                return;
            }
        }

        RESTClient client = new RESTClient(accessToken);

        if (request != null) {

            if (request.getMethod().equals("POST")) {
                boolean res = true;
                res = client.Post(request.getUri(), request.getJson());

            } else if (request.getMethod().equals("PUT")) {
                boolean res = true;
                res = client.Put(request.getUri(), request.getJson());
            }


        }

    }

    public  boolean add(JsonStorage s) {

        boolean ret = false;

        if(mContext!=null) {

            DBHandler helper = null;
            SQLiteDatabase db = null;
            try {
                helper = DBHandler.getInstance(mContext);
                db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put(DBHandler.COLUMN_URI, s.getUri());
                values.put(DBHandler.COLUMN_JSON, s.getJson());

                db.insert(DBHandler.TABLE_JREQUESTS, null, values);


                ret = true;
            } catch (Exception e) {
                ret = false;
            } finally {
                if (db != null)
                    db.close();

                if (helper != null)
                    helper.close();
            }
        }
        return ret;
    }




    public boolean persist(JsonStorage s)
    {
              boolean ret = false;

        DBHandler helper=null;
        SQLiteDatabase db = null;
        try {
            helper = DBHandler.getInstance(mContext);
            db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(DBHandler.COLUMN_URI, s.getUri());
            values.put(DBHandler.COLUMN_JSON, s.getJson());

            db.insert(DBHandler.TABLE_JREQUESTS, null, values);



            ret = true;
        } catch (Exception e) {
            ret = false;
        } finally {
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }
        return ret;

    }
}
