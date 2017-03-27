package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.models.UserAlert;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.patient.cognition.cognitive.AttentionSwitchingTaskTest;
import com.pdmanager.views.patient.cognition.cognitive.LondonTowersTest;
import com.pdmanager.views.patient.cognition.cognitive.PALPRM;
import com.pdmanager.views.patient.cognition.cognitive.PairedAssociatesLearningTest;
import com.pdmanager.views.patient.cognition.cognitive.PatternRecognitionMemoryTest;
import com.pdmanager.views.patient.cognition.cognitive.SpatialSpanTest;
import com.pdmanager.views.patient.cognition.cognitive.SpatialWorkingMemoryTest;
import com.pdmanager.views.patient.cognition.cognitive.StopSignalTaskTest;
import com.pdmanager.views.patient.cognition.cognitive.VisualAnalogueScaleTest;
import com.pdmanager.views.patient.cognition.cognitive.WisconsinCardSorting;
import com.pdmanager.views.patient.cognition.fingertapping.FingerTappingTestOne;
import com.pdmanager.views.patient.cognition.speech.SpeechTest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    private static final String TAG = "PatientHomeFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    final Handler handler = new Handler();
    TextView mTextNextMed;
    TextView message;
    private Button mButtonNGG;
    private Button mButtonMood;
    //private TextView mSensorStatus;
    //private TextView mMonitoringStatus;
    private LinearLayout mDiary;
    private ImageView mDiaryImage;
    private TextView mDiaryTitle;
    private ImageView mDiaryAct;
    private TextView mDiaryText;

    private LinearLayout mSensors;
    private ImageView mSensorsImage;
    private TextView mSensorsTitle;
    private TextView mTaskTitle;
    private TextView mNoTaskTitle;
    private ImageView mSensorsAct;
    private TextView mSensorsText;
    private Map<String,LinearLayout> codeLayoutMapping;
    private Map<String,TextView> codeTextMapping;
    private LinearLayout mMedication;
    private LinearLayout mMood;
    private LinearLayout mCognition1;
    private LinearLayout mCognition2;

    private LinearLayout mFingerTapping;
    private LinearLayout mVoiceTest;
    private LinearLayout mVisualAnalogue;


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



        final Context context=this.getContext();

        mNoTaskTitle = (TextView) rootView.findViewById(R.id.patient_home_notasks);
        mTaskTitle = (TextView) rootView.findViewById(R.id.patient_home_tasks);
        mVisualAnalogue = (LinearLayout) rootView.findViewById(R.id.patient_home_visualanalogue);
        mFingerTapping = (LinearLayout) rootView.findViewById(R.id.patient_home_fingertapping);
        mVoiceTest = (LinearLayout) rootView.findViewById(R.id.patient_home_voicetest);

        mDiary = (LinearLayout) rootView.findViewById(R.id.patient_home_notifications);
        mDiaryImage = (ImageView) rootView.findViewById(R.id.patient_home_notifications_img);
        mDiaryTitle = (TextView) rootView.findViewById(R.id.patient_home_notifications_title);
        mDiaryAct = (ImageView) rootView.findViewById(R.id.patient_home_notifications_act);
        mDiaryText = (TextView) rootView.findViewById(R.id.patient_home_notifications_text);

        mSensors = (LinearLayout) rootView.findViewById(R.id.patient_home_sensors);
        mSensorsImage = (ImageView) rootView.findViewById(R.id.patient_home_sensors_img);
        mSensorsTitle = (TextView) rootView.findViewById(R.id.patient_home_sensors_title);
        mSensorsAct = (ImageView) rootView.findViewById(R.id.patient_home_sensors_act);
        mSensorsText = (TextView) rootView.findViewById(R.id.patient_home_sensors_text);
        mMedication = (LinearLayout) rootView.findViewById(R.id.patient_home_meds);
        mMood = (LinearLayout) rootView.findViewById(R.id.patient_home_mood);
        mCognition1 = (LinearLayout) rootView.findViewById(R.id.patient_home_cognition1);
        mCognition2 = (LinearLayout) rootView.findViewById(R.id.patient_home_cognition2);
        mSpeech = (LinearLayout) rootView.findViewById(R.id.patient_home_speech);

        codeTextMapping=new HashMap<>();

        codeTextMapping.put("med", (TextView) rootView.findViewById(R.id.patient_home_meds_text));
        codeTextMapping.put("cogn1", (TextView) rootView.findViewById(R.id.patient_home_mood_text));
        codeTextMapping.put("cogn2",(TextView) rootView.findViewById(R.id.patient_home_cognition_text1));
        codeTextMapping.put("speech", (TextView) rootView.findViewById(R.id.patient_home_cognition_text2));
        codeTextMapping.put("mood",(TextView) rootView.findViewById(R.id.patient_home_speech_text));

        codeTextMapping.put("ft",(TextView) rootView.findViewById(R.id.patient_home_fingertapping_text));
        codeTextMapping.put("va",(TextView) rootView.findViewById(R.id.patient_home_visualanalogue_text));
        codeTextMapping.put("vt",(TextView) rootView.findViewById(R.id.patient_home_voicetest_text));

        codeTextMapping.put("diary",mDiaryText);




        codeLayoutMapping=new HashMap<>();


        codeLayoutMapping.put("ft",mFingerTapping);
        codeLayoutMapping.put("vt",mVoiceTest);
        codeLayoutMapping.put("va",mVisualAnalogue);

        codeLayoutMapping.put("med",mMedication);
        codeLayoutMapping.put("cogn1",mCognition1);
        codeLayoutMapping.put("cogn2",mCognition2);
        codeLayoutMapping.put("speech",mSpeech);
        codeLayoutMapping.put("mood",mMood);
        codeLayoutMapping.put("diary",mDiary);





        mVisualAnalogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "VA test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("VA");


                Intent menuPALIntent =
                        new Intent(getActivity(), VisualAnalogueScaleTest.class);
                startActivity(menuPALIntent);



            }
        });

        mDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("LOG", "test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("DIARY");


                Intent diaryTracking =
                        new Intent(getActivity(), DiaryTrackingActivity.class);
                startActivity(diaryTracking);


            }
        });




        mFingerTapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOG", "test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("FT");
                Intent menuPALIntent =
                        new Intent(getActivity(), FingerTappingTestOne.class);
                startActivity(menuPALIntent);



            }
        });

        mVoiceTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOG", "test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("VT");
                Intent menuPALIntent =
                        new Intent(getActivity(), SpeechTest.class);
                startActivity(menuPALIntent);



            }
        });


        mCognition1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOG", "test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("COGN1");


                startCongnitiveTest();
            }
        });


        mCognition2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("LOG", "test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("COGN2");
                startCongnitiveTest();
            }
        });

        mMood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("MOOD");

                Intent menuPALIntent =
                        new Intent(getActivity(), MoodTrackingActivity.class);
                startActivity(menuPALIntent);
              /*  MoodTrackingActivity moodTrackingFragment = new MoodTrackingActivity();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, moodTrackingFragment);
                fragmentTransaction.commit();
                */
            }
        });


        mMedication.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("MED");
                Intent menuPALIntent =
                        new Intent(getActivity(), MedAlertActivity.class);
                startActivity(menuPALIntent);

            }
        });


        mSpeech.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("SPEECH");
                SpeechAnalysisFragment speechAnalysisFragment = new SpeechAnalysisFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, speechAnalysisFragment);
                fragmentTransaction.commit();
            }
        });



        return rootView;
    }




    private void startCongnitiveTest()
    {
        Random rn = new Random();
        int n = 8 + 1;
        int i = rn.nextInt() % n;

        if(i>8)
            i=8;

        switch(i) {
            case 0:
                Intent menuPALPRMIntent =
                        new Intent(getActivity(), PALPRM.class);

                startActivity(menuPALPRMIntent);

                break;

            case 1:
                Intent menuPALIntent =
                        new Intent(getActivity(), PairedAssociatesLearningTest.class);
                startActivity(menuPALIntent);

                break;

            case 2:
                Intent menuPRMIntent =
                        new Intent(getActivity(), PatternRecognitionMemoryTest.class);
                startActivity(menuPRMIntent);

                break;

            case 3:
                Intent menuSWMIntent =
                        new Intent(getActivity(), SpatialWorkingMemoryTest.class);
                startActivity(menuSWMIntent);

                break;

            case 4:
                Intent menuSSIntent =
                        new Intent(getActivity(), SpatialSpanTest.class);
                startActivity(menuSSIntent);

                break;

            case 5:
                Intent menuSSTIntent =
                        new Intent(getActivity(), StopSignalTaskTest.class);
                startActivity(menuSSTIntent);

                break;

            case 6:
                Intent menuASTIntent =
                        new Intent(getActivity(), AttentionSwitchingTaskTest.class);
                startActivity(menuASTIntent);

                break;



            case 7:
                Intent intent = new Intent(getActivity(), WisconsinCardSorting.class);
                startActivity(intent);

                break;

            case 8:
                Intent lndn = new Intent(getActivity(), LondonTowersTest.class);
                startActivity(lndn);

                break;
        }
    }

    public void onDestroy()
    {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }
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

        stopTimer();
        super.onPause();

    }

    @Override
    public void onStop() {

        stopTimer();
        super.onStop();


    }

    private void stopTimer() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }


    }
    private void startTimer()
    {

        if (myTimer == null) {
            try {
                myTimer = new Timer();
                HomeTimerTask myTask = new HomeTimerTask();
                myTimer.schedule(myTask,1000, 60000);

            } catch (Exception ex) {

                Log.e("Timer", ex.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startTimer();
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

        if(mSensors!=null) {
            mSensors.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_red));
            //   mSensorsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
            mSensorsTitle.setTextColor(Color.WHITE);
            mSensorsAct.setVisibility(View.INVISIBLE);
            mSensorsText.setTextColor(Color.WHITE);
            //mSensorsText.setTextColor(Color.parseColor("#ff3333"));
            mSensorsText.setText(error);
        }
    }
    private void showMonitoringOK()
    {
        if(mSensors!=null) {
            mSensors.setVisibility(View.GONE);
         /*   mSensors.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_green));
            //   mSensorsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
            mSensorsTitle.setTextColor(Color.BLACK);
            mSensorsAct.setVisibility(View.VISIBLE);
            mSensorsText.setTextColor(Color.parseColor("#555555"));
            mSensorsText.setText("WORKING");
            */
        }


    }
    private void showNotMonitoring()
    {
        if(mSensors!=null) {


            mSensors.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_grey));
            //mSensorsImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_check_sm_g));
            mSensorsTitle.setTextColor(Color.WHITE);
            mSensorsAct.setVisibility(View.INVISIBLE);
            mSensorsText.setTextColor(Color.WHITE);
            //mSensorsText.setTextColor(Color.parseColor("#e2e2e2"));
            mSensorsText.setText("Not Monitoring");

        }


    }


    void updateUIAlerts()
    {
        final Context context=this.getContext();
       final UserAlertManager manager=new UserAlertManager((this.getContext()));
       final Activity activity = getActivity();
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {

                        for (LinearLayout l : codeLayoutMapping.values()) {

                            if (l != null) {

                                // l.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_green));

                                l.setVisibility(View.GONE);
                          /*  l.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.button_patient_home_grey));
                           l.setAlpha(0.5F);
                            l.setEnabled(false);
                            */

                            } else {

                                Log.w(TAG, "An error layout found when updating UI");
                            }
                        }
                        long mind = 1000000000;
                        String noTaskMessage = "";

                        List<UserAlert> allAlerts = manager.getAlerts();
                        for (UserAlert a : allAlerts) {

                            if (codeTextMapping.containsKey(a.getAlertType())) {

                                Date date1 = new java.util.Date(a.getExpiration());
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(date1);
                                Date date2 = new java.util.Date();
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(date2);
                                long d = (date1.getTime() - date2.getTime()) / 1000 / 60;
                                if (d > 0) {

                                    if (d > 24 * 60) {

                                        if (mind > d) {
                                            mind = d;
                                            noTaskMessage = String.format(context.getString(R.string.next_day), (int) (d / 60 / 24));

                                        }
                                        codeTextMapping.get(a.getAlertType()).setText(String.format(context.getString(R.string.next_day), (int) (d / 60 / 24)));
                                    } else if (d > 60) {

                                        if (mind > d) {
                                            mind = d;
                                            noTaskMessage = String.format(context.getString(R.string.next_hour), (int) (d / 60));

                                        }
                                        codeTextMapping.get(a.getAlertType()).setText(String.format(context.getString(R.string.next_hour), (int) (d / 60)));
                                    } else {

                                        if (mind > d) {
                                            mind = d;
                                            noTaskMessage = String.format(context.getString(R.string.next_minute), (int) (d));

                                        }
                                        codeTextMapping.get(a.getAlertType()).setText(String.format(context.getString(R.string.next_minute), (int) (d)));

                                    }


                                } else {
                                    codeTextMapping.get(a.getAlertType()).setText(context.getString(R.string.now));

                                }


                            }


                        }
                        for (UserAlert a : allAlerts) {

                            if (codeLayoutMapping.containsKey(a.getAlertType())) {

                                Date date1 = new java.util.Date(a.getExpiration());
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(date1);
                                Date date2 = new java.util.Date();
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(date2);
                                long d = (date1.getTime() - date2.getTime()) / 1000 / 60;
                              /*  if (d > 0) {

                                    if (d > 24 * 60) {

                                    } else if (d > 60) {

                                        codeLayoutMapping.get(a.getAlertType()).setVisibility(View.VISIBLE);
                                    } else {

                                    }


                                }
                                */

                                if (d < 0) {
                                    codeLayoutMapping.get(a.getAlertType()).setVisibility(View.VISIBLE);

                                }

                            }


                        }
                        List<UserAlert> alerts = manager.getActive();

                        if (alerts.size() > 0) {

                            mTaskTitle.setText(String.format(context.getString(R.string.task_number), (int) (alerts.size())));

                            mTaskTitle.setVisibility(View.VISIBLE);
                            mNoTaskTitle.setVisibility(View.GONE);


                        } else {
                            mNoTaskTitle.setText(noTaskMessage);
                            mTaskTitle.setVisibility(View.INVISIBLE);
                            mNoTaskTitle.setVisibility(View.VISIBLE);
                        }

                        for (UserAlert a : alerts) {
                            if (codeLayoutMapping.containsKey(a.getAlertType())) {
                                LinearLayout layout = codeLayoutMapping.get(a.getAlertType());
                                if (layout != null) {

                                    // layout.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.button_patient_home_green));
                                    layout.setAlpha(1);
                                    layout.setEnabled(true);
                                }


                            }
                        }

                    }
                    catch (Exception e)
                    {

                        Log.e(TAG,"Home Timer", e.getCause());
                    }


                }



            });
        }






    }

    class HomeTimerTask extends TimerTask {
        public void run() {

//First Update UI Alerts
            updateUIAlerts();
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

                                    showMonitoringOK();
                                    if (service.hasFatalError())

                                    {


                                        if (service.getFatalErrorCode() == 2) {
//                                            mSensorStatus.setText("Band is not recording...please move phone close to your Band");


                                        }

                                        if (service.getFatalErrorCode() == 1) {
                                            //mMonitoringStatus.setText("SOMETHING IS WROKGBand is not paired...please re-pair your Band from the band device...PLEASE ASK YOUR TECHNICIAN IF YOU NEED MORE INFO");
                                            showMonitoringError("Something is wrong");


                                        } else

                                        {

                                        //    mSensorStatus.setText("Exception while connecting to Band");

                                        }

                                    } else {

                                      //  mSensorStatus.setText("Something is wrong...restart the application");

                                    }


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