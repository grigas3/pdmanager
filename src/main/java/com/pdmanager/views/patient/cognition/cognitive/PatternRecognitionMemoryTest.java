package com.pdmanager.views.patient.cognition.cognitive;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.UserTaskCodes;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

/**
 * Cognitive Test Pattern Recognition Memory
 *
 * @authors Thibaud Pacquetet, Samanta Villanueva (svillanueva@lst.tfo.upm.es ), Jorge Cancela (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class PatternRecognitionMemoryTest extends SoundFeedbackActivity {
    private final int TIME_MILLISECONDS_SHOWING_STIMULI = 2000;
    private final int TIME_MILLISECONDS_TASK = 300000;
    private final int MIN_NUMBER_OF_SHOWED_STIMULI = 2;
    private final int NUMBER_OF_LEVELS = 5;
    private final ArrayList<Integer> typeOfStimuli = new ArrayList<>();
    private final ArrayList<Integer> colorOfStimuli = new ArrayList<>();
    private final ArrayList<Integer> orderOfStimuli = new ArrayList<>();
    private final ArrayList<Integer> randomOrder = new ArrayList<>();
    private String LOGGER_TAG = "PRMemoryTest";
    private String
            test = "PRM_Results.csv",
            header = "Timestamp, " +
                    "Type of task, " +
                    "Number of stimuli, " +
                    "Number of errors, " +
                    "Mean time between taps (s), " +
                    "Time between taps STD (s), " +
                    "Total time (s) " +
                    "\r\n";
    private int level;
    private int nLevelStimuli;
    private int counter = 0;
    private int nErrors;
    private int[][] test_1 = {
            {R.drawable.prm1, R.drawable.prm1_b, R.drawable.prm1_r,
                    R.drawable.prm1_ro, R.drawable.prm1_v
            },
            {R.drawable.prm2, R.drawable.prm2_b, R.drawable.prm2_r,
                    R.drawable.prm2_ro, R.drawable.prm2_v
            },
            {R.drawable.prm3, R.drawable.prm3_b, R.drawable.prm3_r,
                    R.drawable.prm3_ro, R.drawable.prm3_v
            },
            {R.drawable.prm4, R.drawable.prm4_b, R.drawable.prm4_r,
                    R.drawable.prm4_ro, R.drawable.prm4_v
            },
            {R.drawable.prm5, R.drawable.prm5_b, R.drawable.prm5_r,
                    R.drawable.prm5_ro, R.drawable.prm5_v
            },
            {R.drawable.prm6, R.drawable.prm6_b, R.drawable.prm6_r,
                    R.drawable.prm6_ro, R.drawable.prm6_v
            }
    };
    private int[][] test_1_90 = {
            {R.drawable.prm11, R.drawable.prm11_b, R.drawable.prm11_r,
                    R.drawable.prm11_ro, R.drawable.prm11_v
            },
            {R.drawable.prm22, R.drawable.prm22_b, R.drawable.prm22_r,
                    R.drawable.prm22_ro, R.drawable.prm22_v
            },
            {R.drawable.prm33, R.drawable.prm33_b, R.drawable.prm33_r,
                    R.drawable.prm33_ro, R.drawable.prm33_v
            },
            {R.drawable.prm44, R.drawable.prm44_b, R.drawable.prm44_r,
                    R.drawable.prm44_ro, R.drawable.prm44_v
            },
            {R.drawable.prm55, R.drawable.prm55_b, R.drawable.prm55_r,
                    R.drawable.prm55_ro, R.drawable.prm55_v
            },
            {R.drawable.prm66, R.drawable.prm66_b, R.drawable.prm66_r,
                    R.drawable.prm66_ro, R.drawable.prm66_v
            }
    };
    private int[] levelTypes = new int[NUMBER_OF_LEVELS];
    private double startTime;
    private double timeLastTap;
    private ArrayList<Double> timeBetweenTaps;

    private boolean isStarted = false;
    private boolean isPaused = false;
    private boolean isReverseOrder;
    private boolean isAnswered = false;

    private CountDownTimer timerTask;
    private CountDownTimer timer;
    private View.OnClickListener clickRightImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                if (!isAnswered) {
                    final ImageView imgCentral = (ImageView) findViewById(R.id.imageViewCentral);

                    boolean isError = false;
                    if (isReverseOrder) isError = (typeOfStimuli.get(counter) == 0);
                    else isError = (typeOfStimuli.get(randomOrder.get(counter)) == 0);
                    if (isError) {
                        nErrors++;
                        imgCentral.setImageResource(R.drawable.red_cross);
                        tones.nackBeep();
                    } else {
                        imgCentral.setImageResource(R.drawable.green_tick);
                        tones.ackBeep();
                    }

                    imgCentral.setVisibility(View.VISIBLE);
                    isAnswered = true;
                    double currentTime = System.currentTimeMillis();
                    timeBetweenTaps.add((currentTime - timeLastTap) / 1000D);
                    timeLastTap = currentTime;

                    timer = new CountDownTimer(TIME_MILLISECONDS_SHOWING_STIMULI, TIME_MILLISECONDS_SHOWING_STIMULI) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            if (isReverseOrder) {
                                counter--;
                                reverseOrder();
                            } else {
                                counter++;
                                randomOrder();
                            }
                        }
                    }.start();
                }

            } catch (Exception e) {

                Log.v(LOGGER_TAG, "Exception e: " + e.toString());
            }
        }
    };
    private View.OnClickListener clickLeftImage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                if (!isAnswered) {

                    final ImageView imgCentral = (ImageView) findViewById(R.id.imageViewCentral);

                    boolean isError = false;
                    if (isReverseOrder) isError = (typeOfStimuli.get(counter) == 1);
                    else isError = (typeOfStimuli.get(randomOrder.get(counter)) == 1);
                    if (isError) {
                        nErrors++;
                        imgCentral.setImageResource(R.drawable.red_cross);
                        tones.nackBeep();
                    } else {
                        imgCentral.setImageResource(R.drawable.green_tick);
                        tones.ackBeep();
                    }
                    imgCentral.setVisibility(View.VISIBLE);
                    isAnswered = true;

                    double currentTime = System.currentTimeMillis();
                    timeBetweenTaps.add((currentTime - timeLastTap) / 1000D);
                    timeLastTap = currentTime;

                    timer = new CountDownTimer(TIME_MILLISECONDS_SHOWING_STIMULI, TIME_MILLISECONDS_SHOWING_STIMULI) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            if (isReverseOrder) {
                                counter--;
                                reverseOrder();
                            } else {
                                counter++;
                                randomOrder();
                            }
                        }
                    }.start();
                }

            } catch (Exception e) {
                Log.v(LOGGER_TAG, "Exception e: " + e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startPRM();
    }

    private void startPRM() {
        if (!isStarted) {

            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.prm_instruction));

            speakFlush(getResources().getString(R.string.prm_instruction));

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

                    level = 1;
                    nLevelStimuli = MIN_NUMBER_OF_SHOWED_STIMULI;
                    results = new ArrayList<>();
                    timeBetweenTaps = new ArrayList<>();
                    setOrderOfLevels();
                    setNewLevel();
                }
            });
        } else {
            setNewLevel();
        }
    }

    private void setNewLevel() {

        if (level != 0) {
            writeFile(test, header);
        }

        results.clear();
        nErrors = 0;
        timeBetweenTaps.clear();
        typeOfStimuli.clear();
        orderOfStimuli.clear();
        colorOfStimuli.clear();
        randomOrder.clear();

        setContentView(R.layout.cognitive_prm_test);

        ImageButton imgLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        ImageButton imgRight = (ImageButton) findViewById(R.id.imageButtonRight);
        imgLeft.setVisibility(View.INVISIBLE);
        imgRight.setVisibility(View.INVISIBLE);

        String message = getResources().getString(R.string.prm_levels) + " " + (level) + " ("
                + nLevelStimuli + " " + getResources().getString(R.string.prm_patterns) + ")";

        final TextView prmInfo = (TextView) findViewById(R.id.prm_info);
        prmInfo.setVisibility(View.VISIBLE);
        prmInfo.setText(message);

        isReverseOrder = levelTypes[level - 1] == 0;

        timer = new CountDownTimer(TIME_MILLISECONDS_SHOWING_STIMULI, TIME_MILLISECONDS_SHOWING_STIMULI) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                prmInfo.setVisibility(View.INVISIBLE);
                showStimuli();
            }
        }.start();

    }

    private void setOrderOfLevels() {

        int nLevels = NUMBER_OF_LEVELS;

        if (!((NUMBER_OF_LEVELS % 2) == 0)) {
            nLevels++;
        }

        ArrayList<Integer> orderLevels = new ArrayList<>();
        for (int i = 0; i < nLevels; i++) {
            if (i < (nLevels / 2)) {
                orderLevels.add(0);
            } else {
                orderLevels.add(1);
            }
        }

        Collections.shuffle(orderLevels);
        for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
            levelTypes[i] = orderLevels.get(i);
        }

    }

    private void showStimuli() {

        // Choose randomly whether show normal or rotate stimuli
        Random r = new Random();

        for (int u = 0; u < nLevelStimuli; u++) {
            typeOfStimuli.add(r.nextInt(2));
        }

        // Choose randomly the color to show on each test
        for (int u = 0; u < nLevelStimuli; u++) {
            colorOfStimuli.add(r.nextInt(test_1[0].length));
        }

        // Choose the order of the stimuli to show
        for (int u = 0; u < nLevelStimuli; u++) {
            orderOfStimuli.add(u);
        }

        // shuffle the numbers in the lists
        Collections.shuffle(orderOfStimuli);

        counter = 0;

        final ImageButton imgLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        final ImageButton imgRight = (ImageButton) findViewById(R.id.imageButtonRight);
        final ImageView imgCentral = (ImageView) findViewById(R.id.imageViewCentral);

        // Init a counter to show the visual stimuli
        timer = new CountDownTimer((nLevelStimuli + 1) * TIME_MILLISECONDS_SHOWING_STIMULI, TIME_MILLISECONDS_SHOWING_STIMULI) {

            @Override
            public void onTick(long millisUntilFinished) {

                if (counter < nLevelStimuli) {

                    if (typeOfStimuli.get(counter) == 0) {
                        imgCentral.setImageResource(test_1[orderOfStimuli.get(counter)][colorOfStimuli.get(counter)]);
                    } else {
                        imgCentral.setImageResource(test_1_90[orderOfStimuli.get(counter)][colorOfStimuli.get(counter)]);
                    }

                    imgLeft.setVisibility(View.INVISIBLE);
                    imgRight.setVisibility(View.INVISIBLE);
                }

                counter++;
            }

            @Override
            public void onFinish() {

                imgCentral.setVisibility(View.INVISIBLE);
                imgLeft.setVisibility(View.VISIBLE);
                imgRight.setVisibility(View.VISIBLE);

                startTime = System.currentTimeMillis();
                timeLastTap = startTime;

                if (isReverseOrder) {
                    counter = nLevelStimuli - 1;
                    reverseOrder();
                } else {
                    counter = 0;
                    for (int u = 0; u < nLevelStimuli; u++) {
                        randomOrder.add(u);
                    }
                    Collections.shuffle(randomOrder);
                    randomOrder();
                }
            }
        }.start();

    }

    private void reverseOrder() {

        final ImageButton imgLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        final ImageButton imgRight = (ImageButton) findViewById(R.id.imageButtonRight);
        final ImageView imgCentral = (ImageView) findViewById(R.id.imageViewCentral);
        imgCentral.setVisibility(View.INVISIBLE);

        if (counter < 0) {

            double timeLevel = (System.currentTimeMillis() - startTime) / 1000D;
            double[] timeTaps = new double[timeBetweenTaps.size()];

            for (int i = 0; i < timeBetweenTaps.size(); i++) {
                timeTaps[i] = timeBetweenTaps.get(i);
            }

            Statistics st = new Statistics(timeTaps);
            addNewResult(timeLevel, st.getMean(), st.getStdDev());

            imgLeft.setVisibility(View.INVISIBLE);
            imgRight.setVisibility(View.INVISIBLE);

            timer = new CountDownTimer(TIME_MILLISECONDS_SHOWING_STIMULI, TIME_MILLISECONDS_SHOWING_STIMULI) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    level++;
                    nLevelStimuli++;

                    if (level > NUMBER_OF_LEVELS) {
                        finishTest();
                    } else {
                        setNewLevel();
                    }
                }
            }.start();

        } else {

            imgLeft.setOnClickListener(clickLeftImage);
            imgRight.setOnClickListener(clickRightImage);

            imgLeft.setImageResource(test_1[orderOfStimuli.get(counter)][colorOfStimuli.get(counter)]);
            imgRight.setImageResource(test_1_90[orderOfStimuli.get(counter)][colorOfStimuli.get(counter)]);

            isAnswered = false;

        }
    }

    private void randomOrder() {

        final ImageButton imgLeft = (ImageButton) findViewById(R.id.imageButtonLeft);
        final ImageButton imgRight = (ImageButton) findViewById(R.id.imageButtonRight);
        final ImageView imgCentral = (ImageView) findViewById(R.id.imageViewCentral);
        imgCentral.setVisibility(View.INVISIBLE);

        if (counter >= nLevelStimuli) {

            if (timerTask != null) {
                timerTask.cancel();
            }

            imgLeft.setVisibility(View.INVISIBLE);
            imgRight.setVisibility(View.INVISIBLE);

            double timeLevel = (System.currentTimeMillis() - startTime) / 1000D;
            double[] timeTaps = new double[timeBetweenTaps.size()];

            for (int i = 0; i < timeBetweenTaps.size(); i++) {
                timeTaps[i] = timeBetweenTaps.get(i);
            }

            Statistics st = new Statistics(timeTaps);
            addNewResult(timeLevel, st.getMean(), st.getStdDev());

            timer = new CountDownTimer(TIME_MILLISECONDS_SHOWING_STIMULI, TIME_MILLISECONDS_SHOWING_STIMULI) {

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    level++;
                    nLevelStimuli++;
                    if (level > NUMBER_OF_LEVELS) {
                        finishTest();
                    } else {
                        setNewLevel();
                    }
                }
            }.start();


        } else {

            imgLeft.setOnClickListener(clickLeftImage);
            imgRight.setOnClickListener(clickRightImage);

            imgLeft.setImageResource(test_1[orderOfStimuli.get(randomOrder.get(counter))][colorOfStimuli.get(randomOrder.get(counter))]);
            imgRight.setImageResource(test_1_90[orderOfStimuli.get(randomOrder.get(counter))][colorOfStimuli.get(randomOrder.get(counter))]);

            isAnswered = false;
        }
    }

    private void addNewResult(double phaseTs, double meanTimeAnswers, double stdTimeAnswers) {

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        Resources res = getResources();
        String meanTime = String.format(Locale.ENGLISH, "%.2f", meanTimeAnswers);
        String stdTime = String.format(Locale.ENGLISH, "%.2f", stdTimeAnswers);
        String ts = String.format(Locale.ENGLISH, "%.2f", phaseTs);
        String task;

        if (isReverseOrder) {
            task = res.getString(R.string.prm_reverse);
        } else {
            task = res.getString(R.string.prm_random);
        }

        resultInfo.append(date + ", ");
        resultInfo.append(task + ", ");
        resultInfo.append(String.valueOf(nLevelStimuli) + ", ");
        resultInfo.append(String.valueOf(nErrors) + ", ");
        resultInfo.append(meanTime + ", ");
        resultInfo.append(stdTime + ", ");
        resultInfo.append(ts + "\r\n");

        results.add(String.valueOf(resultInfo));
    }

    @Override
    protected void finishTest() {

        try {
            writeFile(test, header);

            if (timerTask != null) {
                timerTask.cancel();
            }

            if (timer != null) {
                timer.cancel();
            }

            tones.stopTone();

            speakerSilence();

          /*  setContentView(R.layout.activity_end);

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

        } catch (Exception e) {
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

            level = 1;
            nLevelStimuli = MIN_NUMBER_OF_SHOWED_STIMULI;
            results = new ArrayList<>();
            timeBetweenTaps = new ArrayList<>();
            setOrderOfLevels();
            setNewLevel();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (timerTask != null) {
            timerTask.cancel();
        }

        if (timer != null) {
            timer.cancel();
        }

        speakerSilence();
        isPaused = true;
    }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.COGN + "_" + LOGGER_TAG;
    }

}



