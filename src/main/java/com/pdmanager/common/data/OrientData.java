package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by admin on 2/7/2015.
 */
public class OrientData extends SensorData<OrientReading> {


    @Override
    public int getDataType() {
        return DataTypes.ORIENTATION;
    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Double.toString(mValue.getX()) + "\t" + Double.toString(mValue.getY()) + "\t" + Double.toString(mValue.getZ());
    }
}