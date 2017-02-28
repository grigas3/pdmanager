/*
 * Copyright (c) 2015.  This code is developed for the PDManager EU project and can be used only by the consortium for the purposes of the project only.
 * Author George Rigas.
 */

package com.pdmanager.services;

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
import android.util.Log;
import android.widget.Toast;

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
import com.pdmanager.common.ConnectionResult;
import com.pdmanager.common.Util;
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
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.common.interfaces.IPDToastWriter;
import com.pdmanager.FileDataProcessor;
import com.pdmanager.app.PostureDataProcessor;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.CommunicationRunner;
import com.pdmanager.communication.ICommunicationQueue;
import com.pdmanager.communication.JsonStorage;
import com.pdmanager.communication.NetworkStatus;
import com.pdmanager.communication.SQLCommunicationList;
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

/**
 * Created by George on 5/21/2015.
 */
public class RecordingService extends Service implements IDataHandler, SensorEventListener, IPDToastWriter, ILogHandler, IJsonRequestHandler, INetworkStatusHandler, ITokenUpdater {

    private static final String TAG = "RECORDING";
    // The minimum distance to change Updates in meters
    private static final long MIN_UPDATE_DISTANCE = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 1000 * 5;
    //Try to reconnect at least after XX minutes
    private static final long bandReconnectAttemptInterval = 0 * 60 * 1000;
    private static boolean sessionRunning = false;
    private final IBinder mBinder = new LocalBinder();
    private final List<IDataProcessor> processors = new ArrayList<IDataProcessor>();
    private final SampleRate bandRate = SampleRate.MS16;
    private final float gforce = 9.806F;
    private final BandMessageQueue bandMessageQueue = new BandMessageQueue();
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
    private final Semaphore available = new Semaphore(1, true);
    float[] trueAcceleration = new float[4];
    float[] R = new float[16];
    float[] RINV = new float[16];
    float[] relativacc = new float[4];
    float[] orientVals = new float[3];
    VitalMonitoring vitalMonitoring;
    ActivityMonitoring activityMonitoring;
    private int connectedStateFound = 0;
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
    private ArrayList<IDataHandler> dataHandlers = new ArrayList<IDataHandler>();
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


    /////RECOVER POLICIES
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
    private boolean restartBluetoothAdaptor = true;
    private int devsamples = 0;
    private int bandsamples = 0;
    private int hrsamples = 0;
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
    private BandAccelerometerEventListener mAccelerometerEventListener = new BandAccelerometerEventListener() {
        @Override
        public void onBandAccelerometerChanged(final BandAccelerometerEvent event) {

            mPendingAccelerometerEvent.set(event);
            scheduleSensorHandler();
        }
    };
    private String deviceId;
    private boolean schedulerPause = false;
    private RecordingScheduler scheduler;

    public boolean isSessionRunning() {

        return sessionRunning;
    }

    public void registerHRAccessProvider(IHeartRateAccessProvider handler) {

        heartRateAccessProvider = handler;
    }

    public void registerHandler(IDataHandler handler) {

        if (!dataHandlers.contains(handler))
            dataHandlers.add(handler);

    }

    public void unregisterHandler(IDataHandler handler) {
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
            LogError("Process Log", ex.getCause());

        } finally {

            if (sqlDB != null)
                sqlDB.close();

            if (handler != null)
                handler.close();


        }

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

    @Override
    public void onCreate() {
        // The service is being created

        manager = new CommunicationManager(this);
        InitProcessors();

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

    private void startTimer() {

        if (timer != null) {
            try {
                timer = new Timer();
                timer.scheduleAtFixedRate(new mainTask(this), 30000, 60000);

            } catch (Exception ex) {

                LogError("Timer", ex);
            }
        }

    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }


    }

    public void WriteToastMessage(String message) {

        Message s = new Message();
        s.obj = message;
        toastHandler.sendMessage(s);


    }

    private boolean scheduledRun() {

        // if(schedulerPause)
        //  return false;

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            Calendar c = Calendar.getInstance();

            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

            return !(hourOfDay < settings.getStartHour() || hourOfDay >= settings.getStopHour());

        } else
            return false;


    }

    public boolean getSessionMustRun() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            return settings.getSessionRunning();
        }

        return false;
    }

    @Override
    public void AddRequest(JsonStorage jsonRequest) {

        if (queue != null)
            queue.push(jsonRequest);

    }

    private void startCommQueue() {

        if (queue == null) {

            try {
                Gson gson = new Gson();

                // queue = new CommunicationQueue(CommunicationQueue.CreateQueueFile(), new JsonConverter<JsonStorage>(gson, JsonStorage.class));

                queue = new SQLCommunicationList(this);
                queueRunner = new CommunicationRunner(queue, this, getPatient(), this);

                queueRunner.setQueueRunning(true);
                queueThread = new Thread(queueRunner);
                queueThread.start();


            } catch (Exception ex) {

                LogError("Cannot Create Queue: " + ex.getMessage());
            }

        }


    }

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

    private void enableBluetooth() {
        bluetoothEnabled = isBluetoothEnabled();

        if (!bluetoothEnabled) {

            try {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                mAdapter.enable();
                Thread.sleep(3000);

                LogInfo("Bluetooth activated by service");
                //LogInfo("Bluetooth activated by service");
            } catch (Exception ex) {

                LogError("Cannot activate bluetooth Band Sensors");
                //LogInfoError("Cannot activate bluetooth Band Sensors");

            }

            //activateBluetooth();

        }

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

    private String getPatient() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this);

        if (settings != null) {
            return settings.getPatientID();


        }

        return "TEST_PAT";
    }

    private void updateUsageStatistics() {

        long unixTime = System.currentTimeMillis();

        ///if (unixTime - lastUsageTimestamp > 30* 1000) {
        if (unixTime - lastUsageTimestamp > 60 * 30 * 1000) {

            try {

                String patient = getPatient();

                UsageStatistic obs1 = new UsageStatistic(bandsamples, "001", unixTime, patient, deviceId);
                UsageStatistic obs2 = new UsageStatistic(devsamples, "002", unixTime, patient, deviceId);
                UsageStatistic obs3 = new UsageStatistic(hrsamples, "003", unixTime, patient, deviceId);

                ArrayList<UsageStatistic> obsArray = new ArrayList<>();

                obsArray.add(obs1);
                obsArray.add(obs2);
                obsArray.add(obs3);
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

    ///TODO: ....DO IT ALL ASYNC!!!!!
    //TODO: Add band messages and notifications in a queue and execute them from here.
    //TODO:this will neglect the probability of  duplicate Band Clients

    private void checkForUserNotifications() {

        long unixTime = System.currentTimeMillis();

        if ((unixTime - lastUserNotificationCheck) / 1000 > 60) {

            try {

                UserAlertManager alertmanager = new UserAlertManager(this);
                lastUserNotificationCheck = unixTime;
                if (alertmanager.anyUnNotified()) {

                    UserAlert alert = alertmanager.getUnNotified();

                    if (alert != null) {
                        bandMessageQueue.add(new BandMessage(alert.getTitle(), alert.getMessage(), alert.getExpiration(), false));
                    }
                    ///TODO: ADD TO BAND NOTIFICATION QUEUE
                  /*  if(mClient!=null) {
                        BandNotificationTask.newInstance(mClient, RecordingSettings.newInstance(this).getTileUUID()).execute(alert);

                    }
                    */
                    alertmanager.setNotified(alert);
                }


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

    private void checkForAlerts() throws InterruptedException {

        if (sessionRunning) {
            long unixTime = System.currentTimeMillis();

            if (mBandEnabled) {

                if ((unixTime - lastBandAcquisition) / 1000 > 60) {

                    if (mBandAcquiring) {
                        LogFatal("Band has at least 60 seconds to acquire", 2);
                        //LogWarn("Band has at least 30 seconds to acquire");

                    }

                    boolean shoudTryReconnect = true;
                    if (mClient != null) {
                        if (mClient.getConnectionState() == ConnectionState.DISPOSED) {

                            LogInfo("Connection Disposed");


                        }

                        if (mClient.getConnectionState() == ConnectionState.CONNECTED) {

                            if (!bandSensorsRegistered) {

                                try {

                                    lockBandClient();
                                    initSensorListeners();

                                } catch (Exception ex) {

                                    LogError("ALERT REGISTER LISTENERS", ex.getCause());
                                } finally {
                                    unlockBandClient();
                                }
                                //registerBandSensors(senSensorManager);

                            }
                            LogInfo("Connection CONNECTED");

                            if (connectedStateFound > 10) {

                                shoudTryReconnect = true;

                            } else {
                                shoudTryReconnect = false;
                            }

                            connectedStateFound++;

                        }

                        if (mClient.getConnectionState() == ConnectionState.BOUND) {

                            LogDebug("Connection BOUND");


                        }

                        if (mClient.getConnectionState() == ConnectionState.UNBOUND) {

                            LogDebug("Connection UNBOUND");


                        }
                    }

                    ///TRY TO RECONNECT IF NOT CONNECTED EVERY 1 MIN
                    if (shoudTryReconnect && (unixTime - lastBandConnectionTry) > bandReconnectAttemptInterval) {

                        connectedStateFound = 0;
                        ReconnectBand(restartBluetoothAdaptor);


                    }

                    // lastBandAcquisition=unixTime;
                    mBandAcquiring = false;

                } else {

                    if (!mBandAcquiring) {
                        LogWarn("Band receiving data...");

                    }

                    if (mFatalError) {

                        SendAlert("INFO", "Band Acquiring data", "INFOCODE002");

                    }

                    forwardBandMessages();

                    LogDebug("Service Running  and receiving data...");
                    mFatalError = false;
                    mBandAcquiring = true;
                }


            }

        }


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
        boolean be = isBluetoothEnabled();
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

        try {

            if (be) {

                mAdapter.disable();

            }


        } catch (Exception ex) {
            LogError("Error while disconnecting/connecting bluetooth: " + ex.getMessage());
        }


    }

    private void scheduling() throws InterruptedException {

        if (scheduler != null) {

            if (!schedulerPause) {

                if (scheduler.shouldPause() > 0) {

                    ///DISCONNECT IF NEEDED
                    if (mClient != null) {

                        schedulerPause = true;

                        try {
                            BandSensorManager sensorMgr = mClient.getSensorManager();
                            if (bandSensorsRegistered) {
                                unRegisterBandSensors(sensorMgr);

                            }

                            disconnectBand();
                            mClient = null;


                        } catch (Exception ex) {

                            LogDebug(ex.getMessage());

                        }


                    }

                }

            } else {

                if (scheduler.isResumed()) {

                    schedulerPause = false;

                }

                if (scheduler.shouldResume()) {

                    schedulerPause = false;
                    scheduler.increaseInterval();
                    ///DISCONNECT IF NEEDED

                    ReconnectBand(false);
                   /* if (mClient != null) {


                        disconnectBand();

                        mClient = null;

                    }

                    ConnectToBand();
                    */

                }
            }
        }


    }


    /**
     * Lock Band Client
     * @throws InterruptedException
     */
    private void lockBandClient() throws InterruptedException {

        available.acquire();

    }


    /**
     * Unlock Band Client
     * @throws InterruptedException
     */
    private void unlockBandClient() throws InterruptedException {

        available.release();

    }


    /**
     * Disconnect Band
     * @throws InterruptedException
     */
    private synchronized void disconnectBand() throws InterruptedException {

        int tries = 0;
        if (mClient.isConnected()) {

            boolean disconnected = false;
            do {
                try {

                    //Lock Band Client
                    lockBandClient();

                    if (mClient != null && mClient.isConnected()) {

                        if (bandSensorsRegistered) {
                            BandSensorManager mgr = mClient.getSensorManager();
                            unRegisterBandSensors(mgr);
                        }
                        LogInfo("Disconnecting Band for reconnection");
                        mClient.disconnect().await(60, TimeUnit.SECONDS);

                    }
                    disconnected = true;

                } catch (Exception ex) {

                    LogError("Error while disconnecting: " + ex.getMessage());
                    //MINOR Exception
                } finally {

                    //Finally unlock band client
                    unlockBandClient();
                }
                if (!disconnected)
                    Thread.sleep(1000);
                tries++;
            } while (!disconnected && tries < 10);


        }


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

                    lockBandClient();

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
                                mClient.disconnect().await(60, TimeUnit.SECONDS);

                            }
                            disconnected = true;

                        } catch (Exception ex) {

                            LogError("Error while disconnecting: " + ex.getMessage());
                            //MINOR Exception
                        }

                        if (!disconnected)

                            Thread.sleep(1000);
                        tries++;
                    } while (!disconnected && tries < 10);

                    unlockBandClient();


                }

                //NEXT RESTART BLUETOOTH IF THIS STRATEGY IS APPLIED
                if (dorestartBluetoothAdaptor) {

                    //OK NOW DISABLE AND ENABLE AGAIN THE BLUETOOTH
                    boolean be = isBluetoothEnabled();
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

                    boolean be = isBluetoothEnabled();

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
                    LogInfo(" Reconnecting from scratch");
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

    /**
     * Require permissions for accessing Heart Rate Sensor
     */
    private void requireHeartRatePermissions() {

        if (mClient != null) {
            BandSensorManager sensorMgr = mClient.getSensorManager();
            if (sensorMgr.getCurrentHeartRateConsent() !=
                    UserConsent.GRANTED) {
                // user has not consented, request it
                // the calling class is both an Activity and implements
                // HeartRateConsentListener

                this.heartRateAccessProvider.requestHeartRateConsent(sensorMgr);

            }

        }

    }

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
     * Stop Recording
     */
    public void StopRecording() {
        StopReader();
        stopTimer();
        stopCommQueue();

        closeBluetooth();

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

        if (locListener != null) {

            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                locationManager.removeUpdates(locListener);

                locListener = null;

            } catch (SecurityException ex) {

                LogError("Cannot Remove GPS Listener");
            } catch (Exception ex) {

                LogError("Cannot Remove GPS Listener");
            }

        }

        if (mClient != null && mClient.isConnected()) {

            try {

                BandSensorManager sensorMgr = mClient.getSensorManager();

                //LogInfo("Cannot activate bluetooth Band Sensors");
                LogInfo("Unregister Band Sensors");
                if (bandSensorsRegistered) {
                    unRegisterBandSensors(sensorMgr);
                }

                mClient.disconnect().await(60, TimeUnit.SECONDS);

                mbandConnected = false;

            } catch (Exception ex) {
                // Util.showExceptionAlert(getActivity(), "Disconnect", ex);
                LogError("Disconnect error");
                Util.handleException("Disconnect", ex);

                return false;
            }

        }

        if (mAccDeviceEnabled) {
            try {

                LogInfo("Unregister device sensors");
                senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                senSensorManager.unregisterListener(this);
            } catch (Exception ex) {

                LogError("Unregister device sensors error");
                //  Util.handleException("Unregister device sensors",ex);
            }

        }

        ///Mark that session is not running
        sessionRunning = false;

        if (fileProcessor != null)
            fileProcessor.finalize();

        notifyListeners();

        LogWarn("Service stopped", 1);

        return true;


    }

    private void InitBluetoothListener() {

        if (!receiverRegistered) {
            registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            receiverRegistered = true;
        }
    }

    private synchronized void ConnectToBand() throws InterruptedException {

        lockBandClient();
        if (mClient == null) {

            try {
                BandClientManager manager = BandClientManager.getInstance();
                BandInfo[] mPairedBands = manager.getPairedBands();

                if (mPairedBands.length > 0) {

                    mClient = manager.create(this, mPairedBands[0]);

                } else {

                    if (!mFatalError) {
                        mFatalError = true;
                        mFatalErrorCode = 1;
                        LogFatal("Number of Band Sensors is 0", 1);

                    }

                    return;


                }

            } catch (Exception ex) {

                LogFatal("Exception while connecting to band", 3);
            }


        }

        ///IF Mclient is connected then some leak has occured

        if (!mClient.isConnected()) {

            new ConnectTask().execute(mClient);
        } else {

            unlockBandClient();

        }


    }

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
        mAccBandEnabled = settings.isBandEnabled();
        mHeartRateEnabled = settings.isBandEnabled();
        mGyroBandEnabled = settings.isBandEnabled();

        mAccDeviceEnabled = settings.isDevEnabled();
        mPedoEnabled = settings.isBandEnabled();
        mSTEnabled = settings.isSTEnabled();
        mLocationEnabled = settings.isLocationEnabled();
        deviceId = settings.getDeviceId();

        if (mLocationEnabled) {

            if (locListener == null)
                locListener = new PDLocationListener(this);

            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, locListener);
                LogInfo("GPS listener enabled");

            } catch (SecurityException ex) {

                LogError("Cannot Register GPS listener");
            } catch (Exception ex) {

                LogError("Cannot Register GPS listener");
            }

        }

        sessionRunning = true;

        if (mAccDeviceEnabled) {
            try {

                RegisterDeviceSensors();


            } catch (Exception ex) {

                Util.handleException("Register Device Sensors", ex);

            }

        }
      /*  else

        {

            try {

                LogInfo("Device sensors unregistered");

                senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                senSensorManager.unregisterListener(this);
            } catch (Exception ex) {

                Util.handleException("Unregister Device Sensors",ex);
            }

        }
        */
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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    /**
     * Start Recording
     */
    public void StartRecording() {

       // WriteToastMessage("pdmanager starting");
        LogDebug("PDManager starting");

        startTimer();
        startCommQueue();

        if (scheduledRun()) {
            ///First enable bluetooth
            enableBluetooth();
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

    }

    /**
     * Check if bluetooth is enabled
     * @return
     */
    private boolean isBluetoothEnabled() {

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter != null) {
                return mBluetoothAdapter.isEnabled();

            }
            return false;

        } catch (Exception ex) {

            return false;
        }
    }

    /**
     * Activate Bluetooth
     */
    private void activateBluetooth() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {

                try {
                    bluetoothEnabled = false;

                    Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    // The REQUEST_ENABLE_BT constant passed to startActivityForResult() is a locally defined integer (which must be greater than 0), that the system passes back to you in your onActivityResult()
                    // implementation as the requestCode parameter.
                    int REQUEST_ENABLE_BT = 1;
                    startActivity(intentBtEnabled);

                } catch (Exception ex) {

                    LogError("Error In getting bluetooth information");
                }

            }
        } else {

            bluetoothEnabled = true;
        }


    }

    /**
     * Register Band Sensors
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
     * Register Device Sensors
     */
    private void RegisterDeviceSensors() {

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

        }
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            AccMData accData = new AccMData();
            accData.setValue(new AccReading(sensorEvent.values[0] / gforce, sensorEvent.values[1] / gforce, sensorEvent.values[2] / gforce));
            accData.setTimestamp(new Date());
            accData.setTicks(sensorEvent.timestamp);

            handleData(accData);

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

    private void LogFatal(String e, int code) {

        LogError(e);
        mFatalError = true;
        mFatalErrorCode = code;
        ProcessLog("FATAL", e);
        notifyListeners();
        SendAlert(e, String.format("ERROR%03d", code));


    }

    private void LogError(String source) {

        Log.e(TAG,source);
        ProcessLog(TAG, source);

    }

    private void LogError(String source, Throwable cause) {

        Log.e(TAG,source, cause);
        ProcessLog(TAG, source);

    }

    private void LogError(String e, Exception ex) {

        Log.e(TAG,e, ex.getCause());
        ProcessLog(TAG, e);

    }

    private void LogInfo(String e) {

        Log.i("INFO", e);

    }

    private void LogDebug(String e) {
        Log.d(TAG,e);


    }

    private void LogWarn(String e) {
        Log.w(TAG, e);
        ProcessLog("WARNING", e);

    }

    /**
     * Send Warning with code
     * This is also sent as an alert
     *
     * @param e
     * @param code
     */
    private void LogWarn(String e, int code) {
        Log.w("WARNING", e);
        ProcessLog("WARNING", e);
        SendAlert(e, String.format("WARN%03d", code));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

        //LogInfo(1,"Accuracy Changed  to"+i);

    }

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

    public int getFatalErrorCode() {

        return mFatalErrorCode;

    }

    public boolean hasFatalError() {

        return mFatalError;


    }

    private synchronized void AddBandMessage(String title, String message, long expiration, boolean isMessage) {

        try {
            bandMessageQueue.add(new BandMessage(title, message, expiration, isMessage));

        } catch (Exception ex) {

            LogError("Add Band MEssage", ex.getCause());
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
            //  toastHandler.sendEmptyMessage(0);



            boolean bandActionTaken = false;

            if (!schedulerPause) {

                if (!scheduledRun()) {

                    if (sessionRunning) {

                        ///Notify user using toast messagfe
                        if (toastHandler != null) {
                            Message s = new Message();
                            s.obj = "Service stopped for today";
                            toastHandler.sendMessage(s);
                        }

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


                        }


                    }

                }
                if (!bandActionTaken) {

                    try {

                        checkForAlerts();

                    } catch (InterruptedException ex) {

                        LogError("EX ON SCHEDULING", ex.getCause());
                    } catch (Exception ex) {

                        LogError("EX ON SCHEDULING", ex.getCause());
                    }

                }

                ///Check For Alert only if

            }

            if (!bandActionTaken) {

                try {
                    scheduling();

                } catch (InterruptedException ex) {

                    LogError("EX ON SCHEDULING", ex.getCause());
                } catch (Exception ex) {

                    LogError("EX ON SCHEDULING", ex.getCause());
                }

            }

            //Check for Medication User Alert
            checkForMedAlerts();
            //Check For Alerts

            //Update Usage Statistics
            updateUsageStatistics();

            //Check For User Notifications
            checkForUserNotifications();
        }


    }

    public class LocalBinder extends Binder {
        public RecordingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RecordingService.this;
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
    private class CheckAlertTask extends AsyncTask<Void, Void, UserAlert> {


        private Context mContext;

        private CheckAlertTask(Context pContext) {

            this.mContext = pContext;

        }

        @Override
        protected UserAlert doInBackground(Void... clientParams) {

            UserAlertManager alertmanager = new UserAlertManager(mContext);

            try {

                alertmanager.deleteExpired();

                if (alertmanager.anyUnNotified()) {

                    UserAlert alert = alertmanager.getUnNotified();

                    alertmanager.setNotified(alert);

                    return alert;
                }

            } catch (Exception ex) {

                LogError("Create ALERT TASK", ex.getCause());

            }

            return null;
        }

        protected void onPostExecute(UserAlert alert) {

            bandMessageQueue.add(new BandMessage(alert.getTitle(), alert.getMessage(), alert.getExpiration(), false));


        }
    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class ConnectTask extends AsyncTask<BandClient, Void, ConnectionResult> {
        @Override
        protected ConnectionResult doInBackground(BandClient... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                pendingResult = clientParams[0].connect();

                ConnectionState result = pendingResult.await();

                return new ConnectionResult(result);


            } catch (InterruptedException ex) {
                Util.handleException("Connect to band", ex);
                return new ConnectionResult(ex);
                // handle InterruptedException
            } catch (BandException ex) {

                Util.handleException("Connect to band", ex);
                return new ConnectionResult(ex);
                // handle BandException
            } catch (Exception ex) {

                Util.handleException("Connect to band", ex);
                return new ConnectionResult(ex);
                // handle BandException
            }
        }

        protected void onPostExecute(ConnectionResult result) {

            if (result.hasException()) {
                LogError("Error disconnecting from Microsoft Band " + result.getException().getMessage());

            } else {

                if (mClient != null && result.getState() == ConnectionState.CONNECTED) {

                    mbandConnected = true;

                    LogInfo("Connected to Microsoft Band");

                    requireHeartRatePermissions();
                    initSensorListeners();


                }
                if (mClient != null && result.getState() == ConnectionState.BOUND) {

                    LogInfo("Bound to Microsoft Band...needs res");

                }
                if (mClient != null && result.getState() == ConnectionState.DISPOSED) {

                    try {

                        mClient.disconnect().await(60, TimeUnit.SECONDS);

                        LogInfo("Disconnecting to Microsoft Band");
                    } catch (Exception ex) {
                        LogError("Error disconnecting from Microsoft Band");

                    }


                }
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
