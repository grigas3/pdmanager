package com.pdmanager.communication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.pdmanager.interfaces.IJsonRequestHandler;
import com.pdmanager.persistence.DBHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by george on 5/1/2017.
 */

public class SQLCommunicationQueue implements Queue<JsonStorage>, ICommunicationQueue, IJsonRequestHandler {

    private Context ctx;

    public SQLCommunicationQueue(Context context) {

        ctx = context.getApplicationContext();
        //helper = DBHandler.getInstance(context);
    }

    @Override
    public int size() {
        int size = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        DBHandler helper = null;
        try {

            helper = DBHandler.getInstance(ctx);
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select count(*) from " + DBHandler.TABLE_JREQUESTS, null);
            cursor.moveToFirst();
            size = cursor.getInt(0);

        } catch (Exception e) {

        } finally {

            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();


            if (helper != null)
                helper.close();
        }
        return size;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] ts) {
        return null;
    }

    @Override
    public boolean add(JsonStorage s) {


        boolean ret = false;

        DBHandler helper = null;
        SQLiteDatabase db = null;
        try {
            helper = DBHandler.getInstance(ctx);
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
    public boolean addAll(Collection<? extends JsonStorage> collection) {
        return false;
    }

    @Override
    public void clear() {

        SQLiteDatabase db = null;
        DBHandler helper = null;
        try {
            helper = DBHandler.getInstance(ctx);
            db = helper.getWritableDatabase();
            db.execSQL("delete from " + DBHandler.TABLE_JREQUESTS);
        } catch (Exception ex) {
            Log.d("SQLCOMMQUEUE", ex.getMessage());

        } finally {
            if (db != null)
                db.close();
            if (helper != null)
                helper.close();
        }
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @NonNull
    @Override
    public Iterator<JsonStorage> iterator() {
        return null;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean offer(JsonStorage s) {
        return false;
    }

    @Override
    public JsonStorage remove() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public ArrayList<JsonStorage> getLastN(int n) {
        return null;
    }

    @Override
    public JsonStorage poll() {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        try {
            helper = DBHandler.getInstance(ctx);

            db = helper.getWritableDatabase();
            db.beginTransaction();
            cursor = db.query(DBHandler.TABLE_JREQUESTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_URI, DBHandler.COLUMN_JSON}, null, null, null, null, DBHandler.COLUMN_ID + " asc", "1");


            cursor.moveToFirst();
            if (cursor.getCount() == 0) {

            } else {
                int id = cursor.getInt(0);
                result = new JsonStorage(cursor.getString(2), cursor.getString(1));

                db.delete(DBHandler.TABLE_JREQUESTS, DBHandler.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            }


            db.setTransactionSuccessful();
            db.endTransaction();

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

    @Override
    public JsonStorage element() {
        return null;
    }

    @Override
    public JsonStorage peek() {
        SQLiteDatabase db = null;
        JsonStorage result = null;
        DBHandler helper = null;
        Cursor cursor = null;
        try {
            helper = DBHandler.getInstance(ctx);
            db = helper.getReadableDatabase();
            db.beginTransaction();
            cursor = db.query(false, DBHandler.TABLE_JREQUESTS, new String[]{DBHandler.COLUMN_ID, DBHandler.COLUMN_URI, DBHandler.COLUMN_JSON}, null, null, null, null, DBHandler.COLUMN_ID, "1");
            cursor.moveToFirst();

            result = new JsonStorage(cursor.getString(1), cursor.getString(2));


            db.endTransaction();


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


    @Override
    public boolean push(JsonStorage s) {
        return add(s);

    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public void addRequest(JsonStorage jsonRequest) {

        add(jsonRequest);

    }
}




