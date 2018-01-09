package com.pdmanager.views.patient.cognition.cognitive;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.Locale;


/**
 * Cognitive Test Attention Switching Task
 *
 * @authors: Adrian LUIS, Samanta VILLANUEVA (svillanueva@lst.tfo.upm.es), Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */
public class AttentionSwitchingTaskTest extends SoundFeedbackActivity {

    private final String LOGGER_TAG = "AST_test";

    private final int TIME_MILLISECONDS_TASK = 240000;
    private final int TIME_MILLISECONDS_FEEDBACK = 1000;
    private final int NUMBER_OF_TASKS = 4;
    private final int NUMBER_OF_TRIALS = 13;
    private final int NUMBER_OF_LEVELS = 2;

    private int taskNumber = 0;
    private int lastTaskNumber = 0;
    private int nTrial = 1;
    private int level = 0;


    private int nErrorsDirectionCongruency;
    private int nErrorsDirectionIncongruency;
    private int nErrorsPositionCongruency;
    private int nErrorsPositionIncongruency;
    private int nErrorsTimeoutCongruency;
    private int nErrorsTimeoutIncongruency;
    private ArrayList<Double> reactionTimesCongruency;
    private ArrayList<Double> reactionTimesIncongruency;

    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isAnswered = false;

    private CountDownTimer timerTask;
    private CountDownTimer timer;


    private String
            header = "Timestamp, " +
            "errors congruency, " +
            "errors incongruency, " +
            "errors direction congruency, " +
            "errors direction incongruency, " +
            "errors position congruency, " +
            "errors position incongruency, " +
            "errors timeout congruency, " +
            "errors timeout incongruency, " +
            "errors (%), " +
            "mean reaction time congruency (ms), " +
            "STD reaction time congruency (ms), " +
            "max reaction time congruency (ms), " +
            "min reaction time congruency (ms), " +
            "mean reaction time incongruency (ms), " +
            "STD reaction time incongruency (ms), " +
            "max reaction time incongruency (ms), " +
            "min reaction time incongruency (ms), " +
            "mean reaction time (ms), " +
            "STD reaction time (ms) " +
            "max reaction time (ms), " +
            "min reaction time (ms) " + "\r\n",
            test = "AttentionSwitchingTask.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAST();
    }

    private void startAST() {

        // Introduction to the task
        if (!isStarted) {
            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.ast_instruction));
            speakFlush(getResources().getString(R.string.ast_instruction));

            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    speakerSilence();
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
                    setContentView(R.layout.cognitive_ast_test);

                    results = new ArrayList<>();
                    nTrial = 1;
                    level = 0;
                    nErrorsDirectionCongruency = 0;
                    nErrorsDirectionIncongruency = 0;
                    nErrorsPositionCongruency = 0;
                    nErrorsPositionIncongruency = 0;
                    nErrorsTimeoutCongruency = 0;
                    nErrorsTimeoutIncongruency = 0;
                    reactionTimesCongruency = new ArrayList<Double>();
                    reactionTimesIncongruency = new ArrayList<Double>();
                    setNewLevel();
                }
            });
        }else{
            setNewLevel();
        }

    }

    private void startSecondPartOfTest() {

        try {

            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.ast_instruction));
            speakFlush(getResources().getString(R.string.ast_instruction));

            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    speakerSilence();

                    timerTask = new CountDownTimer(TIME_MILLISECONDS_TASK, TIME_MILLISECONDS_TASK) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            finishTest();
                        }
                    }.start();

                    setContentView(R.layout.cognitive_ast_test);
                    getTaskNumber();
                    startLevel();


                }
            });

        } catch (Exception e){
            Log.v(LOGGER_TAG, "Exception e: " + e.toString());
        }
    }

    private void setNewLevel() {

        if (level != 0) {
            writeFile(test, header);
        }

        level++;
        nTrial = 1;

        if (level > NUMBER_OF_LEVELS) {

            speakerSilence();
            if (timer != null) {
                timer.cancel();
            }
            getTestResult();
            finishTest();

        } else {

            isAnswered = false;

            if (level == 2) {
                if (timerTask != null) {
                    timerTask.cancel();
                }
                startSecondPartOfTest();
            } else {
                // random task number
                getTaskNumber();
                startLevel();
            }


        }
    }



    private void startLevel()
    {
        final int timeToRespond = 5*1000;
        final CountDownTimer cdt = new CountDownTimer(timeToRespond, timeToRespond)
        {
            @Override public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish()
            {
                isAnswered = false;
                if (level == 1) {
                    nErrorsTimeoutCongruency++;
                } else {
                    nErrorsTimeoutIncongruency++;
                }
                if (nTrial < NUMBER_OF_TRIALS)
                {
                    nTrial++;
                    getTaskNumber();
                    startLevel();
                }
                else setNewLevel();
            }
        };
        cdt.start();

        TextView textCue = (TextView) findViewById(R.id.textView_arrowCue);

        ImageView imageRight = (ImageView) findViewById(R.id.imageView_RightArrow);
        ImageView imageLeft = (ImageView) findViewById(R.id.imageView_LeftArrow);

        Button buttonRight = (Button) findViewById(R.id.buttonRight);
        Button buttonLeft = (Button) findViewById(R.id.buttonLeft);

        final Long tsStart;

        // Set cue
        if (level == 1) {
            textCue.setText(R.string.ast_cue_arrowDirection);
        } else {
            textCue.setText(R.string.ast_cue_arrowPosition);
        }

        // Set arrow
        if (taskNumber == 1) { //Right direction, right side
            imageRight.setImageResource(R.drawable.ic_right_arrow);
            imageLeft.setImageDrawable(null);
        }
        if (taskNumber == 2) { //Left direction, right side
            imageRight.setImageResource(R.drawable.ic_left_arrow);
            imageLeft.setImageDrawable(null);
        }
        if (taskNumber == 3) {
            imageRight.setImageDrawable(null); //Right direction, left side
            imageLeft.setImageResource(R.drawable.ic_right_arrow);
        }
        if (taskNumber == 4) {
            imageRight.setImageDrawable(null); //Left direction, left side
            imageLeft.setImageResource(R.drawable.ic_left_arrow);
        }

        tsStart = System.currentTimeMillis();

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdt.cancel();
                if (!isAnswered) {
                    final ImageView feedback = (ImageView) findViewById(R.id.imageViewCentral);
                    Long tsEnd = System.currentTimeMillis();
                    double time = (tsEnd - tsStart);
                    if ((taskNumber == 1 || taskNumber == 4)) {
                        reactionTimesCongruency.add(time);
                    } else {
                        reactionTimesIncongruency.add(time);
                    }
                    isAnswered = true;

                    if ((level == 1 &&(taskNumber == 2 || taskNumber == 4)) || (level == 2 &&(taskNumber == 3 || taskNumber == 4))) {
                        feedback.setImageResource(R.drawable.green_tick);
                        tones.ackBeep();

                    } else {
                        feedback.setImageResource(R.drawable.red_cross);
                        tones.nackBeep();
                        if (level == 1) {
                            if (taskNumber == 1) {
                                nErrorsDirectionCongruency++;
                            } else {
                                nErrorsDirectionIncongruency++;
                            }
                        } else {
                            if (taskNumber == 1) {
                                nErrorsPositionCongruency++;
                            } else {
                                nErrorsPositionIncongruency++;
                            }
                        }
                    }

                    feedback.setVisibility(View.VISIBLE);

                    timer = new CountDownTimer(TIME_MILLISECONDS_FEEDBACK, TIME_MILLISECONDS_FEEDBACK) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            feedback.setVisibility(View.INVISIBLE);
                            isAnswered = false;

                            if (nTrial < NUMBER_OF_TRIALS) {
                                nTrial++;
                                getTaskNumber();
                                startLevel();
                            } else {
                                setNewLevel();
                            }
                        }
                    }.start();


                }
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cdt.cancel();
                if (!isAnswered) {
                    final ImageView feedback = (ImageView) findViewById(R.id.imageViewCentral);
                    Long tsEnd = System.currentTimeMillis();
                    double time = (tsEnd - tsStart);
                    if ((taskNumber == 1 || taskNumber == 4)) {
                        reactionTimesCongruency.add(time);
                    } else {
                        reactionTimesIncongruency.add(time);
                    }

                    isAnswered = true;

                    if (((level == 1) && (taskNumber == 1 || taskNumber == 3)) || ((level == 2) && (taskNumber == 1 || taskNumber == 2))) {
                        feedback.setImageResource(R.drawable.green_tick);
                        tones.ackBeep();

                    } else {
                        feedback.setImageResource(R.drawable.red_cross);
                        tones.nackBeep();
                        if (level == 1) {
                            if (taskNumber == 4) {
                                nErrorsDirectionCongruency++;
                            } else {
                                nErrorsDirectionIncongruency++;
                            }
                        } else {
                            if (taskNumber == 4) {
                                nErrorsPositionCongruency++;
                            } else {
                                nErrorsPositionIncongruency++;
                            }
                        }
                    }

                    feedback.setVisibility(View.VISIBLE);

                    timer = new CountDownTimer(TIME_MILLISECONDS_FEEDBACK, TIME_MILLISECONDS_FEEDBACK) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            feedback.setVisibility(View.INVISIBLE);
                            isAnswered = false;

                            if (nTrial < NUMBER_OF_TRIALS) {
                                nTrial++;
                                getTaskNumber();
                                startLevel();
                            } else {
                                setNewLevel();
                            }
                        }
                    }.start();


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;
    }

    private void getTaskNumber () {
        int randomNumber = (int) (Math.random() * (NUMBER_OF_TASKS - 1)) + 1;

        if (lastTaskNumber == randomNumber) {
            getTaskNumber();
        } else {

            taskNumber = randomNumber;
            lastTaskNumber = taskNumber;
        }

    }



    private void getTestResult () {

        int nErrorsCongruency = nErrorsDirectionCongruency + nErrorsPositionCongruency + nErrorsTimeoutCongruency;
        int nErrorsIncongruency = nErrorsDirectionIncongruency + nErrorsPositionIncongruency + nErrorsTimeoutIncongruency;

        double errorsPerCent = ((nErrorsCongruency + nErrorsIncongruency)*100.00)/(NUMBER_OF_TRIALS*NUMBER_OF_LEVELS);

        double[] congruencyReactionTimes = new double[reactionTimesCongruency.size()];
        double[] incongruencyReactionTimes = new double[reactionTimesIncongruency.size()];
        double[] reactionTimes = new double[reactionTimesCongruency.size() + reactionTimesIncongruency.size()];

        for (int i= 0; i<reactionTimesCongruency.size(); i++) {
            congruencyReactionTimes[i] = reactionTimesCongruency.get(i);
            reactionTimes[i] = reactionTimesCongruency.get(i);
        }

        for (int i= 0; i<reactionTimesIncongruency.size(); i++) {
            incongruencyReactionTimes[i] = reactionTimesIncongruency.get(i);
            reactionTimes[reactionTimesCongruency.size() + i] = reactionTimesIncongruency.get(i);
        }

        Statistics stReactionTimeCongruency = new Statistics(congruencyReactionTimes);
        Statistics stReactionTimeIncongruency = new Statistics(incongruencyReactionTimes);
        Statistics stReactionTimes = new Statistics(reactionTimes);

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime()) ;

        String errors = String.format(Locale.ENGLISH, "%.2f", errorsPerCent);
        String meanReactionTimeCongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeCongruency.getMean());
        String stdReactionTimeCongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeCongruency.getStdDev());
        String maxReactionTimeCongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeCongruency.getMax());
        String minReactionTimeCongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeCongruency.getMin());
        String meanReactionTimeIncongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeIncongruency.getMean());
        String stdReactionTimeIncongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeIncongruency.getStdDev());
        String maxReactionTimeIncongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeIncongruency.getMax());
        String minReactionTimeIncongruency = String.format(Locale.ENGLISH, "%.2f", stReactionTimeIncongruency.getMin());
        String meanReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getMean());
        String stdReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getStdDev());
        String maxReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getMax());
        String minReactionTime = String.format(Locale.ENGLISH, "%.2f", stReactionTimes.getMin());


        resultInfo.append(date + ", ");

        resultInfo.append(Integer.toString(nErrorsCongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsIncongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsDirectionCongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsDirectionIncongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsPositionCongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsPositionIncongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsTimeoutCongruency) + ", ");
        resultInfo.append(Integer.toString(nErrorsTimeoutIncongruency) + ", ");
        resultInfo.append(errors + ", ");
        resultInfo.append(meanReactionTimeCongruency + ", ");
        resultInfo.append(stdReactionTimeCongruency + ", ");
        resultInfo.append(maxReactionTimeCongruency + ", ");
        resultInfo.append(minReactionTimeCongruency + ", ");
        resultInfo.append(meanReactionTimeIncongruency + ", ");
        resultInfo.append(stdReactionTimeIncongruency + ", ");
        resultInfo.append(maxReactionTimeIncongruency + ", ");
        resultInfo.append(minReactionTimeIncongruency + ", ");
        resultInfo.append(meanReactionTime + ", ");
        resultInfo.append(stdReactionTime + ", ");
        resultInfo.append(maxReactionTime + ", ");
        resultInfo.append(minReactionTime + "\r\n");

        results.add(String.valueOf(resultInfo));
        sendObservations(nErrorsDirectionCongruency, nErrorsDirectionIncongruency, nErrorsPositionCongruency,
                nErrorsPositionIncongruency, nErrorsTimeoutCongruency, nErrorsTimeoutIncongruency,
                Double.parseDouble(meanReactionTimeCongruency), Double.parseDouble(maxReactionTimeCongruency),
                Double.parseDouble(minReactionTimeCongruency), Double.parseDouble(meanReactionTimeIncongruency),
                Double.parseDouble(maxReactionTimeIncongruency), Double.parseDouble(minReactionTimeIncongruency));
    }

    public void sendObservations (int dirErrorsC, int dirErrorsI, int posErrorsC, int posErrorsI, int timeoutErrorsC,
                                  int timeoutErrorsI, double meanTimeC, double maxTimeC, double minTimeC,
                                  double meanTimeI, double maxTimeI, double minTimeI) {
        //Observations
        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();
            Observation obsASTDirC = new Observation(dirErrorsC, patientCode, "PDTAST_DIR_C", time);
            obsASTDirC.PatientId = patientCode;
            Observation obsASTDirI = new Observation(dirErrorsI, patientCode, "PDTAST_DIR_I", time);
            obsASTDirI.PatientId = patientCode;
            Observation obsASTPosC = new Observation(posErrorsC, patientCode, "PDTAST_POS_C", time);
            obsASTPosC.PatientId = patientCode;
            Observation obsASTPosI = new Observation(posErrorsI, patientCode, "PDTAST_POS_I", time);
            obsASTPosI.PatientId = patientCode;
            Observation obsASTTimeoutC = new Observation(timeoutErrorsC, patientCode, "PDTAST_TIMEOUT_C", time);
            obsASTTimeoutC.PatientId = patientCode;
            Observation obsASTTimeoutI = new Observation(timeoutErrorsI, patientCode, "PDTAST_TIMEOUT_I", time);
            obsASTTimeoutI.PatientId = patientCode;
            Observation obsASTMeanRTC = new Observation(meanTimeC, patientCode, "PDTAST_MEAN_C", time);
            obsASTMeanRTC.PatientId = patientCode;
            Observation obsASTMaxRTC = new Observation(maxTimeC, patientCode, "PDTAST_MAX_C", time);
            obsASTMaxRTC.PatientId = patientCode;
            Observation obsASTMinRTC = new Observation(minTimeC, patientCode, "PDTAST_MIN_C", time);
            obsASTMinRTC.PatientId = patientCode;
            Observation obsASTMeanRTI = new Observation(meanTimeI, patientCode, "PDTAST_MEAN_I", time);
            obsASTMeanRTI.PatientId = patientCode;
            Observation obsASTMaxRTI = new Observation(maxTimeI, patientCode, "PDTAST_MAX_I", time);
            obsASTMaxRTI.PatientId = patientCode;
            Observation obsASTMinRTI = new Observation(minTimeI, patientCode, "PDTAST_MIN_I", time);
            obsASTMinRTI.PatientId = patientCode;

            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsASTDirC);
            observations.add(obsASTDirI);
            observations.add(obsASTPosC);
            observations.add(obsASTPosI);
            observations.add(obsASTTimeoutC);
            observations.add(obsASTTimeoutI);
            observations.add(obsASTMeanRTC);
            observations.add(obsASTMaxRTC);
            observations.add(obsASTMinRTC);
            observations.add(obsASTMeanRTI);
            observations.add(obsASTMaxRTI);
            observations.add(obsASTMinRTI);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }

    @Override
    public void finishTest(){

        try {
            writeFile(test, header);

            speakerSilence();

            if (timerTask != null) {
                timerTask.cancel();
            }

            if (timer != null) {
                timer.cancel();
            }

            tones.stopTone();

            /*
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
            buttonExit.setVisibility(View.GONE);
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

            if (timer != null) {
                timer.cancel();
            }

            speakerSilence();
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
            setContentView(R.layout.cognitive_ast_test);

            results = new ArrayList<String>();
            nTrial = 1;
            level = 0;
            isPaused = false;
            setNewLevel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        speakerSilence();
        isPaused = true;

        if (timerTask != null) {
            timerTask.cancel();
        }

        if (timer != null) {
            timer.cancel();
        }

    }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.COGN + "_" + LOGGER_TAG;
    }
}
