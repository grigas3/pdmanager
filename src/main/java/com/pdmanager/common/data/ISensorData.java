package com.pdmanager.common.data;

/**
 * Created by George Rigas on 16/5/2015.
 * All in-app data comming from sensor implement ISensorData
 */
public interface ISensorData {


    /**
     * Data Type (See DataTypes)
     *
     * @return
     */
    int getDataType();


    /**
     * Display for logging
     *
     * @return
     */
    String getDisplay();

    /**
     * Raw Display  (not used)
     *
     * @return
     */
    String getRawDisplay();

}
