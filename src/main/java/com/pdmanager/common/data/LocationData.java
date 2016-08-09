package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by george on 27/11/2015.
 */
public class LocationData extends SensorData<LocationReading> {


    public int getDataType() {
        return DataTypes.GPS;
    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Double.toString(mValue.getLatitude()) + "\t" + Double.toString(mValue.getLognitude());
    }

}
