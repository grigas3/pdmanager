package com.pdmanager.core.monitoring;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.PostureData;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.core.communication.CommunicationManager;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.posturedetector.Core.PostureTypes;

import java.util.ArrayList;

/**
 * Vital Signal Monitoring Class
 */
public class ActivityMonitoring implements IDataProcessor {


    private static int MAXOBS = 10;
    private static int MAXST = 20;
    CommunicationManager mCommManager;
    private double lastPedo = -1;
    private int hrCount = 0;
    private double hrMean = 0;
    private int stCount = 0;
    private String patientIdentifier;
    private double stMean = 0;


    private ArrayList<Observation> tmpObservations;
    private boolean mEnabled = true;

    public ActivityMonitoring(CommunicationManager manager, String pid) {

        patientIdentifier = pid;
        mCommManager = manager;
        tmpObservations = new ArrayList<Observation>();

    }

    @Override
    public boolean requiresData(int dataType) {


        return dataType == DataTypes.ACTIVITY;


    }

    public void setPatient(String pid) {
        patientIdentifier = pid;
    }

    @Override
    public void addData(ISensorData data) {


        int dataType = data.getDataType();

        if (dataType == DataTypes.ACTIVITY) {
            PostureData hrD = (PostureData) data;
            if (hrD != null) {
                int hr = hrD.getValue();


                if (hr != PostureTypes.SITTING_LYING) {
                    stMean++;
                }


                stCount++;

                if (stCount >= MAXST) {


                    ///Active observation
                    Observation obs = new Observation(stMean / stCount, "ACT", hrD.getTicks(), patientIdentifier);
                    tmpObservations.add(obs);
                    stCount = 0;
                    stMean = 0;

                }


            }


        }


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


    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;

    }
}
