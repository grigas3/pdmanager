package com.pdmanager.core.symptomdetector.aggregators;


import com.pdmanager.core.communication.CommunicationManager;
import com.pdmanager.core.models.Observation;

import java.util.ArrayList;

/**
 * Created by george on 6/6/2016.
 */
public abstract class BaseAggregator {

    private static int MAXOBS = 2;
    CommunicationManager mCommManager;
    private String patientIdentifier;
    private int hrCount = 0;

    private int stCount = 0;


    private ArrayList<Observation> tmpObservations;


    public BaseAggregator(CommunicationManager manager, String pid) {

        mCommManager = manager;
        patientIdentifier = pid;
        tmpObservations = new ArrayList<>();
    }

    public void setPatient(String pid) {
        patientIdentifier = pid;
    }


    protected void SendObservation(double value, String code, long ticks) {


        Observation obs = new Observation(value, code, ticks, patientIdentifier);
        tmpObservations.add(obs);

        if (tmpObservations.size() >= MAXOBS) {


            final ArrayList<Observation> tmpItems = new ArrayList<>();

            tmpItems.addAll(tmpObservations);
            tmpObservations.clear();

            SendData(tmpItems);


        }
    }

    private void SendData(ArrayList<Observation> tmpItems) {


        if (mCommManager != null)
            mCommManager.SendItems(tmpItems);
        //CommunicationHandler.getInstance().SendItems(tmpItems);


    }

}
