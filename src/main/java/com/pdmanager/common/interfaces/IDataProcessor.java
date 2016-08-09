package com.pdmanager.common.interfaces;

import com.pdmanager.common.data.ISensorData;

/**
 * Created by George Rigas on 16/5/2015.
 */
public interface IDataProcessor {


    boolean requiresData(int dataType);

    void addData(ISensorData data);


    boolean isEnabled();

    void setEnabled(boolean enabled);


}
