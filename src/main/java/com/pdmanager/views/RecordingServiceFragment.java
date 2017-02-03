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

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.pdmanager.core.R;
import com.pdmanager.core.alerting.UserAlertManager;
import com.pdmanager.core.communication.DataReceiver;
import com.pdmanager.core.interfaces.IBandTileManager;
import com.pdmanager.core.interfaces.ISensorStatusListener;
import com.pdmanager.core.interfaces.IServiceStatusListener;
import com.pdmanager.core.logging.LogHandler;
import com.pdmanager.core.medication.MedManager;
import com.pdmanager.core.models.Device;
import com.pdmanager.core.models.DeviceResult;
import com.pdmanager.core.models.PatientMedicationResult;
import com.pdmanager.core.sensor.IHeartRateAccessProvider;
import com.pdmanager.core.sensor.RecordingServiceHandler;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.patient.MSSyncActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RecordingServiceFragment extends BasePDFragment implements FragmentListener, IServiceStatusListener, ISensorStatusListener, IHeartRateAccessProvider, HeartRateConsentListener {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CONTACTS = 1;
    private static final int REQUEST_PHONE_STATE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,


    };
    private static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,


    };
    private static String[] PERMISSIONS_PHONE = {
            Manifest.permission.READ_PHONE_STATE


    };
    final Handler handler = new Handler();
    private Button mButtonConnect;
    private Button mButtonChooseBand;
    private Button mButtonSetLimits;
    private Button mButtonRequireStorPermissions;
    private Button mbuttonGetMedications;
    private Button mbuttonMSHealthSync;
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

            new GetDeviceTask(settings.getPatientID(), settings.getToken()).execute();


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
    private View.OnClickListener mButtonRequirePermissions = new View.OnClickListener() {
        @Override
        public void onClick(View button) {


            requireStoragePermissions(getActivity());

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
    ///Connect button listener
    private View.OnClickListener mButtonConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {


            RecordingSettings settings = getSettings();


            if (getService().isSessionRunning()) {


                //  RemoveReminders(settings);


                LogHandler.getInstance().Log("Session Stopped by user");
                mButtonConnect.setEnabled(false);
                mButtonConnect.setBackgroundColor(Color.GRAY);
                settings.setSessionRunning(false);


                getService().StopRecording();

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
                        requireStoragePermissions(getActivity());

                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        settings.setSessionFolder(CreateFolder(today));
                        LogHandler.getInstance().Log("Session started by user");
                        mButtonConnect.setEnabled(false);
                        mButtonConnect.setBackgroundColor(Color.GRAY);
                        UserAlertManager.newInstance(getContext()).clearAll();


                        getService().StartRecording();

                                 /*   if (!res) {
                                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                        alert.setTitle("Error");
                                        alert.setMessage("Check Band is connected or disable band recording");
                                        alert.setPositiveButton("OK", null);
                                        alert.show();
                                        LogHandler.getInstance().LogError("Could not Start session");
                                        mButtonConnect.setEnabled(true);
                                        mButtonConnect.setColor(Color.GREEN);

                                    }
                                    else
                                    */

                        {


                            //AddReminders(settings.getStartHour(),settings.getStopHour(),settings);

                            settings.setSessionRunning(true);
                            settings.setRecordingStart(System.currentTimeMillis());
                        }
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

    public void setTileManager(IBandTileManager manager) {

        this.tileManager = manager;
    }


    private boolean checkPermissions(Activity activity)
    {

        boolean requiresPermissions=false;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            requiresPermissions=true;

        }

        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            requiresPermissions=true;
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            requiresPermissions=true;
        }

        return requiresPermissions;
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void requireStoragePermissions(Activity activity) {
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


        mbuttonGetMedications = (Button) rootView.findViewById(R.id.buttonGetMedications);
        mbuttonGetMedications.setOnClickListener(mbuttonGetMedicationsListener);

        mbuttonMSHealthSync = (Button) rootView.findViewById(R.id.buttonGetMSHealth);
        mbuttonMSHealthSync.setOnClickListener(mbuttonMSHealthSyncListener);


        mMonitoringStatus = (TextView) rootView.findViewById(R.id.textConnectionStatus);
        mSensorStatus = (TextView) rootView.findViewById(R.id.textSensorStatus);

        busyIndicator = (ProgressBar) rootView.findViewById(R.id.sync_progress);

        layout = (LinearLayout) rootView.findViewById(R.id.mainLayout);

        mDeviceId.setText(RecordingSettings.newInstance(this.getContext()).getDeviceId());



        mTextLoggedIn=(TextView) rootView.findViewById(R.id.textLoggedIn);
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

    private void updateLoggedInInfo()
    {
        RecordingSettings settings =getSettings();
        if (settings != null) {

            if (settings.getLoggedIn()) {

                mTextLoggedIn.setText(" Logged in as "+settings.getUserName());

                mTextLoggedIn.setTextColor(Color.GREEN);
            }
            else
            {
                mTextLoggedIn.setText("Not Logged In");
                mTextLoggedIn.setTextColor(Color.YELLOW);
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
                    }
                    catch (Exception ex)
                    {

                        Log.e(("SERVICE FRAGMENT"),"Refresh Controls",ex.getCause());

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


        sensorManager.requestHeartRateConsent(getActivity(), this);


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

    private class GetDeviceTask extends AsyncTask<Void, Void, DeviceResult> {


        private String code;
        private String accessToken;

        public GetDeviceTask(String pcode, String a) {


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
                            res.DeviceId = d.Id;

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

            if (!result.HasError) {
                RecordingSettings settings = new RecordingSettings(getContext());
                mDeviceId.setText(result.DeviceId);
                settings.setDeviceId(result.DeviceId);
            }

            if (tileManager != null)
                tileManager.createTile();


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


                Log.e("MEDS","Get Meds",ex.getCause());
                res.setError(true);
                //Util.handleException("Getting data", ex);

                // handle BandException
            }
            return res;
        }


        protected void onPostExecute(PatientMedicationResult result) {
            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);

            if(!result.hasError()) {
                MedManager manager = new MedManager(getContext());
                //Clear Medication Orders
                manager.clearMedOrders();
                //Add Medication Orders
                manager.addMedicationOrders(result.orders);



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
