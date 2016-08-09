package com.pdmanager.common.interfaces;


import android.app.Activity;
import android.os.AsyncTask;

import com.pdmanager.common.data.ISensorData;

/**
 * Created by George Rigas on 14/5/2015.
 */
public interface ISensorManager {


    AsyncTask connect();

    AsyncTask disconnect();

    void setDataHandler(IDataHandler handler);

    boolean isConnected();


    void handleData(ISensorData data);

    void setContext(Activity context);

}
