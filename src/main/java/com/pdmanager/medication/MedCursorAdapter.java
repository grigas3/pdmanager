package com.pdmanager.medication;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pdmanager.persistence.DBHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by george on 20/11/2015.
 */
public class MedCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    private Context mContext;
    private int mSelectedPosition;

    public MedCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public MedCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View v = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        //    bindView(v, context, cursor);
        return v;


    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        try {


            int messageIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_DRUG);
            int timeIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_TIME);
            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
            int doseIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_DOSE);
            text1.setText(cursor.getString(messageIndex)+"  ("+  cursor.getString(doseIndex)+")");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String shortTimeStr = sdf.format(new Date(cursor.getLong(timeIndex) * 1000).getTime());
            text2.setText(shortTimeStr);


        } catch (Exception ex) {

            Log.d("ERROR", ex.getMessage());
        }

    }


}
