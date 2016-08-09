package com.pdmanager.common.data;

/**
 * Created by admin on 16/5/2015.
 */
public class AccReading {

    private float mX;
    private float mY;
    private float mZ;


    public AccReading() {


    }


    public AccReading(float pX, float pY, float pZ) {
        this.mX = pX;
        this.mY = pY;
        this.mZ = pZ;

    }

    public float getX() {
        return mX;
    }

    public void setX(float mX) {
        this.mX = mX;
    }

    public float getY() {
        return mY;
    }

    public void setY(float mY) {
        this.mY = mY;
    }

    public float getZ() {
        return mZ;
    }

    public void setZ(float mZ) {
        this.mZ = mZ;
    }


}
