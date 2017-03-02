package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.IAlertDisplay;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.patient.cognition.MainMenu;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class PatientHomeFragment extends AlertPDFragment implements IServiceStatusListener,ISensorStatusListener,IAlertDisplay {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    final Handler handler = new Handler();
    TextView mTextNextMed;
    TextView message;
    private Button mButtonNGG;
    private Button mButtonMood;
    //private TextView mSensorStatus;
    //private TextView mMonitoringStatus;
    private LinearLayout mNotifications;
    private ImageView mNotificationsImage;
    private TextView mNotificationsTitle;
    private ImageView mNotificationsAct;
    private TextView mNotificationsText;

    private LinearLayout mSensors;
    private ImageView mSensorsImage;
    private TextView mSensorsTitle;
    private ImageView mSensorsAct;
    private TextView mSensorsText;


    private LinearLayout mMood;
    private LinearLayout mCognition;
    private LinearLayout mSpeech;
    private boolean debugToggle = false;
    private RelativeLayout layout;
    public PatientHomeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PatientHomeFragment newInstance(int sectionNumber) {
        PatientHomeFragment fragment = new PatientHomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_home, container, false);

    //    mMonitoringStatus = (TextView) rootView.findViewById(R.id.patient_home_sensors_text);
      /*  message = (TextView) rootView.findViewById(R.id.textWelcome);
        mTextNextMed =(TextView) rootView.findViewById(R.id.textNextMedication);
        mButtonNGG =(Button) rootView.findViewById(R.id.nfgButton);

        mMonitoringStatus = (TextView) rootView.findViewById(R.id.textConnectionStatus);
        mSensorStatus = (TextView) rootView.findViewById(R.id.textSensorStatus);


        mButtonNGG.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                NotFeelingGoodFragment ffragment=new NotFeelingGoodFragment();
                fragmentTransaction.addToBackStack(null);
                ffragment.show(fragmentTransaction, "dialog");



            }
        });

        mButtonMood =(Button) rootView.findViewById(R.id.moodButton);




        mButtonMood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction =
                        fragmentManager.beginTransaction();
                MoodTrackingFragment ffragment=new MoodTrackingFragment();
                fragmentTransaction.addToBackStack(null);
                ffragment.show(fragmentTransaction, "dialog");




            }
        });
        */


        mNotifications = (LinearLayout) rootView.findViewById(R.id.patient_home_notifications);
        mNotificationsImage = (ImageView) rootView.findViewById(R.id.patient_home_notifications_img);
        mNotificationsTitle = (TextView) rootView.findViewById(R.id.patient_home_notifications_title);
        mNotificationsAct = (ImageView) rootView.findViewById(R.id.patient_home_notifications_act);
        mNotificationsText = (TextView) rootView.findViewById(R.id.patient_home_notifications_text);

        mSensors = (LinearLayout) rootView.findViewById(R.id.patient_home_sensors);
        mSensorsImage = (ImageView) rootView.findViewById(R.id.patient_home_sensors_img);
        mSensorsTitle = (TextView) rootView.findViewById(R.id.patient_home_sensors_title);
        mSensorsAct = (ImageView) rootView.findViewById(R.id.patient_home_sensors_act);
        mSensorsText = (TextView) rootView.findViewById(R.id.patient_home_sensors_text);

        mMood = (LinearLayout) rootView.findViewById(R.id.patient_home_mood);
        mCognition = (LinearLayout) rootView.findViewById(R.id.patient_home_cognition);
        mSpeech = (LinearLayout) rootView.findViewById(R.id.patient_home_speech);

        mNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugToggle = !debugToggle;
                if(debugToggle) {
                    mNotifications.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_blue));
                    mNotificationsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_notif_w));
                    mNotificationsTitle.setTextColor(Color.WHITE);
                    mNotificationsAct.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_w));
                    mNotificationsText.setTextColor(Color.WHITE);
                    mNotificationsText.setText("Display now");
                } else {
                    mNotifications.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_green));
                    mNotificationsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_notif_g));
                    mNotificationsTitle.setTextColor(Color.BLACK);
                    mNotificationsAct.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
                    mNotificationsText.setTextColor(Color.parseColor("#555555"));
                    mNotificationsText.setText("Clear");
                }
            }
        });

        mCognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOG", "test");
                startActivity(new Intent(getActivity(), MainMenu.class));
            }
        });

        mMood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction =
//                        fragmentManager.beginTransaction();
//                MoodTrackingFragment ffragment=new MoodTrackingFragment();
//                fragmentTransaction.addToBackStack(null);
//                ffragment.show(fragmentTransaction, "dialog");
                MoodTrackingFragment moodTrackingFragment = new MoodTrackingFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, moodTrackingFragment);
                fragmentTransaction.commit();
            }
        });

        mSpeech.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SpeechAnalysisFragment speechAnalysisFragment = new SpeechAnalysisFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, speechAnalysisFragment);
                fragmentTransaction.commit();
            }
        });



        return rootView;
    }




    public void onDestroy()
    {

        super.onDestroy();

    }





    @Override
    public void notifyServiceStatusChanged() {
        refreshControls();
    }

    private RecordingService getService() {
        return RecordingServiceHandler.getInstance().getService();
    }
    private void refreshControls() {



    }

    private Timer myTimer;

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
        HomeTimerTask myTask = new HomeTimerTask();
        myTimer.schedule(myTask, 10000, 30000);


        refreshControls();
    }

    @Override
    public void notifySensorStatusChanged(boolean status) {

    }

    @Override
    public void setAlertDisplay(final String messageTxt) {

        Activity activity = getActivity();
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(message!=null)
                    message.setText(messageTxt);

                }
            });
        }
    }

    private void showMonitoringError(String error)
    {

        mSensors.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_red));
     //   mSensorsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
        mSensorsTitle.setTextColor(Color.WHITE);
        mSensorsAct.setVisibility(View.INVISIBLE);
        mSensorsText.setTextColor(Color.WHITE);
        //mSensorsText.setTextColor(Color.parseColor("#ff3333"));
        mSensorsText.setText(error);
    }
    private void showMonitoringOK()
    {

        mSensors.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_green));
     //   mSensorsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
        mSensorsTitle.setTextColor(Color.BLACK);
        mSensorsAct.setVisibility(View.VISIBLE);
        mSensorsText.setTextColor(Color.parseColor("#555555"));
        mSensorsText.setText("WORKING");


    }
    private void showNotMonitoring()
    {

        mSensors.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_grey));
        //mSensorsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
        mSensorsTitle.setTextColor(Color.WHITE);
        mSensorsAct.setVisibility(View.INVISIBLE);
        mSensorsText.setTextColor(Color.WHITE);
        //mSensorsText.setTextColor(Color.parseColor("#e2e2e2"));
        mSensorsText.setText("Not Monitoring");


    }

    class HomeTimerTask extends TimerTask {
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

                        //   mMonitoringStatus.setTextColor(Color.GREEN);
                      //     mMonitoringStatus.setText("monitoring");




                            //    mSensorStatus.setVisibility(View.VISIBLE);
                                if (service.isAllRecording()) {

                                    showMonitoringOK();
                                    //showMonitoringError("Something is wrong");

                                } else

                                {


                                    if (service.hasFatalError())

                                    {


                                        if (service.getFatalErrorCode() == 2) {
//                                            mSensorStatus.setText("Band is not recording...please move phone close to your Band");


                                        }

                                        if (service.getFatalErrorCode() == 1) {
                                            //mMonitoringStatus.setText("SOMETHING IS WROKGBand is not paired...please re-pair your Band from the band device...PLEASE ASK YOUR TECHNICIAN IF YOU NEED MORE INFO");



                                        } else

                                        {

                                        //    mSensorStatus.setText("Exception while connecting to Band");

                                        }

                                    } else {

                                      //  mSensorStatus.setText("Something is wrong...restart the application");

                                    }
                                    showMonitoringError("Something is wrong");

                                }



                        } else {

                        showNotMonitoring();

                           // if (mSensorStatus != null)
                             //   mSensorStatus.setVisibility(View.INVISIBLE);
                        }

                    }
                });


            }
        }
    }

}