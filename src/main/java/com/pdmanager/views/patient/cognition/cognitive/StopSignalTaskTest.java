package com.pdmanager.views.patient.cognition.cognitive;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.UserTaskCodes;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 * Cognitive Test Stop Signal Task
 *
 * @authors: Thibaud Pacquetet, Quentin DELEPIERRE, Samanta VILLANUEVA (svillanueva@lst.tfo.upm.es), Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */


public class StopSignalTaskTest extends SoundFeedbackActivity {

    private static final long THREE_SECONDS = 3000L;
    private final String LOGGER_TAG = "SST_test";
    private final int TIME_MILLISECONDS_LEVEL_TASK = 240000;
    private final int TIME_MILLISECONDS_TRANSITIONS = 250;
    private final int NUMBER_OF_LEVELS = 2;
    private final int NUMBER_OF_TRIALS = 15;
    private final int NUMBER_OF_TYPES_FIRST_PART = 2;
    private final int NUMBER_OF_TYPES_SECOND_PART = 4;
    private String
            test = "StopSignalTask.csv",
            header = "Timestamp, " +
                    "direction errors GO, " +
                    "direction errors No-GO, " +
                    "tap errors GO, " +
                    "tap errors No-GO, " +
                    "timeout errors GO, " +
                    "timeout errors No-GO, " +
                    "number of stimuli with audio, " +
                    "total errors GO (%), " +
                    "total errors No-GO (%)" +
                    "total errors (%), " +
                    "mean reaction time GO (ms), " +
                    "STD reaction time GO (ms), " +
                    "max. reaction time GO (ms), " +
                    "min. reaction time GO (ms), " +
                    "mean reaction time No-GO (ms), " +
                    "STD reaction time No-GO (ms), " +
                    "max. reaction time No-GO (ms), " +
                    "min. reaction time No-GO (ms), " +
                    "mean reaction time (ms), " +
                    "STD reaction time (ms), " +
                    "max. reaction time (ms), " +
                    "min. reaction time (ms)" +
                    "\r\n";
    private int level = 0;
    private int nTrial;

    private long timeLastTrial;
    private long tStartLevel;

    private int[] correctDirections = new int[NUMBER_OF_TRIALS];

    private int nStimuliWithAudio;
    private int nDirectionErrorsGO;
    private int nDirectionErrorsNoGO;
    private int nTapErrorsGO;
    private int nTapErrorsNoGO;
    private int nTimeoutErrorsGO;
    private int nTimeoutErrorsNoGO;
    private ArrayList<Double> reactionTimesGO;
    private ArrayList<Double> reactionTimesNoGO;

    private CountDownTimer timerTask;
    private CountDownTimer timer;
    private Handler handler;
    private Runnable thread;

    private boolean isStarted = false;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            startSST();

        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void startSST() {
        try{
            if (!isStarted) {

                setContentView(R.layout.activity_start);
                TextView textViewToChange = (TextView) findViewById(R.id.level);
                textViewToChange.setText(getResources().getString(R.string.sst_instruction_first));
                speakFlush(getResources().getString(R.string.sst_instruction_first));

                Button buttonStart = (Button) findViewById(R.id.play);
                buttonStart.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        speakerSilence();

                        timerTask = new CountDownTimer(TIME_MILLISECONDS_LEVEL_TASK, TIME_MILLISECONDS_LEVEL_TASK) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                level = 1;
                                setNewLevel();
                            }
                        }.start();

                        results = new ArrayList<>();
                        level = 0;
                        isStarted = true;
                        reactionTimesGO = new ArrayList<Double>();
                        reactionTimesNoGO = new ArrayList<Double>();
                        nDirectionErrorsGO = 0;
                        nDirectionErrorsNoGO = 0;
                        nTapErrorsGO = 0;
                        nTapErrorsNoGO = 0;
                        nTimeoutErrorsGO = 0;
                        nTimeoutErrorsNoGO = 0;
                        setNewLevel();
                    }
                });
            }  else {
                setNewLevel();
            }

        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void setNewLevel() {

        try{

            if (level !=0) {
                writeFile(test, header);
            }

            for (int i=0; i<NUMBER_OF_TRIALS; i++) {
                correctDirections[i] = -1;
            }

            level++;

            if (level > NUMBER_OF_LEVELS){
                speakerSilence();
                if (timer != null) {
                    timer.cancel();
                }
                if (timerTask != null) {
                    timerTask.cancel();
                }
                tones.stopTone();
                getTestResult();
                finishTest();
            }
            else {
                if (level == 2) { //Second part

                    if (timerTask != null) {
                        timerTask.cancel();
                    }

                    startSecondPartOfTest();

                } else { // First part
                    setNewTrial();
                }
            }

        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void startSecondPartOfTest() {

        try {

            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.sst_instruction_second));
            speakFlush(getResources().getString(R.string.sst_instruction_second));

            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    speakerSilence();

                    timerTask = new CountDownTimer(TIME_MILLISECONDS_LEVEL_TASK, TIME_MILLISECONDS_LEVEL_TASK) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            finishTest();
                        }
                    }.start();

                    setNewTrial();
                }
            });

        } catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void setNewTrial() {

        try {
            setContentView(R.layout.cognitive_sst_test);
            ImageView circle = (ImageView) findViewById(R.id.imageView3);
            circle.setImageResource(R.drawable.white_circle);

            if (level == 1) { // GO
                ArrayList<Integer> directions = new ArrayList<>();

                int nTypes = NUMBER_OF_TRIALS/ NUMBER_OF_TYPES_FIRST_PART;
                if ((NUMBER_OF_TRIALS% NUMBER_OF_TYPES_FIRST_PART) != 0) {
                    nTypes++;
                }

                for (int i=0; i<nTypes; i++) {
                    for (int j=0; j< NUMBER_OF_TYPES_FIRST_PART; j++) {
                        directions.add(j);
                    }
                }

                Collections.shuffle(directions);
                for (int i = 0; i<NUMBER_OF_TRIALS; i++) {
                    correctDirections[i] = directions.get(i);
                }

            } else { // NO GO

                ArrayList<Integer> directions = new ArrayList<>();
                int nTypes = NUMBER_OF_TRIALS/ NUMBER_OF_TYPES_SECOND_PART;
                if ((NUMBER_OF_TRIALS% NUMBER_OF_TYPES_SECOND_PART) != 0) {
                    nTypes++;
                }

                for (int i=0; i<nTypes; i++) {
                    for (int j=0; j< NUMBER_OF_TYPES_SECOND_PART; j++) {
                        directions.add(j);
                    }
                }

                Collections.shuffle(directions);
                for (int i = 0; i<NUMBER_OF_TRIALS; i++) {
                    correctDirections[i] = directions.get(i);
                }
            }

            nTrial = 1;
            tStartLevel = System.currentTimeMillis();
            timeLastTrial = tStartLevel;


            startNewTrial();

        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void startNewTrial() {

        try {

            ImageButton rightButton = (ImageButton) findViewById(R.id.rightImageButton);
            ImageButton leftButton = (ImageButton) findViewById(R.id.leftImageButton);
            TextView number=(TextView)findViewById(R.id.textView13);
            number.setText(getResources().getString(R.string.sst_trial) + ": " + String.valueOf(nTrial) + "/" + String.valueOf(NUMBER_OF_TRIALS));
            final ImageView circle = (ImageView) findViewById(R.id.imageView3);
            circle.setImageResource(R.drawable.white_circle);
            final TextView feedback = (TextView)findViewById(R.id.bottom);


            timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TRANSITIONS) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {

                    if (nTrial <=NUMBER_OF_TRIALS) {

                        if (correctDirections[nTrial - 1] == 0) { //Arrow pointing right, no sound
                            circle.setImageResource(R.drawable.white_circle_right_arrow);
                        }

                        if (correctDirections[nTrial - 1] == 1) { //Arrow pointing left, no sound
                            circle.setImageResource(R.drawable.white_circle_left_arrow);
                        }

                        if (correctDirections[nTrial - 1] == 2) { //Arrow pointing right, with sound
                            circle.setImageResource(R.drawable.white_circle_right_arrow);
                            nStimuliWithAudio++;
                            tones.beep();
                        }

                        if (correctDirections[nTrial - 1] == 3) { //Arrow pointing left, with sound
                            circle.setImageResource(R.drawable.white_circle_left_arrow);
                            nStimuliWithAudio++;
                            tones.beep();
                        }
                    }
                }
            }.start();

            handler = new Handler();
            thread = new Runnable() {
                @Override
                public void run() {
                    if (nTrial<=NUMBER_OF_TRIALS) {
                        long time = System.currentTimeMillis();
                        double reactionTime = (time - timeLastTrial);
                        if (level==1) {
                            reactionTimesGO.add(reactionTime);
                        } else {
                            reactionTimesNoGO.add(reactionTime);
                        }
                        timeLastTrial = time;

                        if (correctDirections[nTrial - 1] == 0 || correctDirections[nTrial - 1] == 1) { // user too late (GO),

                            if (level==1) {
                                nTimeoutErrorsGO++;
                            } else {
                                nTimeoutErrorsNoGO++;
                            }
                            feedback.setText(getResources().getString(R.string.sst_too_late));
                            feedback.setVisibility(View.VISIBLE);
                        }

                    }

                    timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS*2, TIME_MILLISECONDS_TRANSITIONS*2) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            feedback.setVisibility(View.INVISIBLE);
                        }
                    }.start();

                    nTrial++;
                    if(nTrial<=NUMBER_OF_TRIALS) {
                        startNewTrial();
                    }
                    else{

                        setNewLevel();
                    }
                }
            };

            if (nTrial <= NUMBER_OF_TRIALS) {
                handler.removeCallbacks(thread);
                handler.postDelayed(thread, THREE_SECONDS);
            }

            leftButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    handler.removeCallbacks(thread);

                    if (nTrial <= NUMBER_OF_TRIALS) {
                        long time = System.currentTimeMillis();
                        double reactionTime = (time - timeLastTrial);
                        if (level == 1) {
                            reactionTimesGO.add(reactionTime);
                        } else {
                            reactionTimesNoGO.add(reactionTime);
                        }
                        timeLastTrial = time;

                        if (correctDirections[nTrial - 1] == 0) {// errorDirection
                            if (level==1) {
                                nDirectionErrorsGO++;
                            } else {
                                nDirectionErrorsNoGO++;
                            }
                            feedback.setText(getResources().getString(R.string.sst_wrong));
                            feedback.setVisibility(View.VISIBLE);
                        }

                        if (correctDirections[nTrial - 1] == 2) {// tapError && errorDirection
                            if (level == 1) {
                                nDirectionErrorsGO ++;
                                nTapErrorsGO++;
                            } else {
                                nDirectionErrorsNoGO ++;
                                nTapErrorsNoGO++;
                            }
                            feedback.setText(getResources().getString(R.string.sst_wrong));
                            feedback.setVisibility(View.VISIBLE);
                        }

                        if (correctDirections[nTrial - 1] == 3) {// tapError
                            if (level==1) {
                                nTapErrorsGO++;
                            } else {
                                nTapErrorsNoGO++;
                            }
                            feedback.setText(getResources().getString(R.string.sst_wrong));
                            feedback.setVisibility(View.VISIBLE);
                        }
                    }

                    timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS * 2, TIME_MILLISECONDS_TRANSITIONS * 2) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            feedback.setVisibility(View.INVISIBLE);
                        }
                    }.start();

                    nTrial++;

                    if (nTrial <= NUMBER_OF_TRIALS) {
                        startNewTrial();
                    } else {
                        setNewLevel();
                    }

                }
            });

            rightButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                        handler.removeCallbacks(thread);

                        if (nTrial <= NUMBER_OF_TRIALS) {
                            long time = System.currentTimeMillis();
                            double reactionTime = (time - timeLastTrial);
                            if (level==1) {
                                reactionTimesGO.add(reactionTime);
                            } else {
                                reactionTimesNoGO.add(reactionTime);
                            }
                            timeLastTrial = time;

                            if (correctDirections[nTrial - 1] == 1) {// errorDirection
                                if (level==1) {
                                    nDirectionErrorsGO++;
                                } else {
                                    nDirectionErrorsNoGO++;
                                }
                                feedback.setText(getResources().getString(R.string.sst_wrong));
                                feedback.setVisibility(View.VISIBLE);
                            }

                            if (correctDirections[nTrial - 1] == 2) {// tapError
                                if (level==1) {
                                    nTapErrorsGO++;
                                } else {
                                    nTapErrorsNoGO++;
                                }
                                feedback.setText(getResources().getString(R.string.sst_wrong));
                                feedback.setVisibility(View.VISIBLE);
                            }

                            if (correctDirections[nTrial - 1] == 3) {// tapError && errorDirection

                                if (level==1) {
                                    nDirectionErrorsGO++;
                                    nTapErrorsGO++;
                                } else {
                                    nDirectionErrorsNoGO++;
                                    nTapErrorsNoGO++;
                                }

                                feedback.setText(getResources().getString(R.string.sst_wrong));
                                feedback.setVisibility(View.VISIBLE);
                            }
                        }

                        timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS * 2, TIME_MILLISECONDS_TRANSITIONS * 2) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                feedback.setVisibility(View.INVISIBLE);
                            }
                        }.start();

                        nTrial++;

                        if (nTrial <= NUMBER_OF_TRIALS) {
                            startNewTrial();
                        } else {

                            setNewLevel();
                        }
                }
            });

        }catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }

    }


    private void getTestResult () {

        int nErrorsGO = nDirectionErrorsGO + nTapErrorsGO + nTimeoutErrorsGO;
        int nErrorsNoGO = nDirectionErrorsNoGO + nTapErrorsNoGO + nTimeoutErrorsNoGO;

        double totalErrorsGOPerCent;
        double totalErrorsNoGOPerCent;

        if ((nErrorsGO + nErrorsNoGO)==0) {
            totalErrorsGOPerCent = 0;
            totalErrorsNoGOPerCent = 0;
        } else {
            totalErrorsGOPerCent = (nErrorsGO*100.00)/(nErrorsGO+nErrorsNoGO);
            totalErrorsNoGOPerCent = (nErrorsNoGO*100.00)/(nErrorsGO+nErrorsNoGO);
        }


        double errorsPerCent = ((nErrorsGO + nErrorsNoGO)*100.00)/(NUMBER_OF_TRIALS*NUMBER_OF_LEVELS);

        double[] reactionTimesLevel1 = new double[reactionTimesGO.size()];
        double[] reactionTimesLevel2 = new double[reactionTimesNoGO.size()];
        double[] reactionTimes = new double[reactionTimesNoGO.size() + reactionTimesGO.size()];

        for (int i= 0; i<reactionTimesGO.size(); i++) {
            reactionTimesLevel1[i] = reactionTimesGO.get(i);
            reactionTimes[i] = reactionTimesGO.get(i);
        }

        for (int i= 0; i<reactionTimesNoGO.size(); i++) {
            reactionTimesLevel2[i] = reactionTimesNoGO.get(i);
            reactionTimes[reactionTimesGO.size() + i] = reactionTimesNoGO.get(i);
        }

        Statistics stReactionTimeGO = new Statistics(reactionTimesLevel1);
        Statistics stReactionTimeNoGo = new Statistics(reactionTimesLevel2);
        Statistics stReactionTimes = new Statistics(reactionTimes);

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime()) ;

        String errorsGO = String.format(Locale.ENGLISH, "%.2f", totalErrorsGOPerCent);
        String errorsNoGO = String.format(Locale.ENGLISH, "%.2f", totalErrorsNoGOPerCent);
        String errors = String.format(Locale.ENGLISH, "%.2f", errorsPerCent);

        String meanReactionTimeGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeGO.getMean());
        String stdReactionTimeGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeGO.getStdDev());
        String maxReactionTimeGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeGO.getMax());
        String minReactionTimeGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeGO.getMin());
        String meanReactionTimeNoGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeNoGo.getMean());
        String stdReactionTimeNoGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeNoGo.getStdDev());
        String maxReactionTimeNoGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeNoGo.getMax());
        String minReactionTimeNoGO = String.format(Locale.ENGLISH, "%.2f", stReactionTimeNoGo.getMin());
        String meanReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getMean());
        String stdReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getStdDev());
        String maxReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getMax());
        String minReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getMin());


        resultInfo.append(date + ", ");
        resultInfo.append(Integer.toString(nDirectionErrorsGO) + ", ");
        resultInfo.append(Integer.toString(nDirectionErrorsNoGO) + ", ");
        resultInfo.append(Integer.toString(nTapErrorsGO) + ", ");
        resultInfo.append(Integer.toString(nTapErrorsNoGO) + ", ");
        resultInfo.append(Integer.toString(nTimeoutErrorsGO) + ", ");
        resultInfo.append(Integer.toString(nTimeoutErrorsNoGO) + ", ");
        resultInfo.append(Integer.toString(nStimuliWithAudio) + ", ");
        resultInfo.append(errorsGO + ", ");
        resultInfo.append(errorsNoGO + ", ");
        resultInfo.append(errors + ", ");
        resultInfo.append(meanReactionTimeGO + ", ");
        resultInfo.append(stdReactionTimeGO + ", ");
        resultInfo.append(maxReactionTimeGO + ", ");
        resultInfo.append(minReactionTimeGO + ", ");
        resultInfo.append(meanReactionTimeNoGO + ", ");
        resultInfo.append(stdReactionTimeNoGO + ", ");
        resultInfo.append(maxReactionTimeNoGO + ", ");
        resultInfo.append(minReactionTimeNoGO + ", ");
        resultInfo.append(meanReactionTime);
        resultInfo.append(stdReactionTime + ", ");
        resultInfo.append(maxReactionTime + ", ");
        resultInfo.append(minReactionTime + "\r\n");

        results.add(String.valueOf(resultInfo));
        sendObservations(nDirectionErrorsGO, nDirectionErrorsNoGO, nTapErrorsGO, nTapErrorsNoGO, nTimeoutErrorsGO,
                nTimeoutErrorsNoGO, nStimuliWithAudio, Double.parseDouble(meanReactionTimeGO), Double.parseDouble(maxReactionTimeGO),
                Double.parseDouble(minReactionTimeGO), Double.parseDouble(meanReactionTimeNoGO), Double.parseDouble(maxReactionTimeNoGO),
                Double.parseDouble(minReactionTimeNoGO));
    }

    public void sendObservations (int dirErrorsGO, int dirErrorsNoGO, int tapErrorsGO, int tapErrorsNoGO, int timeoutErrorsGO,
                                  int timeoutErrorsNoGO, int nStimuli, double meanRTGO, double maxRTGO, double minRTGO,
                                  double meanRTNoGO, double maxRTNoGO, double minRTNoGO) {
        //Observations
        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();
            Observation obsSSTDirGO = new Observation (dirErrorsGO, patientCode, "PDTSST_DIR_GO", time);
            obsSSTDirGO.PatientId = patientCode;
            Observation obsSSTDirNoGO = new Observation(dirErrorsNoGO, patientCode, "PDTSST_DIR_NOGO", time);
            obsSSTDirNoGO.PatientId = patientCode;
            Observation obsSSTTapGO = new Observation(tapErrorsGO, patientCode, "PDTSST_TAP_GO", time);
            obsSSTTapGO.PatientId = patientCode;
            Observation obsSSTTapNoGO = new Observation(tapErrorsNoGO, patientCode, "PDTSST_TAP_NOGO", time);
            obsSSTTapNoGO.PatientId = patientCode;
            Observation obsSSTTimeoutGO = new Observation (timeoutErrorsGO, patientCode, "PDTSST_TIMEOUT_GO", time);
            obsSSTTimeoutGO.PatientId = patientCode;
            Observation obsSSTTimeoutNoGO = new Observation (timeoutErrorsNoGO, patientCode, "PDTSST_TIMEOUT_NOGO", time);
            obsSSTTimeoutNoGO.PatientId = patientCode;
            Observation obsSSTStimuli = new Observation(nStimuli, patientCode, "PDTSST_STIMULI", time);
            obsSSTStimuli.PatientId = patientCode;
            Observation obsSSTMeanRTGO = new Observation(meanRTGO, patientCode, "PDTSST_MEAN_GO", time);
            obsSSTMeanRTGO.PatientId = patientCode;
            Observation obsSSTMaxRTGO = new Observation(maxRTGO, patientCode, "PDTSST_MAX_GO", time);
            obsSSTMaxRTGO.PatientId = patientCode;
            Observation obsSSTMinRTGO = new Observation(minRTGO, patientCode, "PDTSST_MIN_GO", time);
            obsSSTMinRTGO.PatientId = patientCode;
            Observation obsSSTMeanRTNoGO = new Observation(meanRTNoGO, patientCode, "PDTSST_MEAN_NOGO", time);
            obsSSTMeanRTNoGO.PatientId = patientCode;
            Observation obsSSTMaxRTNoGO = new Observation(maxRTNoGO, patientCode, "PDTSST_MAX_NOGO", time);
            obsSSTMaxRTNoGO.PatientId = patientCode;
            Observation obsSSTMinRTNoGO = new Observation(minRTNoGO, patientCode, "PDTSST_MIN_NOGO", time);
            obsSSTMinRTNoGO.PatientId = patientCode;

            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsSSTDirGO);
            observations.add(obsSSTDirNoGO);
            observations.add(obsSSTTapGO);
            observations.add(obsSSTTapNoGO);
            observations.add(obsSSTTimeoutGO);
            observations.add(obsSSTTimeoutNoGO);
            observations.add(obsSSTStimuli);
            observations.add(obsSSTMeanRTGO);
            observations.add(obsSSTMaxRTGO);
            observations.add(obsSSTMinRTGO);
            observations.add(obsSSTMeanRTNoGO);
            observations.add(obsSSTMaxRTNoGO);
            observations.add(obsSSTMinRTNoGO);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }


    @Override
    protected void finishTest(){

        try {
            writeFile (test, header);

            speakerSilence();

            if (timerTask != null) {
                timerTask.cancel();
            }

            if (timer !=null) {
                timer.cancel();
            }

            tones.stopTone();
            handler.removeCallbacks(thread);

         /*   setContentView(R.layout.activity_end);

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

            speakerSilence();

            if (timerTask != null) {
                timerTask.cancel();
            }

            if (timer !=null) {
                timer.cancel();
            }

            handler.removeCallbacks(thread);

            speakerSilence();

            timerTask = new CountDownTimer(TIME_MILLISECONDS_LEVEL_TASK, TIME_MILLISECONDS_LEVEL_TASK) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    level = 1;
                    setNewLevel();

                }
            }.start();

            results = new ArrayList<>();
            level = 0;
            isStarted = true;
            setNewLevel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        speakerSilence();

        if (timerTask != null) {
            timerTask.cancel();
        }

        if (timer !=null) {
            timer.cancel();
        }

        if (handler!=null) handler.removeCallbacks(thread);
        isPaused = true;
    }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.COGN + "_" + LOGGER_TAG;
    }
}