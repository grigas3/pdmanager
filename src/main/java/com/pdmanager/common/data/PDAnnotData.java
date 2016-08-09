package com.pdmanager.common.data;

/**
 * Created by George on 5/22/2015.
 */
public class PDAnnotData {


    public int mLidUPDRS;
    public int mBradUPDRS;
    public int mTremorUPDRS;


    public PDAnnotData(int plUPDRS, int pbUPDRS, int ptUPDRS) {

        this.mLidUPDRS = plUPDRS;
        this.mBradUPDRS = pbUPDRS;
        this.mTremorUPDRS = ptUPDRS;


    }

    public int getLidUPDRS() {
        return mLidUPDRS;
    }

    public void setLidUPDRS(int pLidUPDRS) {
        this.mLidUPDRS = pLidUPDRS;
    }

    public int getBradUPDRS() {
        return mBradUPDRS;
    }

    public void setBradUPDRS(int pBradUPDRS) {
        this.mBradUPDRS = pBradUPDRS;
    }

    public int getTremorUPDRS() {
        return mTremorUPDRS;
    }

    public void setTremorUPDRS(int pTremorUPDRS) {
        this.mTremorUPDRS = pTremorUPDRS;
    }
}
