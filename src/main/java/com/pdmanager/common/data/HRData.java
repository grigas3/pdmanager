package com.pdmanager.common.data;


import com.pdmanager.common.DataTypes;

/**
 * Created by admin on 18/5/2015.
 */
public class HRData extends SensorData<HRReading> {

    @Override
    public int getDataType() {
        return DataTypes.HR;
    }

    @Override
    public String getDisplay() {


        return Integer.toString(this.mValue.getHR());


    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Integer.toString(mValue.getHR()) + "\t" + Integer.toString(mValue.getQuality());
    }
}
