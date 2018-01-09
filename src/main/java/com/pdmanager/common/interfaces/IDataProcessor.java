package com.pdmanager.common.interfaces;

import com.pdmanager.common.data.ISensorData;

/**
 * Created by George Rigas on 16/5/2015.
 */
public interface IDataProcessor {


    /**
     * Requires data
     * The main data handler when new data are received checks whether a data processor "needs" those data
     *
     * @param dataType Data Type defined in com.pdmanager.common.DataTypes
     * @return
     */
    boolean requiresData(int dataType);

    /**
     * Add data
     *
     * @param data
     */
    void addData(ISensorData data);


    boolean isEnabled();

    void setEnabled(boolean enabled);


}
