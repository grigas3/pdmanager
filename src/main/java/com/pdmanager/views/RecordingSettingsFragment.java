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


public class RecordingSettingsFragment extends BasePDFragment implements FragmentListener, IServiceStatusListener {


    private Switch mSwitchBandSensors;
    //private Switch mSwitchHeartRate;
    //private Switch mSwitchST;
    // private Switch mSwitchBandAcc;
    //private Switch mSwitchBandGyro;
    private Switch mSwitchDevAcc;


    private Spinner mLangSpin;
    private Spinner mSendorDelaySpin;

    private HashSet<Switch> mSensorMap = new HashSet<Switch>();
    private boolean enableSpinnerListener = false;
    private Switch mSwitchRecordFile;
    private Switch mSwitchUseDiary;
    private Switch mSwitchUseDetectors;
    private Switch mSwitchUseDeviceLock;
    private Switch mSwitchUseSpeech;



    private CompoundButton.OnCheckedChangeListener mToggleSensorSection = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // Turn on the appropriate sensor
            Switch sw = (Switch) buttonView;

            final boolean running = RecordingServiceHandler.getInstance().getService().isSessionRunning();


            RecordingSettings settings = getSettings();


            if (!running) {

                if (sw == mSwitchBandSensors) {


                    settings.setBandEnabled(isChecked);
                    if (isChecked) {

                        mSendorDelaySpin.setEnabled(true);

                    } else {


                        mSendorDelaySpin.setEnabled(false);



                    }


                } else if (sw == mSwitchDevAcc) {


                    settings.setDevEnabled(isChecked);

                } else if (sw == mSwitchUseDiary) {

                    settings.setEnableDiary(isChecked);


                } else if (sw == mSwitchUseDetectors) {

                    settings.setUseDetectors(isChecked);

                } else if (sw == mSwitchRecordFile) {

                    settings.setRecordFiles(isChecked);


                } else if (sw == mSwitchUseDeviceLock) {

                    settings.setUseDeviceLock(isChecked);


                } else if (sw == mSwitchUseSpeech) {

                    settings.setUseSpeech(isChecked);


                }

            } else

            {

            }



        }
    };


    public RecordingSettingsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);



        mSwitchRecordFile = (Switch) rootView.findViewById(R.id.switchRecordFile);
        mSwitchRecordFile.setOnCheckedChangeListener(mToggleSensorSection);

        mSwitchUseDiary = (Switch) rootView.findViewById(R.id.switchUseDiary);
        mSwitchUseDiary.setOnCheckedChangeListener(mToggleSensorSection);


        //Switch Setup
        mSwitchBandSensors = (Switch) rootView.findViewById(R.id.switchBandSensors);
        mSwitchBandSensors.setOnCheckedChangeListener(mToggleSensorSection);




        mSwitchDevAcc = (Switch) rootView.findViewById(R.id.switchDeviceSensors);
        mSendorDelaySpin = (Spinner) rootView.findViewById(R.id.sensorDelaySpinner);

        mLangSpin=(Spinner) rootView.findViewById(R.id.langSpin);
        //  mSwitchST.setOnCheckedChangeListener(mToggleSensorSection);
        mSwitchDevAcc.setOnCheckedChangeListener(mToggleSensorSection);


        mSwitchRecordFile = (Switch) rootView.findViewById(R.id.switchRecordFile);
        mSwitchRecordFile.setOnCheckedChangeListener(mToggleSensorSection);

        mSwitchUseDetectors = (Switch) rootView.findViewById(R.id.switchUseDetectors);
        mSwitchUseDetectors.setOnCheckedChangeListener(mToggleSensorSection);

        mSwitchUseDeviceLock = (Switch) rootView.findViewById(R.id.swithUseDeviceLock);
        mSwitchUseDeviceLock.setOnCheckedChangeListener(mToggleSensorSection);

        mSwitchUseSpeech = (Switch) rootView.findViewById(R.id.switchUseSpeech);
        mSwitchUseSpeech.setOnCheckedChangeListener(mToggleSensorSection);


        mSensorMap.add(mSwitchDevAcc);
        mSensorMap.add(mSwitchBandSensors);

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
            mSwitchBandSensors.setChecked(settings.isBandEnabled());

            mSwitchDevAcc.setChecked(settings.isDevEnabled());
            mSwitchRecordFile.setChecked(settings.getRecordFiles());
            mSwitchUseDetectors.setChecked(settings.getUseDetectors());
            mSwitchUseDeviceLock.setChecked(settings.getUseDeviceLock());
            mLangSpin.setSelection(getLangSelection(settings.getLang()));


            //mPatientCode.setSelection(getPatientSelection(settings.getPatientID()));
            //mOrganizationSpin.setSelection(getOrgSelection(settings.getOrganization()));
            mSendorDelaySpin.setSelection(getDelaySelection(settings.getSensorDelay()));
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


        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list1);
        mSendorDelaySpin.setAdapter(dataAdapter1);


/*        ArrayList<String> list2 = new ArrayList<String>();



        list2.add(RecordingSettings.UOI);
        list2.add(RecordingSettings.IRCCS);
        list2.add(RecordingSettings.URI);
        list2.add(RecordingSettings.UNIVERSITYOFSURREY);
        list2.add(RecordingSettings.FONDAZIONESANTALUCIA);


        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list2);
        mOrganizationSpin.setAdapter(dataAdapter2);
        */


        ArrayList<String> list3 = new ArrayList<String>();

        list3.add("en");
        list3.add("el");
        list3.add("it");
        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list3);
        mLangSpin.setAdapter(dataAdapter3);

     /*   for(int i=1;i<100;i++)
            list3.add(String.format("PAT%02d", i));




        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list3);
        mPatientCode.setAdapter(dataAdapter3);
        */

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


        //   mOrganizationSpin.setOnItemSelectedListener(new OrgSelectedListener());

        mLangSpin.setOnItemSelectedListener(new LangSelectedListener());
        // mPatientCode.setOnItemSelectedListener(new PatientSelectedListener());
        mSendorDelaySpin.setOnItemSelectedListener(new SensorDelaySelectedListener());
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
