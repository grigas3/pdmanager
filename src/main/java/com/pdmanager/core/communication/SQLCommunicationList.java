package com.pdmanager.core.communication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pdmanager.core.interfaces.IJsonRequestHandler;
import com.pdmanager.core.persistence.DBHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by george on 5/1/2017.
 */

public class SQLCommunicationList implements  ICommunicationQueue, IJsonRequestHandler {

        private Context ctx;
    public SQLCommunicationList(Context context) {

        ctx=context.getApplicationContext();
        //helper = DBHandler.getInstance(context);
    }



    @Override
    public void close() {

    }


    public ArrayList<JsonStorage> getLastN(int n) {
        SQLiteDatabase db = null;

        DBHandler helper=null;
        ArrayList<JsonStorage> jsonsRet=new ArrayList<JsonStorage>();
        Cursor cursor=null;
        try {
            helper=DBHandler.getInstance(ctx);

            db = helper.getReadableDatabase();
           // db.beginTransaction();
            cursor = db.query(DBHandler.TABLE_JREQUESTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_URI, DBHandler.COLUMN_JSON}, null, null, null, null, DBHandler.COLUMN_ID + " asc",Integer.toString(n));


            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            }
            else {


                do {

                    jsonsRet.add(new JsonStorage(cursor.getInt(0),cursor.getString(2), cursor.getString(1)));
                }while(cursor.moveToNext());

            }




        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE",ex.getMessage());

        } finally {

            if(cursor!=null)
            {
                cursor.close();
            }
            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }
        return jsonsRet;
    }



    @Override
    public JsonStorage poll() {
        return null;
    }

    @Override
    public  boolean push(JsonStorage s) {
        return add(s);

    }
    public  boolean add(JsonStorage s) {


        boolean ret = false;

        DBHandler helper=null;
        SQLiteDatabase db = null;
        try {
            helper=DBHandler.getInstance(ctx);
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
    @Override
    public boolean delete(String id) {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper=null;

        boolean ret=false;
        try {
            helper=DBHandler.getInstance(ctx);

            db = helper.getWritableDatabase();
        db.delete(DBHandler.TABLE_JREQUESTS, DBHandler.COLUMN_ID+" = ?", new String[]{String.valueOf(id)});

            ret=true;

        } catch (Exception ex) {

            Log.d("SQLCOMMQUEUE",ex.getMessage());

        } finally {


            if (db != null)
                db.close();

            if (helper != null)
                helper.close();
        }

        return ret;
    }

    @Override
    public  void AddRequest(JsonStorage jsonRequest) {

        add(jsonRequest);

    }
}




