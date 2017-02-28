package com.pdmanager.models;


/**
 * Created by george on 2/3/2016.
 */
public class PingEntity extends PDEntity {


    public String pid;

    public PingEntity(String p) {

        pid = p;
    }

    @Override
    public String getPDType() {
        return "PingEntity";
    }
}
