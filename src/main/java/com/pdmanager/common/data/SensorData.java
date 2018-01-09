package com.pdmanager.common.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by George Rigas on 14/5/2015.
 * Base Implementation of ISensorData interface
 */



public abstract class SensorData<T> extends BaseSensorData implements ISensorData {


    protected T mValue;
    protected Date mTimestamp;
    protected long mTicks;

    public abstract int getDataType();


    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        this.mValue = value;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.mTimestamp = timestamp;


    }

    public long getTicks() {
        return mTicks;
    }

    public void setTicks(long ticks) {
        this.mTicks = ticks;
    }

    public void setTimestampFromLong(long timestamp) {
        this.mTimestamp = new Date(timestamp);
    }


    public String getDisplay() {

        return mValue.toString();

    }

    public String getRawDisplay() {

        return mValue.toString();

    }


    protected String getTime() {

        SimpleDateFormat dformat = new SimpleDateFormat("HH:mm:ss:SSS");

        return dformat.format(mTimestamp);

    }


}
