package com.pdmanager.core.symptomdetector.aggregators;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.TremorData;
import com.pdmanager.common.data.UPDRS;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.core.communication.CommunicationManager;

/**
 * Created by george on 6/6/2016.
 */
public class TremorAggregator extends BaseAggregator implements IDataProcessor {

    private double mamp = 0;
    private int tfound = 0;
    private int samples = 0;
    private int esamples = 0;


    public TremorAggregator(CommunicationManager manager, String pid) {
        super(manager, pid);


    }

    @Override
    public boolean requiresData(int dataType) {
        return dataType == DataTypes.TREMOR;
    }

    @Override
    public void addData(ISensorData data) {


        if (data.getDataType() == DataTypes.TREMOR) {

            TremorData d = (TremorData) data;
            UPDRS u = d.getValue();

            if (u.getUPDRS() > 0) {

                tfound++;
                mamp += u.getUPDRS();


            }
            if (u.getUPDRS() >= -0.1) {

                esamples++;
                //mamp+=u.getUPDRS();


            }
            samples++;

            if (samples == 20) {


                if (esamples > 3) {
                    double value = 0;
                    double r = 0;
                    if (mamp > 0 && tfound > 3) {
                        r = mamp / esamples;

                        value = 4 * tfound / samples;
                    }


                    SendObservation(r, "TREMOR_A", d.getTicks());
                    SendObservation(value, "TREMOR_C", d.getTicks());

                    esamples = 0;
                    tfound = 0;
                    mamp = 0;

                }
                samples = 0;


            }


        }


    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }
}

