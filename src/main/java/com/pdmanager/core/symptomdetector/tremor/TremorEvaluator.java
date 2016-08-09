package com.pdmanager.core.symptomdetector.tremor;


import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroData;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.TremorData;
import com.pdmanager.common.data.UPDRS;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;

import java.util.Date;

/**
 * Created by george on 3/6/2016.
 */
public class TremorEvaluator implements IDataProcessor {


    private SignalCollection accSignal;
    private SignalCollection gyroSignal;
    private TremorDetector detector;
    private TremorEstimator estimator;
    private TremorAccPreprocess accpreprocessor;
    private TremorGyroPreprocess gyropreprocessor;
    private NamedSignalCollection signalCollection;

    private IDataHandler handler;
    private int accSampleCount = 0;
    private int gyroSampleCount = 0;
    private Object lock1 = new Object();


    public TremorEvaluator(IDataHandler phander, double fs) {
        try {

            signalCollection = new NamedSignalCollection();
            accpreprocessor = new TremorAccPreprocess();
            gyropreprocessor = new TremorGyroPreprocess();
            detector = new TremorDetector();
            int w = (int) Math.floor(3 * fs);
            accSignal = new SignalCollection(3, w);
            gyroSignal = new SignalCollection(3, w);
            estimator = new TremorEstimator(w, fs);
            handler = phander;

        } catch (Exception ex) {


        }


    }

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.ACCELEROMETER || dataType == DataTypes.GYRO;
    }

    @Override
    public void addData(ISensorData data) {

        if (data.getDataType() == DataTypes.ACCELEROMETER)
            handleAcc(data);
        else
            handleGyro(data);


    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    private void handleAcc(ISensorData data) {

        AccData acc = (AccData) data;
        AccReading vals = acc.getValue();
        TremorData s = null;
        synchronized (lock1) {
            if (accSampleCount == accSignal.getSize() - 1) {

                if (gyroSampleCount == accSignal.getSize() - 1) {


                    try {

                        accpreprocessor.process(accSignal, signalCollection);
                        gyropreprocessor.process(gyroSignal, signalCollection);
                        float updrs = 0;
                        int res = detector.detectTremor(signalCollection);
                        if (res == 1) {


                            estimator.update(signalCollection);

                            updrs = (float) estimator.getUPDRS();

                        } else {

                            updrs = res;

                        }


                        s = new TremorData();

                        Date d = new Date();
                        s.setTimestamp(d);
                        s.setTicks(d.getTime());
                        UPDRS u = new UPDRS();
                        u.setUPDRS(updrs);
                        s.setValue(u);

                    } catch (Exception ex) {


                    }


                    gyroSampleCount = 0;
                    accSampleCount = 0;


                }


            } else {


                try {
                    accSignal.setValue(0, accSampleCount, vals.getX());
                    accSignal.setValue(1, accSampleCount, vals.getY());
                    accSignal.setValue(2, accSampleCount, vals.getZ());
                    accSampleCount++;


                } catch (Exception ex) {

                }

            }


        }


        if (s != null)
            handler.handleData(s);


    }


    private void handleGyro(ISensorData data) {
        GyroData gyro = (GyroData) data;
        AccReading vals = gyro.getValue();

        synchronized (lock1) {
            if (gyroSampleCount == gyroSignal.getSize() - 1) {


            } else {


                try {
                    gyroSignal.setValue(0, gyroSampleCount, vals.getX() / (2 * Math.PI));
                    gyroSignal.setValue(1, gyroSampleCount, vals.getY() / (2 * Math.PI));
                    gyroSignal.setValue(2, gyroSampleCount, vals.getZ() / (2 * Math.PI));
                    gyroSampleCount++;


                } catch (Exception ex) {

                }

            }
        }


    }


}

