package com.pdmanager.views.patient.cognition.cognitive;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.MainMenu;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;


/**
 * Cognitive Test Spatial Span
 *
 * @authors: Thibaud Pacquetet, Samanta VILLANUEVA (svillanueva@lst.tfo.upm.es), Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class SpatialSpanTest extends SoundFeedbackActivity {

    private final String LOGGER_TAG = "SSP test";

        private String
        test = "SpatialSpan.csv",
        header = "Timestamp, " +
                "max. level reached, " +
                "mean number errors per trial, " +
                "max. number errors per trial, " +
                "min. number errors per trial, " +
                "total errors, " +
                "mean time trial (s), " +
                "STD time trial (s), " +
                "max time trial (s), " +
                "min time trial (s), " +
                "Total time (s)" +
                "\r\n";

    private final int TIME_MILLISECONDS_TRANSITIONS = 1000;
    private final int TIME_MILLISECONDS_TICK = 600;
    private final int TIME_MILLISECONDS_TASK = 480000;
    private final int NUMBER_OF_BOXES = 8;
    private final int NUMBER_OF_TRIALS = 3;
    private final int MIN_NUMBER_OF_TOKENS = 2;
    private final int NUMBER_OF_LEVELS = 7;

    private int[] boxesID ={R.id.imageButton1,R.id.imageButton2,R.id.imageButton3,R.id.imageButton4,R.id.imageButton5,
            R.id.imageButton6,R.id.imageButton7,R.id.imageButton8,R.id.imageButton9,R.id.imageButton10,
            R.id.imageButton11,R.id.imageButton12,R.id.imageButton13,R.id.imageButton14,R.id.imageButton15,
            R.id.imageButton16,R.id.imageButton17,R.id.imageButton19,R.id.imageButton21,R.id.imageButton23};

    private int[] levelShowedBoxesID =new int[NUMBER_OF_BOXES];
    private int[] trialShowedTokensID;
    private int[] orderTrialTokensID;
    private int[] selectedTrialTokensID;

    private int indexShowedToken;
    private int nMoves;
    private int nTrial;
    private int nTap;
    private int level;
    private int nCorrectTrials;

    private ArrayList<Integer> errors;
    private ArrayList<Double> timePerTrial;

    private double startTimeTrial;

    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isTrialStarted = false;

    private CountDownTimer timerTask;
    private CountDownTimer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startSSP();
    }

    private void startSSP() {

        if (!isStarted) {
            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.ssp_instruction));
            speak.speakFlush(getResources().getString(R.string.ssp_instruction));

            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new View.OnClickListener() {
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

                    isStarted = true;
                    results = new ArrayList<>();
                    level = 0;
                    nMoves = MIN_NUMBER_OF_TOKENS;
                    errors = new ArrayList<Integer>();
                    timePerTrial = new ArrayList<Double>();
                    setNewLevel();
                }
            });
        } else {
            setNewLevel();
        }
    }

    private void setNewLevel(){

        if (level!=0) {
            writeFile(test, header);
        }

        level++;

        if (level>NUMBER_OF_LEVELS){
            if (timerTask != null) {
                timerTask.cancel();
            }
            getTestResult();
            finishTest();
        } else {
            if (level != 1) {
                nMoves ++;
            }

            nTrial = 1;
            nCorrectTrials = 0;
            indexShowedToken=-2;

            startNewLevel();
        }
    }

    private void startNewLevel(){

        setContentView(R.layout.cognitive_ssp_test);

        for (int i=0; i<boxesID.length; i++) {
            ImageButton image = (ImageButton) findViewById(boxesID[i]);
            image.setVisibility(View.INVISIBLE);
        }

        TextView textViewToChange = (TextView) findViewById(R.id.SpatialSpan_info);
        textViewToChange.setText(getResources().getString(R.string.ssp_levels) + " " + (level) + " ("
                + nMoves + " " + getResources().getString(R.string.moves) + ")");

        timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {

                setRandomLevelBoxes();
                setNewTrial();
            }
        }.start();
    }

    private void setRandomLevelBoxes () {

        ArrayList<Integer> randomShowedBoxes = new ArrayList<>();

        for (int i= 0; i<boxesID.length; i++) {
            if (i<NUMBER_OF_BOXES) {
                randomShowedBoxes.add(1);
            } else {
                randomShowedBoxes.add(0);
            }
        }

        Collections.shuffle(randomShowedBoxes);

        int index = 0;
        for (int i=0; i<randomShowedBoxes.size(); i++) {
            ImageButton image = (ImageButton) findViewById(boxesID[i]);
            image.setImageResource(R.drawable.white_square);

            if (randomShowedBoxes.get(i) == 1) {
                image.setVisibility(View.VISIBLE);
                levelShowedBoxesID[index] = boxesID[i];
                index++;
            } else {
                image.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void setRandomTrialTokens () {

        ArrayList<Integer> randomShowedTokens = new ArrayList<>();

        for (int i= 0; i<levelShowedBoxesID.length; i++) {
            if (i<nMoves) {
                randomShowedTokens.add(1);
            } else {
                randomShowedTokens.add(0);
            }
        }

        Collections.shuffle(randomShowedTokens);

        ArrayList<Integer> orderTokens = new ArrayList<>();

        int index = 0;
        for (int i=0; i<randomShowedTokens.size(); i++) {

            if (randomShowedTokens.get(i) == 1) {
                trialShowedTokensID[index] = levelShowedBoxesID[i];
                orderTokens.add(levelShowedBoxesID[i]);
                index++;
            }
        }

        Collections.shuffle(orderTokens);

        for (int i=0; i<orderTokens.size(); i++) {
            orderTrialTokensID[i] = orderTokens.get(i);
        }

        for (int i=0; i<levelShowedBoxesID.length; i++) {
            ImageButton image = (ImageButton) findViewById(levelShowedBoxesID[i]);
            image.setImageResource(R.drawable.white_square);
            image.setVisibility(View.VISIBLE);
        }

    }

    private void setNewTrial(){

        for (int i=0; i<levelShowedBoxesID.length; i++) {
            ImageButton image = (ImageButton) findViewById(levelShowedBoxesID[i]);
            image.setVisibility(View.INVISIBLE);
        }

        isTrialStarted = false;
        trialShowedTokensID = new int[nMoves];
        orderTrialTokensID = new int[nMoves];
        selectedTrialTokensID = new int[nMoves];

        nTap = 0;
        indexShowedToken = -2;

        TextView textViewToChange = (TextView) findViewById(R.id.SpatialSpan_info);
        textViewToChange.setText(getResources().getString(R.string.ssp_levels) + " "
                + (level) + " (" + nMoves + " " + getResources().getString(R.string.moves) +
                ")" + " - " + getResources().getString(R.string.ssp_trial) + " "
                + (nTrial) + "/" + NUMBER_OF_TRIALS);

        timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                setRandomTrialTokens();
            }
        }.start();

        timer = new CountDownTimer((nMoves + 3) * TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {

            @Override
            public void onTick(long millisUntilFinished) {

                if (indexShowedToken < nMoves) {

                    if (indexShowedToken >= 0) {

                        for (int i = 0; i < orderTrialTokensID.length; i++) {
                            ImageButton token = (ImageButton) findViewById(orderTrialTokensID[i]);

                            if (i == indexShowedToken) {
                                token.setImageResource(R.drawable.violet_square);
                            } else {
                                token.setImageResource(R.drawable.white_square);
                            }
                        }
                    }
                    indexShowedToken++;
                }
            }

            @Override
            public void onFinish() {
                for (int i = 0; i < orderTrialTokensID.length; i++) {
                    ImageButton token = (ImageButton) findViewById(orderTrialTokensID[i]);
                    token.setImageResource(R.drawable.white_square);
                }

                try {
                    tones.beep();
                    startNewTrial();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void startNewTrial(){

        isTrialStarted = true;
        startTimeTrial = System.currentTimeMillis();

        try {

            final ArrayList<ImageButton> buttons = new ArrayList<>();

            for (int i= 0; i<levelShowedBoxesID.length; i++) {
                ImageButton btn = (ImageButton) findViewById(levelShowedBoxesID[i]);
                buttons.add(btn);
            }

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (nTap>=nMoves) isTrialStarted = false;
                    else
                    {
                        final ImageButton pressedBtn = (ImageButton) v;
                        final ImageButton correctBtn = (ImageButton) findViewById(orderTrialTokensID[nTap]);

                        timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TICK) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (isTrialStarted) {

                                    for (int i = 0; i < nTap; i++) {
                                        ImageButton btn = (ImageButton) findViewById(selectedTrialTokensID[i]);
                                        btn.setImageResource(R.drawable.white_square);
                                    }

                                    pressedBtn.setImageResource(R.drawable.violet_square);

                                    if (pressedBtn.getId() == (correctBtn.getId())) tones.ackBeep();
                                    else tones.nackBeep();

                                } else {
                                    pressedBtn.setImageResource(R.drawable.white_square);
                                }
                            }

                            @Override
                            public void onFinish() {
                                pressedBtn.setImageResource(R.drawable.white_square);
                            }
                        }.start();

                        if (isTrialStarted && nTap<nMoves) {
                            selectedTrialTokensID[nTap] = pressedBtn.getId();
                            nTap++;


                            if (nTap == nMoves) {
                                double timeCurrentTrial = System.currentTimeMillis();

                                timePerTrial.add(((timeCurrentTrial - startTimeTrial)) / 1000D);
                                timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        checkTrial();
                                    }
                                }.start();
                            }
                        }
                    }
                }
            };

            for (ImageButton btn: buttons) {
                btn.setOnClickListener(listener);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTrial() {

        isTrialStarted = false;
        boolean isCorrect = true;
        int trialErrors = 0;

        for (int i = 0; i < nMoves; i++) {

            if (orderTrialTokensID[i] != selectedTrialTokensID[i]) {
                isCorrect = false;
                trialErrors ++;


            }
        }

        if (isCorrect) {
            nCorrectTrials++;
        }


        errors.add(trialErrors);


        if ( (nTrial == NUMBER_OF_TRIALS && nCorrectTrials >=2)) {

            timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    setNewLevel();
                }
            }.start();

        } else if (nTrial == NUMBER_OF_TRIALS && nCorrectTrials <2) {
            if (timerTask != null) {
                timerTask.cancel();
            }
            if (timer != null) {
                timer.cancel();
            }

            getTestResult();
            finishTest();
        } else {

            nTrial++;
            timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    setNewTrial();
                }
            }.start();
        }

    }

    private void getTestResult () {

        double[] errorsPerTrial = new double[errors.size()];
        double[] timesPerTrial = new double[timePerTrial.size()];

        for (int i= 0; i<errors.size(); i++) {
            errorsPerTrial[i] = errors.get(i);
        }

        for (int i= 0; i<timePerTrial.size(); i++) {
            timesPerTrial[i] = timePerTrial.get(i);
        }

        Statistics stErrorsPerTrial = new Statistics(errorsPerTrial);
        Statistics stTimesPerTrial = new Statistics(timesPerTrial);

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime()) ;

        String meanErrorsPerTrial = String.format(Locale.ENGLISH, "%.2f", stErrorsPerTrial.getMean());
        String maxErrorPerTrial = String.format(Locale.ENGLISH, "%.2f", stErrorsPerTrial.getMax());
        String minErrorPerTrial = String.format(Locale.ENGLISH, "%.2f", stErrorsPerTrial.getMin());
        String totalErrors = String.format(Locale.ENGLISH, "%.2f", stErrorsPerTrial.getSum());
        String meanTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getMean());
        String stdTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getStdDev());
        String maxTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getMax());
        String minTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getMin());
        String totalTime = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getSum());

        resultInfo.append(date + ", ");
        resultInfo.append(String.valueOf(level) + ", ");
        resultInfo.append(meanErrorsPerTrial + ", ");
        resultInfo.append(maxErrorPerTrial + ", ");
        resultInfo.append(minErrorPerTrial + ", ");
        resultInfo.append(totalErrors + ", ");
        resultInfo.append(meanTimePerTrial + ", ");
        resultInfo.append(stdTimePerTrial + ", ");
        resultInfo.append(maxTimePerTrial + ", ");
        resultInfo.append(minTimePerTrial + ", ");
        resultInfo.append(totalTime + "\r\n");

        results.add(String.valueOf(resultInfo));
        sendObservations(level, Double.parseDouble(meanErrorsPerTrial), Double.parseDouble(maxErrorPerTrial),
                Double.parseDouble(minErrorPerTrial), Double.parseDouble(totalErrors), Double.parseDouble(meanTimePerTrial),
                Double.parseDouble(maxTimePerTrial), Double.parseDouble(minTimePerTrial));
    }

    public void sendObservations (int maxLevel, double meanErrors, double maxErrors, double minErrors, double totalErrors,
                                  double meanTime, double maxTime, double minTime) {
        //Observations
        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();
            Observation obsSSPLevel = new Observation (maxLevel, patientCode, "PDTSSP_MAXLEVEL", time);
            obsSSPLevel.PatientId = patientCode;
            Observation obsSSPMeanErrors = new Observation(meanErrors, patientCode, "PDTSSP_MEAN_ERRORS", time);
            obsSSPMeanErrors.PatientId = patientCode;
            Observation obsSSPMaxErrors = new Observation(maxErrors, patientCode, "PDTSSP_MAX_ERRORS", time);
            obsSSPMaxErrors.PatientId = patientCode;
            Observation obsSSPMinErrors = new Observation(minErrors, patientCode, "PDTSSP_MIN_ERRORS", time);
            obsSSPMinErrors.PatientId = patientCode;
            Observation obsSSPTotalErrors = new Observation (totalErrors, patientCode, "PDTSSP_TOTAL_ERRORS", time);
            obsSSPTotalErrors.PatientId = patientCode;
            Observation obsSSPMeanTime = new Observation (meanTime, patientCode, "PDTSSP_MEAN_TIME", time);
            obsSSPMeanTime.PatientId = patientCode;
            Observation obsSSPMaxTime = new Observation(maxTime, patientCode, "PDTSSP_MAX_TIME", time);
            obsSSPMaxTime.PatientId = patientCode;
            Observation obsSSPMinTime = new Observation(minTime, patientCode, "PDTSSP_MIN_TIME", time);
            obsSSPMinTime.PatientId = patientCode;

            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsSSPLevel);
            observations.add(obsSSPMeanErrors);
            observations.add(obsSSPMaxErrors);
            observations.add(obsSSPMinErrors);
            observations.add(obsSSPTotalErrors);
            observations.add(obsSSPMeanTime);
            observations.add(obsSSPMaxTime);
            observations.add(obsSSPMinTime);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }

    @Override
    protected void finishTest(){

        try {

            writeFile(test, header);

            speak.silence();
            if (timerTask != null) {
                timerTask.cancel();
            }
            if (timer != null) {
                timer.cancel();
            }


           /* setContentView(R.layout.activity_end);

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
            */


        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception finishing activity: " + e.toString());
        }
        super.finishTest();
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

            isStarted = true;
            isPaused = false;
            results = new ArrayList<>();
            level = 0;
            nMoves = MIN_NUMBER_OF_TOKENS;

            setNewLevel();
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
