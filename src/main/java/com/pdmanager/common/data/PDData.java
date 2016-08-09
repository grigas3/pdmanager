package com.pdmanager.common.data;

import com.pdmanager.common.DataTypes;

/**
 * Created by George on 5/22/2015.
 */
public class PDData extends SensorData<PDAnnotData> {
    @Override
    public int getDataType() {
        return DataTypes.ANNOTATION;
    }

    @Override
    public String getRawDisplay() {

        return getTime() + "\t" + Integer.toString(mValue.getLidUPDRS()) + "\t" + Integer.toString(mValue.getBradUPDRS()) + "\t" + Integer.toString(mValue.getTremorUPDRS());
    }
}
