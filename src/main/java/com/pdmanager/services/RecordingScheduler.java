package com.pdmanager.services;

import android.util.Log;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.GyroData;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataProcessor;

/**
 * Created by george on 22/1/2017.
 */

public class RecordingScheduler implements IDataProcessor {


    private static final String TAG = "RECORDINGSCHEDULER";
    private static final int STOPREASON_SCHEDULEDPAUSE = 1;
    private static final int STOPREASON_NOWEARINGPAUSE = 2;
    private static int baseInterval = 1 * 60;
    private static int durationHours = 7;
    //MAIN BAND FREQ IS 62.5 ==125/2
    private static int samplesPerInterval = 5 * 125 * baseInterval / 2;
    private boolean isEnabled = true;
    private int samplesAcquired = 0;
    private int nonWearing = 0;
    private boolean postPone = false;
    private boolean nonWearingPostPone = true;
    private int intervalsToAcquire = 1;
    private int lastSampleTick = 0;
    private int intervalToNoWearingRation = 5;
    private int currentStopReason = STOPREASON_SCHEDULEDPAUSE;
    private int notWearingIntervalsFound = 0;
    private int notWearingPauseInterval = 5 * 60 * 1000;
    private long lastAcquisition = 0;
    private long lastStop = 0;
    private long plannedStop = 0;

    private boolean isPaused = false;


    public RecordingScheduler(int startHour, int endHour) {

        calculateSkip(startHour, endHour);
    }

    public static long calculateSkipTest(int startHour, int endHour) {
        RecordingScheduler r = new RecordingScheduler(startHour, endHour);
        return r.plannedStop;

    }

    //Planned stop after 5 minutes
    private void calculateSkip(int startHour, int endHour) {

        double r = endHour - startHour;
        double p = durationHours;

        double skipMinutes = (Math.max(0, -(5 * (p - r)) / p));
        //  skipIntervals=(int)Math.floor((endHour-startHour)/durationHours)-1;
        intervalsToAcquire = 1;


        //IN MILLISECONDS
        plannedStop = (long) (skipMinutes * baseInterval * 1000);

        Log.d(TAG, "PLANNED STOP =" + ((long) (skipMinutes * baseInterval)));
    }

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.HR || dataType == DataTypes.ACCELEROMETER || dataType == DataTypes.GYRO;
    }

    @Override
    public void addData(ISensorData data) {


        if (!isPaused) {
            lastAcquisition = System.currentTimeMillis();

            int dataType = data.getDataType();
            if (dataType == DataTypes.ACCELEROMETER && intervalsToAcquire > 0) {


                AccData accd = (AccData) data;
                long t = accd.getTicks();

                //IF there is a period when band is not receiving (band away from the phone
                //Then zero the total number of samples acquired in order to collect at least
                //5-mins of consequtive data
                if (t - lastAcquisition > 60 * 1000) {
                    samplesAcquired = 0;
                }
                lastAcquisition = t;


                samplesAcquired++;

            }

            if (samplesAcquired > samplesPerInterval) {

                samplesAcquired = 0;
                intervalsToAcquire--;

            }

            //if Finally use gyroscope for wearing uncomment

            if (dataType == DataTypes.GYRO) {

                GyroData gData = (GyroData) data;
                double e = Math.sqrt((gData.getValue().getX() * gData.getValue().getX()) + (gData.getValue().getY() * gData.getValue().getY()) + (gData.getValue().getZ() * gData.getValue().getZ()));

                if (e > 50) {
                    // Log.v(TAG,Double.toString(e));
                    nonWearing = 0;
                    nonWearingPostPone = false;
                }


            }


            if (dataType == DataTypes.HR) {
                HRData hrD = (HRData) data;
                if (hrD != null) {
                    HRReading hr = hrD.getValue();

                    if (hr.getQuality() == 0) {

                        //  Log.v(TAG,"LOW HR QUALITY SAMPLE");
                        //Not Wearing
                        //Maybe check also accelerometer?
                        nonWearing++;

                    } else {
                        //  Log.v(TAG,"HIGH HR QUALITY SAMPLE");
                        nonWearing = 0;
                        nonWearingPostPone = false;

                    }
                }
            }

            //Not Wearing band for ~55-60 seconds
            //Not exactly 60 in order to occur in first timer
            if (nonWearing > 55) {
                Log.d(TAG, "SET NON WEARING");
                nonWearing = 0;
                nonWearingPostPone = true;
                notWearingIntervalsFound++;
                if (notWearingIntervalsFound >= intervalToNoWearingRation) {
                    notWearingIntervalsFound = 0;
                    intervalsToAcquire++;

                }
            }

        }

    }

    public void resume() {
        isPaused = false;
        nonWearing = 0;
        nonWearingPostPone = false;
        intervalsToAcquire++;

    }

    public void pause() {
        isPaused = true;

    }

    public boolean isPaused() {

        return isPaused;
    }

    public boolean shouldResume() {

        long t = System.currentTimeMillis();
        long stopInterval = (currentStopReason == STOPREASON_SCHEDULEDPAUSE) ? plannedStop : notWearingPauseInterval;
        Log.v(TAG, "Last Acquisition " + lastAcquisition + "  Current time " + t + " Stop Interval " + stopInterval);

        return (t - lastAcquisition) > stopInterval;
    }


    public int shouldPause() {

        if (intervalsToAcquire <= 0) {
            currentStopReason = STOPREASON_SCHEDULEDPAUSE;
            return STOPREASON_SCHEDULEDPAUSE;
        } else if (nonWearingPostPone) {

            currentStopReason = STOPREASON_NOWEARINGPAUSE;
            return STOPREASON_NOWEARINGPAUSE;

        }


        return 0;
    }


    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


    public void increaseInterval() {
        nonWearing = 0;
        nonWearingPostPone = false;
        intervalsToAcquire++;
    }
}

