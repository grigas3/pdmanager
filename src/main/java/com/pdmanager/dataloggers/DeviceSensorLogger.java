package com.pdmanager.dataloggers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

import com.pdmanager.common.data.AccMCData;
import com.pdmanager.common.data.AccMData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroMData;
import com.pdmanager.common.data.OrientData;
import com.pdmanager.common.data.OrientReading;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.interfaces.IDataLogger;
import com.pdmanager.models.LoggerStat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

public class DeviceSensorLogger extends BaseDataLogger implements IDataLogger, SensorEventListener {

    private static final String TAG = "DeviceSensorLogger";
    private final float gforce = 9.806F;
    float[] trueAcceleration = new float[4];
    float[] R = new float[16];
    float[] RINV = new float[16];
    float[] relativacc = new float[4];
    float[] orientVals = new float[3];
    private SensorManager senSensorManager;
    private boolean hasAllDeviceSensors = false;
    private long devsamples = 0;
    private float[] acceleration = new float[4];
    private float[] linearAcceleration = new float[4];
    private float[] gravityMatrix = new float[4];
    private float[] magneticFieldMatrix = new float[4];
    private boolean linearAccelerationAcquired = false;
    private long linearAccelerationTime = 0;
    private long linearAccelerationTime2 = 0;
    private boolean receiverRegistered = false;
    private long lastBandAcquisition = -100000;
    private long lastUserNotificationCheck = -100000;
    private boolean bluetoothRestarted = false;
    private boolean mAccDeviceEnabled = false;
    private boolean hasLinearAccelerationSensor = false;
    private boolean hasGyroscopeSensor = false;

    public DeviceSensorLogger(ISensorDataHandler pHandler, Context pContext) {
        super(pHandler, pContext);

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void start() {


        registerDeviceSensors();
    }

    @Override
    public void pause() {

        unRegisterDeviceSensors();
    }

    @Override
    public void stop() {

        unRegisterDeviceSensors();
    }

    @Override
    public void resume() {
        registerDeviceSensors();
    }

    @Override
    public Collection<LoggerStat> getUsageStats() {

        ArrayList<LoggerStat> statArray = new ArrayList<LoggerStat>();
        LoggerStat obs2 = new LoggerStat(devsamples, "002");
        statArray.add(obs2);
        return statArray;

    }

    @Override
    public void resetStats() {

        devsamples = 0;
    }

    /**
     * Check if Band is connected
     *
     * @throws InterruptedException
     */
    private void checkMobileDeviceDataAcquired() throws InterruptedException {


        /// IF no data acquired for 30 seconds
        if (devsamples == 0) {

            LogError("Dev samples are zero");
            unRegisterDeviceSensors();

            ///wakeLock();
            //Ok give it some time
            // Initial 500 now give 1000 ms
            // Thread.sleep(1000);

            //Register Device Sensors
            registerDeviceSensors();

            //Release Lock
            /// releaseLock();


        }


    }


    /**
     * Register Device Motion Sensors
     */
    private void registerDeviceSensors() {

        ///Get Device Sensors
        senSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor senlinAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor senGravity = senSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor senGyro = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        List<Sensor> deviceSensors = senSensorManager.getSensorList(Sensor.TYPE_ALL);

        ///Check Device Sensors
        boolean hasMagneticSensor = false;
        boolean hasGravitySensor = false;
        boolean hasAccelerationSensor = false;

        for (int i = 0; i < deviceSensors.size(); i++) {
            if (deviceSensors.get(i).getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                hasMagneticSensor = true;

            }

            if (deviceSensors.get(i).getType() == Sensor.TYPE_GRAVITY) {
                hasGravitySensor = true;

            }

            if (deviceSensors.get(i).getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                hasLinearAccelerationSensor = true;

            }

            if (deviceSensors.get(i).getType() == Sensor.TYPE_GYROSCOPE) {
                hasGyroscopeSensor = true;

            }

            if (deviceSensors.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {

                hasAccelerationSensor = true;

            }


        }

        if (!hasMagneticSensor)
            LogWarn("NO TYPE_MAGNETIC_FIELD");
        if (!hasGravitySensor)
            LogWarn("NO TYPE_GRAVITY");

        if (!hasLinearAccelerationSensor)
            LogWarn("NO TYPE_LINEAR_ACCELERATION");
        if (!hasGyroscopeSensor)
            LogWarn("NO TYPE_GYROSCOPE");
        if (!hasAccelerationSensor)
            LogWarn("NO TYPE_ACCELEROMETER");

        if (hasLinearAccelerationSensor && hasGravitySensor && hasMagneticSensor) {

            hasAllDeviceSensors = true;
            LogInfo("Device Linear Accelerometer registered");
            LogInfo("Device Magnetic Sensor registered");
            LogInfo("Device Gravity registered");

            senSensorManager.registerListener(this, senlinAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            // senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            senSensorManager.registerListener(this, senMagnetic, SensorManager.SENSOR_DELAY_GAME);
            senSensorManager.registerListener(this, senGravity, SensorManager.SENSOR_DELAY_GAME);
        } else {
            hasAllDeviceSensors = false;

        }
        if (hasAccelerationSensor) {

            LogInfo("Device Accelerometer registered");
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        }

        if (hasGyroscopeSensor) {

            LogInfo("Device Gyroscope registered");
            senSensorManager.registerListener(this, senGyro, SensorManager.SENSOR_DELAY_GAME);

        }

    }


    /**
     * Unregister Device Sensors
     */
    private void unRegisterDeviceSensors() {
        try {

            LogInfo("Unregister device sensors");
            senSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
            senSensorManager.unregisterListener(this);
        } catch (Exception ex) {

            LogError("Unregister device sensors error");
            //  Util.handleException("Unregister device sensors",ex);
        }

    }

    /***
     * On Sensor Changed Method
     * For Mobile device sensors
     *
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            ////Push Orientation Data
            GyroMData accData = new GyroMData();
            accData.setValue(new AccReading(sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]));
            accData.setTimestamp(new Date());
            accData.setTicks(sensorEvent.timestamp);

            handleData(accData);
            devsamples++;

        }
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            AccMData accData = new AccMData();
            accData.setValue(new AccReading(sensorEvent.values[0] / gforce, sensorEvent.values[1] / gforce, sensorEvent.values[2] / gforce));
            accData.setTimestamp(new Date());
            accData.setTicks(sensorEvent.timestamp);

            handleData(accData);

            //Accelerometer is disabled when all sensors available
            //Therefore we added also in linear accelerometer
            devsamples++;


        }

        if (hasAllDeviceSensors) {

            if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

                linearAcceleration[0] = sensorEvent.values[0] / gforce;
                linearAcceleration[1] = sensorEvent.values[1] / gforce;
                linearAcceleration[2] = sensorEvent.values[2] / gforce;
                linearAccelerationTime = sensorEvent.timestamp;
                linearAccelerationTime2 = System.currentTimeMillis();
                linearAccelerationAcquired = true;
                // devsamples++;

            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
                gravityMatrix[0] = sensorEvent.values[0];
                gravityMatrix[1] = sensorEvent.values[1];
                gravityMatrix[2] = sensorEvent.values[2];
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                magneticFieldMatrix[0] = sensorEvent.values[0];
                magneticFieldMatrix[1] = sensorEvent.values[1];
                magneticFieldMatrix[2] = sensorEvent.values[2];

            }

            if (linearAccelerationAcquired) {

                linearAccelerationAcquired = false;

                boolean success = SensorManager.getRotationMatrix(R, RINV, gravityMatrix, magneticFieldMatrix);

                if (success) {

                    SensorManager.getOrientation(R, orientVals);
                    float azimuth = (float) Math.toDegrees(orientVals[0]);
                    float pitch = (float) Math.toDegrees(orientVals[1]);
                    float roll = (float) Math.toDegrees(orientVals[2]);

                    ////Push Orientation Data
                    OrientData orData = new OrientData();
                    orData.setValue(new OrientReading(azimuth, pitch, roll));
                    orData.setTimestamp(new Date());
                    orData.setTicks(linearAccelerationTime);

                    handleData(orData);

                    relativacc[0] = linearAcceleration[0];
                    relativacc[1] = linearAcceleration[1];
                    relativacc[2] = linearAcceleration[2];
                    relativacc[3] = 0;

                    Matrix.invertM(RINV, 0, R, 0);
                    Matrix.multiplyMV(trueAcceleration, 0, RINV, 0, relativacc, 0);

                    ////Push Data
                    AccMCData accData = new AccMCData();
                    accData.setValue(new AccReading(trueAcceleration[0], trueAcceleration[1], trueAcceleration[2]));
                    accData.setTimestamp(new Date());
                    accData.setTicks(linearAccelerationTime);

                    handleData(accData);
                } else {

                    LogDebug("onSensorChanged .");
                }


            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

        //LogDebug("Accuracy Changed  to"+i);

    }

}
