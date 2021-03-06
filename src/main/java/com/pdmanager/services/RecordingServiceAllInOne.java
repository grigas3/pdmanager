/*
 * Copyright (c) 2015.  This code is developed for the PDManager EU project and can be used only by the consortium for the purposes of the project only.
 * Author George Rigas.
 */

package com.pdmanager.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.google.gson.Gson;
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
import com.pdmanager.FileDataProcessor;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.app.PostureDataProcessor;
import com.pdmanager.common.ConnectionResult;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccMCData;
import com.pdmanager.common.data.AccMData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroData;
import com.pdmanager.common.data.GyroMData;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.OrientData;
import com.pdmanager.common.data.OrientReading;
import com.pdmanager.common.data.PedoData;
import com.pdmanager.common.data.STData;
import com.pdmanager.common.data.STReading;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.common.interfaces.IPDToastWriter;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.CommunicationRunner;
import com.pdmanager.communication.ICommunicationQueue;
import com.pdmanager.communication.JsonStorage;
import com.pdmanager.communication.NetworkStatus;
import com.pdmanager.communication.SQLCommunicationList;
import com.pdmanager.helpers.BluetoothHelper;
import com.pdmanager.interfaces.IJsonRequestHandler;
import com.pdmanager.interfaces.INetworkStatusHandler;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.interfaces.ITokenUpdater;
import com.pdmanager.location.PDLocationListener;
import com.pdmanager.logging.ILogHandler;
import com.pdmanager.medication.MedManager;
import com.pdmanager.models.Alert;
import com.pdmanager.models.LoginModel;
import com.pdmanager.models.UsageStatistic;
import com.pdmanager.models.UserAlert;
import com.pdmanager.monitoring.ActivityMonitoring;
import com.pdmanager.monitoring.VitalMonitoring;
import com.pdmanager.notification.BandMessage;
import com.pdmanager.notification.BandMessageQueue;
import com.pdmanager.notification.BandMessageTask;
import com.pdmanager.notification.BandNotificationTask;
import com.pdmanager.notification.LocalNotificationTask;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.sensor.IHeartRateAccessProvider;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.symptomdetector.aggregators.TremorAggregator;
import com.pdmanager.symptomdetector.dyskinesia.DyskinesiaEvaluator;
import com.pdmanager.symptomdetector.tremor.HandPostureDetector;
import com.pdmanager.symptomdetector.tremor.TremorEvaluator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

//import com.pdmanager.logging.Log4jConfigure;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * Created by George on 5/21/2015.
 */
public class RecordingServiceAllInOne extends Service implements ISensorDataHandler, SensorEventListener, IPDToastWriter, ILogHandler, IJsonRequestHandler, INetworkStatusHandler, ITokenUpdater {


    //region private fields
    private static final int FATAL_GPS_SECURITY = 0;
    private static final int FATAL_GPS_EXCEPTION = 1;
    private static final int FATAL_BAND_SENSNUMBER = 3;


    ///Since most timing is performed in second accuracy
    /// Add a margin of seconds to perform operations (60+5)
    private static final long mainTimerInterval = 65 * 1000;
    private static final long minMainTimerInterval = 20 * 1000;

//endregion

    //    final static Logger logger = LogManager.getLogger("RecordingService");
    private static final int FATAL_BAND_CONNECTION = 4;
    private static final int FATAL_DEVICE_SENSORS = 5;
    private static final int FATAL_START_QUEUE = 6;
    private static final int FATAL_START_TIMER = 7;
    private static final int FATAL_SERVICE_STOPPED_BY_ANDROID = 8;

    private static final String TAG = "RECORDING";
    private static final String WL_TAG = "RECORDING_WAKE";
    // The minimum distance to change Updates in meters
    private static final long MIN_UPDATE_DISTANCE = 10;
    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 1000 * 5;
    //Try to reconnect at least after 10 minutes
    private static final long bandReconnectAttemptInterval = 10 * 60 * 1000;
    //Update Usage StatisticsInterval
    private static final long updateUsageStatisticsInterval = 60 * 30 * 1000;

    //Minimum interval regarding Band Data Acquisitions
    private static final long minimumNoBandDataInterval = 60 * 2 * 1000;

    //Wait interval in seconds used for connecting/disconnecting from Band
    private static final long bandClientWaitIntervalInSeconds = 5;


    public static PowerManager.WakeLock WAKELOCK = null;
    private static boolean sessionRunning = false;
    private final IBinder mBinder = new LocalBinder();
    private final List<IDataProcessor> processors = new ArrayList<IDataProcessor>();
    private final SampleRate bandRate = SampleRate.MS16;
    private final float gforce = 9.806F;
    private final BandMessageQueue bandMessageQueue = new BandMessageQueue();
    private final Semaphore available = new Semaphore(1, true);
    //region Handler and receivers
    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            try {
                String message = (String) msg.obj;

                //   LogInfo(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

            }
        }
    };
    private final int NOTIFICATION_ID = 1233;
    float[] trueAcceleration = new float[4];
    float[] R = new float[16];
    float[] RINV = new float[16];
    float[] relativacc = new float[4];
    float[] orientVals = new float[3];
    VitalMonitoring vitalMonitoring;
    ActivityMonitoring activityMonitoring;
    long totalRAMMemoryUsed = 0;
    int numberOfRamMemoryChecks = 0;
    private SensorManager senSensorManager;
    private boolean hasAllDeviceSensors = false;
    private BandClient mClient;
    private CommunicationManager manager;
    private boolean mFatalError = false;
    private int mFatalErrorCode = 0;
    private boolean mbandConnected = false;
    private boolean mHeartRateEnabled = false;
    private boolean mAccBandEnabled = false;
    private boolean mAccDeviceEnabled = false;
    private boolean mGyroBandEnabled = false;
    private boolean mPedoEnabled = false;
    private boolean mSTEnabled = false;
    private boolean mLocationEnabled = false;
    private boolean mBandEnabled = false;
    private boolean mUseLinearAcceleration = false;
    private boolean hasLinearAccelerationSensor = false;
    private boolean hasGyroscopeSensor = false;
    private boolean mBandAcquiring = false;
    private long linearAccelerationTime2 = 0;
    //Last Band Connection timestamp (milliseconds) when tried to reconnect
    private long lastBandConnectionTry = 0;
    private float[] acceleration = new float[4];
    private float[] linearAcceleration = new float[4];
    private float[] gravityMatrix = new float[4];
    private float[] magneticFieldMatrix = new float[4];
    private boolean linearAccelerationAcquired = false;
    private long linearAccelerationTime = 0;
    private boolean receiverRegistered = false;
    private long lastBandAcquisition = -100000;
    private long lastUserNotificationCheck = -100000;
    private boolean bluetoothRestarted = false;
    private long lastMedAlertCheck = -100000;
    private PostureDataProcessor postureProcessor = null;
    private FileDataProcessor fileProcessor = null;
    private HandPostureDetector handPostureDetector = null;
    private TremorEvaluator tremorEvaluator = null;
    private DyskinesiaEvaluator disEvaluator = null;
    private TremorAggregator tremorAggregator = null;
    private PDLocationListener locListener = null;
    private String paired;
    private IHeartRateAccessProvider heartRateAccessProvider;
    private Looper mServiceLooper;
    //  private ServiceHandler mServiceHandler;
    private Date sessionStart;
    private ArrayList<ISensorDataHandler> dataHandlers = new ArrayList<ISensorDataHandler>();
    private ArrayList<IServiceStatusListener> listeners = new ArrayList<IServiceStatusListener>();
    private ArrayList<ISensorStatusListener> sensorListeners = new ArrayList<ISensorStatusListener>();
    private volatile boolean mIsHandlerScheduled;
    private AtomicReference<BandHeartRateEvent> mPendingHeartRateEvent = new AtomicReference<BandHeartRateEvent>();
    private AtomicReference<BandPedometerEvent> mPendingPedometerEvent = new AtomicReference<BandPedometerEvent>();
    private AtomicReference<BandSkinTemperatureEvent> mPendingSTEvent = new AtomicReference<BandSkinTemperatureEvent>();
    private AtomicReference<BandGyroscopeEvent> mPendingGyroEvent = new AtomicReference<BandGyroscopeEvent>();
    //private AlertManager alertManager = null;
    private AtomicReference<BandAccelerometerEvent> mPendingAccelerometerEvent = new AtomicReference<BandAccelerometerEvent>();
    private Timer timer = null;
    private Context ctx;
    private CommunicationRunner queueRunner = null;
    private ICommunicationQueue queue = null;
    private Thread queueThread = null;
    private long lastUsageTimestamp = 0;
    private boolean bandSensorsRegistered = false;
    private boolean useRestartBluetoothAdaptorPolicy = true;
    private int devsamples = 0;
    private int bandsamples = 0;
    private int hrsamples = 0;
    private boolean bluetoothEnabled = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_OFF) {

                    Message s = new Message();
                    s.obj = "Bluetooth disconnected";
                    //toastHandler.sendMessage(s);

                    bluetoothEnabled = false;

                }

                if (state == BluetoothAdapter.STATE_ON) {

                    Message s = new Message();
                    s.obj = "Bluetooth connected";
                    //toastHandler.sendMessage(s);
                    bluetoothEnabled = true;
                }

                // Bluetooth is disconnected, do handling here
            }

        }

    };
    private String deviceId;
    private boolean schedulerPause = false;
    private RecordingScheduler scheduler;
    //endregion
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
//endregion

    //region registers
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            mPendingAccelerometerEvent.set(event);
            scheduleSensorHandler();
        }
    };
    private boolean handlerInitialized = false;
    //region Tasks
    private Handler mainTaskHandler = new Handler();
    private final Runnable mainTaskrunnable = new Runnable() {
        @Override
        public void run() {
                         /* do what you need to do */
            serviceCheck();
      /* and here comes the "trick" */
            mainTaskHandler.postDelayed(this, mainTimerInterval);
        }

    };
    //
    private long lastTimerCheck = 0;

    public static boolean testSheduledRun(int hourOfDay, int startHour, int endHour) {

        return conditionScheduledRun(hourOfDay, startHour, endHour);


    }

    private static boolean conditionScheduledRun(int hourOfDay, int startHour, int stopHour) {

        return !(hourOfDay < startHour || hourOfDay >= stopHour);

    }

    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(this, "calling bind for service", Toast.LENGTH_SHORT).show();
        notifyListeners();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
        //  return mBinder;
    }
    //endregion

    public void registerHRAccessProvider(IHeartRateAccessProvider handler) {

        heartRateAccessProvider = handler;
    }

    public void registerHandler(ISensorDataHandler handler) {

        if (!dataHandlers.contains(handler))
            dataHandlers.add(handler);

    }

    public void unregisterHandler(ISensorDataHandler handler) {
        if (dataHandlers.contains(handler))
            dataHandlers.remove(handler);

    }

    public void registerSensorListener(ISensorStatusListener handler) {

        if (handler != null) {
            sensorListeners.add(handler);

            // handler.notifySensorStatusChanged();

        }

    }

    public void unregisterSensorListener(ISensorStatusListener handler) {

        sensorListeners.remove(handler);
    }

    /*
    Register Service Status Listener
    Those listeners are notified for the the status of the recording service
     */
    public void registerListener(IServiceStatusListener handler) {

        if (handler != null) {
            listeners.add(handler);
            handler.notifyServiceStatusChanged();

        }

    }

    public void unregisterListener(IServiceStatusListener handler) {

        listeners.remove(handler);


    }

    private void notifyListeners() {

        for (int i = 0; i < listeners.size(); i++) {

            IServiceStatusListener listener = listeners.get(i);
            if (listener != null)
                listener.notifyServiceStatusChanged();
        }

    }

    private void notifySensorListeners(boolean status) {

        for (int i = 0; i < sensorListeners.size(); i++) {

            ISensorStatusListener listener = sensorListeners.get(i);
            if (listener != null)
                listener.notifySensorStatusChanged(status);
        }

    }

    @Override
    public void ProcessLog(String logType, String message) {


        //REMOVED FOR PILOT


        DBHandler handler = null;
        SQLiteDatabase sqlDB = null;
        try {

            long unixTime = System.currentTimeMillis() / 1000L;

            handler = DBHandler.getInstance(this);

            sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(DBHandler.COLUMN_LOGTYPE, logType);
            values.put(DBHandler.COLUMN_LOGMESSAGE, message);
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            sqlDB.insert(DBHandler.TABLE_LOGS, null, values);


        } catch (Exception ex) {

            Log.e(TAG, ex.getMessage(), ex.getCause());
            //LogError("Process Log", ex.getCause());

        } finally {

            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();


        }


    }

    @Override
    public void onCreate() {
        // The service is being created

        manager = new CommunicationManager(this);
        InitProcessors();

        //  Log4jConfigure.configureRollingFile();//configureFromXmlString

        LogDebug("Services OnCreate");

        if (!sessionRunning) {

            if (getSessionMustRun()) {

                StartRecording();
            }


        } else {

            startTimer();
            startCommQueue();

        }
        InitBluetoothListener();
        foreground();


        // alertManager = new AlertManager(getApplicationContext());
    }

    private boolean recordFiles() {
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);
        return settings.getRecordFiles();
    }

    private boolean useDetectors() {
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);
        return settings.getUseDetectors();
    }

    /***
     * Get the Use Device Wake Lock setting
     * Depending on that when session is running the CPU is Wake LOCKED
     * @return
     */
    ///
    private boolean useDeviceLock() {
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);
        return settings.getUseDeviceLock();
    }

    private boolean useForeground() {
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);
        return settings.getForegroundService();
    }

    /***
     ********************
     Init Data Processors
     Data Processors are workers that are receiving any data captured by the recording service
     *****************************
     */
    private void InitProcessors() {

        if (recordFiles()) {
            fileProcessor = new FileDataProcessor();
            processors.add(fileProcessor);
        }

        if (scheduler == null) {
            RecordingSettings settings = RecordingSettings.newInstance(this);
            scheduler = new RecordingScheduler(settings.getStartHour(), settings.getStopHour());
            processors.add(scheduler);

        }

        try {

            ///Get Patient ID
            String pid = getPatient();

            ///Check if setting have enabled use of detectors
            if (useDetectors()) {
                tremorEvaluator = new TremorEvaluator(this, 62.5);
                handPostureDetector = new HandPostureDetector(this, 0.003F, 0.005F, 20, 62.5);
                tremorAggregator = new TremorAggregator(manager, pid);
                disEvaluator = new DyskinesiaEvaluator(manager, pid, this, 62.5);

                processors.add(disEvaluator);
                processors.add(tremorEvaluator);
                processors.add(handPostureDetector);
                processors.add(tremorAggregator);

                ///Add HR Monitoring
                vitalMonitoring = new VitalMonitoring(manager, pid);
                processors.add(vitalMonitoring);

                ///Add Activity Monitoring
                activityMonitoring = new ActivityMonitoring(manager, pid);
                processors.add(activityMonitoring);


            }
        } catch (Exception ex) {

            LogError("Init Posture");
        }


    }

    private synchronized void serviceCheck() {

        try {

            boolean bandActionTaken = false;

            StringBuilder str = new StringBuilder();
            str.append("TIMER CHECK");
            Log.d(TAG, "Timer check");

            if (isServiceRunningInForeground(this.getApplicationContext(), RecordingServiceAllInOne.class))
                Log.v(TAG, "Service in foreground");
            else
                Log.v(TAG, "Service in background");

            if (isServiceRunning("com.microsoft.band.service.BandService"))
                Log.v(TAG, "Band Service is running");
            else
                LogWarn("Band service is not running");

            if (isPowerSaveMode()) {
                Bugfender.d(TAG, "Power save");
                str.append("/POWER ON");

            } else {

                Log.v(TAG, "Power on");
                str.append("/POWER ON");

            }

            if (isScreenOn()) {
                Log.v(TAG, "Screen on");
                str.append("/SCREEN ON");

            } else {

                Log.v(TAG, "Screen off");
                str.append("/Screen off");

            }
            //checkServicesRunning();

            //If sessions is not pased
            //region Block for not paused
            if (!schedulerPause) {

                if (!scheduledRun()) {

                    ///IF SESSION SHOULD STOP DUE TO START AND END DATE
                    ///IS SCHEDULING SETTINGS
                    if (sessionRunning) {
                        /// IF SESSION RUNNING STOP SESSION
                        ///Notify Server
                        LogWarn("Service stopped for today", 2);
                        //SendAlert("INFO", "Service stopped for today", "INFO0002");
                        str.append("/ STOPING SERVICDE");
                        bandActionTaken = true;
                        //Stop Reader
                        StopReader();
                    }


                } else {

                    ///IF SESSION SHOULD RUN
                    boolean sessionMustRun = getSessionMustRun();

                    if (sessionMustRun) {


                        //IF BLUETOOTH IS DISABLED THEN ENABLE BLUETOOTH
                        BluetoothHelper.enableBluetooth();

                        //IF Session is Not Running then start it again
                        if (!sessionRunning) {

                            str.append("/ START READER ");
                            bandActionTaken = true;
                            //Start Reader
                            StartReader();


                        } else {


                            //Session Running
                            // Check For Errors
                            if (!bandActionTaken) {
                                str.append("/BAND CHECK");
                                try {

                                    bandActionTaken = bandActionTaken || checkBandConnectionStatus();

                                } catch (InterruptedException ex) {

                                    LogError("EX ON SCHEDULING", ex.getCause());
                                } catch (Exception ex) {

                                    LogError("EX ON SCHEDULING", ex.getCause());
                                }

                            }

                        }


                    }

                }

                ///Check For Alert only if

            }
            //endregion

            /// If no band action taken perform scheduling
            /// Scheduling will check if the recording should stop or resume
            if (!bandActionTaken) {
                str.append("/ SCHEDULING ");
                try {
                    scheduling();

                } catch (InterruptedException ex) {

                    LogError("EX ON SCHEDULING", ex.getCause());
                } catch (Exception ex) {

                    LogError("EX ON SCHEDULING", ex.getCause());
                }

            }
            ProcessLog("INFO", str.toString());

            ///Check If Mobile Device Sensors have acquired data the last minute
            ///If no data acquired unregister and register sensors listeners
            if (scheduledRun()) {

                if (sessionRunning) {
                    checkMobileDeviceDataAcquired();
                }
            }

            //Check for Medication User Alert
            //checkForMedAlerts();
            //Check For Alerts
            if (scheduledRun()) {

                if (sessionRunning) {
                    //Update Usage Statistics
                    updateUsageStatistics();

                }
            }

            //Check For User Notifications
            checkForUserNotifications();


        } catch (Exception ex) {

            Log.e(TAG, ex.getMessage());
        }

    }

    private synchronized void startTimer() {

        if (timer == null) {
            try {


                timer = new Timer();
                timer.scheduleAtFixedRate(new mainTask(this), mainTimerInterval, mainTimerInterval);


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

    public void WriteToastMessage(String message) {

        Message s = new Message();
        s.obj = message;
        toastHandler.sendMessage(s);


    }

    private boolean scheduledRun() {

        ///JUST FOR DEBUGGING
        ///return true;


        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            Calendar c = Calendar.getInstance();

            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

            return conditionScheduledRun(hourOfDay, settings.getStartHour(), settings.getStopHour());

        } else
            return false;

    }

    //region Memory Usage

    public boolean getSessionMustRun() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            return settings.getSessionRunning();
        }

        return false;
    }
    //endregion

    @Override
    public void addRequest(JsonStorage jsonRequest) {

        if (queue != null)
            queue.push(jsonRequest);

    }

    /**
     * Start Communication Queue
     */
    private synchronized void startCommQueue() {

        if (queue == null) {

            try {
                Gson gson = new Gson();

                queue = new SQLCommunicationList(this);
                queueRunner = new CommunicationRunner(queue, this, getPatient(), this);

                queueRunner.setQueueRunning(true);
                queueThread = new Thread(queueRunner);
                queueThread.start();


            } catch (Exception ex) {

                LogFatal("QUEUE", ex, FATAL_START_QUEUE);
                //LogError("Cannot Create Queue: " + ex.getMessage());
            }

        }


    }

    /**
     * This is probably called be Android
     * When task removed we ask for alarm to restart our service
     *
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        LogFatal("KILLED BY ANDROID", FATAL_SERVICE_STOPPED_BY_ANDROID);
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 2 * 60 * 1000, restartServicePI);

    }

    /**
     * Stop Communication Queue
     */
    private void stopCommQueue() {

        if (queueRunner != null)
            queueRunner.setQueueRunning(false);
        try {

            if (queueThread != null) {
                queueThread.join(30000);

            }

            if (queue != null) {
                queue.close();
                queue = null;
            }


        } catch (Exception ex) {

            LogError("Cannot join queue thread");
        }


    }

    private long getUsedMemorySize() {

        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory();
            totalSize = info.totalMemory();
            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedSize;

    }

    /**
     * Check if network is connected
     *
     * @return True of false
     */
    @Override
    public boolean IsNetworkConnected() {

        boolean ret = false;

        try {

            ret = NetworkStatus.IsNetworkConnected(this);


        } catch (Exception ex) {

            LogError("Error while checking for network connection", ex.getCause());

        }

        return ret;


    }


    @Override
    public String getAccessToken() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            return settings.getToken();


        }

        return null;
    }

    @Override
    public void updateToken(String token, int expires_in) {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            settings.setToken(token);
            settings.setExpiration(Calendar.getInstance().getTimeInMillis() + expires_in * 1000);

        }


    }

    @Override
    public LoginModel getLoginToken() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {

            LoginModel model = new LoginModel(settings.getUserName(), settings.getPassword());
            return model;
        }

        return null;


    }

    @Override
    public boolean hasTokenExpired() {
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        long currentTime = Calendar.getInstance().getTimeInMillis();
        return currentTime > settings.getExpiration();


    }

    ///TODO: ....DO IT ALL ASYNC!!!!!
    //TODO: Add band messages and notifications in a queue and execute them from here.
    //TODO:this will neglect the probability of  duplicate Band Clients

    @Override
    public void onLowMemory() {
        LogWarn("Low memory");


    }

    private String getPatient() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            return settings.getPatientID();


        }

        return "TEST_PAT";
    }

    /*
            Update Usage Statistics evety UpdateUsageStatisticsInterval (default 30 min)
     */
    private void updateUsageStatistics() {

        long unixTime = System.currentTimeMillis();

        long memory = getUsedMemorySize();
        long memoryInKb = (memory / 1024L);

        totalRAMMemoryUsed += memoryInKb;
        numberOfRamMemoryChecks++;
        LogDebug("Memory Used " + Long.toString(memoryInKb));
        ///if (unixTime - lastUsageTimestamp > 30* 1000) {
        //Every Half any hour
        if (unixTime - lastUsageTimestamp > updateUsageStatisticsInterval) {

            try {

                String patient = getPatient();

                UsageStatistic obs1 = new UsageStatistic(bandsamples, "001", unixTime, patient, deviceId);
                UsageStatistic obs2 = new UsageStatistic(devsamples, "002", unixTime, patient, deviceId);
                UsageStatistic obs3 = new UsageStatistic(hrsamples, "003", unixTime, patient, deviceId);
                ArrayList<UsageStatistic> obsArray = new ArrayList<>();

                obsArray.add(obs1);
                obsArray.add(obs2);
                obsArray.add(obs3);
                try {

                    UsageStatistic obs4 = new UsageStatistic(totalRAMMemoryUsed / numberOfRamMemoryChecks, "004", unixTime, patient, deviceId);
                    obsArray.add(obs4);
                    totalRAMMemoryUsed = 0;
                    numberOfRamMemoryChecks = 0;


                } catch (Exception ex) {
                    LogError("Error getting memory", ex.getCause());
                }


                manager.SendItems(obsArray);

                bandsamples = 0;
                devsamples = 0;
                hrsamples = 0;
                lastUsageTimestamp = unixTime;

            } catch (Exception ex) {

                LogError(ex.getMessage(), ex.getCause());
            }


        }


    }

    private void checkForUserNotifications() {

        long unixTime = System.currentTimeMillis();

        if ((unixTime - lastUserNotificationCheck) / 1000 > 60) {

            try {

                UserAlertManager alertmanager = new UserAlertManager(this);
                lastUserNotificationCheck = unixTime;
                if (alertmanager.anyUnNotified()) {

                    UserAlert alert = alertmanager.getUnNotified();
                    if (alert != null) {
                        alertmanager.setNotified(alert);

                        //Send a vibration to Band if Band is connected
                        if (mClient != null && mClient.isConnected()) {
                            BandNotificationTask.newInstance(mClient).execute();

                        }
                        // Send a local notifation
                        new LocalNotificationTask(this).execute(alert);
                    }
                }

                alertmanager.updateExpired();

            } catch (Exception ex) {

                LogError(ex.getMessage(), ex.getCause());
            }

        }
    }

    /**
     * Check if we should create a user alert
     */
    private void checkForMedAlerts() {
        long unixTime = System.currentTimeMillis();
        if ((unixTime - lastMedAlertCheck) / 1000 > 60) {
            LogDebug("Check Medication");

            lastMedAlertCheck = unixTime;

            try {
                new CheckMedicationTask(this).execute();

            } catch (Exception ex) {

                LogError(ex.getMessage(), ex.getCause());

            }

        }


    }

    /**
     * Check if Band is connected
     *
     * @throws InterruptedException
     */
    private void checkMobileDeviceDataAcquired() throws InterruptedException {

        if (mAccDeviceEnabled) {

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


    }

    /***
     * Check Band Connection status
     * and more specifically if data are received the last minute
     * @throws InterruptedException
     */
    private boolean checkBandConnectionStatus() throws InterruptedException {

        boolean actionTaken = false;

        if (sessionRunning) {
            long unixTime = System.currentTimeMillis();

            if (mBandEnabled) {

                if ((unixTime - lastBandAcquisition) > minimumNoBandDataInterval) {

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
                                        return false;
                                    }
                                    initSensorListeners();

                                } catch (Exception ex) {

                                    LogError("ALERT REGISTER LISTENERS", ex.getCause());
                                } finally {

                                    try {
                                        unlockBandClient();
                                    } catch (InterruptedException ex) {

                                        Log.d(TAG, "Unlock Band client", ex.getCause());

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
                    } else {
                        LogWarn("Band CLIENT IS NULL");

                    }


                    ///TRY TO RECONNECT IF NOT CONNECTED EVERY 1 MIN
                    if ((unixTime - lastBandConnectionTry) > bandReconnectAttemptInterval) {
                        actionTaken = true;
                        ReconnectBand(useRestartBluetoothAdaptorPolicy);
                    }

                    // lastBandAcquisition=unixTime;
                    mBandAcquiring = false;

                } else {

                    ///Previous Band Acquiring state was false (also initial state is false)
                    if (!mBandAcquiring) {
                        LogWarn("Band receiving data...");

                    }

                    if (mFatalError) {

                        //   SendAlert("INFO", "Band Acquiring data", "INFOCODE002");

                    }

                    //   forwardBandMessages();

                    LogDebug("Service Running  and receiving data...");
                    mFatalError = false;
                    mBandAcquiring = true;
                }


            }

        }

        return actionTaken;

    }

    private void forwardBandMessages() {

        if (mClient != null && mClient.isConnected() && !bandMessageQueue.isEmpty()) {

            BandMessage message = bandMessageQueue.poll();

            if (message != null) {

                try {
                    BandMessageTask.newInstance(mClient, RecordingSettings.GetRecordingSettings(getApplicationContext()).getTileUUID()).execute(message);

                } catch (Exception e) {
                    LogError(e.getMessage(), e.getCause());

                }

            }


        }


    }

    private void closeBluetooth() {

        //OK NOW DISABLE AND ENABLE AGAIN THE BLUETOOTH
        boolean be = BluetoothHelper.isBluetoothEnabled();
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

        try {

            if (be) {

                mAdapter.disable();

            }


        } catch (Exception ex) {
            LogError("Error while disconnecting/connecting bluetooth: " + ex.getMessage());
        }


    }

    /**
     * Scheduling of Recording Service
     * Scheduling occur if no Band action is performed
     *
     * @throws InterruptedException
     */
    private void scheduling() throws InterruptedException {

        if (scheduler != null) {

            if (!schedulerPause) {

                int pauseReason = scheduler.shouldPause();
                if (pauseReason > 0) {

                    LogDebug("RECORDING PAUSES REASON=" + pauseReason);
                    ///DISCONNECT IF NEEDED
                    if (mClient != null) {

                        //NOT WEARING OR SCHEDULED PAUSE .....THEN DISCONNECT BAND
                        try {

                            if (disconnectBand()) {
                                mClient = null;
                                //Un register Mobile Phone Device Sensors
                                //TODO: Check if this is required...maybe continue collecting device data
                                unRegisterDeviceSensors();
                                schedulerPause = true;
                            }


                        } catch (Exception ex) {

                            LogDebug(ex.getMessage());

                        }

                        //If MS Band is on rest
                        //We also put at rest the CPU of the device
                        // In order to save battery also for smart phone
                        if (schedulerPause && useDeviceLock())
                            releaseLock();


                    }

                }

            } else {

                // Should Resume is called only if previously paused
                // Pauses occur fot two reasons
                // 1) Scheduled Pause for optimizing MS Band to hold the entire scheduled session
                // 2) Patient is not wearing band

                if (scheduler.shouldResume()) {

                    LogDebug("RESUMING");
                    schedulerPause = false;
                    scheduler.increaseInterval();

                    ///DISCONNECT IF NEEDED
                    //Before Any reconnection or reseting device
                    //We wake lock
                    if (useDeviceLock())
                        wakeLock();

                    ReconnectBand(false);

                    registerDeviceSensors();

                } else {
                    LogDebug("RECORDING PAUSED....NOT RESUMING YET");
                }

            }
        }


    }

    /**
     * Lock Band Client
     *
     * @throws InterruptedException
     */
    private boolean lockBandClient() throws InterruptedException {

        boolean ret = available.tryAcquire(3, TimeUnit.SECONDS);

        if (ret)
            LogWarn("Band Client lock acquired");

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
            LogWarn("Band Client lock released");
        }

    }

    /**
     * Disconnect Band
     *
     * @throws InterruptedException
     */
    private synchronized boolean disconnectBand() throws Exception {
        boolean disconnected = false;
        int tries = 0;
        if (mClient != null && mClient.isConnected()) {

            do {

                //Lock Band Client
                try {

                    if (!lockBandClient())
                        throw new InterruptedException();
                } catch (InterruptedException ex) {

                    LogError("LOCK Band Client", ex);

                    throw ex;

                }

                try {
                    if (mClient != null && mClient.isConnected()) {


                        LogInfo("Disconnecting Band for reconnection");
                        mClient.disconnect().await(bandClientWaitIntervalInSeconds, TimeUnit.SECONDS);

                        //First undregister and then disconnect
                        if (bandSensorsRegistered) {
                            BandSensorManager mgr = mClient.getSensorManager();
                            unRegisterBandSensors(mgr);
                        }


                    }
                    disconnected = true;
                    LogInfo("Successfully Disconnected from BAND");

                } catch (Exception ex) {

                    LogError("Error while disconnecting: " + ex.getMessage());

                }

                try {
                    unlockBandClient();
                } catch (InterruptedException ex) {

                    Log.d(TAG, "Lock Band client", ex.getCause());


                }

                if (!disconnected)
                    Thread.sleep(1000);
                tries++;
            } while (!disconnected && tries < 3);


        }

        return disconnected;


    }

    /**
     * TODO: MERGE WITH CHECK FOR ALERT
     *
     * @throws InterruptedException
     */
    private synchronized void ReconnectBand(boolean dorestartBluetoothAdaptor) throws InterruptedException {

        boolean reconnect = true;
        if (mBandEnabled) {

            try {
                ///DISCONNECT IF NEEDED
                if (mClient != null) {

                    try {
                        if (!lockBandClient())
                            return;

                    } catch (InterruptedException ex) {

                        return;

                    }

                    BandSensorManager sensorMgr = mClient.getSensorManager();
                    if (bandSensorsRegistered) {
                        unRegisterBandSensors(sensorMgr);

                    }

                    int tries = 0;
                    boolean disconnected = false;
                    do {
                        try {

                            //Lock Band Client

                            if (mClient != null && mClient.isConnected()) {
                                LogInfo("Disconnecting Band for reconnection");
                                mClient.disconnect().await(bandClientWaitIntervalInSeconds, TimeUnit.SECONDS);
                            }
                            disconnected = true;

                        } catch (Exception ex) {

                            LogError("Error while disconnecting: " + ex.getMessage());
                            //MINOR Exception
                        }

                        if (!disconnected)
                            Thread.sleep(1000);

                        tries++;
                    } while (!disconnected && tries < 3);

                    try {
                        unlockBandClient();


                    } catch (InterruptedException ex) {

                        Log.d(TAG, "Unlock Band client", ex.getCause());

                    }


                }

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
                                mAdapter.disable();
                                reconnect = false;
                                // return;

                            }

                        } else {
                            //IF NOT ENABLED
                            //THE NEXT TIME DISABLE THE BLUETOOTH FIRST
                            bluetoothRestarted = false;
                            mAdapter.enable();
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

                            BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

                            mAdapter.enable();
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
                    ConnectToBand();

                }

            } catch (Exception ex) {

                LogError("Error while reconnecting: " + ex.getMessage());


            }


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

    /**
     * Stop Recording
     */
    public void StopRecording() {
        StopReader();
        stopTimer();
        stopCommQueue();
        closeBluetooth();

    }

    private void InitBluetoothListener() {

        if (!receiverRegistered) {
            registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            receiverRegistered = true;
        }
    }

    //endregion

    /**
     *
     */
    private void wakeLock() {

        try {
            if (WAKELOCK == null) {

                LogWarn("WAKE LOCK DEVICE");
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                WAKELOCK = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WL_TAG);
                WAKELOCK.acquire();
                //startService(new Intent(getApplicationContext(), SerialPortService.class));
            }
        } catch (Exception ex) {
            LogError(TAG, (ex));
        }


    }

    private void releaseLock() {
        try {
            if (WAKELOCK != null)
                WAKELOCK.release();
            WAKELOCK = null;
            LogWarn("Release LOCK");

        } catch (Exception ex) {
            LogError(TAG, (ex));
        }

    }

    /**
     * Start Main Data Reader
     *
     * @return
     */
    private boolean StartReader() {

        if (fileProcessor != null)
            fileProcessor.initialize();

        String pid = getPatient();

        if (vitalMonitoring != null)
            vitalMonitoring.setPatient(pid);

        if (tremorAggregator != null)
            tremorAggregator.setPatient(pid);
        if (activityMonitoring != null)
            activityMonitoring.setPatient(pid);

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);//Handler.getInstance().getSettings();
        mBandEnabled = settings.isBandEnabled();

        mAccDeviceEnabled = settings.isDevEnabled();
        mSTEnabled = settings.isSTEnabled();
        mLocationEnabled = settings.isLocationEnabled();
        deviceId = settings.getDeviceId();

        try {
            //Start Reader Wake Lock
            if (useDeviceLock()) {
                wakeLock();
                Thread.sleep(1000);
            }

        } catch (Exception ex) {
            LogInfo("LOCK EXCEPTION");
        }

        registerLocationListener();
        sessionRunning = true;

        if (mAccDeviceEnabled) {
            try {
                //  wakeLock();
                //  Thread.sleep(1000);

                ///Wait a little bit
                registerDeviceSensors();

                //releaseLock();

            } catch (Exception ex) {

                LogFatal("SENSORS", ex, FATAL_DEVICE_SENSORS);

            }

        }

        LogWarn("RECORDING RESUMED", 43);

        if (mBandEnabled) {

            try {
                ConnectToBand();


            } catch (Exception ex) {

                LogError(ex.getMessage());
            }


        }

        return true;
    }

    /**
     * Stop Reader for Recording
     *
     * @return
     */
    private boolean StopReader() {

        /// UnRegister Receiver
        if (receiverRegistered) {

            unregisterReceiver(mReceiver);
            receiverRegistered = false;
        }

        //Unregister Location Listener
        unRegisterLocationListener();


        //Unregister Device Sensors
        if (mAccDeviceEnabled) {
            unRegisterDeviceSensors();
        }

        ///Mark that session is not running
        sessionRunning = false;

        //CLose File processors
        if (fileProcessor != null)
            fileProcessor.finalize();

        //Stop Reader Release Lock
        if (useDeviceLock())
            releaseLock();

        //Finally disconnect band since that can fail
        try {
            disconnectBand();
            LogInfo("Unregister Band Sensors");
        } catch (Exception ex) {

            LogError("Disconnection error");

        }


        notifyListeners();

        LogWarn("Service stopped", 1);

        return true;


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Log4jConfigure.configureRollingFile();//configureFromXmlString

        LogDebug("Services start command");

        if (!sessionRunning) {

            if (getSessionMustRun()) {

                StartRecording();
            }


        } else {

            startTimer();
            startCommQueue();

        }
        InitBluetoothListener();
        foreground();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    //region Bluetooth

    /**
     * Start Recording
     */
    public void StartRecording() {

        // WriteToastMessage("pdmanager starting");
        LogDebug("PDManager starting");

        //Set Foreground


        startTimer();
        startCommQueue();

        if (scheduledRun()) {
            ///First enable bluetooth
            BluetoothHelper.enableBluetooth();
            ///Start Reader
            StartReader();
        }

        notifyListeners();


    }

    @Override
    public void onDestroy() {

        LogDebug("Services destroy");
        stopTimer();
        StopReader();
        stopCommQueue();

        super.onDestroy();

    }
    //endregion

    //region Register Sensor Listeners


    /**
     * Register Location Listener
     */
    private void registerLocationListener() {
        if (mLocationEnabled) {

            if (locListener == null)
                locListener = new PDLocationListener(this);

            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, locListener);
                LogInfo("GPS listener enabled");

            } catch (SecurityException ex) {

                LogFatal("Cannot Register GPS listener for Security reasons", FATAL_GPS_SECURITY);
            } catch (Exception ex) {

                LogFatal("Cannot Register GPS listener", FATAL_GPS_EXCEPTION);
            }

        }
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
    //endregion

    //region Unregister listeners

    /**
     * Register Device Motion Sensors
     */
    private void registerDeviceSensors() {

        ///Get Device Sensors
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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

    /**
     * Un Register location Listener
     */
    private void unRegisterLocationListener() {
        if (locListener != null) {

            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                locationManager.removeUpdates(locListener);

                locListener = null;

            } catch (SecurityException ex) {

                LogError("Cannot Remove GPS Listener");
            } catch (Exception ex) {

                LogError("Cannot Remove GPS Listener", ex);
            }

        }

    }

    //endregion

    //region Data Handlers

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
     * Unregister Device Sensors
     */
    private void unRegisterDeviceSensors() {
        try {

            LogInfo("Unregister device sensors");
            senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            senSensorManager.unregisterListener(this);
        } catch (Exception ex) {

            LogError("Unregister device sensors error");
            //  Util.handleException("Unregister device sensors",ex);
        }

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

    /**
     * Handle Data
     *
     * @param data Data to Handle
     */
    public void handleData(ISensorData data) {

        addDataToProcessors(data);

        for (int i = 0; i < dataHandlers.size(); i++) {

            dataHandlers.get(i).handleData(data);

        }
    }

    /**
     * Add Data to processors
     *
     * @param data Data to add
     */
    private void addDataToProcessors(ISensorData data) {
        for (int i = 0; i < processors.size(); i++)
            if (processors.get(i).requiresData(data.getDataType()))
                processors.get(i).addData(data);

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

    //region Logs
    private void LogFatal(String e, int code) {
        Log.e(TAG, e);

        // LogError(e);
        mFatalError = true;
        mFatalErrorCode = code;
        ProcessLog("FATAL", e);
        notifyListeners();
        SendAlert(e, String.format("ERROR%03d", code));
        Bugfender.f(TAG, String.format("ERROR%03d", code));


    }

    private void LogFatal(String mess, Exception ex, int code) {

        Log.e(TAG, mess, ex.getCause());

        // logger.log(Level.FATAL,String.format("ERROR%03d", code));
        mFatalError = true;
        mFatalErrorCode = code;
        ProcessLog("FATAL", mess);
        notifyListeners();
        SendAlert(mess, String.format("ERROR%03d", code));


    }

    private void LogError(String source) {
        // logger.log(Level.ERROR,source);

        Bugfender.e(TAG, source);
        Log.e(TAG, source);
        // ProcessLog(TAG, source);

    }

    private void LogError(String source, Throwable cause) {

        //logger.log(Level.ERROR,source,cause);
        Bugfender.e(TAG, source);
        Log.e(TAG, source, cause);
        // ProcessLog(TAG, source);

    }

    private void LogError(String e, Exception ex) {
        //logger.log(Level.ERROR,e,ex.getCause());

        Bugfender.e(TAG, e);
        Log.e(TAG, e, ex.getCause());
        // ProcessLog(TAG, e);

    }

    private void LogInfo(String e) {
        //logger.log(Level.INFO,e);
        Log.i(TAG, e);

    }

    private void LogDebug(String e) {

        //logger.log(Level.DEBUG,e);
        Log.d(TAG, e);


    }

    private void LogWarn(String e) {
        //logger.log(Level.WARN,e);
        Log.w(TAG, e);
        Bugfender.w(TAG, e);
        // ProcessLog("WARNING", e);

    }

    /**
     * Send Warning with code
     * This is also sent as an alert
     *
     * @param e
     * @param code
     */
    private void LogWarn(String e, int code) {
        //logger.log(Level.WARN,e);
        Log.w(TAG, e);
        //  ProcessLog("WARNING", e);

        SendAlert(e, String.format("WARN%03d", code));

    }
    //endregion

    //region Send Alert
    private void SendAlert(String message, String code) {

        try {
            String title = "FATAL ERROR";

            LogDebug(message);

            ArrayList<Alert> alerts = new ArrayList<>();
            alerts.add(new Alert(title, message, code, System.currentTimeMillis(), deviceId));
            manager.SendItems(alerts);
        } catch (Exception ex) {
            LogError(ex.getMessage());
        }


    }

    //endregion

    /**
     * Send Alert to PD Cloud
     *
     * @param title
     * @param message
     * @param code
     */
    private void SendAlert(String title, String message, String code) {

        try {

            Log.i(code, message);
            ArrayList<Alert> alerts = new ArrayList<>();
            alerts.add(new Alert(title, message, code, System.currentTimeMillis(), getPatient()));
            manager.SendItems(alerts);

        } catch (Exception ex) {
            LogDebug(ex.getMessage());

        }


    }

    //region Checks

    private synchronized void AddBandMessage(String title, String message, long expiration, boolean isMessage) {

        try {
            bandMessageQueue.add(new BandMessage(title, message, expiration, isMessage));

        } catch (Exception ex) {

            LogError("Add Band MEssage", ex.getCause());
        }

    }

    /**
     * Check if session is running
     *
     * @return
     */

    public boolean isSessionRunning() {

        return sessionRunning;
    }

    /**
     * Check if all sensors are recording
     *
     * @return
     */
    public boolean isAllRecording() {

        boolean allOK = true;
        long t = System.currentTimeMillis();
        if (mBandEnabled) {

            if (t - lastBandAcquisition > 60 * 1000) {

                allOK = false;

            }


        }

        if (mAccDeviceEnabled) {

            if (t - linearAccelerationTime2 > 60 * 1000) {
                allOK = false;

            }

        }

        return allOK;


    }
    //endregion

    //region Main Task

    public int getFatalErrorCode() {

        return mFatalErrorCode;

    }

    //endregion

    public boolean hasFatalError() {

        return mFatalError;


    }

    private synchronized void ConnectToBand() throws InterruptedException {

        try {
            if (!lockBandClient()) {
                LogWarn("Band Client locked");
                return;
            }


        } catch (Exception ex) {

            Log.d(TAG, "Lock Band Client (ConnectToBand)", ex.getCause());
        }
        if (mClient == null) {

            try {
                BandClientManager manager = BandClientManager.getInstance();
                BandInfo[] mPairedBands = manager.getPairedBands();

                if (mPairedBands.length > 0) {

                    mClient = manager.create(this, mPairedBands[0]);

                } else {
                    LogFatal("Number of Band Sensors is 0", FATAL_BAND_SENSNUMBER);
                    if (!mFatalError) {
                        mFatalError = true;
                        mFatalErrorCode = 1;


                    }
                    //Don't forget to unlock band client
                    try {

                        unlockBandClient();

                    } catch (InterruptedException ex) {

                        Log.d(TAG, "Unlock Band client", ex.getCause());

                    }

                    return;


                }

            } catch (Exception ex) {

                LogFatal("Exception while connecting to band", FATAL_BAND_CONNECTION);
            }


        }

        ///IF Mclient is connected then some leak has occured

        if (!mClient.isConnected()) {

            new ConnectTask().execute(mClient);
        } else {

            try {
                unlockBandClient();
            } catch (InterruptedException ex) {

                Log.d(TAG, "Unlock Band client", ex.getCause());

            }


        }


    }


    //endregion

    //region diagnostics

    private boolean isServiceRunning(String serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkServicesRunning() {

        Log.v(TAG, "List of running services");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.v(TAG, service.service.getClassName());
        }

    }

    private boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {

        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }

                }
            }
        } catch (Exception ex) {

        }

        return false;
    }

    private boolean isPowerSaveMode() {

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return pm.isDeviceIdleMode();
        } catch (Exception ex) {

            LogError("Power save Mode", ex.getCause());
        }

        return false;

    }


    private boolean isScreenOn() {

        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            return pm.isInteractive();
        } catch (Exception ex) {

            LogError("Screen On check", ex.getCause());
        }

        return false;

    }
    //endregion

    /**
     * Place the service into the foreground
     */
    public void foreground() {

        if (useForeground() && !isServiceRunningInForeground(this.getApplicationContext(), RecordingServiceAllInOne.class)) {
            LogWarn("Setting service foreground");
            startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    /**
     * Return the service to the background
     */
    public void background() {

        if (isServiceRunningInForeground(this.getApplicationContext(), RecordingServiceAllInOne.class)) {
            LogWarn("Setting Service in Background");
            stopForeground(true);
        }
    }

    /**
     * Creates a notification for placing the service into the foreground
     *
     * @return a notification for interacting with the service when in the foreground
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("PD Manager Service Active")
                .setContentText("Thanks for participating to our study.")
                .setSmallIcon(com.pdmanager.R.drawable.pdmanagersmall);

        Intent resultIntent = new Intent(this, RecordingServiceAllInOne.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }

    //region foreground and background service

    //region Local Binder
    public class LocalBinder extends Binder {
        public RecordingServiceAllInOne getService() {
            // Return this instance of LocalService so clients can call public methods
            return RecordingServiceAllInOne.this;
        }
    }

    /**
     * Main Task
     */
    private class mainTask extends TimerTask {

        private final IPDToastWriter writer;

        public mainTask(IPDToastWriter pwriter) {

            writer = pwriter;


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
                        serviceCheck();
      /* and here comes the "trick" */

                    }
                });

            } else {

                LogDebug("Timer sooner than expected");
            }

            // serviceCheck();
            //Commented previous implementation
            /*
                    try {

                        boolean bandActionTaken = false;

                        Log.d(TAG, "Timer check");

                        //region Block for not paused
                        if (!schedulerPause) {

                            if (!scheduledRun()) {

                                if (sessionRunning) {
                                    /// IF SESSION RUNNING STOP SESSION
                                    ///Notify Server
                                    LogInfo("Service stopped for today");
                                    SendAlert("INFO", "Service stopped for today", "INFO0002");

                                    bandActionTaken = true;
                                    //Stop Reader
                                    StopReader();
                                }


                            } else {

                                boolean sessionMustRun = getSessionMustRun();

                                if (sessionMustRun) {

                                    enableBluetooth();

                                    //IF Session Not Running
                                    if (!sessionRunning) {

                                        bandActionTaken = true;
                                        //Start Reader
                                        StartReader();


                                    } else {

                                        //Session Running
                                        // Check For Errors
                                        if (!bandActionTaken) {

                                            try {

                                                checkBandConnectionStatus();

                                            } catch (InterruptedException ex) {

                                                LogError("EX ON SCHEDULING", ex.getCause());
                                            } catch (Exception ex) {

                                                LogError("EX ON SCHEDULING", ex.getCause());
                                            }

                                        }

                                    }


                                }

                            }

                            ///Check For Alert only if

                        }
                        //endregion

                        if (!bandActionTaken) {

                            try {
                                scheduling();

                            } catch (InterruptedException ex) {

                                LogError("EX ON SCHEDULING", ex.getCause());
                            } catch (Exception ex) {

                                LogError("EX ON SCHEDULING", ex.getCause());
                            }

                        }

                        ///Check For Device Sensors
                        if (scheduledRun()) {

                            if (sessionRunning) {

                                checkMobileDeviceDataAcquired();
                            }

                        }

                        //Check for Medication User Alert
                        //checkForMedAlerts();
                        //Check For Alerts
                        if (scheduledRun()) {

                            if (sessionRunning) {
                                //Update Usage Statistics
                                updateUsageStatistics();

                            }
                        }

                        //Check For User Notifications
                        checkForUserNotifications();


                    } catch (Exception ex) {

                        Log.e(TAG, ex.getMessage());
                    }  */
            //endregion
        }


    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class CheckMedicationTask extends AsyncTask<Void, Void, UserAlert> {


        private Context mContext;

        private CheckMedicationTask(Context pContext) {

            this.mContext = pContext;

        }

        @Override
        protected UserAlert doInBackground(Void... clientParams) {

            LogDebug("Check Medication");

            ///First clear all alerts
            try {
                MedManager manager = new MedManager(mContext);
                UserAlertManager alertmanager = new UserAlertManager(mContext);

                ///Check for alert
                int ret = manager.pendingMedication();
                if (ret > 0) {
                    UserAlert alert = manager.getPendingMedAlert(ret);
                    alertmanager.add(alert);
                }

                //OK CHECK FOR NEXT MEDICATION MESSAGE
                UserAlert alert = manager.getNextMedication();
                if (alert != null) {

                    manager.setLastMessage(alert.getSource());
                }
                return alert;


            } catch (Exception ex) {

                LogError(ex.getMessage());
            }
            return null;
        }

        /**
         * TODO: ADD MESSAGE TO A BAND MESSAGE QUEUE
         *
         * @param alert
         */
        protected void onPostExecute(UserAlert alert) {

            if (alert != null) {

                AddBandMessage(alert.getTitle(), alert.getMessage(), alert.getExpiration(), true);
            }


        }
    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class ConnectTask extends AsyncTask<BandClient, Void, ConnectionResult> {
        @Override
        protected ConnectionResult doInBackground(BandClient... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
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

                        try {
                            LogWarn("Disconnecting to Microsoft Band");
                            mClient.disconnect().await(bandClientWaitIntervalInSeconds, TimeUnit.SECONDS);


                        } catch (Exception ex) {
                            LogWarn("Error disconnecting from Microsoft Band");

                        }


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

            notifyListeners();

        }
    }
}

