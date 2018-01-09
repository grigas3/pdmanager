package com.pdmanager.notification;

/**
 * Created by george on 28/1/2017.
 */

public class BandMessage {


    private final String mTitle;
    private final String mMessage;
    private final long mTimestamp;
    private final boolean mBandMessage;

    public BandMessage(String title, String message, long timestamp, boolean bandMes) {

        this.mTitle = title;
        this.mMessage = message;
        this.mTimestamp = timestamp;
        this.mBandMessage = bandMes;
    }


    public String getTitle()

    {
        return mTitle;
    }

    public String getMessage() {

        return mMessage;
    }

    public long getTimestamp() {

        return mTimestamp;
    }


    public boolean isMessage()

    {
        return mBandMessage;
    }
}
