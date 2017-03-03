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
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.DecimalFormat;
import java.util.Vector;

/**
 *
 * Alternate Finger Tapping Test Activity
 *
 * @authors Quentin DELEPIERRE, Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */
public class FingerTappingTestTwo extends SoundFeedbackActivity {

    private String LOGGER_TAG = "LOGGER_TAG FingerTappingTestTwo:";

    public static final String DECIMAL_FORMAT_TIME = "#,#00.0";

    public static final String INTENT_MEAN_TWO = "meanTimeTwo";
    public static final String INTENT_STD_TWO = "stdTimeTwo";
    public static final String INTENT_MEDIAN_TWO = "medianTimeTwo";
    public static final String INTENT_MAX_TWO = "maxTimeTwo";
    public static final String INTENT_MIN_TWO = "minTimeTwo";
    public static final String INTENT_TEST_TIME_TWO = "testTimeTwo";
    public static final String INTENT_TAPS_NUMBER_TWO = "tapsNumberTwo";
    public static final String INTENT_TAPS_ERROR_TWO = "tapsErrorTwo";

    public final int DURATION_TEST_MILLISECONDS= 10000;
    public final int REFRESH_PERIOD_MILLISECONDS= 100;

    private int durationTestOne = 0;
    private int tapsTestOne = 0;
    private int condition=0;
    private int numberOfTaps = 0;
    private int tapErrors = 0;

    private Double meanTimeTestOne = new Double(0);
    private Double stdTimeTestOne = new Double(0);
    private Double medianTimeTestOne = new Double(0);
    private Double maxTimeTestOne = new Double(0);
    private Double minTimeTestOne = new Double(0);
    private Double startTime = Double.valueOf(0);
    private Double timeInMilli = Double.valueOf(0);

    private TextView tvNumberTaps = null;
    private TextView tvTime = null;

    private ProgressBar mProgress;

    private Vector vect = new Vector();

    private boolean test=true;

    private CountDownTimer sdTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        speak.speakFlush(getResources().getString(R.string.fttTwoTvInstructions));

        setContentView(R.layout.finger_tapping_test_two);

        Button click1 = (Button)findViewById(R.id.click1);
        Button click2 = (Button)findViewById(R.id.click2);

        mProgress = (ProgressBar) findViewById(R.id.progressBar1);
        mProgress.setMax(DURATION_TEST_MILLISECONDS);
        mProgress.setProgress(DURATION_TEST_MILLISECONDS);

        tvNumberTaps = (TextView)findViewById(R.id.fttTwoTvNumberOfTaps);
        tvTime =(TextView)findViewById(R.id.fttTwoTvTime);

        click1.setOnClickListener(clickButton1);
        click2.setOnClickListener(clickButton2);

        try {

            tapsTestOne = getIntent().getIntExtra(FingerTappingTestOne.INTENT_TAPS_NUMBER_ONE, 0);
            meanTimeTestOne = getIntent().getDoubleExtra(FingerTappingTestOne.INTENT_MEAN_ONE, 0);
            stdTimeTestOne = getIntent().getDoubleExtra(FingerTappingTestOne.INTENT_STD_ONE, 0);
            medianTimeTestOne = getIntent().getDoubleExtra(FingerTappingTestOne.INTENT_MEDIAN_ONE, 0);
            maxTimeTestOne = getIntent().getDoubleExtra(FingerTappingTestOne.INTENT_MAX_ONE, 0);
            minTimeTestOne = getIntent().getDoubleExtra(FingerTappingTestOne.INTENT_MIN_ONE, 0);

        }catch (Exception e){
            Log.v(LOGGER_TAG, e.toString());
        }

    }

    private View.OnClickListener clickButton1=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        speak.silence();

        if (condition==1){
            tapErrors++;
        }
        if (condition==0) {
            if (test) {

                startTime = Double.longBitsToDouble(SystemClock.uptimeMillis());
                restart();
                test= false;

                sdTimer = new CountDownTimer(DURATION_TEST_MILLISECONDS, REFRESH_PERIOD_MILLISECONDS) {

                    public void onTick(long millisUntilFinished) {
                        mProgress.setProgress((int) millisUntilFinished);

                        tvTime.setText(getResources().getString(R.string.fttTwoTvTime) + " : "
                                + new DecimalFormat(DECIMAL_FORMAT_TIME).format(Double.valueOf(millisUntilFinished / Double.valueOf(1000)))
                                + " "
                                + getResources().getString(R.string.fttoneTvSeconds));
                    }

                    public void onFinish() {

                        setContentView(R.layout.splash);
                        Thread welcomeThread = new Thread() {

                            @Override
                            public void run() {
                            try {
                                super.run();
                                sleep(1000) ; //Delay of 3 seconds
                            } catch (Exception e) {

                            } finally {

                                Intent intent = new Intent(FingerTappingTestTwo.this, FingerTappingEnd.class);
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

                                intent.putExtra(INTENT_TAPS_NUMBER_TWO, numberOfTaps);
                                intent.putExtra(INTENT_TAPS_ERROR_TWO, tapErrors);
                                intent.putExtra(INTENT_MEAN_TWO, meanTime);
                                intent.putExtra(INTENT_STD_TWO, stdTime);
                                intent.putExtra(INTENT_MEDIAN_TWO, medianTime);
                                intent.putExtra(INTENT_MAX_TWO, maxTime);
                                intent.putExtra(INTENT_MIN_TWO, minTime);

                                intent.putExtra(FingerTappingTestOne.INTENT_TAPS_NUMBER_ONE, tapsTestOne);
                                intent.putExtra(FingerTappingTestOne.INTENT_MEAN_ONE, meanTimeTestOne);
                                intent.putExtra(FingerTappingTestOne.INTENT_STD_ONE, stdTimeTestOne);
                                intent.putExtra(FingerTappingTestOne.INTENT_MEDIAN_ONE, medianTimeTestOne);
                                intent.putExtra(FingerTappingTestOne.INTENT_MAX_ONE, maxTimeTestOne);
                                intent.putExtra(FingerTappingTestOne.INTENT_MIN_ONE, minTimeTestOne);

                                startActivity(intent);
                                finish();
                            }
                            }
                        };
                        welcomeThread.start();
                    }
                }.start();
            }

            condition=1;
            update();
        }
        }
    };

    private View.OnClickListener clickButton2=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        if (condition==0){
            tapErrors++;
        }
        if (condition == 1) {
            condition=0;
            update();
        }
        }
    };

    private void update(){

        numberOfTaps++;
        tvNumberTaps.setText(String.valueOf(numberOfTaps) + " " + getResources().getString(R.string.fttTwoTvTimeNumberOfTaps));
        timeInMilli=SystemClock.uptimeMillis() - startTime;
        vect.addElement(timeInMilli);
    }

    private void restart() {
        numberOfTaps=0;
        tapErrors = 0;

        vect.removeAllElements();

        timeInMilli = Double.valueOf(0);

        mProgress.setProgress(DURATION_TEST_MILLISECONDS);

        tvNumberTaps.setText(String.valueOf(0) + " " + getResources().getString(R.string.fttoneTvTimeNumberOfTaps));
        tvTime.setText(getResources().getString(R.string.fttTwoTvTime) + " : " + new DecimalFormat(DECIMAL_FORMAT_TIME).format(DURATION_TEST_MILLISECONDS / 1000) + " " + getResources().getString(R.string.fttTwoTvSeconds));

        if (sdTimer != null)
            sdTimer.cancel();

        test=true;
    }

    public void onResume(){
        super.onResume();
        restart();
    }

    public void onPause(){
        super.onResume();

        speak.silence();

        restart();
    }
}
