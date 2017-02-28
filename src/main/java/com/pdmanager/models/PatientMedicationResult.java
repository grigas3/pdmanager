package com.pdmanager.models;

import java.util.List;

/**
 * Created by george on 16/1/2017.
 */

public class PatientMedicationResult
    {


        public List<MedicationOrder> orders;
        public List<MedicationIntake> intakes;
        private boolean error;

    public void setError(boolean b) {

        error = b;
    }

    public boolean hasError() {

        return error;
    }



}
