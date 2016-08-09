package com.pdmanager.common.data;


import com.pdmanager.common.DataTypes;

/**
 * Created by george on 3/6/2016.
 */
public class TremorData extends SensorData<UPDRS> {

    @Override
    public int getDataType() {
        return DataTypes.TREMOR;
    }

    @Override
    public String getDisplay() {


        if (this.mValue.getUPDRS() >= -0.1)
            return Float.toString(Math.round(this.mValue.getUPDRS() * 10.F) / 10.0F);
        else {

            return "?";
        }


    }

    @Override
    public String getRawDisplay() {


        return getTime() + "\t" + Float.toString(mValue.getUPDRS());
    }
}