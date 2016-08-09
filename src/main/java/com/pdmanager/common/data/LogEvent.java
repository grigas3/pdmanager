package com.pdmanager.common.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by george on 4/9/2015.
 */
public class LogEvent {

    private Date mTimestamp;
    private String mEvent;
    private int mLogType;

    public LogEvent(int pLogType, Date pTimestamp, String pEvent) {

        mTimestamp = pTimestamp;
        mEvent = pEvent;
        mLogType = pLogType;

    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public String getEvent() {
        return mEvent;
    }

    public int getLogType() {
        return mLogType;
    }


    @Override
    public String toString() {

        SimpleDateFormat dformat = new SimpleDateFormat("HH:mm:ss:SSS");

        return dformat.format(mTimestamp) + "  " + mEvent;
        //return super.toString();
    }
}
