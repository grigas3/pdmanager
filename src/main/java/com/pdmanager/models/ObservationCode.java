package com.pdmanager.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by george on 25/1/2016.
 */
public class ObservationCode {

    @SerializedName("Title")
    public String Title;
    @SerializedName("Code")
    public String Code;
    @SerializedName("Unit")
    public String Unit;

    public ObservationCode() {

    }

    public ObservationCode(String t, String c, String u) {
        Title = t;
        Code = c;
        Unit = u;

    }
}
