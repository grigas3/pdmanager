package com.pdmanager.common.interfaces;

import com.pdmanager.common.data.ISensorData;

/**
 * Created by George Rigas on 14/5/2015.
 * Sensor Data Handler is implemented by entities that are
 * going to handle sensor data
 * Main Recording service has a registerData
 */
public interface ISensorDataHandler {

    /**
     * Handle Data
     *
     * @param data Sensor Data
     */
    void handleData(ISensorData data);

}
