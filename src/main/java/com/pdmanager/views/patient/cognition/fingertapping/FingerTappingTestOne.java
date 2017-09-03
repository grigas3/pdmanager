package com.pdmanager.views.patient.cognition.fingertapping;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.UserTaskCodes;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.DecimalFormat;
import java.util.Vector;


/**
 *
 * Finger Tapping Test Activity
 *
 * @authors Quentin DELEPIERRE, Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class FingerTappingTestOne extends SoundFeedbackActivity {
    public static final String DECIMAL_FORMAT_TIME = "#,#00.0";
    public static final String INTENT_MEAN_ONE = "meanTimeOne";
    public static final String INTENT_STD_ONE = "stdTimeOne";
    public static final String INTENT_MEDIAN_ONE = "medianTimeOne";
    public static final String INTENT_MAX_ONE = "maxTimeOne";
    public static final String INTENT_MIN_ONE = "minTimeOne";
    public static final String INTENT_TAPS_NUMBER_ONE = "tapsNumberOne";
    public final int DURATION_TEST_MILLISECONDS= 10000;
    public final int REFRESH_PERIOD_MILLISECONDS= 100;
    public final int TIME_MILLISECONDS_SLEEP = 1000;
    private String LOGGER_TAG = "FingerTappingTestOne";
    private TextView tvNumberTaps = null;
    private TextView tvTime = null;

    private int numberOfTaps;
    private Double startTime = Double.valueOf(0);
    private Double timeInMilli = Double.valueOf(0);

    private ProgressBar mProgress;

    private boolean test=true;

    private Vector vect = new Vector();

    private CountDownTimer sdTimer = null;
    private View.OnClickListener clickButton=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        speak.silence();

        if (test) {
            startTime = Double.longBitsToDouble(SystemClock.uptimeMillis());
            restart();
            test= false;

            sdTimer = new CountDownTimer(DURATION_TEST_MILLISECONDS, REFRESH_PERIOD_MILLISECONDS) {

                public void onTick(long millisUntilFinished) {

                    mProgress.setProgress((int) millisUntilFinished);

                    tvTime.setText(getResources().getString(R.string.fttoneTvTime) + " : "
                            + new DecimalFormat(DECIMAL_FORMAT_TIME).format(Double.valueOf(millisUntilFinished / Double.valueOf(1000)))
                            + " " + getResources().getString(R.string.fttoneTvSeconds));

                }

                public void onFinish() {

                    setContentView(R.layout.splash);

                    Thread welcomeThread = new Thread() {

                        @Override
                        public void run() {
                        try {
                            super.run();
                            sleep(TIME_MILLISECONDS_SLEEP) ; //Delay of 3 seconds
                        } catch (Exception e) {

                        } finally {

                            Intent intent = new Intent(FingerTappingTestOne.this, FingerTappingTestTwo.class);

                            double[] myData = new double[vect.size()];

                            for (int i = 0; i < vect.size() - 1; i++) {
                                Double o = (Double) vect.elementAt(i);
                                Double p = (Double) vect.elementAt(i + 1);
                                myData[i] = (p - o);
                            }

                            Statistics myStats = new Statistics(myData);

                            Double meanTime = myStats.getMean();
                            Double stdTime = myStats.getStdDev();
                            Double medianTime = myStats.median();
                            Double maxTime = myStats.getMax();
                            Double minTime = myStats.getMin();

                            intent.putExtra(INTENT_TAPS_NUMBER_ONE, numberOfTaps);
                            intent.putExtra(INTENT_MEAN_ONE, meanTime);
                            intent.putExtra(INTENT_STD_ONE, stdTime);
                            intent.putExtra(INTENT_MEDIAN_ONE, medianTime);
                            intent.putExtra(INTENT_MAX_ONE, maxTime);
                            intent.putExtra(INTENT_MIN_ONE, minTime);

                            startActivity(intent);
                            finish();
                        }
                        }
                    };
                    welcomeThread.start();
                }
            }.start();
        }

        numberOfTaps++;
        tvNumberTaps.setText(String.valueOf(numberOfTaps) + " " + getResources().getString(R.string.fttoneTvTimeNumberOfTaps));

        timeInMilli= SystemClock.uptimeMillis() - Double.valueOf(startTime);

        vect.addElement(timeInMilli);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            speakFlush(getResources().getString(R.string.fttoneTvInstructions));

            setContentView(R.layout.finger_tapping_test_one);

            mProgress = (ProgressBar) findViewById(R.id.progressBar1);
            mProgress.setMax(DURATION_TEST_MILLISECONDS);
            mProgress.setProgress(DURATION_TEST_MILLISECONDS);

            Button buttonTap = (Button) findViewById(R.id.buttonFTTOneTap);

            tvNumberTaps = (TextView) findViewById(R.id.fttoneNumberOfTaps);
            tvTime = (TextView) findViewById(R.id.fttoneTvTime);

            buttonTap.setOnClickListener(clickButton);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }

    }

    private void restart() {

        try {

            numberOfTaps = 0;
            vect.removeAllElements();
            timeInMilli = Double.valueOf(0);
            mProgress.setProgress(DURATION_TEST_MILLISECONDS);

            tvNumberTaps.setText(String.valueOf(0) + " " + getResources().getString(R.string.fttoneTvTimeNumberOfTaps));
            tvTime.setText(getResources().getString(R.string.fttoneTvTime) + " : " + new DecimalFormat(DECIMAL_FORMAT_TIME).format(DURATION_TEST_MILLISECONDS / 1000) + " " + getResources().getString(R.string.fttoneTvSeconds));

            if (sdTimer != null)
                sdTimer.cancel();

            test = true;

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception restarting: " + e.toString());
        }
    }

    public void onResume(){
        super.onResume();
        restart();
    }

    public void onPause(){
        super.onPause();

        restart();
    }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.FT + "_" + LOGGER_TAG;
    }
}
