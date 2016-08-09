package com.pdmanager.common.data;


import com.pdmanager.common.DataTypes;

/**
 * Created by george on 3/6/2016.
 */
public class LIDData extends SensorData<UPDRS> {

    @Override
    public int getDataType() {
        return DataTypes.LID;
    }

    @Override
    public String getDisplay() {


        if (mValue != null && mValue.getUPDRS() > 0) {
            return "Yes";

        } else
            return "No";
        //return Float.toString(this.mValue.getUPDRS());


    }

    @Override
    public String getRawDisplay() {


        return getTime() + "\t" + Float.toString(mValue.getUPDRS());
    }
}