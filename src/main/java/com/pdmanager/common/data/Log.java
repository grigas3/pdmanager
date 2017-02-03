package com.pdmanager.common.data;

/**
 * Created by george on 17/11/2015.
 */
public class Log {


    private int mid;
    private long mTimestamp;
    private String mMessage;
    private String mType;
    private String mSource;

    public int getID() {
        return mid;

    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }


    public String getSource() {
        return mSource;
    }

    public void setSource(String mMessage) {
        this.mSource = mSource;
    }

}
