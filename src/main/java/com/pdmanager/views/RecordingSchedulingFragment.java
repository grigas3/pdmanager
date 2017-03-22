package com.pdmanager.views;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.pdmanager.R;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.settings.RecordingSettings;

import java.util.ArrayList;
import java.util.HashSet;


public class RecordingSchedulingFragment extends BasePDFragment implements FragmentListener, IServiceStatusListener {


    private Spinner mCognitiveTestHour2;
    private Spinner mCognitiveTestHour1;
    private Spinner mMoodHour;
    private Spinner mStartHourSpin;

    private Spinner mEndHourSpin;
    private Spinner mDiaryHour;
    private Spinner mMedAlertHour2;
    private Spinner mMedAlertHour1;


    private HashSet<Switch> mSensorMap = new HashSet<Switch>();
    private boolean enableSpinnerListener = false;




    public RecordingSchedulingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();


        initSettings();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings_scheduling, container, false);


        mStartHourSpin = (Spinner) rootView.findViewById(R.id.startHourSpinner);
        mEndHourSpin = (Spinner) rootView.findViewById(R.id.stopHourSpinner);



        mCognitiveTestHour1 = (Spinner) rootView.findViewById(R.id.cognHourSpinner1);
        mCognitiveTestHour2 = (Spinner) rootView.findViewById(R.id.cognHourSpinner2);
        mMoodHour = (Spinner) rootView.findViewById(R.id.moodSetHour);

        mMedAlertHour1 = (Spinner) rootView.findViewById(R.id.medSetHour1);
        mMedAlertHour2 = (Spinner) rootView.findViewById(R.id.medSetHour2);
        mDiaryHour = (Spinner) rootView.findViewById(R.id.diaryHour);


        enableSpinnerListener = false;
        addItemsOnSpinners();
        addListenerOnSpinnerItemSelection();
        //   mButtonPatients=(Button) rootView.findViewById(R.id.button);



        initSettings();


    /*    mSwitchBandSensors.setChecked(settings.isBandEnabled());
        mSwitchBandAcc.setChecked(settings.isBandaccEnabled());
        mSwitchBandGyro.setChecked(settings.isBandgyroEnabled());
        mSwitchHeartRate.setChecked(settings.isheartRateEnabled());
        mSwitchDevAcc.setChecked(settings.isDevEnabled());


*/


        return rootView;


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {


        enableSpinnerListener = false;
        super.onDetach();

    }

    private void initSettings() {


        try {

            enableSpinnerListener = false;
            RecordingSettings settings = getSettings();
              mStartHourSpin.setSelection(settings.getStartHour() - 6);
            mEndHourSpin.setSelection(settings.getStopHour() - 6);



            mCognitiveTestHour1.setSelection(settings.getCognHour1() - 6);
            mCognitiveTestHour2.setSelection(settings.getCognHour2() - 6);



            mMedAlertHour1.setSelection(settings.getMedHour1() - 6);
            mMedAlertHour2.setSelection(settings.getMedHour2() - 6);



            mDiaryHour.setSelection(settings.getDiaryHour() - 6);
            mMoodHour.setSelection(settings.getMoodHour() - 6);




            enableSpinnerListener = true;
        } catch (Exception ex) {


            Log.d("Settings", ex.getMessage());

        }
        refreshControls();


    }


    @Override
    public void onFragmentSelected() {


        initSettings();

    }


    private void refreshControls() {


        Activity activity = getActivity();
        if (activity != null) {


                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final boolean running = RecordingServiceHandler.getInstance().getService().isSessionRunning();

                            for (Switch sw : mSensorMap) {


                                sw.setSelected(true);
                                sw.setEnabled(!running);
                                sw.setClickable(!running);
                            }

                        }
                        catch (Exception ex)
                        {

                            Log.e("Settings Fragment","Refresh Controls",ex.getCause());
                        }


                    }
                });




        }


    }

    @Override
    public void notifyServiceStatusChanged() {


        refreshControls();
    }

    private int getDelaySelection(int p) {


        if (p == SensorManager.SENSOR_DELAY_NORMAL)
            return 0;


        if (p == SensorManager.SENSOR_DELAY_GAME)
            return 1;


        if (p == SensorManager.SENSOR_DELAY_FASTEST)
            return 2;

        return 0;


    }
    private int getLangSelection(String p) {


        if (p == "en")
            return 0;


        if (p == "el")
            return 1;


        if (p == "it")
            return 2;

        return 0;


    }
    // add items into spinner dynamically
    public void addItemsOnSpinners() {


        ArrayList<String> list = new ArrayList<String>();
        for (int i = 6; i < 24; i++)
            list.add("" + i);


        ArrayList<String> list1 = new ArrayList<String>();


        list1.add("Normal");
        list1.add("Game");
        list1.add("Fastest");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStartHourSpin.setAdapter(dataAdapter);

        mEndHourSpin.setAdapter(dataAdapter);
        mCognitiveTestHour1.setAdapter(dataAdapter);
        mCognitiveTestHour2.setAdapter(dataAdapter);
        mMoodHour.setAdapter(dataAdapter);
        mMedAlertHour1.setAdapter(dataAdapter);
        mMedAlertHour2.setAdapter(dataAdapter);
        mDiaryHour.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list1);

    }

    /*private int getOrgSelection(String p)
    {




        if(p.equals(RecordingSettings.UOI))
            return 0;


        if(p.equals(RecordingSettings.IRCCS))
            return 1;



        if(p.equals(RecordingSettings.URI))
            return 2;
        if(p.equals(RecordingSettings.UNIVERSITYOFSURREY))
            return 3;

        if(p.equals(RecordingSettings.FONDAZIONESANTALUCIA))
            return 4;
        return 0;


    }
    */
    private int getPatientSelection(String p) {


        for (int i = 1; i < 100; i++) {

            if (p.equals(String.format("PAT%02d", i)))
                return i - 1;
        }

        return 0;


    }

    public void addListenerOnSpinnerItemSelection() {

        mStartHourSpin.setOnItemSelectedListener(new StartHourSelectedListener());
        mEndHourSpin.setOnItemSelectedListener(new EndHourSelectedListener());
        mCognitiveTestHour1.setOnItemSelectedListener(new CognHour1SelectedListener());
        mCognitiveTestHour2.setOnItemSelectedListener(new CognHour2SelectedListener());
        mMoodHour.setOnItemSelectedListener(new MoodHourSelectedListener());


        mMedAlertHour1.setOnItemSelectedListener(new MedAlert1SelectedListener());
        mMedAlertHour2.setOnItemSelectedListener(new MedAlert2SelectedListener());
        mDiaryHour.setOnItemSelectedListener(new DiarySelectedListener());

    }

    /*
        private class OrgSelectedListener implements AdapterView.OnItemSelectedListener {

            public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {


                String delay= RecordingSettings.UOI;
                if(pos==1)
                    delay=RecordingSettings.IRCCS;
                if(pos==2)
                    delay=RecordingSettings.URI;

                if(pos==3)
                    delay=RecordingSettings.UNIVERSITYOFSURREY;

                if(pos==4)
                    delay=RecordingSettings.FONDAZIONESANTALUCIA;


                if(enableSpinnerListener) {
                    RecordingSettings settings = getSettings();
                    if (settings != null) {
                        settings.setOrganization(delay);


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        }
        */
    // get the selected dropdown list Value
    public void addListenerOnButton() {


    }

    private class StartHourSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;
            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setStartHour(hour);

                }
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }


    private class CognHour1SelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setCognHour1(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private class CognHour2SelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setCognHour2(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }


    private class MoodHourSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setMoodHour(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }




    private class MedAlert1SelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setMedHour1(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private class MedAlert2SelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setMedHour2(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }


    private class DiarySelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setDiaryHour(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }




    private class EndHourSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            int hour = pos + 6;

            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {

                    settings.setStopHour(hour);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private class SensorDelaySelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            int delay = SensorManager.SENSOR_DELAY_NORMAL;
            if (pos == 1)
                delay = SensorManager.SENSOR_DELAY_GAME;
            if (pos == 2)
                delay = SensorManager.SENSOR_DELAY_FASTEST;


            if (enableSpinnerListener) {

                RecordingSettings settings = getSettings();
                if (settings != null) {
                    settings.setSensorDelay(delay);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
    private class LangSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            String lang ="en";
            if (pos == 1)
                lang = "el";
            if (pos == 2)
                lang = "it";


            if (enableSpinnerListener) {

                RecordingSettings settings = getSettings();
                if (settings != null) {
                    settings.setLang(lang);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }
    private class PatientSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            String delay = String.format("TEST%02d", pos + 1);


            if (enableSpinnerListener) {
                RecordingSettings settings = getSettings();
                if (settings != null) {
                    settings.setPatientID(delay);


                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

}
