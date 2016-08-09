package com.pdmanager.common.data;


import com.pdmanager.common.DataTypes;

/**
 * Created by admin on 18/5/2015.
 */
public class STData extends SensorData<STReading> {

    @Override
    public int getDataType() {
        return DataTypes.ST;
    }

    @Override
    public String getDisplay() {


        return Float.toString(this.mValue.getST());


    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Float.toString(mValue.getST());
    }
}
