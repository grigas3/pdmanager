package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by George on 5/18/2015.
 */
public class PedoData extends SensorData<Long> {


    @Override
    public int getDataType() {
        return DataTypes.PEDOMETER;
    }

    @Override
    public String getDisplay() {


        return Long.toString(mValue);


    }


}