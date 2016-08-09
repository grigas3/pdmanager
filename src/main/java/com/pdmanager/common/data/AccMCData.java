package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by george on 4/9/2015.
 */
public class AccMCData extends SensorData<AccReading> {


    @Override
    public int getDataType() {
        return DataTypes.CORRECTED_ACCELEROMETER_DEVICE;
    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Double.toString(mValue.getX()) + "\t" + Double.toString(mValue.getY()) + "\t" + Double.toString(mValue.getZ());
    }
}
