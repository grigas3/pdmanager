package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by George on 5/22/2015.
 */
public class GyroData extends SensorData<AccReading> {


    @Override
    public int getDataType() {
        return DataTypes.GYRO;
    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Double.toString(mValue.getX()) + "\t" + Double.toString(mValue.getY()) + "\t" + Double.toString(mValue.getZ());
    }
}
