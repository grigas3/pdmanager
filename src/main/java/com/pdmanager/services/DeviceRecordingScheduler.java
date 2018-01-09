package com.pdmanager.services;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.GyroMData;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataProcessor;

/**
 * Created by george on 22/1/2017.
 */

public class DeviceRecordingScheduler implements IDataProcessor {



    private boolean isEnabled=true;

    private int notMoving=0;

    private boolean notMovingPostPone=true;
    private int intervalsToAcquire=1;
    private static int baseInterval=1*60;


    private  int lastSampleTick=0;
    private int intervalToNoWearingRation=5;
    private  int notWearingIntervalsFound=0;

    //DEV BAND FREQ IS 31.5

    private long lastAcquisition=0;
    public DeviceRecordingScheduler()
    {


    }




    private long lastStop=0;
    private long plannedStop=0;

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.GYRO_DEVICE;
    }

    @Override
    public void addData(ISensorData data) {





        lastAcquisition = System.currentTimeMillis();

        int dataType = data.getDataType();
        if (dataType == DataTypes.GYRO_DEVICE && intervalsToAcquire > 0) {


            GyroMData gyro = (GyroMData) data;

            long t = gyro.getTicks();

            float x=gyro.getValue().getX();
            float y=gyro.getValue().getY();
            float z=gyro.getValue().getZ();



            if(Math.sqrt(x*x+y*y+z*z)<2)

                    notMoving++;

                } else {
                    notMoving = 0;
                    notMovingPostPone=false;

                }






        //Not Wearing band for 1 minute
        if(notMoving>31*60*60)
        {
            notMoving=0;
            notMovingPostPone=true;

        }


    }
    public boolean isResumed() {

        long t = System.currentTimeMillis();
        return (t - lastAcquisition) <1000;
    }
    public boolean shouldResume() {

        long t = System.currentTimeMillis();
        return (t - lastAcquisition) > plannedStop;
    }
    public long shouldPause()
    {


        if(notMovingPostPone)
            return 2*60*1000;


        return 0;
    }



    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled=enabled;
    }


    public void increaseInterval() {
        notMoving=0;
        notMovingPostPone=false;
        intervalsToAcquire++;
    }
}

