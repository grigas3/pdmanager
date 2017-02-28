package com.pdmanager.viewmodels;

/**
 * Created by George on 6/22/2016.
 */
public class PDTestResult {
    public String mCode;
    public double mValue;


    public PDTestResult(String c, double v) {
        this.mCode = c;
        this.mValue = v;
    }

    public PDTestResult() {

    }

    public String getCode() {
        return mCode;
    }

    public double getValue() {
        return mValue;
    }
}
