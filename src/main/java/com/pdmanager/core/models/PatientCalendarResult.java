package com.pdmanager.core.models;

import java.util.List;

/**
 * Created by George on 6/4/2016.
 */
public class PatientCalendarResult {


    public List<Observation> data;
    public List<Observation> tremor;
    public List<Observation> off;
    public List<Observation> med_adh;
    public List<Observation> lid;
    private String Error;
    private boolean HasError;


}
