package com.pdmanager.models;

/**
 * Created by George on 6/20/2016.
 */
public class MedTiming {


    public String Id;
    public long Time;
    public String Dose;

    public MedTiming(String id, String d, long t) {
        this.Id = id;
        Dose = d;
        Time = t;

    }

    public MedTiming() {

    }

}
