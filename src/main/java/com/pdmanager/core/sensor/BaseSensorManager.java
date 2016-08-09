package com.pdmanager.core.sensor;

import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.common.interfaces.ISensorManager;


public abstract class BaseSensorManager
        implements ISensorManager {

    private IDataHandler mDataHandler;

    public BaseSensorManager() {
    }

    public void handleData(ISensorData data) {
        mDataHandler.handleData(data);

    }


    public void setDataHandler(IDataHandler handler) {
        mDataHandler = handler;
    }


}