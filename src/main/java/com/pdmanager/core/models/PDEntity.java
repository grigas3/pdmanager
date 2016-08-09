package com.pdmanager.core.models;

import com.pdmanager.common.interfaces.IPDEntity;

/**
 * Created by george on 6/1/2016.
 */
public abstract class PDEntity implements IPDEntity {

    public String Id;
    public String PatientId;
    public String Organization;

    @Override
    public abstract String getPDType();

}
