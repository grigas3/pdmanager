package com.pdmanager.models;

import java.util.List;

/**
 * Created by george on 25/1/2016.
 */
public class MedIntakeListResult {


    public List<MedicationIntake> intakes;
    public List<MedicationOrder> orders;
    private String Error;
    private boolean HasError;


}
