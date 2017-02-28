package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.IAlertDisplay;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.services.RecordingService;

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
    private TextView mSensorStatus;
    private TextView mMonitoringStatus;

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


        message = (TextView) rootView.findViewById(R.id.textWelcome);
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

                            mMonitoringStatus.setTextColor(Color.GREEN);
                           mMonitoringStatus.setText("monitoring");

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
//                                            mSensorStatus.setText("Band is not recording...please move phone close to your Band");


                                        }

                                        if (service.getFatalErrorCode() == 1) {
                                            mSensorStatus.setText("Band is not paired...please re-pair your Band from the band device...PLEASE ASK YOUR TECHNICIAN IF YOU NEED MORE INFO");


                                        } else

                                        {

                                        //    mSensorStatus.setText("Exception while connecting to Band");

                                        }

                                    } else {

                                      //  mSensorStatus.setText("Something is wrong...restart the application");

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