package com.pdmanager.views.patient.cognition.cognitive;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.views.patient.cognition.MainMenu;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 *
 * Cognitive Test Paired Associates Learning
 *
 * @authors Thibaud Pacquetet, Samanta Villanueva (svillanueva@lst.tfo.upm.es ), Jorge Cancela (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class PairedAssociatesLearningTest extends SoundFeedbackActivity {

    private final String LOGGER_TAG = "LOGGER_TAG: PAL test";

    private final int TIME_MILLISECONDS_SHOW_STIMULI = 2000;
    private final int TIME_MILLISECONDS_SHOW_STIMULI_TICK = 1000;
    private final int TIME_MILLISECONDS_SHOW_STIMULI_SLEEP = 500;
    private final int TIME_MILLISECONDS_MESSAGE = 2000;
    private final int TIME_MILLISECONDS_TASK = 600000;
    private final int N_LEVELS = 8;
    private final int maxErrors = 3;
    private int accumulatedErrors = 0;

    // These are the 6 Image Views. TODO this can be optimized in the future
    private int[] imgArray = {R.id.case1, R.id.case2, R.id.case3, R.id.case4, R.id.case5, R.id.case6};
    private int[] imgBisArray = {R.id.case1bis, R.id.case2bis, R.id.case3bis, R.id.case4bis,R.id.case5bis, R.id.case6bis};

    // These are the 8 Image Views (for levels 7 and 8)
    private int[] imgArrayLastLevels = {R.id.box1, R.id.box2, R.id.box3, R.id.box4, R.id.box5, R.id.box6, R.id.box7, R.id.box8};
    private int[] imgBisArrayLastLevels = {R.id.box1bis, R.id.box2bis, R.id.box3bis, R.id.box4bis, R.id.box5bis, R.id.box6bis,R.id.box7bis, R.id.box8bis};

    // These are the different stimuli to show
    private int[] imgResArray = {R.drawable.pal1,R.drawable.pal2,R.drawable.pal3,R.drawable.pal4,R.drawable.pal5,
            R.drawable.pal6,R.drawable.pal7,R.drawable.pal8,R.drawable.pal9,R.drawable.pal10,
            R.drawable.pal11,R.drawable.pal12,R.drawable.pal13,R.drawable.pal14,R.drawable.pal15,
            R.drawable.pal16,R.drawable.pal17,R.drawable.pal18,R.drawable.pal19,R.drawable.pal20,
            R.drawable.pal21,R.drawable.pal22,R.drawable.pal23,R.drawable.pal24,R.drawable.pal25,
            R.drawable.pal26,R.drawable.pal27,R.drawable.pal28,R.drawable.pal29,R.drawable.pal30,
            R.drawable.pal31,R.drawable.pal32,R.drawable.pal33,R.drawable.pal34,R.drawable.pal35,
            R.drawable.pal36,R.drawable.pal37,R.drawable.pal38,R.drawable.pal39,R.drawable.pal40,
            R.drawable.pal41,R.drawable.pal42,R.drawable.pal43,R.drawable.pal44,R.drawable.pal45,
            R.drawable.pal46,R.drawable.pal47,R.drawable.pal48,R.drawable.pal49,R.drawable.pal50,
            R.drawable.pal51,R.drawable.pal52,R.drawable.pal53,R.drawable.pal54,R.drawable.pal55,
            R.drawable.pal56,R.drawable.pal57,R.drawable.pal58,R.drawable.pal59,R.drawable.pal60,
            R.drawable.pal61,R.drawable.pal62,R.drawable.pal63,R.drawable.pal64,R.drawable.pal65,
            R.drawable.pal66,R.drawable.pal67,R.drawable.pal68,R.drawable.pal69,R.drawable.pal70,
            R.drawable.pal71,R.drawable.pal72,R.drawable.pal73,R.drawable.pal74,R.drawable.pal75,
            R.drawable.pal76,R.drawable.pal77,R.drawable.pal78,R.drawable.pal79,R.drawable.pal80,
            R.drawable.pal81,R.drawable.pal82,R.drawable.pal83,R.drawable.pal84,R.drawable.pal85,
            R.drawable.pal86,R.drawable.pal87,R.drawable.pal88,R.drawable.pal89,R.drawable.pal90,
            R.drawable.pal91,R.drawable.pal92,R.drawable.pal93,R.drawable.pal94,R.drawable.pal95,
            R.drawable.pal96,R.drawable.pal97,R.drawable.pal98,R.drawable.pal99,R.drawable.pal100,
            R.drawable.pal101,R.drawable.pal102,R.drawable.pal103,R.drawable.pal104,R.drawable.pal105,
            R.drawable.pal106,R.drawable.pal107,R.drawable.pal108,R.drawable.pal109,R.drawable.pal110,
            R.drawable.pal111,R.drawable.pal112,R.drawable.pal113,R.drawable.pal114,R.drawable.pal115,
            R.drawable.pal116,R.drawable.pal117,R.drawable.pal118,R.drawable.pal119,R.drawable.pal120,
            R.drawable.pal121,R.drawable.pal122,R.drawable.pal123,R.drawable.pal124,R.drawable.pal125,
            R.drawable.pal126,R.drawable.pal127,R.drawable.pal128,R.drawable.pal129,R.drawable.pal130,
            R.drawable.pal131,R.drawable.pal132,R.drawable.pal133,R.drawable.pal134,R.drawable.pal135};

    // Array with the order pattern of buttons
    private int[] imgPlaceArray = new int[N_LEVELS-2];
    private int[] imgPlaceArrayLastLevels = new int[N_LEVELS];

    // Array with the stimuli to show in level
    private int[] imgTabArray = new int[N_LEVELS-2];
    private int[] imgTabArrayLastLevels = new int[N_LEVELS];

    private int nTotalErrors;
    private int nCurrentErrors;
    private int nAnswers;
    private int level=0;
    private int arrayIndex =0;
    private int nPattern;

    // To store results, time between taps and order of presentation of stimuli
    private ArrayList<Double> timeBetweenTaps;
    private ArrayList<Integer> orderStimuli;

    private double tStartLevel;
    private double tsStartPattern;

    private boolean isPaused = false;
    private boolean isLastTrialWrong = false;
    private boolean finishTest = false;
    private boolean isWaitingAnswer = false;

    private CountDownTimer timerTask;
    private CountDownTimer timer;

    private String
        test = "PAL_Results.csv",
        header = "Timestamp, " +
            "Level, " +
            "Number of errors, " +
            "Mean time between taps (s), " +
            "Time between taps (s) STD, " +
            "Total time (s)" +
            "\r\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            startPAL();
        } catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void startPAL() {

        // Introduction to the task
        if (level == 0) {

            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.pal_instruction));
            speak.speakFlush(getResources().getString(R.string.pal_instruction));
            results = new ArrayList<>();
            timeBetweenTaps = new ArrayList<>();
            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                speak.silence();

                timerTask = new CountDownTimer(TIME_MILLISECONDS_TASK, TIME_MILLISECONDS_TASK) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        finishTest();
                    }
                }.start();

                level ++;
                showNewLevelMessage();
                startNewLevel();
                }
            });
        } else {
            showNewLevelMessage();
            startNewLevel();
        }
    }

    private void setRandomPatternsLevel(){

        int nBoxes;

        if (level<=(N_LEVELS-2)) {
            nBoxes = N_LEVELS-2;
        } else {
            nBoxes = N_LEVELS;
        }

        //create a list of number with the position of the cells and the stimuli
        ArrayList<Integer> numbersCells = new ArrayList<>();
        orderStimuli = new ArrayList<>();

        for(int u=0;u<nBoxes;u++){
            numbersCells.add(u);
        }

        ArrayList<Integer> numbersStimuli = new ArrayList<>();
        for(int u=0;u<=imgResArray.length-1;u++){
            numbersStimuli.add(u);
        }

        // shuffle the numbers in the lists
        Collections.shuffle(numbersCells);
        Collections.shuffle(numbersStimuli);

        for (Integer i: numbersCells) {
            orderStimuli.add(i);
        }
        //shuffle the number in the list
        Collections.shuffle(orderStimuli);

        for (int round=0; round<level; round++) {
            ImageButton imageBisButton;

            //choose image button by number in the array
            if (level <= (N_LEVELS-2)) {
                imgPlaceArray[round]=(numbersCells.get(round));
                imageBisButton = (ImageButton) findViewById(imgBisArray[imgPlaceArray[round]]);
            } else {
                imgPlaceArrayLastLevels[round]=(numbersCells.get(round));
                imageBisButton = (ImageButton) findViewById(imgBisArrayLastLevels[imgPlaceArrayLastLevels[round]]);
            }

            //set a random image to the image button
            if (level <= (N_LEVELS-2)) {
                imgTabArray[round] = (numbersStimuli.get(round));
                imageBisButton.setImageResource(imgResArray[imgTabArray[round]]);
            } else {
                imgTabArrayLastLevels[round] = (numbersStimuli.get(round));
                imageBisButton.setImageResource(imgResArray[imgTabArrayLastLevels[round]]);
            }
        }
    }

    private void showNewMessage (String msg) {

        if (level <=(N_LEVELS-2)) {
            setContentView(R.layout.cognitive_pal_test);
        } else {
            setContentView(R.layout.cognitive_pal_test_two);
        }

        TextView tvPALInfo = (TextView) findViewById(R.id.pal_info);
        tvPALInfo.setVisibility(View.VISIBLE);
        tvPALInfo.setText(msg);
    }

    private void showNewLevelMessage () {

        if (level <=(N_LEVELS-2)) {
            setContentView(R.layout.cognitive_pal_test);
        } else {
            setContentView(R.layout.cognitive_pal_test_two);
        }

        String message = getResources().getString(R.string.pal_levels) + " " + (level) + " (" + level + " " + getResources().getString(R.string.pal_patterns) + ")";

        TextView tvPALInfo = (TextView) findViewById(R.id.pal_info);
        tvPALInfo.setVisibility(View.VISIBLE);
        tvPALInfo.setText(message);
    }

    private void startNewLevel () {

        if (level != 0) {
            writeFile(test, header);
        }

        results.clear();
        nTotalErrors = 0;
        nCurrentErrors = 0;
        isLastTrialWrong = false;
        isWaitingAnswer = false;
        tStartLevel = System.currentTimeMillis();
        nAnswers = 0;
        arrayIndex =0;
        nPattern =1;

        showNewLevelMessage();

        timer = new CountDownTimer(TIME_MILLISECONDS_MESSAGE, TIME_MILLISECONDS_MESSAGE) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                // When the patient press button start
                TextView tvPALInfo = (TextView) findViewById(R.id.pal_info);
                tvPALInfo.setVisibility(View.GONE);

                setRandomPatternsLevel();
                showPatternsLevel();
            }

        }.start();
    }

    private void repeatLastLevel() {

        arrayIndex =0;
        nPattern = 1;
        nCurrentErrors = 0;
        isWaitingAnswer = false;
        showNewLevelMessage();

        timer = new CountDownTimer(TIME_MILLISECONDS_MESSAGE, TIME_MILLISECONDS_MESSAGE) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {

                TextView tvPALInfo = (TextView) findViewById(R.id.pal_info);
                tvPALInfo.setVisibility(View.GONE);

                for (int round=0; round<level; round++) {

                    if (level <= (N_LEVELS-2)) {
                        ImageButton imageBisButton = (ImageButton) findViewById(imgBisArray[imgPlaceArray[round]]);
                        imageBisButton.setImageResource(imgResArray[imgTabArray[round]]);
                    } else {
                        ImageButton imageBisButton = (ImageButton) findViewById(imgBisArrayLastLevels[imgPlaceArrayLastLevels[round]]);
                        imageBisButton.setImageResource(imgResArray[imgTabArrayLastLevels[round]]);
                    }
                }
                showPatternsLevel();
            }
        }.start();
    }

    private void showPatternsLevel() {

        final ImageButton imageButton;

        if (level <= (N_LEVELS-2)) {
            imageButton = (ImageButton) findViewById(imgArray[orderStimuli.get(arrayIndex)]);
        } else {
            imageButton = (ImageButton) findViewById(imgArrayLastLevels[orderStimuli.get(arrayIndex)]);
        }

        timer = new CountDownTimer(TIME_MILLISECONDS_SHOW_STIMULI, TIME_MILLISECONDS_SHOW_STIMULI_TICK) {

            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    wait(TIME_MILLISECONDS_SHOW_STIMULI_SLEEP);
                } catch (InterruptedException e) {
                    Log.v(LOGGER_TAG, "InterruptedException: " + e.toString());
                } catch (Exception e) {
                    Log.v(LOGGER_TAG, "Exception: " + e.toString());
                }
                imageButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish() {

                imageButton.setVisibility(View.VISIBLE);
                int length;
                if (level <=(N_LEVELS-2)) {
                    length = imgArray.length;
                } else {
                    length = imgArrayLastLevels.length;
                }

                if (arrayIndex == (length - 1)) {
                    isWaitingAnswer = true;
                    setNextPattern();

                } else {
                    arrayIndex = arrayIndex + 1;
                    showPatternsLevel();
                }
             }
        }.start();

    }

    private void setNextPattern() {

        try {

            showNewMessage(getResources().getString(R.string.pal_instructions_choose));

            final ImageView patternViewer = (ImageView) findViewById(R.id.viewer);
            final ImageButton imageTestButton;
            ArrayList<ImageButton> imgButton = new ArrayList<>();

            if (level <=(N_LEVELS-2)) {
                patternViewer.setImageResource(imgResArray[imgTabArray[nPattern - 1]]);
                imageTestButton = (ImageButton) findViewById(imgArray[imgPlaceArray[nPattern - 1]]);
                for (int i: imgArray) {
                    ImageButton im = (ImageButton) findViewById(i);
                    imgButton.add(im);
                }
            } else {
                patternViewer.setImageResource(imgResArray[imgTabArrayLastLevels[nPattern - 1]]);
                imageTestButton = (ImageButton) findViewById(imgArrayLastLevels[imgPlaceArrayLastLevels[nPattern - 1]]);
                for (int i: imgArrayLastLevels) {
                    ImageButton im = (ImageButton) findViewById(i);
                    imgButton.add(im);
                }
            }


            tsStartPattern = System.currentTimeMillis();

            OnClickListener listener = new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    if (isWaitingAnswer) {
                        final ImageButton pressedBtn = (ImageButton) v;
                        if (isWaitingAnswer) {
                            pressedBtn.setImageResource(R.drawable.green_square);
                            isWaitingAnswer = false;
                        } else {
                            pressedBtn.setImageResource(R.drawable.white_square);
                        }

                        final Long tsEndPattern = System.currentTimeMillis();

                        timeBetweenTaps.add((tsEndPattern - tsStartPattern) / 1000D);
                        nAnswers = nAnswers + 1;

                        if (nPattern != level) {

                            if (!v.equals(imageTestButton)) {
                                nTotalErrors = nTotalErrors + 1;
                                nCurrentErrors = nCurrentErrors + 1;
                                patternViewer.setImageResource(R.drawable.red_cross);
                                tones.nackBeep();
                            } else {
                                patternViewer.setImageResource(R.drawable.green_tick);
                                tones.ackBeep();
                            }

                            TextView tvPALInfo = (TextView) findViewById(R.id.pal_info);
                            tvPALInfo.setVisibility(View.INVISIBLE);
                            patternViewer.setVisibility(View.VISIBLE);
                            nPattern = nPattern + 1;

                            timer = new CountDownTimer(TIME_MILLISECONDS_SHOW_STIMULI, TIME_MILLISECONDS_SHOW_STIMULI) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    pressedBtn.setImageResource(R.drawable.white_square);
                                    isWaitingAnswer = true;
                                    setNextPattern();
                                }
                            }.start();

                        } else {

                            final Resources res = getResources();

                            if (!v.equals(imageTestButton)) {
                                patternViewer.setImageResource(R.drawable.red_cross);
                                tones.nackBeep();
                            } else {
                                patternViewer.setImageResource(R.drawable.green_tick);
                                tones.ackBeep();
                            }
                            final TextView tvPALInfo = (TextView) findViewById(R.id.pal_info);
                            tvPALInfo.setVisibility(View.INVISIBLE);
                            patternViewer.setVisibility(View.VISIBLE);

                            timer = new CountDownTimer(TIME_MILLISECONDS_SHOW_STIMULI, TIME_MILLISECONDS_SHOW_STIMULI) {

                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    if ((nCurrentErrors == 0) && (v.equals(imageTestButton))) {
                                        showNewMessage(res.getString(R.string.pal_all_correct));
                                        double tsLevel = ((tsEndPattern - tStartLevel) / 1000D);
                                        double[] timeTaps = new double[timeBetweenTaps.size()];
                                        for (int i=0; i<timeBetweenTaps.size(); i++) {
                                            timeTaps[i] = timeBetweenTaps.get(i);
                                        }

                                        Statistics st = new Statistics(timeTaps);
                                        st.setSize(nAnswers);
                                        addNewLevelResult(tsLevel, st.getMean(), st.getStdDev());

                                        timer = new CountDownTimer(TIME_MILLISECONDS_MESSAGE, TIME_MILLISECONDS_MESSAGE) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            @Override
                                            public void onFinish() {
                                                // When the patient press button start
                                                tvPALInfo.setVisibility(View.GONE);
                                                level++;

                                                if (level > N_LEVELS) {
                                                    finishTest();
                                                } else {
                                                    startNewLevel();
                                                }
                                            }

                                        }.start();

                                    } else
                                    {
                                        accumulatedErrors++;
                                        level++;
                                        if (level>N_LEVELS || accumulatedErrors>=maxErrors) finishTest();
                                        else startNewLevel();

                                        /*
                                        showNewMessage(res.getString(R.string.pal_all_try_again));

                                        if (!v.equals(imageTestButton)) {
                                            nTotalErrors = nTotalErrors + 1;
                                            nCurrentErrors = nCurrentErrors + 1;

                                            if (nCurrentErrors==level)
                                            {
                                                if (isLastTrialWrong) {
                                                    finishTest = true;
                                                    finishTest();
                                                } else {
                                                    isLastTrialWrong = true;
                                                }
                                            } else {
                                                isLastTrialWrong = false;
                                            }
                                        }

                                        timer = new CountDownTimer(TIME_MILLISECONDS_MESSAGE, TIME_MILLISECONDS_MESSAGE) {

                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            @Override
                                            public void onFinish() {
                                                // When the patient press button start
                                                tvPALInfo.setVisibility(View.GONE);
                                                if (!finishTest) {
                                                    repeatLastLevel();
                                                }
                                            }
                                        }.start(); */
                                    }
                                }
                            }.start();
                        }

                    }
                }
            };


            imageTestButton.setOnClickListener(listener);

            for (ImageButton im : imgButton) {
                im.setOnClickListener(listener);
            }


        } catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e. " + e.toString());
        }

    }

    private void addNewLevelResult(double tsLevel, double meanTapsTime, double stdTapsTime) {

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime()) ;

        String ts = String.format(Locale.ENGLISH, "%.2f", tsLevel);
        String meanTime = String.format(Locale.ENGLISH, "%.2f", meanTapsTime);
        String stdTime = String.format(Locale.ENGLISH, "%.2f", stdTapsTime);

        resultInfo.append(date + ", ");
        resultInfo.append(Double.toString(level) + ", ");
        resultInfo.append(String.valueOf(nTotalErrors) + ", ");
        resultInfo.append(meanTime + ", ");
        resultInfo.append(stdTime + " , ");
        resultInfo.append(ts + "\r\n");

        results.add(String.valueOf(resultInfo));
    }

    private void finishTest(){

        try {

            writeFile(test, header);

            speak.silence();

            if (timerTask != null) {
                timerTask.cancel();
            }

            if (timer != null) {
                timer.cancel();
            }

            tones.stopTone();

            setContentView(R.layout.activity_end);

            Button buttonRepeat=(Button) findViewById(R.id.buttonFTTEndRepeat);
            buttonRepeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                //Intent menu1Intent = new Intent(getApplicationContext(), MainMenu.class);
                //startActivity(menu1Intent);
                    finish();
                }
            });

            Button buttonExit=(Button) findViewById(R.id.buttonFTTEndExit);
            buttonExit.setVisibility(View.GONE);
            buttonExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();

                }
            });

        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception finishing activity: " + e.toString());
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        if (isPaused) {
            speak.silence();
            if (timerTask != null) {
                timerTask.cancel();
            }
            if (timer != null) {
                timer.cancel();
            }

            timerTask = new CountDownTimer(TIME_MILLISECONDS_TASK, TIME_MILLISECONDS_TASK) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    finishTest();
                }
            }.start();

            level = 1;
            isPaused = false;
            showNewLevelMessage();
            startNewLevel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        speak.silence();
        isPaused = true;

        if (timerTask != null) {
            timerTask.cancel();
        }

        if (timer != null) {
            timer.cancel();
        }

    }
}


