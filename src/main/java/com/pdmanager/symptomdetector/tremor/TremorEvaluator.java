package com.pdmanager.symptomdetector.tremor;


import android.util.Log;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroData;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.TremorData;
import com.pdmanager.common.data.UPDRS;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.posturedetector.Core.Signals.SignalCollection;

import java.util.Date;

/**
 * Created by george on 3/6/2016.
 * Tremor Evaluator
 */
public class TremorEvaluator implements IDataProcessor {



    private static final String TAG="TREMOREVALUATOR";

    //Private fields
    private SignalCollection accSignal;
    private SignalCollection gyroSignal;
    private TremorDetector detector;
    private TremorEstimator estimator;
    private TremorAccPreprocess accpreprocessor;
    private TremorGyroPreprocess gyropreprocessor;
    private NamedSignalCollection signalCollection;

    private ISensorDataHandler handler;
    private int accSampleCount = 0;
    private int gyroSampleCount = 0;
    private Object lock1 = new Object();


    /**
     * Constructor
     * @param pHander Data Handler (to pass the output of the classifier)
     * @param fs Sampling Frequency
     */
    public TremorEvaluator(ISensorDataHandler pHander, double fs) {

        handler = pHander;



    }

    /**
     * Init
     * @param fs Sampling frequency
     */
    private void init(double fs)
    {
        try {

            signalCollection = new NamedSignalCollection();
            accpreprocessor = new TremorAccPreprocess();
            gyropreprocessor = new TremorGyroPreprocess();
            detector = new TremorDetector();
            int w = (int) Math.floor(3 * fs);
            accSignal = new SignalCollection(3, w);
            gyroSignal = new SignalCollection(3, w);
            estimator = new TremorEstimator(w, fs);

        } catch (Exception ex) {

            Log.e(TAG,"Contructor ACC",ex.getCause());
        }


    }

    /**
     * Returns if Data Type is required by the model
     * @param dataType
     * @return Required or not
     */
    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.ACCELEROMETER || dataType == DataTypes.GYRO;
    }

    /**
     * Add Data
     * @param data The interface to get data
     */
    @Override
    public void addData(ISensorData data) {


        //In tremor we have two different buffers on for acc and one for gyro
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

    /**
     * Handle Accelerometer
     * @param data
     */
    private void handleAcc(ISensorData data) {

        AccData acc = (AccData) data;
        AccReading vals = acc.getValue();
        TremorData s = null;
        synchronized (lock1) {

            //If both buffers are full
            if (accSampleCount == accSignal.getSize() - 1&&gyroSampleCount == accSignal.getSize() - 1) {




                //TODO: Important. In the final implementation buffers should be copied to tmp buffers and all the processing should be performed in ASYNC TASK or Thread (see DataBuffer)

                    try {

                        //Preprocessing
                        accpreprocessor.process(accSignal, signalCollection);
                        gyropreprocessor.process(gyroSignal, signalCollection);
                        float updrs = 0;


                        //Tremor Detection
                        int res = detector.detectTremor(signalCollection);
                        if (res == 1) {


                            estimator.update(signalCollection);

                            updrs = (float) estimator.getUPDRS();

                        } else {

                            updrs = res;

                        }

                        ///Create a tremor observation
                        s = new TremorData();
                        Date d = new Date();
                        s.setTimestamp(d);
                        s.setTicks(d.getTime());
                        UPDRS u = new UPDRS();
                        u.setUPDRS(updrs);
                        s.setValue(u);

                    } catch (Exception ex) {


                        Log.e(TAG,"HANDLE ACC",ex.getCause());

                    }

                    //Zero sample count

                    gyroSampleCount = 0;
                    accSampleCount = 0;





            } else {



                if(accSampleCount < accSignal.getSize() ) {
                    //Otherwise add sample buffer
                    try {
                        accSignal.setValue(0, accSampleCount, vals.getX());
                        accSignal.setValue(1, accSampleCount, vals.getY());
                        accSignal.setValue(2, accSampleCount, vals.getZ());
                        accSampleCount++;


                    } catch (Exception ex) {
                        Log.e(TAG, "HANDLE ACC", ex.getCause());
                    }
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

                //Buffer full
                //Handle Acc will

            } else {


                try {
                    gyroSignal.setValue(0, gyroSampleCount, vals.getX() / (2 * Math.PI));
                    gyroSignal.setValue(1, gyroSampleCount, vals.getY() / (2 * Math.PI));
                    gyroSignal.setValue(2, gyroSampleCount, vals.getZ() / (2 * Math.PI));
                    gyroSampleCount++;


                } catch (Exception ex) {
                    Log.e(TAG,"HANDLE GYRO",ex.getCause());
                }

            }
        }


    }


}

