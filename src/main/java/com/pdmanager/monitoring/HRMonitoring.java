package com.pdmanager.monitoring;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.PedoData;
import com.pdmanager.common.data.STData;
import com.pdmanager.common.data.STReading;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.models.Observation;

import java.util.ArrayList;

/**
 * Created by george on 6/1/2016.
 */
public class HRMonitoring implements IDataProcessor {


    private static int MAXOBS = 10;
    private static int MAXHR = 30;
    private static int MAXST = 10;
    private final String mpID;
    private final String mOrgID;
    CommunicationManager mCommManager;
    private double lastPedo = -1;
    private int hrCount = 0;
    private double hrMean = 0;
    private int stCount = 0;
    private double stMean = 0;
    private ArrayList<Observation> tmpObservations;
    private boolean mEnabled = true;

    public HRMonitoring(CommunicationManager manager, String pid, String organizationID) {

        mpID = pid;
        mOrgID = organizationID;
        mCommManager = manager;
        tmpObservations = new ArrayList<Observation>();

    }

    @Override
    public boolean requiresData(int dataType) {


        return dataType == DataTypes.ST || dataType == DataTypes.PEDOMETER || dataType == DataTypes.HR;


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


                    Observation obs = new Observation(stMean / stCount, "STST", hrD.getTicks(), mpID);
                    tmpObservations.add(obs);
                    stCount = 0;
                    stMean = 0;

                }


            }


        } else if (dataType == DataTypes.PEDOMETER) {

            PedoData hrD = (PedoData) data;
            if (hrD != null) {
                double p = hrD.getValue();


                if (lastPedo >= 0) {


                    if (p - lastPedo > 0) {
                        Observation obs = new Observation(p - lastPedo, "STPEDO", hrD.getTicks(), mpID);
                        tmpObservations.add(obs);
                    }

                }

                lastPedo = p;


            }


        } else {


            HRData hrD = (HRData) data;
            if (hrD != null) {
                HRReading hr = hrD.getValue();

                if (hr.getQuality() == 1) {


                    hrMean += hr.getHR();
                    hrCount++;


                    if (hrCount >= MAXHR) {

                        Observation obs = new Observation(hrMean / hrCount, "STHR", hrD.getTicks(), mpID);
                        tmpObservations.add(obs);
                        hrCount = 0;
                        hrMean = 0;


                    }


                }
            }


        }


        if (tmpObservations.size() >= MAXOBS) {


            final ArrayList<Observation> tmpItems = new ArrayList<>();

            tmpItems.addAll(tmpObservations);
            tmpObservations.clear();

            SendData(tmpItems);

        /*    Thread t = new Thread(new Runnable() {
                @Override
                public void run() {



                }
            });
            t.start();

        */
        }


    }

    private void SendData(ArrayList<Observation> tmpItems) {

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
