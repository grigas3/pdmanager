package com.pdmanager.common.interfaces;


import android.app.Activity;
import android.os.AsyncTask;

import com.pdmanager.common.data.ISensorData;

/**
 * Created by George Rigas on 14/5/2015.
 */
public interface ISensorManager {


    /**
     * Connect
     *
     * @return
     */
    AsyncTask connect();

    /**
     * Disconnect
     *
     * @return
     */
    AsyncTask disconnect();

    /**
     * Set Data Handler
     *
     * @param handler
     */
    void setDataHandler(ISensorDataHandler handler);


    /**
     * Is Connected
     *
     * @return
     */
    boolean isConnected();


    /**
     * Handle Data. Called whenever new data are available
     *
     * @param data
     */
    void handleData(ISensorData data);

    /**
     * Set Context
     *
     * @param context
     */
    void setContext(Activity context);

}
