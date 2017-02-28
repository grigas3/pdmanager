package com.pdmanager.alerting;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pdmanager.persistence.DBHandler;

import java.util.Date;

/**
 * Created by george on 20/11/2015.
 */
public class AlertCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    private Context mContext;
    private int mSelectedPosition;

    public AlertCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public AlertCursorAdapter(Context context, Cursor c, boolean autoRequery) {
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


            int messageIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_ALERT);
            int timeIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_TIMESTAMP);
            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
            text1.setText(cursor.getString(messageIndex));
            text2.setText(new Date(cursor.getLong(timeIndex)).toString());


        } catch (Exception ex) {

        }

    }


}
