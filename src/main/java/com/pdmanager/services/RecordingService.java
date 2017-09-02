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
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.google.gson.Gson;
import com.pdmanager.FileDataProcessor;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.common.interfaces.IPDToastWriter;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.CommunicationRunner;
import com.pdmanager.communication.ICommunicationQueue;
import com.pdmanager.communication.JsonStorage;
import com.pdmanager.communication.NetworkStatus;
import com.pdmanager.communication.SQLCommunicationList;
import com.pdmanager.dataloggers.DeviceSensorLogger;
import com.pdmanager.dataloggers.LocationDataLogger;
import com.pdmanager.dataloggers.MSBandDataLogger;
import com.pdmanager.helpers.BluetoothHelper;
import com.pdmanager.interfaces.IDataLogger;
import com.pdmanager.interfaces.IJsonRequestHandler;
import com.pdmanager.interfaces.INetworkStatusHandler;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.interfaces.ITokenUpdater;
import com.pdmanager.logging.ILogHandler;
import com.pdmanager.medication.MedManager;
import com.pdmanager.models.Alert;
import com.pdmanager.models.LoggerStat;
import com.pdmanager.models.LoginModel;
import com.pdmanager.models.UsageStatistic;
import com.pdmanager.models.UserAlert;
import com.pdmanager.monitoring.ActivityMonitoring;
import com.pdmanager.monitoring.VitalMonitoring;
import com.pdmanager.notification.BandMessage;
import com.pdmanager.notification.BandMessageQueue;
import com.pdmanager.notification.LocalNotificationTask;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.symptomdetector.aggregators.TremorAggregator;
import com.pdmanager.symptomdetector.dyskinesia.DyskinesiaEvaluator;
import com.pdmanager.symptomdetector.tremor.HandPostureDetector;
import com.pdmanager.symptomdetector.tremor.TremorEvaluator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.pdmanager.logging.LogCodes.FATAL_SERVICE_STOPPED_BY_ANDROID;
import static com.pdmanager.logging.LogCodes.FATAL_START_QUEUE;
import static com.pdmanager.logging.LogCodes.FATAL_START_TIMER;

//import com.pdmanager.logging.Log4jConfigure;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/**
 * Created by George on 5/21/2015.
 */
public class RecordingService extends Service implements ISensorDataHandler, IPDToastWriter, ILogHandler, IJsonRequestHandler, INetworkStatusHandler, ITokenUpdater {


    ///Since most timing is performed in second accuracy
    /// Add a margin of seconds to perform operations (60+5)
    private static final long mainTimerInterval = 65 * 1000;
    private static final long minMainTimerInterval = 20 * 1000;

//endregion


    private static final String TAG = "RECORDING";
    private static final String WL_TAG = "RECORDING_WAKE";
    //Update Usage StatisticsInterval
    private static final long updateUsageStatisticsInterval = 60 * 30 * 1000;
    public static PowerManager.WakeLock WAKELOCK = null;
    private static boolean sessionRunning = false;
    private final IBinder mBinder = new LocalBinder();

    private final BandMessageQueue bandMessageQueue = new BandMessageQueue();

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
    private final ArrayList<ISensorDataHandler> dataHandlers = new ArrayList<ISensorDataHandler>();
    private final ArrayList<IServiceStatusListener> listeners = new ArrayList<IServiceStatusListener>();
    private final ArrayList<ISensorStatusListener> sensorListeners = new ArrayList<ISensorStatusListener>();
    private final ArrayList<IDataLogger> dataLoggers = new ArrayList<IDataLogger>();
    private final List<IDataProcessor> processors = new ArrayList<IDataProcessor>();
    VitalMonitoring vitalMonitoring;
    ActivityMonitoring activityMonitoring;
    long totalRAMMemoryUsed = 0;
    int numberOfRamMemoryChecks = 0;
    MSBandDataLogger msBandDataLogger;
    private CommunicationManager manager;
    private boolean mFatalError = false;
    private int mFatalErrorCode = 0;
    private long lastMedAlertCheck = -100000;
    private FileDataProcessor fileProcessor = null;
    private HandPostureDetector handPostureDetector = null;
    private TremorEvaluator tremorEvaluator = null;
    private DyskinesiaEvaluator disEvaluator = null;
    private TremorAggregator tremorAggregator = null;
    private Timer timer = null;
    private Context ctx;
    private CommunicationRunner queueRunner = null;
    private ICommunicationQueue queue = null;
    private Thread queueThread = null;
    private long lastUsageTimestamp = 0;

    private long lastUserNotificationCheck = 0;
    private int devsamples = 0;
    private int bandsamples = 0;
    private int hrsamples = 0;
    private String deviceId;
    private RecordingScheduler scheduler;
    //endregion


    //region Tasks
    private Handler mainTaskHandler = new Handler();

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

        //Get Settings
        RecordingSettings settings = RecordingSettings.newInstance(this);

        //File Data Processor
        if (recordFiles()) {
            fileProcessor = new FileDataProcessor();
            processors.add(fileProcessor);
        }

        //Recoding Scheduler
        if (scheduler == null) {
            scheduler = new RecordingScheduler(settings.getStartHour(), settings.getStopHour());
            processors.add(scheduler);
        }


        try {


            msBandDataLogger = new MSBandDataLogger(this, this);
            if (settings.isBandEnabled()) {
                dataLoggers.add(msBandDataLogger);
            }


            if (settings.isDevEnabled()) {
                DeviceSensorLogger deviceLogger = new DeviceSensorLogger(this, this);
                dataLoggers.add(deviceLogger);

            }

            if (settings.isLocationEnabled()) {

                LocationDataLogger locationListener = new LocationDataLogger(this, this);
                dataLoggers.add(locationListener);
            }

        } catch (Exception e) {
            LogError("Init Data Loggers");

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

            LogError("Init Processors");
        }


    }

    //region Service Check

    /**
     * Main Service Check
     */
    private synchronized void serviceCheck() {

        try {

            StringBuilder str = new StringBuilder();
            str.append("TIMER CHECK");
            Log.d(TAG, "Timer check");

            if (isServiceRunningInForeground(this.getApplicationContext(), RecordingService.class))
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

            if (!scheduledRun()) {

                ///IF SESSION SHOULD STOP DUE TO START AND END DATE
                ///IS SCHEDULING SETTINGS
                if (sessionRunning) {
                    /// IF SESSION RUNNING STOP SESSION
                    ///Notify Server
                    LogWarn("Service stopped for today", 2);
                    //SendAlert("INFO", "Service stopped for today", "INFO0002");
                    str.append("/ STOPING SERVICDE");

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

                        //Start Reader
                        StartReader();


                    }

                }
            }

            ///Check For Alert only if


            //endregion

            /// If no band action taken perform scheduling
            /// Scheduling will check if the recording should stop or resume

            str.append("/ SCHEDULING ");
            try {
                scheduling();

            } catch (InterruptedException ex) {

                LogError("EX ON SCHEDULING", ex.getCause());
            } catch (Exception ex) {

                LogError("EX ON SCHEDULING", ex.getCause());
            }

            Log.v(TAG, str.toString());
            ProcessLog("INFO", str.toString());


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

    /**
     * Start Timer
     */
    private void startTimer() {

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

    /**
     * Stop Timer
     */
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
    //endregion


    //region Communication Queue


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

    //endregion

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
                ArrayList<UsageStatistic> obsArray = new ArrayList<>();

                for (IDataLogger logger : dataLoggers) {

                    Collection<LoggerStat> stats = logger.getUsageStats();

                    for (LoggerStat stat : stats) {

                        UsageStatistic obs = new UsageStatistic(stat.getValue(), stat.getCode(), unixTime, patient, deviceId);
                        obsArray.add(obs);
                    }

                }

                UsageStatistic obs1 = new UsageStatistic(bandsamples, "001", unixTime, patient, deviceId);
                UsageStatistic obs2 = new UsageStatistic(devsamples, "002", unixTime, patient, deviceId);
                UsageStatistic obs3 = new UsageStatistic(hrsamples, "003", unixTime, patient, deviceId);

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


                        msBandDataLogger.sendNotification();

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

            if (!scheduler.isPaused()) {

                int pauseReason = scheduler.shouldPause();
                if (pauseReason > 0) {

                    scheduler.pause();

                    LogDebug("RECORDING PAUSES REASON=" + pauseReason);
                    ///DISCONNECT IF NEEDED

                    for (IDataLogger dataLogger : dataLoggers) {
                        dataLogger.pause();

                    }


                }

            } else {

                // Should Resume is called only if previously paused
                // Pauses occur fot two reasons
                // 1) Scheduled Pause for optimizing MS Band to hold the entire scheduled session
                // 2) Patient is not wearing band

                if (scheduler.shouldResume()) {

                    LogDebug("RESUMING");
                    scheduler.resume();

                    ///DISCONNECT IF NEEDED
                    //Before Any reconnection or reseting device
                    //We wake lock
                    if (useDeviceLock())
                        wakeLock();

                    //Resume data Loggers
                    for (IDataLogger dataLogger : dataLoggers) {
                        dataLogger.resume();

                    }


                } else {
                    LogDebug("RECORDING PAUSED....NOT RESUMING YET");
                }

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


        sessionRunning = true;

        for (IDataLogger logger : dataLoggers) {

            logger.start();

        }


        return true;
    }

    /**
     * Stop Reader for Recording
     *
     * @return
     */
    private boolean StopReader() {


        for (IDataLogger logger : dataLoggers) {
            logger.start();
        }
        ///Mark that session is not running
        sessionRunning = false;

        //CLose File processors
        if (fileProcessor != null)
            fileProcessor.finalize();

        //Stop Reader Release Lock
        if (useDeviceLock())
            releaseLock();


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


    //endregion

    //region Unregister listeners


    //endregion

    //region Data Handlers


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

    //endregion

    //region Main Task
    public int getFatalErrorCode() {

        return mFatalErrorCode;

    }

    //endregion

    public boolean hasFatalError() {

        return mFatalError;


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

    //region Foreground Service

    /**
     * Place the service into the foreground
     */
    public void foreground() {

        if (useForeground() && !isServiceRunningInForeground(this.getApplicationContext(), RecordingService.class)) {
            LogWarn("Setting service foreground");
            startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    /**
     * Return the service to the background
     */
    public void background() {

        if (isServiceRunningInForeground(this.getApplicationContext(), RecordingService.class)) {
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

        Intent resultIntent = new Intent(this, RecordingService.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }


    //endregion

    /**
     * Require permissions for accessing Heart Rate Sensor
     */
    public void requireHeartRatePermissions() {

        if (msBandDataLogger != null) {
            msBandDataLogger.requireHeartRatePermissions();

        }

    }

    //endregion

    //region Main Task

    //region Local Binder
    public class LocalBinder extends Binder {
        public RecordingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RecordingService.this;
        }
    }
    //endregion

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

        }


    }


    //region Hear Rate Permissions

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
    //endregion

}

