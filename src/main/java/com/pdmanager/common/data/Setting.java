package com.pdmanager.common.data;

/**
 * Created by george on 17/11/2015.
 */
public class Setting {


    private int mid;
    private long mTimestamp;
    private String mValue;
    private String mType;

    public int getID() {
        return mid;

    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }


}
