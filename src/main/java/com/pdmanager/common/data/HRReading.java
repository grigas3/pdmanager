package com.pdmanager.common.data;

/**
 * Created by admin on 18/5/2015.
 */
public class HRReading {

    private int mHR;
    private int mQuality;


    public HRReading() {


    }


    public HRReading(int pHR, int pQuality) {
        this.mHR = pHR;

        this.mQuality = pQuality;

    }

    public int getHR() {
        return mHR;
    }

    public void setHR(int pHR) {
        this.mHR = pHR;
    }

    public int getQuality() {
        return mQuality;
    }

    public void setQuality(int pQ) {
        this.mQuality = pQ;
    }


}
