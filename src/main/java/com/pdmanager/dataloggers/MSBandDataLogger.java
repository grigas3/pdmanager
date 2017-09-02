package com.pdmanager.dataloggers;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandAccelerometerEvent;
import com.microsoft.band.sensors.BandAccelerometerEventListener;
import com.microsoft.band.sensors.BandGyroscopeEvent;
import com.microsoft.band.sensors.BandGyroscopeEventListener;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.SampleRate;
import com.pdmanager.common.ConnectionResult;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroData;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.PedoData;
import com.pdmanager.common.data.STData;
import com.pdmanager.common.data.STReading;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.helpers.BluetoothHelper;
import com.pdmanager.interfaces.IDataLogger;
import com.pdmanager.logging.LogCodes;
import com.pdmanager.models.LoggerStat;
import com.pdmanager.notification.BandNotificationTask;
import com.pdmanager.sensor.IHeartRateAccessProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.pdmanager.logging.LogCodes.FATAL_BAND_CONNECTION;
import static com.pdmanager.logging.LogCodes.FATAL_START_TIMER;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

public class MSBandDataLogger extends BaseDataLogger implements IDataLogger {
    private static final String TAG = "MSBandDataLogger";
    //Try to reconnect at least after 10 minutes
    private static final long bandReconnectAttemptInterval = 10 * 60 * 1000;
    //Update Usage StatisticsInterval
    private static final long updateUsageStatisticsInterval = 60 * 30 * 1000;
    //Minimum interval regarding Band Data Acquisitions
    private static final long minimumNoBandDataInterval = 60 * 2 * 1000;
    private static final long minMainTimerInterval = 60 * 1000;
    ///Since most timing is performed in second accuracy
    /// Add a margin of seconds to perform operations (60+5)
    private static final long mainTimerInterval = 65 * 1000;
    //Wait interval in seconds used for connecting/disconnecting from Band
    private static final long bandClientWaitIntervalInSeconds = 5;
    //Band Sampling rate
    private final SampleRate bandRate = SampleRate.MS16;
    //Available band client
    private final Semaphore available = new Semaphore(1, true);
    private Handler mainTaskHandler = new Handler();
    private long lastTimerCheck = 0;
    private boolean mBandAcquiring = false;
    private long lastBandAcquisition = 0;
    private long lastBandConnectionTry = 0;
    private IHeartRateAccessProvider heartRateAccessProvider;
    private boolean bandSensorsRegistered = false;
    private boolean mHeartRateEnabled = true;
    private boolean mAccBandEnabled = true;
    private boolean mGyroBandEnabled = true;
    private boolean mPedoEnabled = true;
    private boolean mSTEnabled = true;
    private boolean bluetoothRestarted = false;
    private boolean useRestartBluetoothAdaptorPolicy = true;
    private boolean mbandConnected = false;
    //Band Client
    private BandClient mClient;
    private Timer timer = null;


    private boolean isPaused = false;

    private long hrsamples = 0;
    private long bandsamples = 0;

    private volatile boolean mIsHandlerScheduled;
    private AtomicReference<BandHeartRateEvent> mPendingHeartRateEvent = new AtomicReference<BandHeartRateEvent>();
    private AtomicReference<BandPedometerEvent> mPendingPedometerEvent = new AtomicReference<BandPedometerEvent>();
    private AtomicReference<BandSkinTemperatureEvent> mPendingSTEvent = new AtomicReference<BandSkinTemperatureEvent>();
    private AtomicReference<BandGyroscopeEvent> mPendingGyroEvent = new AtomicReference<BandGyroscopeEvent>();
    private AtomicReference<BandAccelerometerEvent> mPendingAccelerometerEvent = new AtomicReference<BandAccelerometerEvent>();
    //region Event Listeners
    //region event listeners
    private BandSkinTemperatureEventListener mSTEventListener = new BandSkinTemperatureEventListener() {
        @Override
        public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
            mPendingSTEvent.set(event);
            scheduleSensorHandler();
        }
    };
    private BandHeartRateEventListener mHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            mPendingHeartRateEvent.set(event);
            scheduleSensorHandler();
        }
    };
    //endregion
    private BandPedometerEventListener mPedometerEventListener = new BandPedometerEventListener() {
        @Override
        public void onBandPedometerChanged(final BandPedometerEvent event) {
            mPendingPedometerEvent.set(event);
            scheduleSensorHandler();
        }
    };
    private BandGyroscopeEventListener mGyroEventListener = new BandGyroscopeEventListener() {
        @Override
        public void onBandGyroscopeChanged(final BandGyroscopeEvent event) {

            mPendingGyroEvent.set(event);
            scheduleSensorHandler();
        }
    };
    //region registers
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            mPendingAccelerometerEvent.set(event);
            scheduleSensorHandler();
        }
    };

    /***
     * Constructor
     * @param pContext
     */
    public MSBandDataLogger(ISensorDataHandler handler, Context pContext) {
        super(handler, pContext);


    }

    @Override
    protected String getTag() {

        return TAG;
    }

    //Start Data Processor
    @Override
    public void start() {


        startTimer();

        long unixTime = System.currentTimeMillis();
        lastBandAcquisition = unixTime;
        lastBandConnectionTry = unixTime;

        try {
            connectToBand();


        } catch (Exception ex) {

            LogError(ex.getMessage());
        }


    }

    @Override
    public void pause() {

        //Pause does not stop timer but rather stops the internal timer check
        isPaused = true;

        //Finally disconnect band since that can fail
        try {
            disconnectBand();
            LogInfo("Unregister Band Sensors");
        } catch (Exception ex) {

            LogError("Disconnection error");

        }

    }

    @Override
    public void stop() {
        stopTimer();
        //Finally disconnect band since that can fail
        try {
            disconnectBand();

        } catch (Exception ex) {

            LogError("Disconnection error");

        }

    }

    @Override
    public void resume() {

        //Pause does not stop timer but rather stops the internal timer check
        isPaused = false;

        long unixTime = System.currentTimeMillis();
        lastBandAcquisition = unixTime;
        lastBandConnectionTry = unixTime;

        try {
            connectToBand();


        } catch (Exception ex) {

            LogError(ex.getMessage());
        }
    }

    @Override
    public Collection<LoggerStat> getUsageStats() {
        ArrayList<LoggerStat> statArray = new ArrayList<LoggerStat>();
        LoggerStat obs1 = new LoggerStat(bandsamples, "001");
        LoggerStat obs2 = new LoggerStat(hrsamples, "003");
        statArray.add(obs1);
        statArray.add(obs2);
        return statArray;
    }

    @Override
    public void resetStats() {

        bandsamples = 0;
        hrsamples = 0;
    }
//endregion

    public boolean isConnection() {

        return mClient != null && mClient.isConnected();
    }

    //
    // Queue an action to run on the UI thread to process sensor updates. Make sure
    // that we have at most one callback queued for the UI thread.
    //
    private synchronized void scheduleSensorHandler() {
        if (mIsHandlerScheduled) {
            return;
        }
        handlePendingSensorReports();
    }

    /**
     * This method is scheduled to run on the UI thread after a sensor event has been received.
     * We clear our "is scheduled" flag and then update the UI controls for any new sensor
     * events (which we also clear).
     */
    private void handlePendingSensorReports() {
        // Because we clear this flag before reading the sensor events, it's possible that a
        // newly-generated event will schedule the handler to run unnecessarily. This is
        // harmless. If we update the flag after checking the sensors, we could fail to call
        // the handler at all.
        mIsHandlerScheduled = false;
        //String band = BasicsFragment.getPairedDevice();

        BandHeartRateEvent heartRateEvent = mPendingHeartRateEvent.getAndSet(null);
        if (heartRateEvent != null) {

            String quality = heartRateEvent.getQuality().toString();
            int hrQuality = 0;
            if (quality == "LOCKED") {

                hrQuality = 1;

                hrsamples++;
            }

            HRData hrData = new HRData();
            HRReading reading = new HRReading(heartRateEvent.getHeartRate(), hrQuality);
            hrData.setValue(reading);
            //hrData.setTimestamp(new Date());
            hrData.setTicks(heartRateEvent.getTimestamp());
            handleData(hrData);


        }

        BandSkinTemperatureEvent stevent = mPendingSTEvent.getAndSet(null);
        //BandPedometerEvent pedometerEvent = mPendingPedometerEvent.getAndSet(null);

        if (stevent != null) {
            STData pData = new STData();
            STReading reading = new STReading(stevent.getTemperature());
            //pData.setTimestamp(new Date());

            pData.setValue(reading);
            pData.setTicks(stevent.getTimestamp());

            handleData(pData);
        }

        BandPedometerEvent pedometerEvent = mPendingPedometerEvent.getAndSet(null);

        if (pedometerEvent != null) {
            PedoData pData = new PedoData();
            pData.setValue(pedometerEvent.getTotalSteps());
            pData.setTimestamp(new Date());
            pData.setTicks(pedometerEvent.getTimestamp());

            handleData(pData);
        }
        BandGyroscopeEvent gyroEvent = mPendingGyroEvent.getAndSet(null);

        if (gyroEvent != null) {
            GyroData pData = new GyroData();
            pData.setValue(new AccReading(gyroEvent.getAngularVelocityX(), gyroEvent.getAngularVelocityY(), gyroEvent.getAngularVelocityZ()));
            pData.setTimestamp(new Date());

            pData.setTicks(gyroEvent.getTimestamp());
            handleData(pData);
        }

        BandAccelerometerEvent accEvent = mPendingAccelerometerEvent.getAndSet(null);
        if (accEvent != null) {
            AccData accData = new AccData();
            accData.setValue(new AccReading(accEvent.getAccelerationX(), accEvent.getAccelerationY(), accEvent.getAccelerationZ()));
            accData.setTimestamp(new Date());
            accData.setTicks(accEvent.getTimestamp());
            handleData(accData);
            bandsamples++;
        }

        lastBandAcquisition = System.currentTimeMillis();
    }

    /***
     * Check Band Connection status
     * and more specifically if data are received the last minute
     * @throws InterruptedException
     */
    private void checkBandConnectionStatus() throws InterruptedException {


        long unixTime = System.currentTimeMillis();

        if (!isPaused) {

            if (unixTime - lastBandAcquisition > minimumNoBandDataInterval) {

                Log.v(TAG, "Band not receiving data");

                if (mBandAcquiring) {
                    LogWarn("Band seems not to receive data", 2);
                    //LogWarn("Band has at least 30 seconds to acquire");

                }


                if (mClient != null) {
                    if (mClient.getConnectionState() == ConnectionState.DISPOSED) {
                        LogInfo("Connection Disposed");
                    }

                    if (mClient.getConnectionState() == ConnectionState.CONNECTED) {

                        if (!bandSensorsRegistered) {

                            try {

                                if (!lockBandClient()) {
                                    LogError("Error locking Unlock Band client");
                                    return;
                                }
                                initSensorListeners();

                            } catch (Exception ex) {

                                LogError("ALERT REGISTER LISTENERS", ex.getCause());
                            } finally {

                                try {
                                    unlockBandClient();
                                } catch (InterruptedException ex) {

                                    LogError("Error Unlocking Band client", ex.getCause());

                                }

                            }
                            //registerBandSensors(senSensorManager);

                        }
                        LogInfo("Connection CONNECTED");

                    }

                    if (mClient.getConnectionState() == ConnectionState.BOUND) {
                        LogDebug("Connection BOUND");
                    }

                    if (mClient.getConnectionState() == ConnectionState.UNBOUND) {
                        LogDebug("Connection UNBOUND");
                    }
                }

                ///TRY TO RECONNECT IF NOT CONNECTED EVERY 1 MIN
                if ((unixTime - lastBandConnectionTry) > bandReconnectAttemptInterval) {
                    ReconnectBand(useRestartBluetoothAdaptorPolicy);
                }

                // lastBandAcquisition=unixTime;
                mBandAcquiring = false;

            } else {

                Log.v(TAG, "Band not receiving data");
                //   forwardBandMessages();
                mBandAcquiring = true;
            }
        }


    }


    private void stopTimer() {

        //If using timer

        if (timer != null) {

            Log.d(TAG, "stop timer");
            timer.cancel();
            timer.purge();
            timer = null;
        }


        /*******
         * USING MAIN TASK HANDLER AND RUNNABLE
         */
        //  mainTaskHandler.removeCallbacks(mainTaskrunnable);
        //    handlerInitialized = false;


    }

    private void loggingCheck() {

        try {

            checkBandConnectionStatus();
        } catch (Exception e) {

            LogError("MS Logging Check", e);

        }

    }

    /**
     * TODO: MERGE WITH CHECK FOR ALERT
     *
     * @throws InterruptedException
     */
    private synchronized void ReconnectBand(boolean dorestartBluetoothAdaptor) throws InterruptedException {

        boolean reconnect = true;


        try {
            ///DISCONNECT IF NEEDED

            disconnectBand();
            //Await twice the time to client timeout
            Thread.sleep(2 * bandClientWaitIntervalInSeconds);

            //NEXT RESTART BLUETOOTH IF THIS STRATEGY IS APPLIED
            if (dorestartBluetoothAdaptor) {

                //OK NOW DISABLE AND ENABLE AGAIN THE BLUETOOTH
                boolean be = BluetoothHelper.isBluetoothEnabled();
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

                try {

                    if (be) {

                        if (!bluetoothRestarted) {

                            bluetoothRestarted = true;
                            //OK BLUETOOTH ENABLED
                            //AND RESTART IS REQUIRED
                            BluetoothHelper.disableBluetooth();
                            reconnect = false;
                            // return;

                        }

                    } else {
                        //IF NOT ENABLED
                        //THE NEXT TIME DISABLE THE BLUETOOTH FIRST
                        bluetoothRestarted = false;
                        BluetoothHelper.enableBluetooth();
                        reconnect = false;
                    }


                } catch (Exception ex) {
                    LogError("Error while disconnecting/connecting bluetooth: " + ex.getMessage());
                }

            } else

            {

                //IF NO RECONNECT STRATEGY
                //JUST ENSURE BLUETOOTH IS CONNECTED

                boolean be = BluetoothHelper.isBluetoothEnabled();

                if (!be) {
                    //Just make bluetooth is enabled
                    try {


                        BluetoothHelper.enableBluetooth();
                        reconnect = false;
                        //Thread.sleep(10000);

                    } catch (Exception ex) {
                        LogError("Error while disconnecting/connecting bluetooth: " + ex.getMessage());
                    }


                }


            }

            if (reconnect) {
                LogInfo("Reconnecting from scratch");
                ///RECONNECT FROM SCRATCH
                mClient = null;
                lastBandConnectionTry = System.currentTimeMillis();
                connectToBand();

            }

        } catch (Exception ex) {

            LogError("Error while reconnecting: " + ex.getMessage());


        }


    }

    /**
     * IsConnected Method
     *
     * @return If the Band sensor is connected
     */
    public boolean IsConnected() {

        return mClient != null && mClient.isConnected();


    }

    //region WAKE LOCK

    /**
     * Require permissions for accessing Heart Rate Sensor
     */
    public void requireHeartRatePermissions() {

        if (mClient != null) {
            BandSensorManager sensorMgr = mClient.getSensorManager();
            if (sensorMgr.getCurrentHeartRateConsent() !=
                    UserConsent.GRANTED) {
                // user has not consented, request it
                // the calling class is both an Activity and implements
                // HeartRateConsentListener
                if (this.heartRateAccessProvider != null)
                    this.heartRateAccessProvider.requestHeartRateConsent(sensorMgr);

            }

        }

    }


    private synchronized void startTimer() {

        if (timer == null) {
            try {


                timer = new Timer();
                timer.scheduleAtFixedRate(new mainTask(), mainTimerInterval, mainTimerInterval);

            } catch (Exception ex) {

                LogFatal("Timer", ex, FATAL_START_TIMER);

            }
        }


        /***
         * USING MAIN TASK HANDLER
         */
     /*   if (!handlerInitialized) {
            handlerInitialized = true;
            mainTaskHandler.postDelayed(mainTaskrunnable, mainTimerInterval);
        }
*/

    }


    //region Lock/Unlock Band client

    /**
     * Lock Band Client
     *
     * @throws InterruptedException
     */
    private boolean lockBandClient() throws InterruptedException {

        boolean ret = available.tryAcquire(3, TimeUnit.SECONDS);

        if (ret)
            LogDebug("Band Client lock acquired");

        return ret;

    }


    /**
     * Unlock Band Client
     *
     * @throws InterruptedException
     */
    private void unlockBandClient() throws InterruptedException {

        if (available != null) {
            available.release();
            LogDebug("Band Client lock released");
        }

    }
    //endregion

    /**
     * Init Sensor Listeners (for device and band)
     */
    private void initSensorListeners() {

        if (mClient != null && mClient.isConnected()) {

            if (!bandSensorsRegistered) {
                BandSensorManager sensorMgr = mClient.getSensorManager();

                // Turn on the appropriate sensor
                try {

                    registerBandSensors(sensorMgr);


                } catch (Exception ex) {

                    LogError("Init Sensors Listener", ex.getCause());//.printStackTrace();

                } finally {

                }

            }
        }


    }

//region Register and Unregioste band sensors

    /**
     * Unregister Band Sensors
     *
     * @param sensorMgr
     * @throws Exception
     */
    private void unRegisterBandSensors(BandSensorManager sensorMgr) throws Exception {

        if (mAccBandEnabled) {

            LogInfo("UnRegister Band Accelerometer");
            sensorMgr.unregisterAccelerometerEventListener(mAccelerometerEventListener);
        }

        if (mPedoEnabled) {
            //
            LogInfo("UnRegister Band Pedo");
            sensorMgr.unregisterPedometerEventListener(mPedometerEventListener);
        }

        if (mSTEnabled) {

            LogInfo("UnRegister Band Skin Temperature");
            sensorMgr.unregisterSkinTemperatureEventListener(mSTEventListener);
        }

        if (mGyroBandEnabled) {
            LogInfo("UnRegister Gyro Accelerometer");
            sensorMgr.unregisterGyroscopeEventListener(mGyroEventListener);

        }

        if (mHeartRateEnabled) {
            LogInfo("UnRegister Heart Rate Accelerometer");
            sensorMgr.unregisterHeartRateEventListener(mHeartRateEventListener);

        }

        bandSensorsRegistered = false;

    }


    /**
     * Register Band Sensors
     *
     * @param sensorMgr Band Sensor Manager
     * @throws BandException A band exception
     */
    private void registerBandSensors(BandSensorManager sensorMgr) throws BandException {
        if (mHeartRateEnabled) {

            LogInfo("Band Heart Rate Sensor registered");
            //LogInfo("Band Heart Rate Sensor registered");
            sensorMgr.registerHeartRateEventListener(mHeartRateEventListener);
        } else {

            LogInfo("Band Heart rate unregistered");
            sensorMgr.unregisterHeartRateEventListener(mHeartRateEventListener);


        }
        if (mGyroBandEnabled) {

            LogInfo("Band Gyroscope registered");
            sensorMgr.registerGyroscopeEventListener(mGyroEventListener, bandRate);
        } else

        {
            LogInfo("Band Gyroscope unregistered");
            sensorMgr.unregisterGyroscopeEventListener(mGyroEventListener);


        }
        if (mSTEnabled) {

            LogInfo("Band Skin Temperature registered");
            sensorMgr.registerSkinTemperatureEventListener(mSTEventListener);
        } else

        {
            LogInfo("Band Pedo unregistered");
            sensorMgr.unregisterPedometerEventListener(mPedometerEventListener);


        }

        if (mPedoEnabled) {

            LogInfo("Band Pedo registered");
            sensorMgr.registerPedometerEventListener(mPedometerEventListener);
        } else

        {
            LogInfo("Band Pedo unregistered");
            sensorMgr.unregisterPedometerEventListener(mPedometerEventListener);


        }

        if (mAccBandEnabled) {

            LogInfo("Band Accelerometer registered");
            sensorMgr.registerAccelerometerEventListener(mAccelerometerEventListener, bandRate);


        } else {
            LogInfo("Band Accelerometer unregistered");
            sensorMgr.unregisterAccelerometerEventListener(mAccelerometerEventListener);
        }
        bandSensorsRegistered = true;
    }


    /**
     * Disconnect Band
     *
     * @throws InterruptedException
     */
    private void disconnectBand() throws Exception {


        // do {

        //   try {
        if (mClient != null && mClient.isConnected()) {

            LogInfo("Disconnecting Band for reconnection");

            //First undregister and then disconnect
            if (bandSensorsRegistered) {
                BandSensorManager mgr = mClient.getSensorManager();
                unRegisterBandSensors(mgr);
            }


            new disconnectTask().execute(mClient);


        }


    }

    private void connectToBand() throws InterruptedException {


        if (mClient == null) {

            try {
                BandClientManager manager = BandClientManager.getInstance();
                BandInfo[] mPairedBands = manager.getPairedBands();

                try {
                    if (!lockBandClient()) {
                        LogWarn("Band Client NOT locked");
                        return;
                    }


                } catch (Exception ex) {

                    Log.d(TAG, "Lock Band Client (connectToBand)", ex.getCause());
                }

                if (mPairedBands.length > 0) {

                    mClient = manager.create(getContext(), mPairedBands[0]);

                } else {
                    LogFatal("Number of Band Sensors is 0", LogCodes.FATAL_BAND_SENSNUMBER);

                }
                //Don't forget to unlock band client
                try {

                    unlockBandClient();

                } catch (InterruptedException ex) {

                    Log.d(TAG, "Unlock Band client", ex.getCause());

                }


            } catch (Exception ex) {

                LogFatal("Exception while connecting to band", FATAL_BAND_CONNECTION);
            }


        }

        ///IF Mclient is connected then some leak has occured

        if (!mClient.isConnected()) {

            new connectTask().execute(mClient);
        }


    }

    //Send a vibration to Band if Band is connected
    public void sendNotification() {
        if (mClient != null && mClient.isConnected()) {
            BandNotificationTask.newInstance(mClient).execute();

        }
    }


    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class disconnectTask extends AsyncTask<BandClient, Void, ConnectionResult> {
        @Override
        protected ConnectionResult doInBackground(BandClient... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {

                //Lock Band Client
                if (!lockBandClient()) {
                    LogWarn("Band Client NOT Locked");

                }
                BandClient mClient = clientParams[0];
                mClient.disconnect().await(bandClientWaitIntervalInSeconds, TimeUnit.SECONDS);


                return new ConnectionResult(ConnectionState.DISPOSED);


            } catch (InterruptedException ex) {
                LogError("Connect to band", ex);
                return new ConnectionResult(ex);
                // handle InterruptedException
            } catch (BandException ex) {

                String exceptionMessage = "Connect to band";
                switch (ex.getErrorType()) {
                    case TOO_MANY_CONCURRENT_COMMANDS_ERROR:
                        exceptionMessage = "Microsoft Band TOO_MANY_CONCURRENT_COMMANDS_ERROR";
                    case BAND_FULL_ERROR:
                        exceptionMessage = "Microsoft Band Full Error";
                        break;
                    case SERVICE_ERROR:

                        exceptionMessage = "Microsoft Band SERVICE_ERROR";
                        break;
                    case DEVICE_ERROR:
                        exceptionMessage = "Microsoft Band DEVICE_ERROR";
                        break;
                    case TIMEOUT_ERROR:
                        exceptionMessage = "Microsoft Band SERVICE_ERROR";
                        break;

                    case UNKNOWN_ERROR:
                        exceptionMessage = "Microsoft Band UNKNOWN_ERROR";
                    default:
                        //   exceptionMessage = "Unknown error occurred: " + e.getMessage();
                        break;
                }
                LogError("Connect to band..." + exceptionMessage, ex);

                return new ConnectionResult(ex);
                // handle BandException
            } catch (Exception ex) {
                LogError("Connect to band...", ex);

                return new ConnectionResult(ex);
                // handle BandException
            }
        }

        protected void onPostExecute(ConnectionResult result) {

            if (result != null) {
                if (result.hasException()) {
                    LogError("Error disconnecting from Microsoft Band " + result.getException().getMessage());

                } else {

                    LogDebug("Disconnected to Microsoft Band");

                }
            } else {

                LogError("Connection Result is null");

            }

            try {
                unlockBandClient();

            } catch (Exception ex) {
                LogError("Error Realising Band", ex.getCause());
            }


        }
    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class connectTask extends AsyncTask<BandClient, Void, ConnectionResult> {
        @Override
        protected ConnectionResult doInBackground(BandClient... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {

                //Lock Band Client
                if (!lockBandClient())
                    LogDebug("Band Client NOT LOCKED");


                ConnectionState result = clientParams[0].connect().await(bandClientWaitIntervalInSeconds, TimeUnit.SECONDS);

                return new ConnectionResult(result);


            } catch (InterruptedException ex) {
                LogError("Connect to band", ex);
                return new ConnectionResult(ex);
                // handle InterruptedException
            } catch (BandException ex) {

                String exceptionMessage = "Connect to band";
                switch (ex.getErrorType()) {
                    case TOO_MANY_CONCURRENT_COMMANDS_ERROR:
                        exceptionMessage = "Microsoft Band TOO_MANY_CONCURRENT_COMMANDS_ERROR";
                    case BAND_FULL_ERROR:
                        exceptionMessage = "Microsoft Band Full Error";
                        break;
                    case SERVICE_ERROR:

                        exceptionMessage = "Microsoft Band SERVICE_ERROR";
                        break;
                    case DEVICE_ERROR:
                        exceptionMessage = "Microsoft Band DEVICE_ERROR";
                        break;
                    case TIMEOUT_ERROR:
                        exceptionMessage = "Microsoft Band SERVICE_ERROR";
                        break;

                    case UNKNOWN_ERROR:
                        exceptionMessage = "Microsoft Band UNKNOWN_ERROR";
                    default:
                        //   exceptionMessage = "Unknown error occurred: " + e.getMessage();
                        break;
                }
                LogError("Connect to band..." + exceptionMessage, ex);

                return new ConnectionResult(ex);
                // handle BandException
            } catch (Exception ex) {
                LogError("Connect to band...", ex);

                return new ConnectionResult(ex);
                // handle BandException
            }
        }

        protected void onPostExecute(ConnectionResult result) {

            if (result != null) {
                if (result.hasException()) {
                    LogError("Error disconnecting from Microsoft Band " + result.getException().getMessage());

                } else {

                    if (mClient != null && result.getState() == ConnectionState.CONNECTED) {

                        mbandConnected = true;

                        LogWarn("Connected to Microsoft Band");

                        requireHeartRatePermissions();
                        initSensorListeners();


                    } else if (mClient != null && result.getState() == ConnectionState.BOUND) {

                        LogWarn("Bound to Microsoft Band...needs res");

                    } else if (mClient != null && result.getState() == ConnectionState.DISPOSED) {


                        //TODO: Check that again
                        LogWarn("DISPOSED Connection Microsoft Band");
                       /* try {
                            LogWarn("Disconnecting to Microsoft Band");
                            mClient.disconnect().await(bandClientWaitIntervalInSeconds, TimeUnit.SECONDS);


                        } catch (Exception ex) {
                            LogWarn("Error disconnecting from Microsoft Band");

                        }
                        */


                    } else {
                        LogWarn("Connection state= " + ConnectionState.DISPOSED + "  unhandled ");

                    }
                }
            } else {

                LogWarn("Connection Result is null");

            }

            try {
                unlockBandClient();

            } catch (Exception ex) {
                LogError("Error Realising Band", ex.getCause());
            }


        }
    }

    /**
     * Main Task
     */
    private class mainTask extends TimerTask {


        public mainTask() {


        }


        public void run() {

            /****
             * Check that the last execution time was at least  minMainTimerInterval before
             * We may observe a behavior where timer is postoponned for a timer of period
             */
            long unixTime = System.currentTimeMillis();

            if (unixTime - lastTimerCheck > minMainTimerInterval) {

                //  toastHandler.sendEmptyMessage(0);
                mainTaskHandler.post(new Runnable() {
                    @Override
                    public void run() {
                         /* do what you need to do */
                        loggingCheck();
      /* and here comes the "trick" */

                    }
                });

            } else {

                LogDebug("Timer sooner than expected");
            }


        }


    }


}
