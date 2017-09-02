package com.pdmanager.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

public class LoggerStat {

    @SerializedName("Value")
    private double Value;
    @SerializedName("CodeId")
    private String CodeId;

    public LoggerStat(double value, String code) {


        this.Value = value;
        this.CodeId = code;
    }

    public String getCode() {
        return CodeId;
    }

    public double getValue() {
        return Value;
    }
}
