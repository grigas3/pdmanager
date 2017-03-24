package com.pdmanager.monitoring;


import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.STData;
import com.pdmanager.common.data.STReading;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.models.Observation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Vital Signal Monitoring Class
 */
public class VitalMonitoring implements IDataProcessor {


    private static int MAXOBS = 10;
    private static int MAXHR = 30;
    private static int MAXST = 10;
    CommunicationManager mCommManager;
    private double lastPedo = -1;
    private int hrCount = 0;
    private double hrMean = 0;
    private int stCount = 0;
    private String patientIdentifier;
    private double stMean = 0;
    private long lastDataSend = 0;


    private ArrayList<Observation> tmpObservations;
    private boolean mEnabled = true;

    public VitalMonitoring(CommunicationManager manager, String pid) {

        patientIdentifier = pid;
        mCommManager = manager;
        tmpObservations = new ArrayList<Observation>();

    }

    private boolean sameDay()

    {

        Date date1 = new java.util.Date();
        Date date2 = new java.util.Date(lastDataSend);
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;

    }

    @Override
    public boolean requiresData(int dataType) {


        return dataType == DataTypes.ST || dataType == DataTypes.HR;


    }

    public void setPatient(String pid) {
        patientIdentifier = pid;
    }

    @Override
    public void addData(ISensorData data) {


        int dataType = data.getDataType();

        if (dataType == DataTypes.ST) {
            STData hrD = (STData) data;
            if (hrD != null) {
                STReading hr = hrD.getValue();


                stMean += hr.getST();
                stCount++;

                if (stCount >= MAXST) {

                    Observation obs = new Observation(stMean / stCount, "STST", hrD.getTicks(), patientIdentifier);
                    tmpObservations.add(obs);
                    stCount = 0;
                    stMean = 0;

                }


            }


        }
        /*else if(dataType== DataTypes.PEDOMETER)
        {

            PedoData hrD=(PedoData)data;
            if(hrD!=null) {
                double p = hrD.getValue();


                if(lastPedo>=0) {


                    if(p-lastPedo>0) {
                        Observation obs = new Observation(p - lastPedo, "003", hrD.getTicks(),patientIdentifier);
                        tmpObservations.add(obs);
                    }

                }

                lastPedo=p;



            }


        }
        */
        else {


            HRData hrD = (HRData) data;
            if (hrD != null) {
                HRReading hr = hrD.getValue();

                if (hr.getQuality() == 1) {


                    hrMean += hr.getHR();
                    hrCount++;


                    if (hrCount >= MAXHR) {

                        Observation obs = new Observation(hrMean / hrCount, "STHR", hrD.getTicks(), patientIdentifier);
                        tmpObservations.add(obs);
                        hrCount = 0;
                        hrMean = 0;


                    }


                }
            }


        }


        if (tmpObservations.size() >= MAXOBS) {


            final ArrayList<Observation> tmpItems = new ArrayList<>();


            if (!sameDay()) {
                Date date1 = new java.util.Date();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);

                Observation obs = new Observation(1, "DATA", cal1.getTimeInMillis(), patientIdentifier);
                lastDataSend = cal1.getTimeInMillis();
                tmpObservations.add(obs);

            }

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
