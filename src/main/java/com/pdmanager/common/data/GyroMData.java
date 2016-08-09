package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by admin on 16/5/2015.
 */
public class GyroMData extends SensorData<AccReading> {


    @Override
    public int getDataType() {
        return DataTypes.GYRO_DEVICE;
    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Double.toString(mValue.getX()) + "\t" + Double.toString(mValue.getY()) + "\t" + Double.toString(mValue.getZ());
    }
}
