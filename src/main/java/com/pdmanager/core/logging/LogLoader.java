package com.pdmanager.core.logging;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Created by george on 24/11/2015.
 */
public class LogLoader extends AsyncTaskLoader<Cursor> {

    private static String TAG = "LOADER";

    private Context context;
    private LogAdapter dbHelper;

    public LogLoader(Context context, LogAdapter dbHelper) {
        super(context);
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    protected void onStartLoading() {
        Log.e(TAG, ":::: onStartLoading");

        super.onStartLoading();
    }

    @Override
    public Cursor loadInBackground() {
        Log.e(TAG, ":::: loadInBackground");

        Cursor c = dbHelper.fetchLogs();

        return c;
    }

    @Override
    public void deliverResult(Cursor data) {
        Log.e(TAG, ":::: deliverResult");

        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        Log.e(TAG, ":::: onStopLoading");

        super.onStopLoading();
    }
}