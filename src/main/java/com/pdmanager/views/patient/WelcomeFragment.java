package com.pdmanager.views.patient;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.IAlertDisplay;
import com.pdmanager.interfaces.ISensorStatusListener;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.FragmentListener;

import java.util.Timer;
import java.util.TimerTask;


public class WelcomeFragment extends BasePDFragment implements FragmentListener, IServiceStatusListener, ISensorStatusListener {


    final Handler handler = new Handler();
    private Button mButtonNFG;

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




    public WelcomeFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_band, container, false);


        mMonitoringStatus = (TextView) rootView.findViewById(R.id.textConnectionStatus);
        mSensorStatus = (TextView) rootView.findViewById(R.id.textSensorStatus);

        busyIndicator=(ProgressBar)  rootView.findViewById(R.id.sync_progress);

        layout=(LinearLayout)  rootView.findViewById(R.id.mainLayout);


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



    }

    @Override
    public void notifyServiceStatusChanged() {


        refreshControls();

    }


    @Override
    public void notifySensorStatusChanged(boolean status) {


    }

    @Override
    public void onFragmentSelected() {

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
                                        //    mSensorStatus.setText("Band is not recording...please move phone close to your Band");


                                        }

                                        if (service.getFatalErrorCode() == 1) {
                                            mSensorStatus.setText("Band is not paired...please re-pair your Band from the band device or conntact technician");


                                        } else

                                        {

                                         //   mSensorStatus.setText("Exception while connecting to Band");

                                        }

                                    } else {

                                   //     mSensorStatus.setText("Something is wrong...restart the application");

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
