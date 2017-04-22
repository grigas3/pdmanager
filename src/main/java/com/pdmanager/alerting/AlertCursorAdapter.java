package com.pdmanager.alerting;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.views.patient.DiaryTrackingActivity;

import java.util.Date;

/**
 * Created by george on 20/11/2015.
 */
public class AlertCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = AlertCursorAdapter.class.getName();
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


        View v = mInflater.inflate(R.layout.alert_item, parent, false);
        //    bindView(v, context, cursor);
        return v;


    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        try {


            int titleIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_ALERT);
            int messageIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_ALERTMESSAGE);
            int typeIndex = cursor.getColumnIndexOrThrow(DBHandler.COLUMN_ALERTTYPE);
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.alert_layout);
            TextView text1 = (TextView) view.findViewById(R.id.alert_title);
            TextView text2 = (TextView) view.findViewById(R.id.alert_text);
            text1.setText(cursor.getString(titleIndex));
            text2.setText(cursor.getString(messageIndex));
            String type=cursor.getString(typeIndex);

            if(type.equals("warn")) {

                layout.setBackground(mContext.getResources().getDrawable(R.drawable.button_patient_home_red));
            }



        } catch (Exception ex) {

            Log.e(LOG_TAG,ex.getMessage());
        }

    }


}
