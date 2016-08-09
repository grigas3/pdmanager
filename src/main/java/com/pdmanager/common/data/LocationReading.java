package com.pdmanager.common.data;

/**
 * Created by george on 27/11/2015.
 */
public class LocationReading {

    private double mLat;
    private double mLog;


    public LocationReading() {


    }


    public LocationReading(double pLat, double pLog) {
        this.mLat = pLat;
        this.mLog = pLog;


    }

    public double getLatitude() {
        return mLat;
    }

    public void setLatitude(double mX) {
        this.mLat = mX;
    }

    public double getLognitude() {
        return mLog;
    }

    public void setLognitude(double mY) {
        this.mLog = mY;


    }


}
