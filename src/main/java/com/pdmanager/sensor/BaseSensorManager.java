package com.pdmanager.sensor;

import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.common.interfaces.ISensorManager;


public abstract class BaseSensorManager
        implements ISensorManager {

    private ISensorDataHandler mDataHandler;

    public BaseSensorManager() {
    }

    public void handleData(ISensorData data) {
        mDataHandler.handleData(data);

    }


    public void setDataHandler(ISensorDataHandler handler) {
        mDataHandler = handler;
    }


}