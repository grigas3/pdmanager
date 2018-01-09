package com.pdmanager.communication;/*package com.medlab.pdmanagerhome.communication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.medlab.pdmanagerhome.models.Observation;
import com.medlab.pdmanagerhome.persistence.DBHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;



    public class JsonStorageAdapter {


        private static final String TAG = "LogDbAdapter";
        private DBHandler mDbHelper;
        private SQLiteDatabase mDb;

        private boolean dbOpen=false;


        private final Context mCtx;



        public JsonStorageAdapter(Context ctx) {
            this.mCtx = ctx;
        }

        public JsonStorageAdapter open() throws SQLException {


            mDbHelper = new DBHandler(mCtx);
            mDb = mDbHelper.getWritableDatabase();
            dbOpen=true;
            return this;
        }

        public void close() {
            if (mDbHelper != null) {
                mDbHelper.close();
                dbOpen=false;
            }
        }


    public void clear()
    {

        mDbHelper = new DBHandler(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        mDb.delete(mDbHelper.TABLE_JREQUESTS, null, null);
        mDbHelper.close();
       // mCtx.getContentResolver().notifyChange(DBHandler.URI_TABLE_USERS, null);

    }



    public long addJson(String json,String uri)
    {

        long ret = -1;
        try {

            if (!dbOpen)
                open();
            ContentValues initialValues = new ContentValues();

            // convert date to string

            //initialValues.put(DBHandler.COLUMN_ID,);
            initialValues.put(DBHandler.COLUMN_JSON, json);
            initialValues.put(DBHandler.COLUMN_URI, uri);

            ret = mDb.insert(DBHandler.TABLE_JREQUESTS, null, initialValues);


        }
        catch(Exception ex)
        {


        }
        finally {
            close();
        }

        return ret;

    }
        public ArrayList<JsonStorage> fetchJsons() {

            ArrayList<JsonStorage> res=new ArrayList<JsonStorage>();
            if(!dbOpen)
                open();

            Cursor mCursor = mDb.query(DBHandler.TABLE_JREQUESTS, new String[] {DBHandler.COLUMN_ID, DBHandler.COLUMN_URI,DBHandler.COLUMN_JSON},
                    null, null, null, null, null);

            if (mCursor != null) {
                if(mCursor.moveToFirst())
                {

                    do {

                        res.add(new JsonStorage(mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_JSON)),mCursor.getString(mCursor.getColumnIndex(DBHandler.COLUMN_URI))));
                        // get  the  data into array,or class variable
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
            }

            close();

            return res;


        }




    }


*/