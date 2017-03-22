package com.pdmanager.services;

import com.dropbox.core.DbxOAuth1AccessToken;
import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataProcessor;

import java.util.Random;

/**
 * Created by george on 22/1/2017.
 */

public class RecordingScheduler implements IDataProcessor {



    private boolean isEnabled=true;
    private int samplesAcquired=0;
    private int nonWearing=0;
    private boolean postPone=false;
    private boolean nonWearingPostPone=true;
    private int intervalsToAcquire=1;
    private static int baseInterval=1*60;
    private static int durationHours=6;

    private  int lastSampleTick=0;
    private int intervalToNoWearingRation=5;
    private  int notWearingIntervalsFound=0;

    //MAIN BAND FREQ IS 62.5 ==125/2
    private static int samplesPerInterval=5*125*baseInterval/2;
    private long lastAcquisition=0;
    public RecordingScheduler(int startHour,int endHour)
    {

        calculateSkip(startHour,endHour);
    }


    public static long calculateSkipTest(int startHour,int endHour)
    {
        RecordingScheduler r=new RecordingScheduler(startHour,endHour);
        return r.plannedStop;

    }


    //Planned stop after 5 minutes
    private void calculateSkip(int startHour,int endHour)
    {

        double r=endHour-startHour;
        double p=durationHours;

        double skipMinutes=(Math.max(0,-(5*(p - r))/p));
        //  skipIntervals=(int)Math.floor((endHour-startHour)/durationHours)-1;
        intervalsToAcquire=1;

        //IN MILLISECONDS
        plannedStop=(long)(skipMinutes*baseInterval*1000);
    }


    private long lastStop=0;
    private long plannedStop=0;

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.HR || dataType == DataTypes.ACCELEROMETER;
    }

    @Override
    public void addData(ISensorData data) {





        lastAcquisition = System.currentTimeMillis();

        int dataType = data.getDataType();
        if(dataType==DataTypes.ACCELEROMETER&&intervalsToAcquire>0) {


            AccData accd = (AccData) data;
            long t = accd.getTicks();

            //IF there is a period when band is not receiving (band away from the phone
            //Then zero the total number of samples acquired in order to collect at least
            //5-mins of consequtive data
            if(t-lastAcquisition>60*1000)
            {
                samplesAcquired=0;
            }
            lastAcquisition=t;


            samplesAcquired++;

        }

        if(samplesAcquired>samplesPerInterval)
        {

            samplesAcquired=0;
            intervalsToAcquire--;

        }



        if(dataType==DataTypes.HR) {
            HRData hrD = (HRData) data;
            if (hrD != null) {
                HRReading hr = hrD.getValue();

                if (hr.getQuality() == 0) {


                    nonWearing++;

                } else {
                    nonWearing = 0;
                    nonWearingPostPone=false;

                }
            }
        }



        //Not Wearing band for 60 seconds
        if(nonWearing>60)
        {
            nonWearing=0;
            nonWearingPostPone=true;
            notWearingIntervalsFound++;
            if(notWearingIntervalsFound>=intervalToNoWearingRation) {
                notWearingIntervalsFound=0;
                intervalsToAcquire++;

            }
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



        if(intervalsToAcquire<=0)
        {
            return plannedStop;
        }
        if(nonWearingPostPone)
            return 60*1000;


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
        nonWearing=0;
        nonWearingPostPone=false;
        intervalsToAcquire++;
    }
}

