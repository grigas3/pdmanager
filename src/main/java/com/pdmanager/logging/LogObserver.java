package com.pdmanager.logging;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.util.Log;

/**
 * Created by george on 24/11/2015.
 */
public class LogObserver extends ContentObserver {

    private static String TAG = "LOADER";

    private Loader<Cursor> loader;

    public LogObserver(Handler handler, Loader<Cursor> loader) {
        super(handler);
        Log.e(TAG, ":::: CursorObserver");

        this.loader = loader;
    }

    @Override
    public boolean deliverSelfNotifications() {
        Log.e(TAG, ":::: deliverSelfNotifications");
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.e(TAG, ":::: onChange");

        if (null != loader) {
            loader.onContentChanged();
        }
        super.onChange(selfChange);
    }
}