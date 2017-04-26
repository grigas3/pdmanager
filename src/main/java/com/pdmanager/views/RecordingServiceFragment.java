package com.pdmanager.views;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.pdmanager.R;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.interfaces.IBandTileManager;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.logging.LogHandler;
import com.pdmanager.medication.MedManager;
import com.pdmanager.models.Device;
import com.pdmanager.models.DeviceResult;
import com.pdmanager.models.PatientMedicationResult;
import com.pdmanager.models.UserAlert;
import com.pdmanager.sensor.IHeartRateAccessProvider;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.services.RecordingService;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.common.LoginActivity;
import com.pdmanager.views.patient.MSSyncActivity;
import com.pdmanager.views.patient.MainActivity;
import com.pdmanager.views.patient.TechnicianActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RecordingServiceFragment extends BasePDFragment implements FragmentListener, IServiceStatusListener, ISensorStatusListener, IHeartRateAccessProvider, HeartRateConsentListener {

    private static final String TAG = "RECORDINGFRAGMENT";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CONTACTS = 1;
    private static final int REQUEST_RECORD = 1;
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_PHONE_STATE = 1;
    private static final int REQUEST_VIDEO = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,


    };
    private static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,


    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,


    };
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.RECORD_AUDIO


    };

    private static String[] PERMISSIONS_PHONE = {
            Manifest.permission.READ_PHONE_STATE


    };


    private static String[] PERMISSIONS_VIDEO = {
            Manifest.permission.CAMERA,
            Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT,
            Manifest.permission.CAPTURE_VIDEO_OUTPUT
    };
    final Handler handler = new Handler();
    private Button mButtonConnect;
    private Button mButtonChooseBand;
    private Button mButtonSetLimits;
    private Button mButtonRequireStorPermissions;
    private Button mbuttonGetMedications;
    private Button mButtonCreateTile;

    private TextView mTextGetMedication;
    private TextView mTextGetDevice;
    private Button mbuttonMSHealthSync;
    private Button mButtonLogin;
    private Button mGetDevice;
    private IBandTileManager tileManager;
    private TextView mDeviceId;
    private EditText mLow;
    private EditText mHigh;
    private TextView mTextLoggedIn;
    private EditText mDuration;
    private ProgressBar busyIndicator;
    private LinearLayout layout;
    private ImageView mBandSensorImage;
    private boolean bandRequired = true;
    private Timer myTimer;
    private TextView mSensorStatus;
    //
    // Handle connect/disconnect requests.
    //
    private TextView mMonitoringStatus;
    private TextView mTextMSHealth;
    private View.OnClickListener mButtonCheckClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            refreshControls();

        }
    };
    private View.OnClickListener mButtonDeviceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            RecordingSettings settings = new RecordingSettings(getContext());
            //new MSSyncActivity.SyncHealthDataTask(settings.getPatientID(),settings.getToken()).execute(result);
            busyIndicator.setVisibility(View.VISIBLE);
            layout.setVisibility(View.INVISIBLE);
            new AssignDeviceTask(settings.getPatientID(), settings.getToken()).execute();

            //new GetObservationsTask().execute(new ObservationParams("TEST01","001"));

            //new GetCodesTask().execute();
            //    new GetPatientsTask().execute();

        }
    };
    private View.OnClickListener mbuttonGetMedicationsListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            busyIndicator.setVisibility(View.VISIBLE);
            layout.setVisibility(View.INVISIBLE);
            new GetMedicationTask(getPatientCode(), getAccessToken()).execute();

        }
    };
    private View.OnClickListener mbuttonMSHealthSyncListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            Intent mainIntent = new Intent(getActivity(), MSSyncActivity.class);
            getActivity().startActivity(mainIntent);


        }
    };

    private View.OnClickListener mbuttonLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            RecordingSettings settings = new RecordingSettings(getActivity());
            settings.setLoggedIn(false);


            Intent mainIntent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(mainIntent);

        }
    };

    private View.OnClickListener mbuttonCreateTileListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            if (tileManager != null)
                tileManager.createTile();


        }
    };
    private View.OnClickListener mButtonRequirePermissions = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            requirePermissions(getActivity());

        }
    };
    //
    // If there are multiple bands, the "choose band" button is enabled and
    // launches a dialog where we can select the band to use.
    //
    private View.OnClickListener mButtonChooseBandClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

        }
    };


   /* private void initAlerts(RecordingSettings settings)
    {

        IUserAlertManager alertManager=UserAlertManager.newInstance(getContext());

        alertManager.clearAll();

        MedManager manager = new MedManager(this.getContext());
        ///Check for alert
        List<UserAlert> alerts = manager.getAlerts();
        for(UserAlert alert:alerts)
        {
            alertManager.add(alert);

        }


      //  UserAlert cognAlert1=new UserAlert((settings.getCognHour1());


    }
    */

    ///Connect button listener
    private View.OnClickListener mButtonConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {

            RecordingSettings settings = getSettings();

            if (getService().isSessionRunning()) {

                //  RemoveReminders(settings);

                LogHandler.getInstance().Log("Session Stopped by user");
                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                new StopServiceTask(settings.getPatientID(), settings.getToken()).execute();
                /*
                mButtonConnect.setEnabled(false);
                mButtonConnect.setBackgroundColor(Color.GRAY);
                settings.setSessionRunning(false);



                getService().StopRecording();


                new UpdateDeviceStatusTask(settings.getPatientID(), settings.getToken()).execute();
                */

            } else

            {

                if (!checkExternalMedia()) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Error");
                    alert.setMessage("Your phone does not have external storage");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    LogHandler.getInstance().LogError("Could not Start session. External storage not found");

                    return;


                }

                if (settings != null) {
                    Calendar c = Calendar.getInstance();

                    int hourOfDay = c.get(Calendar.HOUR_OF_DAY);

                    if (hourOfDay <= settings.getStartHour() || hourOfDay >= settings.getStopHour()) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Warning!");
                        alert.setMessage("Your scheduled start and end does not match current time. The recording will start the scheduled Time");
                        alert.setPositiveButton("OK", null);
                        alert.show();

                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        settings.setSessionFolder(CreateFolder(today));

                        // LogHandler.getInstance().LogWarn("Could not Start session due to scheduled start and end");
                        //   mButtonConnect.setEnabled(true);
//                                    mButtonConnect.setColor(Color.GREEN);
                        LogHandler.getInstance().Log("Session started by user");
                        mButtonConnect.setEnabled(false);
                        mButtonConnect.setBackgroundColor(Color.GRAY);

                        //     AddReminders(settings.getStartHour(),settings.getStopHour(),settings);
                        settings.setSessionRunning(true);
                        settings.setRecordingStart(System.currentTimeMillis());


                    } else {

                        ///IN ANDROID 23+ WE NEED TO ASK FOR EXTRA STORAGE PERMISSIONS
                        requirePermissions(getActivity());
                        busyIndicator.setVisibility(View.VISIBLE);
                        layout.setVisibility(View.INVISIBLE);
                        new StartServiceTask(settings.getPatientID(), settings.getToken()).execute();
                       /*

                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        settings.setSessionFolder(CreateFolder(today));
                        LogHandler.getInstance().Log("Session started by user");
                        mButtonConnect.setEnabled(false);
                        mButtonConnect.setBackgroundColor(Color.GRAY);

                        initAlerts(settings);
                        getService().StartRecording();

                        //AddReminders(settings.getStartHour(),settings.getStopHour(),settings);

                        settings.setSessionRunning(true);
                        settings.setRecordingStart(System.currentTimeMillis());
                        */


                    }
                }

            }

            //    }

            //   refreshControls();

        }
    };
    private CompoundButton.OnCheckedChangeListener mToggleSensorSection = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Turn on the appropriate sensor
            Switch sw = (Switch) buttonView;


        }
    };


    public RecordingServiceFragment() {

    }

    private long getTimeFromHour(int hour) {
        Date date1 = new java.util.Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        cal1.set(cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH), hour, 0, 0);


        return cal1.getTimeInMillis()+24*60*60*1000;

 //       return cal1.getTimeInMillis();
    }

    private void initAlerts(RecordingSettings settings) {

        Date date1 = new java.util.Date();

        int cognHour1 = settings.getCognHour1();

        int cognHour2 = settings.getCognHour2();

        int medHour1 = settings.getMedHour1();
        int medHour2 = settings.getMedHour2();
        int moodHour = settings.getMoodHour();
        int diary = settings.getDiaryHour();

        UserAlertManager manager = new UserAlertManager(this.getContext());
        manager.clearAll();
        String msg = this.getContext().getString(com.pdmanager.R.string.cognitiveAlertMsg);
        manager.add(new UserAlert("PD_Manager", msg, "COGN1", date1.getTime(), getTimeFromHour(cognHour1), "SYSTEM"));

        //Cogn2 test
        manager.add(new UserAlert("PD_Manager", msg, "COGN2", date1.getTime(), getTimeFromHour(cognHour2), "SYSTEM"));



        // Med Test 1
        manager.add(new UserAlert("PD_Manager", msg, "MED", date1.getTime(), getTimeFromHour(medHour1), "SYSTEM"));
        // Med Question 2
        manager.add(new UserAlert("PD_Manager", msg, "MED", date1.getTime(), getTimeFromHour(medHour2), "SYSTEM"));
        //
        manager.add(new UserAlert("PD_Manager", msg, "MOOD", date1.getTime(), getTimeFromHour(moodHour), "SYSTEM"));

        //Test
        //getTimeFromHour(diary)

        if(settings.getEnableDiary()) {
            manager.add(new UserAlert("PD_Manager", msg, "DIARY", date1.getTime(), getTimeFromHour(diary), "SYSTEM"));

        }
        manager.add(new UserAlert("PD_Manager", msg, "FT", date1.getTime(), getTimeFromHour(moodHour), "SYSTEM"));

      //  manager.add(new UserAlert("PD_Manager", msg, "VT", date1.getTime(), getTimeFromHour(moodHour), "SYSTEM"));

        manager.add(new UserAlert("PD_Manager", msg, "VA", date1.getTime(), getTimeFromHour(moodHour), "SYSTEM"));


    }

    public void setTileManager(IBandTileManager manager) {

        this.tileManager = manager;
    }

    private boolean hasPermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            return false;
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            return false;
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            return false;
        }

        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);

        if (permission4 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            return false;
        }

        int permission5 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission5 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            return false;
        }


        int permission6 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission6 != PackageManager.PERMISSION_GRANTED) {

             return false;
        }
    /*    try {
            BandClientManager manager = BandClientManager.getInstance();
            BandInfo[] mPairedBands = manager.getPairedBands();

            if (mPairedBands.length > 0) {

                BandClient mClient = manager.create(activity, mPairedBands[0]);
                BandSensorManager sensorMgr = mClient.getSensorManager();

                if(sensorMgr.getCurrentHeartRateConsent()==null)
                    return false;

            }
        }
        catch (Exception e)
        {


            Log.d(TAG,e.getMessage());
        }
        */


        return true;
    }




    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void requirePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CONTACTS,
                    REQUEST_CONTACTS
            );
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_PHONE,
                    REQUEST_PHONE_STATE
            );
        }

        int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);

        if (permission4 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_RECORD,
                    REQUEST_RECORD
            );
        }

        int permission5 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission5 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_LOCATION
            );
        }


        int permission6 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission6 != PackageManager.PERMISSION_GRANTED) {

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_VIDEO,
                    REQUEST_VIDEO
            );
        }

     /*   try {
            BandClientManager manager = BandClientManager.getInstance();
            BandInfo[] mPairedBands = manager.getPairedBands();

            if (mPairedBands.length > 0) {

                BandClient mClient = manager.create(activity, mPairedBands[0]);
                BandSensorManager sensorMgr = mClient.getSensorManager();

                sensorMgr.requestHeartRateConsent(getActivity(), this);



            }
        }
        catch (Exception e)
        {


            Log.e(TAG,e.getMessage());
        }
        */


    }

    public boolean isBluetoothEnabled() {

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return mBluetoothAdapter.isEnabled();

        } catch (Exception ex) {

            return false;
        }

    }

    private void RemoveReminders(RecordingSettings settings) {

        try {
            Uri reminderUri1 = Uri.parse(settings.getReminder1());

            getActivity().getApplicationContext().getContentResolver().delete(reminderUri1, null, null);


        } catch (Exception ex) {

        }

        try {
            Uri reminderUri2 = Uri.parse(settings.getReminder2());

            getActivity().getApplicationContext().getContentResolver().delete(reminderUri2, null, null);


        } catch (Exception ex) {

        }
        try {
            Uri reminderUri1 = Uri.parse(settings.getEvent1());

            getActivity().getApplicationContext().getContentResolver().delete(reminderUri1, null, null);


        } catch (Exception ex) {

        }

        try {
            Uri reminderUri1 = Uri.parse(settings.getEvent2());

            getActivity().getApplicationContext().getContentResolver().delete(reminderUri1, null, null);


        } catch (Exception ex) {

        }
    }

    private void SetReminder(int hour, String title, RecordingSettings settings, int t) {

        try {
            Calendar beginTime = Calendar.getInstance();
            int year = beginTime.get(Calendar.YEAR);
            int month = beginTime.get(Calendar.MONTH);
            int day = beginTime.get(Calendar.DAY_OF_MONTH);
            beginTime.set(year, month, day, hour, 0);
            long startMillis = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance();
            endTime.set(year, month + 1, day, hour, 0);
            long endMillis = endTime.getTimeInMillis();

            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues;
            eventValues = new ContentValues();

            eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
            eventValues.put(CalendarContract.Events.TITLE, "PD_Manager");
            eventValues.put(CalendarContract.Events.DESCRIPTION, title);

            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            eventValues.put(CalendarContract.Events.DTSTART, startMillis);
            eventValues.put(CalendarContract.Events.DTEND, endMillis);

            //eventValues.put(Events.RRULE, "FREQ=DAILY;COUNT=2;UNTIL="+endMillis);
            // eventValues.put("eventStatus", 1);
            // eventValues.put("visibility", 3);
            //   eventValues.put("transparency", 0);
            eventValues.put(CalendarContract.Events.HAS_ALARM, 1);

            Uri eventUri = getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
            long eventID = Long.parseLong(eventUri.getLastPathSegment());

            /***************** Event: Reminder(with alert) Adding reminder to event *******************/

            if (t == 0)
                settings.setEvent1(eventUri.toString());
            else
                settings.setEvent2(eventUri.toString());
            String reminderUriString = "content://com.android.calendar/reminders";

            ContentValues reminderValues = new ContentValues();

            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", 1);
            reminderValues.put("method", 1);

            Uri reminderUri = getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);

            if (t == 0)
                settings.setReminder1(reminderUri.toString());
            else
                settings.setReminder2(reminderUri.toString());


        } catch (Exception ex) {

            Log.d("Error", "InserReminder");
        }


    }

    private void AddReminders(int startHour, int stopHour, RecordingSettings settings) {

        SetReminder(startHour, "Please wear the Bracelet", settings, 0);

        SetReminder(stopHour, "Remove the Bracelet and put it for charge", settings, 1);


    }

    private String CreateFolder(Time today) {

        StringBuilder hrsb = new StringBuilder();

        hrsb.append(String.format("%04d", today.year));                // Year)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.monthDay));          // Day of the month (1-31)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.month + 1));              // Month (0-11))
        hrsb.append("_");
        hrsb.append(today.format("%k:%M:%S"));      // Current time

        String newFileName = hrsb.toString();
        newFileName = newFileName.replaceAll(":", "_");

        return newFileName;

    }

    private boolean checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        return mExternalStorageAvailable;
    }

    public final void onFragmentSelected() {
        if (isVisible()) {
            refreshControls();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band, container, false);

        mGetDevice = (Button) rootView.findViewById(R.id.buttonGetDevice);
        mDeviceId = (TextView) rootView.findViewById(R.id.textDeviceId);
        mGetDevice.setOnClickListener(mButtonDeviceClickListener);

        mButtonConnect = (Button) rootView.findViewById(R.id.buttonConnect);
        mButtonConnect.setOnClickListener(mButtonConnectClickListener);

        mButtonRequireStorPermissions = (Button) rootView.findViewById(R.id.buttonRequirePermissions);
        mButtonRequireStorPermissions.setOnClickListener(mButtonRequirePermissions);

        //    mbuttonGetMedications = (Button) rootView.findViewById(R.id.buttonGetMedications);
        //    mbuttonGetMedications.setOnClickListener(mbuttonGetMedicationsListener);

        mbuttonMSHealthSync = (Button) rootView.findViewById(R.id.buttonGetMSHealth);
        mbuttonMSHealthSync.setOnClickListener(mbuttonMSHealthSyncListener);

            mButtonLogin = (Button) rootView.findViewById(R.id.buttonLogin);
        mButtonLogin.setOnClickListener(mbuttonLoginListener);


        mTextMSHealth=(TextView) rootView.findViewById(R.id.textMSHealth);

        mMonitoringStatus = (TextView) rootView.findViewById(R.id.textConnectionStatus);
        mSensorStatus = (TextView) rootView.findViewById(R.id.textSensorStatus);

        busyIndicator = (ProgressBar) rootView.findViewById(R.id.sync_progress);

        layout = (LinearLayout) rootView.findViewById(R.id.mainLayout);

        mDeviceId.setText(RecordingSettings.newInstance(this.getContext()).getDeviceId());

        //  this.mTextGetMedication=(TextView) rootView.findViewById(R.id.textMedication);
        mTextLoggedIn = (TextView) rootView.findViewById(R.id.textLoggedIn);
        mTextGetDevice = (TextView) rootView.findViewById(R.id.textGetDevice);

        if (hasPermissions(this.getActivity())) {
            ((TextView) rootView.findViewById(R.id.textFilePermission)).setTextColor(Color.GREEN);
        }

        if(getSettings().getMSSynced())
        {
            mTextMSHealth.setTextColor(Color.GREEN);
        }




        /*
        mSwitchHeartRate = (Switch) rootView.findViewById(R.id.heartRateSwitch);

        mSwitchPosture = (Switch) rootView.findViewById(R.id.postureSwitch);

        mSwitchPosture.setOnCheckedChangeListener(mToggleSensorSection);
        mSwitchHeartRate.setOnCheckedChangeListener(mToggleSensorSection);
*/
        refreshControls();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myTimer != null)
            myTimer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (myTimer != null)
            myTimer.cancel();

    }

    @Override
    public void onResume() {
        super.onResume();

        myTimer = new Timer();
        MyTimerTask myTask = new MyTimerTask();
        myTimer.schedule(myTask, 1000, 5000);

        refreshControls();
    }

    private void updateLoggedInInfo() {
        RecordingSettings settings = getSettings();
        if (settings != null) {

            if(settings.getMSSynced())
            {

                mTextLoggedIn.setTextColor(Color.GREEN);

            }
            else
                mTextLoggedIn.setTextColor(Color.RED);

            if (settings.getLoggedIn()) {

                mTextLoggedIn.setText(" Logged in as " + settings.getUserName());

                mTextLoggedIn.setTextColor(Color.GREEN);
            } else {
                mTextLoggedIn.setText("Not Logged In");
                mTextLoggedIn.setTextColor(Color.RED);
            }

        }
    }


    private RecordingService getService() {
        return RecordingServiceHandler.getInstance().getService();
    }

    private void refreshControls() {

        Activity activity = getActivity();
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        RecordingService service = getService();
                        if (service != null) {

                            if (mButtonConnect != null) {
                                mButtonConnect.setEnabled(true);

                                if (service.getSessionMustRun()) {

                                    mButtonConnect.setBackgroundColor(Color.RED);
                                    mButtonConnect.setText("Stop");
                                    mbuttonMSHealthSync.setEnabled((false));
                                    //mButtonConnect.setImageResource(R.drawable.ic_action_stop);
                                } else {

                                    mButtonConnect.setBackgroundColor(Color.GREEN);
                                    mButtonConnect.setText("Start");
                                    mbuttonMSHealthSync.setEnabled((true));
                                    // mButtonConnect.setImageResource(R.drawable.ic_action_play);

                                    //  mButtonConnect.setText("Start");

                                }


                            } else

                            {

                                mButtonConnect.setEnabled(false);
                                mButtonConnect.setBackgroundColor(Color.GREEN);
                                mButtonConnect.setText("Start");
                                //  mButtonConnect.setImageResource(R.drawable.ic_action_play);
                                mButtonConnect.setEnabled(true);

                                mbuttonMSHealthSync.setEnabled((true));




                            }


                        }

                        updateLoggedInInfo();
                    } catch (Exception ex) {

                        Log.e(("SERVICE FRAGMENT"), "Refresh Controls", ex.getCause());

                    }
                }
            });
        }
    }

    @Override
    public void notifyServiceStatusChanged() {

        refreshControls();

    }

    @Override
    public void requestHeartRateConsent(BandSensorManager sensorManager) {

        try {
            sensorManager.requestHeartRateConsent(getActivity(), this);

        }
        catch (Exception ex)
        {

            Log.e(TAG,ex.getMessage());
        }

    }

    @Override
    public void userAccepted(boolean b) {

    }

    @Override
    public void notifySensorStatusChanged(boolean status) {

    }

    private String GetRunningTime() {

        try {
            long startMillis = getSettings().getRecordingStart();
            long now = System.currentTimeMillis();
            long difference = now - startMillis;
            // You can then output it by using DateUtils.formatElapsedTime():
            long differenceInSeconds = difference / DateUtils.SECOND_IN_MILLIS;
            // formatted will be HH:MM:SS or MM:SS
            String formatted = DateUtils.formatElapsedTime(differenceInSeconds);
            return formatted;
        } catch (Exception ex) {

        }

        return "";

    }

    class MyTimerTask extends TimerTask {
        public void run() {

            if (handler != null) {
                // ERROR
                // hTextView.setText("Impossible");
                // how update TextView in link below
                // http://android.okhelp.cz/timer-task-timertask-run-cancel-android-example/
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        RecordingService service = RecordingServiceHandler.getInstance().getService();
                        if (service != null && service.isSessionRunning()) {

                            mMonitoringStatus.setTextColor(Color.GREEN);
                            mMonitoringStatus.setText("monitoring for " + GetRunningTime());

                            if (mSensorStatus != null) {
                                mSensorStatus.setVisibility(View.VISIBLE);
                                if (service.isAllRecording()) {
                                    mSensorStatus.setText("ALL OK");
                                    mSensorStatus.setTextColor(Color.GREEN);
                                } else

                                {

                                    if (service.hasFatalError())

                                    {

                                        if (service.getFatalErrorCode() == 2) {
                                            mSensorStatus.setText("Band is not recording...please move phone close to your Band");


                                        }

                                        if (service.getFatalErrorCode() == 1) {
                                            mSensorStatus.setText("Band is not paired...please re-pair your Band from the band device");


                                        } else

                                        {

                                            mSensorStatus.setText("Exception while connecting to Band");

                                        }

                                    } else {

                                        mSensorStatus.setText("Something is wrong...restart the application");

                                    }
                                    mSensorStatus.setTextColor(Color.RED);
                                }
                            }


                        } else {

                            mMonitoringStatus.setText("not monitoring");
                            mMonitoringStatus.setTextColor(Color.RED);

                            if (mSensorStatus != null)
                                mSensorStatus.setVisibility(View.INVISIBLE);
                        }

                    }
                });


            }
        }
    }


    private class StopServiceTask extends AsyncTask<Void, Void, Void> {
        private String code;
        private String accessToken;

        public StopServiceTask(String pcode, String a) {

            this.code = pcode;
            this.accessToken = a;

        }

        @Override
        protected Void doInBackground(Void... clientParams) {

            RecordingSettings settings=RecordingSettings.GetRecordingSettings(getActivity());
            settings.setSessionRunning(false);
            getService().StopRecording();
            DataReceiver receiver = new DataReceiver(accessToken);

            UserAlertManager manager=new UserAlertManager(getActivity());

            manager.clearAll();

            List<Device> devices = receiver.GetDevices(code);

            if (devices.size() > 0) {

                for (Device d : devices) {

                    if (d.PatientId.equals(code)) {

                        d.setStatus("INACTIVE");
                        DirectSender sender = new DirectSender(accessToken);
                        CommunicationManager mCommManager = new CommunicationManager(sender);
                        mCommManager.UpdateItem(d);

                    }

                }
            }




                return null;

        }
        protected void onPostExecute(Void result) {
            mButtonConnect.setEnabled(false);
            mButtonConnect.setBackgroundColor(Color.GRAY);
            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);
            refreshControls();
        }
    }







    private class StartServiceTask extends AsyncTask<Void, Void, Void> {
        private String code;
        private String accessToken;

        public StartServiceTask(String pcode, String a) {

            this.code = pcode;
            this.accessToken = a;

        }
        @Override
        protected Void doInBackground(Void... clientParams) {

            RecordingSettings settings=RecordingSettings.GetRecordingSettings(getActivity());


            Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            settings.setSessionFolder(CreateFolder(today));
            settings.setMSSynced(false);

            LogHandler.getInstance().Log("Session started by user");
            initAlerts(settings);
            DataReceiver receiver = new DataReceiver(accessToken);
            List<Device> devices = receiver.GetDevices(code);

            if (devices.size() > 0) {

                for (Device d : devices) {

                    if (d.PatientId.equals(code)) {

                        d.setStatus("ACTIVE");
                        DirectSender sender = new DirectSender(accessToken);
                        CommunicationManager mCommManager = new CommunicationManager(sender);
                        mCommManager.UpdateItem(d);

                    }

                }
            }

            getService().StartRecording();


            settings.setSessionRunning(true);
            settings.setRecordingStart(System.currentTimeMillis());


            return null;

        }
        protected void onPostExecute(Void result) {
            mButtonConnect.setEnabled(false);
            mButtonConnect.setBackgroundColor(Color.GRAY);
            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);

            //Intent mainIntent = new Intent(getActivity(), MainActivity.class);
            //getActivity().startActivity(mainIntent);

            updateHeartRatePermissions();

            //refreshControls();
        }
    }

    private void updateHeartRatePermissions()
    {


        try {
            getService().requireHeartRatePermissions();

        }
        catch (Exception ex)
        {

            Log.e(TAG,ex.getMessage(),ex.getCause());
        }
        //requirePermissions(this.getActivity());

    }

    private class UpdateDeviceStatusTask extends AsyncTask<Void, Void, DeviceResult> {


        private String code;
        private String accessToken;

        public UpdateDeviceStatusTask(String pcode, String a) {

            this.code = pcode;
            this.accessToken = a;

        }


        @Override
        protected DeviceResult doInBackground(Void... clientParams) {

            DeviceResult res = new DeviceResult();

            try {

                DataReceiver receiver = new DataReceiver(accessToken);

                List<Device> devices = receiver.GetDevices(code);

                if (devices.size() > 0) {

                    for (Device d : devices) {

                        if (d.PatientId.equals(code)) {

                            d.setStatus("INACTIVE");
                            DirectSender sender = new DirectSender(accessToken);
                            CommunicationManager mCommManager = new CommunicationManager(sender);
                            mCommManager.UpdateItem(d);

                        }

                    }

                    res.HasError = false;


                }
            } catch (Exception e) {
                res.HasError = true;
            }

            return res;

        }

        protected void onPostExecute(DeviceResult result) {
            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);

            if (!result.HasError) {

            }





        }

    }

    private class AssignDeviceTask extends AsyncTask<Void, Void, DeviceResult> {


        private String code;
        private String accessToken;

        public AssignDeviceTask(String pcode, String a) {

            this.code = pcode;
            this.accessToken = a;

        }


        @Override
        protected DeviceResult doInBackground(Void... clientParams) {

            DeviceResult res = new DeviceResult();

            try {

                DataReceiver receiver = new DataReceiver(accessToken);

                List<Device> devices = receiver.GetDevices(code);

                boolean deviceFound=false;
                if (devices.size() > 0) {

                    for (Device d : devices) {


                        Log.d(TAG,"Device Id" +d.Id);
                        Log.d(TAG,"Device PatientId" +d.PatientId);
                        Log.d(TAG,"Device Model" +d.Model);
                        if (d.PatientId!=null&&d.PatientId.equals(code)) {
                            res.DeviceId = d.Id;
                            deviceFound=true;
                        }

                    }
                    if(!deviceFound)
                    {

                        res.Error="No Device Found For Patient "+code;
                        res.HasError=true;

                    }
                    else
                    res.HasError = false;


                }
            } catch (Exception e) {

                Log.e(TAG,e.getMessage(),e.getCause());
                res.HasError = true;
            }

            return res;

        }

        protected void onPostExecute(DeviceResult result) {
            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);

            if (!result.HasError) {
                RecordingSettings settings = new RecordingSettings(getContext());
                mDeviceId.setText(result.DeviceId);
                settings.setDeviceId(result.DeviceId);


                mTextGetDevice.setTextColor(Color.GREEN);
            }
            else
            {
                mTextGetDevice.setText(result.Error);
                mTextGetDevice.setTextColor(Color.RED);

            }





        }

    }

    private class GetMedicationTask extends AsyncTask<Void, Void, PatientMedicationResult> {

        private String accessToken;
        private String patientCode;

        public GetMedicationTask(String code, String a) {

            this.patientCode = code;
            this.accessToken = a;
        }

        @Override
        protected PatientMedicationResult doInBackground(Void... clientParams) {
            PatientMedicationResult res = new PatientMedicationResult();
            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);

                res.orders = receiver.GetMedicationOrders(patientCode);

                return res;

            } catch (Exception ex) {

                Log.e("MEDS", "Get Meds", ex.getCause());
                res.setError(true);
                //Util.handleException("Getting data", ex);

                // handle BandException
            }
            return res;
        }


        protected void onPostExecute(PatientMedicationResult result) {
            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);

            if (!result.hasError()) {
                MedManager manager = new MedManager(getContext());
                //Clear Medication Orders
                manager.clearAll();
                //Add Medication Orders
                manager.addMedicationOrders(result.orders);
                mTextGetMedication.setTextColor(Color.GREEN);


            } else {

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Error");
                alert.setMessage("Could not get medications. Check your internet connection and that the medications are property defined.");
                alert.setPositiveButton("OK", null);
                alert.show();


            }
            /*

            busyIndicator.setVisibility(View.INVISIBLE);

            List<PendingMedication> res = filterCurrentOrders(result);
            //TODO PROPERLY CHECK CONNECTION
            if (res != null) {
                if (res.size() == 0) {


                    emptyList.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                } else {

                    emptyList.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                }
                adapter = new PendingMedOrderAdapter(res);


                listView.setAdapter(adapter);
            }

            */

        }
    }


}
