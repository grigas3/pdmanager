package com.pdmanager.core.logging;

import android.database.DataSetObservable;

/**
 * Created by george on 4/9/2015.
 */
public class LogHandler extends DataSetObservable {

    private static LogHandler INSTANCE = new LogHandler();
    private ILogHandler mService;

    public static LogHandler getInstance() {
        return INSTANCE;
    }


    public void setHandler(ILogHandler client) {
        mService = client;
    }


    public void DeleteAll() {


    }


    public void Log(String event) {

        if (mService != null) {


            mService.ProcessLog("INFO", event);


        }

    }

    public void LogError(String event) {

        if (mService != null) {

            mService.ProcessLog("ERROR", event);

        }

    }


}
