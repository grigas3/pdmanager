package com.pdmanager.models;

import com.google.gson.annotations.SerializedName;
import com.pdmanager.common.interfaces.IPDEntity;

/**
 * Created by george on 6/1/2016.
 */
public abstract class PDEntity implements IPDEntity {

    @SerializedName("Id")
    public String Id;
    @SerializedName("PatientId")
    public String PatientId;
    @SerializedName("Organization")
    public String Organization;

    @Override
    public abstract String getPDType();

}
