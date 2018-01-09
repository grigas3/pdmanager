package com.pdmanager.views.patient.cognition.cognitive;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.UserTaskCodes;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.cognitive.LondonTowers.LondonTowersGame;
import com.pdmanager.views.patient.cognition.cognitive.LondonTowers.LondonTowersGraph;
import com.pdmanager.views.patient.cognition.tools.RNG;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * London Towers Test
 *
 * @authors Miguel Páramo (mparamo@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class LondonTowersTest extends SoundFeedbackActivity {
    private final String LOGGER_TAG = "London_Towers_test";

    private String
            test = "TowersOfLondon.csv",
            header = "Timestamp, "
                    + "number of correct trials, "
                    + "number of errors, "
                    + "number of repetitions, "
                    + "max. level reached, "
                    + "mean time per trial (s), "
                    + "STD time per trial (s), "
                    + "max time per trial (s), "
                    + "min time per trial (s), "
                    + "Total time (s)" + "\r\n";
    private RNG rng;
    private LondonTowersGame london;
    private View s0, s1, s2;
    private TextView tvLevel, tvMoves;
    private ImageView
            ivHand, feedback,
            ts00, ts01, ts02, ts10, ts11, ts12, ts20, ts21, ts22,
            s00, s01, s02, s10, s11, s12, s20, s21, s22;

    private Animation fadeIn, fadeOut;
    private AnimationSet anim;

    private boolean success = true;
    private int
            maxErrors = 3,
            errors = 0;

    private int
            level = 0,
            moves = 0,
            maxLevel = 10;

    private Integer handPeg = null;

    private int nCorrect;
    private int nErrors;
    private int nRepetitions;
    private int maxLevelReached = 0;
    private ArrayList<Double> timePerTrial;
    private OnClickListener oclStack = new OnClickListener() {
        @Override
        public void onClick(View v) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            double startTime = System.currentTimeMillis();
            String date = dateFormat.format(Calendar.getInstance().getTime());

            if (v == s0) {
                if (handPeg != null) {
                    london.init.p0.push(handPeg);
                    handPeg = null;
                    moves++;
                } else handPeg = london.init.p0.pop();
            } else if (v == s1) {
                if (handPeg != null) {
                    london.init.p1.push(handPeg);
                    handPeg = null;
                    moves++;
                } else handPeg = london.init.p1.pop();
            } else if (v == s2) {
                if (handPeg != null) {
                    london.init.p2.push(handPeg);
                    handPeg = null;
                    moves++;
                } else handPeg = london.init.p2.pop();
            }
            updateCurrentTowers();
            updateHand();
            if (london.isResolved()) {
                double endTime = System.currentTimeMillis();
                double totalTime = (endTime - startTime);
                timePerTrial.add(totalTime);
                String totalTimeLevel = String.format(Locale.ENGLISH, "%.2f", totalTime);

                tvMoves.setText(getString(R.string.movement) + ": " + moves + "/" + london.minMoves);
                updateFeedback(true);
            } else updateMoves();
        }
    };

    private void infoTest() {
        if (level == 0) {
            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.london_towers_instruction));
            speakFlush(getResources().getString(R.string.london_towers_instruction));
            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    speakerSilence();
                    start();
                }
            });

        } else start();
    }

    private void updateMoves() {
        tvMoves.setText(getString(R.string.movement) + ": " + moves + "/" + london.minMoves);
        if (moves > london.minMoves) updateFeedback(false);
    }

    private void updateHand() {
        if (handPeg == null) ivHand.setImageResource(R.drawable.handempty);
        else if (handPeg == LondonTowersGraph.R) ivHand.setImageResource(R.drawable.handred);
        else if (handPeg == LondonTowersGraph.G) ivHand.setImageResource(R.drawable.handyellow);
        else if (handPeg == LondonTowersGraph.B) ivHand.setImageResource(R.drawable.handblue);
    }

    private void updateFeedback(boolean s) {
        success = s;
        int icon = R.drawable.red_cross;
        if (success) {
            icon = R.drawable.green_tick;
            tones.ackBeep();
            nCorrect++;
        } else {
            tones.nackBeep();
            nErrors++;
        }
        feedback.setImageResource(icon);
        feedback.setVisibility(View.VISIBLE);
        feedback.startAnimation(anim);
    }

    private void start() {
        setContentView(R.layout.london_towers);
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        tvMoves = (TextView) findViewById(R.id.tvMovements);

        ivHand = (ImageView) findViewById(R.id.imgHand);

        feedback = (ImageView) findViewById(R.id.ivFeedback);
        s0 = findViewById(R.id.s0);
        s0.setOnClickListener(oclStack);
        s1 = findViewById(R.id.s1);
        s1.setOnClickListener(oclStack);
        s2 = findViewById(R.id.s2);
        s2.setOnClickListener(oclStack);

        // Target
        ts00 = (ImageView) findViewById(R.id.ts00);
        ts01 = (ImageView) findViewById(R.id.ts01);
        ts02 = (ImageView) findViewById(R.id.ts02);
        ts10 = (ImageView) findViewById(R.id.ts10);
        ts11 = (ImageView) findViewById(R.id.ts11);
        ts12 = (ImageView) findViewById(R.id.ts12);
        ts20 = (ImageView) findViewById(R.id.ts20);
        ts21 = (ImageView) findViewById(R.id.ts21);
        ts22 = (ImageView) findViewById(R.id.ts22);

        // Game
        s00 = (ImageView) findViewById(R.id.s00);
        s01 = (ImageView) findViewById(R.id.s01);
        s02 = (ImageView) findViewById(R.id.s02);
        s10 = (ImageView) findViewById(R.id.s10);
        s11 = (ImageView) findViewById(R.id.s11);
        s12 = (ImageView) findViewById(R.id.s12);
        s20 = (ImageView) findViewById(R.id.s20);
        s21 = (ImageView) findViewById(R.id.s21);
        s22 = (ImageView) findViewById(R.id.s22);

        nCorrect = 0;
        nErrors = 0;
        nRepetitions = 0;
        timePerTrial = new ArrayList<>();
        // results = new ArrayList<String>();

        nextGame();
    }

    private void retryLevel() {
        moves = 0;
        errors++;
        nRepetitions++;
        double startTime = System.currentTimeMillis();
        tvLevel.setText(getString(R.string.level) + ": " + level + "/" + maxLevel);
        if (errors == maxErrors) {
            double endTime = System.currentTimeMillis();
            double totalTime = (endTime - startTime);
            timePerTrial.add(totalTime);
            maxLevelReached = level;
            getTestResult();
            writeFile(test, header);
            finishTest();
        } else {
            shuffleTowersWithDifficulty(london.minMoves);
            updateMoves();
            updateGUITowers();
        }
    }

    private void nextGame() {
        moves = 0;
        errors = 0;
        level++;
        tvLevel.setText(getString(R.string.level) + ": " + level + "/" + maxLevel);
        if (level == maxLevel + 1) {
            maxLevelReached = level - 1;
            getTestResult();
            writeFile(test, header);
            finishTest();
        } else {
            shuffleTowers();
            updateMoves();
            updateGUITowers();
        }
    }

    private void updatePeg(ImageView iv, Integer n) {
        if (n == null) iv.setImageResource(R.drawable.pegvoid);
        else if (n == LondonTowersGame.R) iv.setImageResource(R.drawable.pegred);
        else if (n == LondonTowersGame.G) iv.setImageResource(R.drawable.pegyellow);
        else if (n == LondonTowersGame.B) iv.setImageResource(R.drawable.pegblue);
    }

    private void updateGUITowers() {
        updateTargetTowers();
        updateCurrentTowers();
    }

    private void updateTargetTowers() {
        updatePeg(ts00, london.target.p0.top);
        updatePeg(ts01, london.target.p0.middle);
        updatePeg(ts02, london.target.p0.bottom);

        updatePeg(ts10, london.target.p1.top);
        updatePeg(ts11, london.target.p1.middle);
        updatePeg(ts12, london.target.p1.bottom);

        updatePeg(ts20, london.target.p2.top);
        updatePeg(ts21, london.target.p2.middle);
        updatePeg(ts22, london.target.p2.bottom);
    }

    private void updateCurrentTowers() {
        updatePeg(s00, london.init.p0.top);
        updatePeg(s01, london.init.p0.middle);
        updatePeg(s02, london.init.p0.bottom);

        updatePeg(s10, london.init.p1.top);
        updatePeg(s11, london.init.p1.middle);
        updatePeg(s12, london.init.p1.bottom);

        updatePeg(s20, london.init.p2.top);
        updatePeg(s21, london.init.p2.middle);
        updatePeg(s22, london.init.p2.bottom);
    }


    private void shuffleTowers() {
        boolean shuffleAgain = setDifficulty();
        while (shuffleAgain) {
            shuffleAgain = setDifficulty();
        }
    }

    private boolean setDifficulty() {
        london.reset();
        boolean res = false;
        if (level <= 3) {
            res = (london.minMoves > 2);
        } else if (level <= 6) {
            res = (london.minMoves != 3 && london.minMoves != 4);
        } else if (london.minMoves < 5) res = true;
        return res;
    }

    private void shuffleTowersWithDifficulty(int difficulty) {
        boolean shuffleAgain = setDifficulty(difficulty);
        while (shuffleAgain) {
            shuffleAgain = setDifficulty(difficulty);
        }
    }

    private boolean setDifficulty(int difficulty) {
        london.reset();
        boolean res = (london.minMoves != difficulty);
        return res;
    }

    private void getTestResult() {

        double[] times = new double[timePerTrial.size()];

        for (int i = 0; i < timePerTrial.size(); i++) {
            times[i] = timePerTrial.get(i);
        }

        Statistics stTimesPerTrial = new Statistics(times);

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        String meanTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getMean());
        String stdTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getStdDev());
        String maxTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getMax());
        String minTimePerTrial = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getMin());
        String totalTime = String.format(Locale.ENGLISH, "%.2f", stTimesPerTrial.getSum());

        resultInfo.append(date + ", ");

        resultInfo.append(Integer.toString(nCorrect) + ", ");
        resultInfo.append(Integer.toString(nErrors) + ", ");
        resultInfo.append(Integer.toString(nRepetitions) + ", ");
        resultInfo.append(Integer.toString(level) + ", ");
        resultInfo.append(meanTimePerTrial + ", ");
        resultInfo.append(stdTimePerTrial + ", ");
        resultInfo.append(maxTimePerTrial + ", ");
        resultInfo.append(minTimePerTrial + ", ");
        resultInfo.append(totalTime + "\r\n");

        results.add(String.valueOf(resultInfo));
        sendObservations(nCorrect, nErrors, nRepetitions, level, Double.parseDouble(meanTimePerTrial),
                Double.parseDouble(maxTimePerTrial), Double.parseDouble(minTimePerTrial));
    }

    public void sendObservations(int nCorrect, int nErrors, int nRepetitions, int maxLevelReached,
                                 double meanTime, double maxTime, double minTime) {
        //Observations
        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();
            Observation obsTOLCorr = new Observation(nCorrect, patientCode, "PDTTOL_CORR", time);
            obsTOLCorr.PatientId = patientCode;
            Observation obsTOLErrors = new Observation(nErrors, patientCode, "PDTTOL_ERRORS", time);
            obsTOLErrors.PatientId = patientCode;
            Observation obsTOLRep = new Observation(nRepetitions, patientCode, "PDTTOL_REP", time);
            obsTOLRep.PatientId = patientCode;
            Observation obsTOLMaxLevel = new Observation(maxLevelReached, patientCode, "PDTTOL_MAXLEVEL", time);
            obsTOLMaxLevel.PatientId = patientCode;
            Observation obsTOLMean = new Observation(meanTime, patientCode, "PDTTOL_MEAN", time);
            obsTOLMean.PatientId = patientCode;
            Observation obsTOLMax = new Observation(maxTime, patientCode, "PDTTOL_MAX", time);
            obsTOLMax.PatientId = patientCode;
            Observation obsTOLMin = new Observation(minTime, patientCode, "PDTTOL_MIN", time);
            obsTOLMin.PatientId = patientCode;


            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsTOLCorr);
            observations.add(obsTOLErrors);
            observations.add(obsTOLRep);
            observations.add(obsTOLMaxLevel);
            observations.add(obsTOLMean);
            observations.add(obsTOLMax);
            observations.add(obsTOLMin);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }

    /*private void finishTest() {
        try {

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


            Button buttonExit = (Button) findViewById(R.id.buttonFTTEndExit);
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

        } catch (Exception e) {}
    }
*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rng = new RNG();
        london = new LondonTowersGame();
        infoTest();

        final int
                durationFadeIn = 1000,
                gap = 2000,
                durationFadeOut = 500;
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(durationFadeIn);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(gap);
        fadeOut.setDuration(durationFadeOut);
        fadeOut.setInterpolator(new DecelerateInterpolator());

        anim = new AnimationSet(false);
        anim.addAnimation(fadeIn);
        anim.addAnimation(fadeOut);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                s0.setEnabled(false);
                s1.setEnabled(false);
                s2.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                s0.setEnabled(true);
                s1.setEnabled(true);
                s2.setEnabled(true);

                feedback.setVisibility(View.INVISIBLE);
                if (success) nextGame();
                else retryLevel();
            }
        });
    }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.COGN + "_" + LOGGER_TAG;
    }

}