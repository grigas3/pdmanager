package com.pdmanager.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
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
import android.widget.Switch;
import android.widget.TextView;

import com.microsoft.band.sensors.BandSensorManager;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.pdmanager.controls.CircleButton;
import com.pdmanager.core.R;
import com.pdmanager.core.RecordingService;
import com.pdmanager.core.interfaces.ISensorStatusListener;
import com.pdmanager.core.interfaces.IServiceStatusListener;
import com.pdmanager.core.logging.LogHandler;
import com.pdmanager.core.sensor.IHeartRateAccessProvider;
import com.pdmanager.core.sensor.RecordingServiceHandler;
import com.pdmanager.core.settings.RecordingSettings;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class RecordingServiceFragment extends BasePDFragment implements FragmentListener, IServiceStatusListener, ISensorStatusListener, IHeartRateAccessProvider, HeartRateConsentListener {


    final Handler handler = new Handler();
    private CircleButton mButtonConnect;
    private Button mButtonChooseBand;
    private Button mButtonSetLimits;
    private EditText mLow;
    private EditText mHigh;
    private EditText mDuration;


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
                mButtonConnect.setColor(Color.GRAY);


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
                        mButtonConnect.setColor(Color.GRAY);


                        //     AddReminders(settings.getStartHour(),settings.getStopHour(),settings);
                        settings.setSessionRunning(true);
                        settings.setRecordingStart(System.currentTimeMillis());


                    } else {

                        Time today = new Time(Time.getCurrentTimezone());
                        today.setToNow();
                        settings.setSessionFolder(CreateFolder(today));
                        LogHandler.getInstance().Log("Session started by user");
                        mButtonConnect.setEnabled(false);
                        mButtonConnect.setColor(Color.GRAY);
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

        mButtonConnect = (CircleButton) rootView.findViewById(R.id.buttonConnect);
        mButtonConnect.setOnClickListener(mButtonConnectClickListener);


        mMonitoringStatus = (TextView) rootView.findViewById(R.id.textConnectionStatus);
        mSensorStatus = (TextView) rootView.findViewById(R.id.textSensorStatus);
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

    private RecordingService getService() {
        return RecordingServiceHandler.getInstance().getService();
    }

    private void refreshControls() {


        Activity activity = getActivity();
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    RecordingService service = getService();
                    if (service != null) {


                        if (mButtonConnect != null) {
                            mButtonConnect.setEnabled(true);

                            if (service.getSessionMustRun()) {

                                mButtonConnect.setColor(Color.RED);
                                mButtonConnect.setImageResource(R.drawable.ic_action_stop);
                            } else {

                                mButtonConnect.setColor(Color.GREEN);
                                mButtonConnect.setImageResource(R.drawable.ic_action_play);

                                //  mButtonConnect.setText("Start");

                            }


                        } else

                        {

                            mButtonConnect.setEnabled(false);
                            mButtonConnect.setColor(Color.GREEN);
                            mButtonConnect.setImageResource(R.drawable.ic_action_play);
                            mButtonConnect.setEnabled(true);


                        }
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

}
