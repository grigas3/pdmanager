package com.pdmanager.core.logging;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pdmanager.core.persistence.DBHandler;

import java.util.Date;

/**
 * Created by george on 20/11/2015.
 */
public class LogCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    private Context mContext;
    private int mSelectedPosition;

    public LogCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public LogCursorAdapter(Context context, Cursor c, boolean autoRequery) {
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

            int typeIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_LOGTYPE);
            int messageIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_LOGMESSAGE);
            int timeIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_TIMESTAMP);
            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            TextView text2 = (TextView) view.findViewById(android.R.id.text2);
            text1.setText(cursor.getString(messageIndex));
            text2.setText(new Date(cursor.getLong(timeIndex) * 1000).toString());
            String logType = cursor.getString(typeIndex);
            if (logType.equals("ERROR")) {
                text1.setTextColor(Color.RED);
            } else if (logType.equals("WARNING"))
                text1.setTextColor(Color.YELLOW);


        } catch (Exception ex) {

            Log.d("ERROR", ex.getMessage());
        }

    }


}
