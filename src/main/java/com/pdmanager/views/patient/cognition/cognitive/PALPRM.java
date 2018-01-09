package com.pdmanager.views.patient.cognition.cognitive;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.pdmanager.views.patient.cognition.tools.RNG;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;


/**
 * PAL / PRM Test
 *
 * @authors Miguel Páramo (mparamo@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class PALPRM extends SoundFeedbackActivity {
    private final String LOGGER_TAG = "PALPRM_test";
    private RNG rng;
    private TextView tvTitle;
    private ViewGroup lPairs, lGame;
    private ImageView imgPair0, imgPair1, img0, img1, img2, img3, imgTarget, feedback;

    // Test log
    private boolean isSemanticRelated;
    private long lastTimestamp;

    private ArrayList<IconPair> questionSet = new ArrayList<IconPair>();

    private Animation fadeIn, fadeOut;
    private AnimationSet anim;

    private ArrayList<Double> timesToAnswer;

    private long tsStartLevel;

    private String
            test = "VisualLearning.csv",
            header = "Timestamp, "
                    + "total correct level 1, "
                    + "total correct level 2, "
                    + "total correct level 3, "
                    + "total correct, "
                    + "correct semantic level 1, "
                    + "correct non semantic level 1, "
                    + "correct semantic level 2, "
                    + "correct non semantic level 2, "
                    + "correct semantic level 3, "
                    + "correct non semantic level 3, "
                    + "correct semantic (%), "
                    + "correct non semantic (%), "
                    + "time level 1 (s), "
                    + "time level 2 (s), "
                    + "time level 3 (s), "
                    + "mean time level (s), "
                    + "STD time level (s), "
                    + "max time level (s), "
                    + "min time level (s), "
                    + "total time (s)" + "\r\n";

    private int[]
            cat0 = {R.drawable.cat0a, R.drawable.cat0b, R.drawable.cat0c, R.drawable.cat0d, R.drawable.cat0e},
            cat1 = {R.drawable.cat1a, R.drawable.cat1b, R.drawable.cat1c, R.drawable.cat1d, R.drawable.cat1e},
            cat2 = {R.drawable.cat2a, R.drawable.cat2b, R.drawable.cat2c, R.drawable.cat2d, R.drawable.cat2e},
            cat3 = {R.drawable.cat3a, R.drawable.cat3b, R.drawable.cat3c, R.drawable.cat3d, R.drawable.cat3e},
            cat4 = {R.drawable.cat4a, R.drawable.cat4b, R.drawable.cat4c, R.drawable.cat4d, R.drawable.cat4e},
            cat5 = {R.drawable.cat5a, R.drawable.cat5b, R.drawable.cat5c, R.drawable.cat5d, R.drawable.cat5e},
            cat6 = {R.drawable.cat6a, R.drawable.cat6b, R.drawable.cat6c, R.drawable.cat6d, R.drawable.cat6e},
            cat7 = {R.drawable.cat7a, R.drawable.cat7b, R.drawable.cat7c, R.drawable.cat7d, R.drawable.cat7e},
            cat8 = {R.drawable.cat8a, R.drawable.cat8b, R.drawable.cat8c, R.drawable.cat8d, R.drawable.cat8e},
            cat9 = {R.drawable.cat9a, R.drawable.cat9b, R.drawable.cat9c, R.drawable.cat9d, R.drawable.cat9e};

    private int[][] cats = {cat0, cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9};
    private ArrayList<Integer> freeIcons;
    private int
            testPhase = 1,
            maxPhase = 3,
            level = 0,
            maxLevel = 10;
    private int
            nCorrectSemanticL1,
            nCorrectNonSemanticL1,
            nCorrectSemanticL2,
            nCorrectNonSemanticL2,
            nCorrectSemanticL3,
            nCorrectNonSemanticL3,
            nSemanticPairsL1,
            nSemanticPairsL2,
            nSemanticPairsL3;
    private ArrayList<Double> times;
    private ArrayList<Integer> indexSemantic;
    private OnClickListener oclCardOk = new OnClickListener() {
        @Override
        public void onClick(View v) {
            disableButtons();
            feedback = (ImageView) v;
            updateFeedback(true);
        }
    };
    private OnClickListener oclCardFail = new OnClickListener() {
        @Override
        public void onClick(View v) {
            disableButtons();
            feedback = (ImageView) v;
            updateFeedback(false);
        }
    };

    private void infoTest() {
        if (level == 0) {
            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.palprm_instruction));
            speakFlush(getResources().getString(R.string.palprm_instruction));
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

    private void updateFeedback(boolean success) {
        long ts = System.currentTimeMillis();
        int icon = R.drawable.red_cross;
        if (success) {
            boolean isSemantic = false;
            for (int i = 0; i < indexSemantic.size(); i++) {
                if (level == indexSemantic.get(i)) {
                    isSemantic = true;
                }
            }

            if (isSemantic) {
                if (testPhase == 1) nCorrectSemanticL1++;
                if (testPhase == 2) nCorrectSemanticL2++;
                if (testPhase == 3) nCorrectSemanticL3++;
            } else {
                if (testPhase == 1) nCorrectNonSemanticL1++;
                if (testPhase == 2) nCorrectNonSemanticL2++;
                if (testPhase == 3) nCorrectNonSemanticL3++;
            }


            icon = R.drawable.green_tick;
            tones.ackBeep();

        } else {

            tones.nackBeep();
        }
        feedback.setImageResource(icon);
        feedback.setVisibility(View.VISIBLE);
        feedback.startAnimation(anim);

        double secondsToAnswer = (System.currentTimeMillis() - lastTimestamp) / 1000;
        timesToAnswer.add(secondsToAnswer);

    }

    private void disableButtons() {
        img0.setClickable(false);
        img1.setClickable(false);
        img2.setClickable(false);
        img3.setClickable(false);
    }

    private void enableButtons() {
        img0.setClickable(true);
        img1.setClickable(true);
        img2.setClickable(true);
        img3.setClickable(true);
    }

    private void start() {
        setContentView(R.layout.palprm);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        lPairs = (ViewGroup) findViewById(R.id.llPairs);
        imgPair0 = (ImageView) findViewById(R.id.imgStack0);
        imgPair1 = (ImageView) findViewById(R.id.imgStack1);
        lGame = (ViewGroup) findViewById(R.id.llGame);
        imgTarget = (ImageView) findViewById(R.id.imgTarget);
        img0 = (ImageView) findViewById(R.id.img0);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        nCorrectSemanticL1 = 0;
        nCorrectNonSemanticL1 = 0;
        nCorrectSemanticL2 = 0;
        nCorrectNonSemanticL2 = 0;
        nCorrectSemanticL3 = 0;
        nCorrectNonSemanticL3 = 0;
        nSemanticPairsL1 = 5;
        nSemanticPairsL2 = 5;
        nSemanticPairsL3 = 5;
        times = new ArrayList<>();
        indexSemantic = new ArrayList<>();

        createInitialPairs();
        showPairs();
    }

    // Sets the layout for showing the pairs
    private void showPairs() {
        String title = getString(R.string.palprm_title0) + " (" + testPhase + "/" + maxPhase + ")";
        tvTitle.setText(title);
        lGame.setVisibility(View.GONE);
        lPairs.setVisibility(View.VISIBLE);
        if (testPhase != 1) {
            ArrayList<Integer> lastIndexSemantic = new ArrayList<>();
            ArrayList<IconPair> lastQuestionSet = new ArrayList<>();
            for (int i = 0; i < indexSemantic.size(); i++) {
                lastIndexSemantic.add(indexSemantic.get(i));
            }
            for (int i = 0; i < questionSet.size(); i++) {
                lastQuestionSet.add(questionSet.get(i));
            }
            indexSemantic.clear();
            Collections.shuffle(questionSet);
            for (int i = 0; i < lastIndexSemantic.size(); i++) {
                for (int j = 0; j < lastQuestionSet.size(); j++) {
                    if (questionSet.get(lastIndexSemantic.get(i)) == lastQuestionSet.get(j)) {
                        indexSemantic.add(j);
                    }
                }
            }
            Collections.sort(indexSemantic);
        }
        nextPair();
    }

    // Shows the actual test (5 icons) layout
    private void showTests() {
        tvTitle.setText(R.string.palprm_title1);
        lPairs.setVisibility(View.GONE);
        lGame.setVisibility(View.VISIBLE);
        level = 0;

        tsStartLevel = System.currentTimeMillis();
        nextTest();
    }

    // Shwos the next pair on the list
    private void nextPair() {
        if (level != maxLevel) {
            dealPair();
            level++;
        } else showTests();
    }

    private void createFreeIconsList() {
        freeIcons = new ArrayList<Integer>();
        for (int i = 0; i < cats.length; i++) {
            for (int j = 0; j < cats[i].length; j++) {
                freeIcons.add(cats[i][j]);
            }
        }
    }

    private IconPair getSemanticPair() {
        int
                catsLen = cats.length - 1,
                catlen = cat0.length - 1;
        int cp0, cp1, ip0, ip1;
        cp0 = rng.getIntInClosedRange(0, catsLen);
        cp1 = cp0;
        ip0 = rng.getIntInClosedRange(0, catlen);
        ip1 = rng.getIntInClosedRangeAvoiding(0, catlen, ip0);

        return new IconPair(cp0, ip0, cp1, ip1);
    }

    private IconPair getNonSemanticPair() {
        int
                catsLen = cats.length - 1,
                catlen = cat0.length - 1;
        int cp0, cp1, ip0, ip1;
        cp0 = rng.getIntInClosedRange(0, catsLen);
        cp1 = rng.getIntInClosedRangeAvoiding(0, catsLen, cp0);
        ip0 = rng.getIntInClosedRange(0, catlen);
        ip1 = rng.getIntInClosedRange(0, catlen);
        return new IconPair(cp0, ip0, cp1, ip1);
    }

    private void createInitialPairs() {
        createFreeIconsList();
        for (int i = 0; i < maxLevel; i++) {
            IconPair ip = null;
            isSemanticRelated = (level % 2 == 0);
            if (isSemanticRelated) {
                boolean generate = true;
                while (generate) {
                    ip = getSemanticPair();
                    boolean
                            g0 = freeIcons.contains(new Integer(cats[ip.card0Cat][ip.card0Icon])),
                            g1 = freeIcons.contains(new Integer(cats[ip.card1Cat][ip.card1Icon]));
                    generate = !(g0 && g1);
                }
                indexSemantic.add(i);
                /*if (testPhase == 1) {
                    nSemanticPairsL1++;
                } else if(testPhase == 2) {
                    nSemanticPairsL2++;
                } else {
                    nSemanticPairsL3++;
                }*/
            } else {
                boolean generate = true;
                while (generate) {
                    ip = getNonSemanticPair();
                    boolean
                            g0 = freeIcons.contains(new Integer(cats[ip.card0Cat][ip.card0Icon])),
                            g1 = freeIcons.contains(new Integer(cats[ip.card1Cat][ip.card1Icon]));
                    generate = !(g0 && g1);
                }
            }
            questionSet.add(ip);
            freeIcons.remove(new Integer(cats[ip.card0Cat][ip.card0Icon]));
            freeIcons.remove(new Integer(cats[ip.card1Cat][ip.card1Icon]));
            level++;
        }
        level = 0;
    }

    private void dealPair() {
        final IconPair ip = questionSet.get(level);

        // Show First One
        imgPair0.setImageResource(cats[ip.card0Cat][ip.card0Icon]);
        imgPair1.setImageResource(cats[ip.card1Cat][ip.card1Icon]);
        imgPair0.setVisibility(View.VISIBLE);
        imgPair1.setVisibility(View.INVISIBLE);

        final int shownTime = 1000;
        final CountDownTimer
                showingBoth = new CountDownTimer(shownTime, shownTime) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                nextPair();
            }
        },

                showingSecond = new CountDownTimer(shownTime, shownTime) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        imgPair0.setVisibility(View.VISIBLE);
                        showingBoth.start();
                    }
                },

                showingFirst = new CountDownTimer(shownTime, shownTime) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        imgPair0.setVisibility(View.INVISIBLE);
                        imgPair1.setVisibility(View.VISIBLE);
                        showingSecond.start();
                    }
                };
        showingFirst.start();
    }

    private void nextTest() {
        if (level != maxLevel) dealTest();
        else {

            level = 0;
            testPhase++;
            double levelTime = (System.currentTimeMillis() - tsStartLevel) / 1000D;
            times.add(levelTime);

            //nSemanticPairs = 0;
            if (testPhase <= maxPhase) showPairs();
            else {
                /*String
                        test = "PALPRM_Results.csv",
                        header = "Timestamp, " + "Number of semantic related pairs, " +
                                "Total correct answers, " + "Total wrong answers, " +
                                "Mean time to answer (s), " + "STD time to answer (s), " +
                                "Total time (s)" + "\r\n";*/

                /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                String date = dateFormat.format(tsStart);
                double totalTime = (System.currentTimeMillis() - tsStart.getTime()) / 1000D;
                String totalTimeLevel = String.format(Locale.ENGLISH, "%.2f", totalTime);

                double[] times = new double[maxLevel*maxPhase];
                for (int i = 0; i < timesToAnswer.size(); i++) {
                    times[i] = timesToAnswer.get(i);
                }

                Statistics st = new Statistics(times);
                String meanTime = String.format(Locale.ENGLISH, "%.2f", st.getMean());
                String stdTime = String.format(Locale.ENGLISH, "%.2f", st.getStdDev());

                String trackLine = "" + date + ", " + Integer.toString(nSemanticPairs) + ", " + Integer.toString(nCorrect) + ", "
                        + Integer.toString(nErrors) + ", " + meanTime + ", " + stdTime + ", " + totalTimeLevel + "\r\n";
                results.add(trackLine);*/
                getTestResult();
                writeFile(test, header);
                finishTest();
            }
        }
    }

    private void dealTest() {
        enableButtons();
        lastTimestamp = System.currentTimeMillis();
        IconPair ip = questionSet.get(level);
        int targetFrame = rng.getIntInClosedRange(0, 3);
        ImageView view;
        switch (targetFrame) {
            case 0: {
                view = img0;
                break;
            }
            case 1: {
                view = img1;
                break;
            }
            case 2: {
                view = img2;
                break;
            }
            default: {
                view = img3;
                break;
            }
        }
        if (rng.getBoolean()) {
            imgTarget.setImageResource(cats[ip.card0Cat][ip.card0Icon]);
            view.setImageResource(cats[ip.card1Cat][ip.card1Icon]);
        } else {
            imgTarget.setImageResource(cats[ip.card1Cat][ip.card1Icon]);
            view.setImageResource(cats[ip.card0Cat][ip.card0Icon]);
        }

        int
                icon0 = randomIcon(),
                icon1 = randomIcon(icon0),
                icon2 = randomIcon(icon0, icon1),
                icon3 = randomIcon(icon0, icon1, icon2);
        if (view != img0) {
            img0.setOnClickListener(oclCardFail);
            img0.setImageResource(icon0);
        }
        if (view != img1) {
            img1.setOnClickListener(oclCardFail);
            img1.setImageResource(icon1);
        }
        if (view != img2) {
            img2.setOnClickListener(oclCardFail);
            img2.setImageResource(icon2);
        }
        if (view != img3) {
            img3.setOnClickListener(oclCardFail);
            img3.setImageResource(icon3);
        }
        view.setOnClickListener(oclCardOk);
        level++;
    }

    private int randomIcon(int... excluding) {
        int pos = rng.getIntInClosedRange(0, freeIcons.size() - 1);
        int icon = freeIcons.get(pos);
        boolean generate = (excluding.length > 0);
        while (generate) {
            pos = rng.getIntInClosedRange(0, freeIcons.size() - 1);
            icon = freeIcons.get(pos);
            boolean matchesExclusion = false;
            for (int i : excluding) {
                if (icon == i) matchesExclusion = true;
                generate = matchesExclusion;
            }
        }
        return icon;
    }

    private void getTestResult() {

        int nCorrectLevel1 = nCorrectSemanticL1 + nCorrectNonSemanticL1;
        int nCorrectLevel2 = nCorrectSemanticL2 + nCorrectNonSemanticL2;
        int nCorrectLevel3 = nCorrectSemanticL3 + nCorrectNonSemanticL3;
        int nCorrect = nCorrectLevel1 + nCorrectLevel2 + nCorrectLevel3;
        int nCorrectSemantic = nCorrectSemanticL1 + nCorrectSemanticL2 + nCorrectSemanticL3;
        int nCorrectNonSemantic = nCorrectNonSemanticL1 + nCorrectNonSemanticL2 + nCorrectNonSemanticL3;
        int nTotalSemanticPairs = nSemanticPairsL1 + nSemanticPairsL2 + nSemanticPairsL3;
        int nTotalNonSemanticPairs = maxPhase * maxLevel - nTotalSemanticPairs;

        double correctSemanticPerCent = (nCorrectSemantic * 100.00) / nTotalSemanticPairs;
        double correctNonSemanticPerCent = (nCorrectNonSemantic * 100.00) / nTotalNonSemanticPairs;

        double[] levelTimes = new double[times.size()];

        for (int i = 0; i < times.size(); i++) {
            levelTimes[i] = times.get(i);
        }

        Statistics stLevelTimes = new Statistics(levelTimes);


        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime());

        String correctSemantic = String.format(Locale.ENGLISH, "%.2f", correctSemanticPerCent);
        String correctNonSemantic = String.format(Locale.ENGLISH, "%.2f", correctNonSemanticPerCent);

        String timeLevel1 = String.format(Locale.ENGLISH, "%.2f", levelTimes[0]);
        String timeLevel2 = String.format(Locale.ENGLISH, "%.2f", levelTimes[1]);
        String timeLevel3 = String.format(Locale.ENGLISH, "%.2f", levelTimes[2]);
        String meanLevelTime = String.format(Locale.ENGLISH, "%.2f", stLevelTimes.getMean());
        String stdLevelTime = String.format(Locale.ENGLISH, "%.2f", stLevelTimes.getStdDev());
        String maxLevelTime = String.format(Locale.ENGLISH, "%.2f", stLevelTimes.getMax());
        String minLevelTime = String.format(Locale.ENGLISH, "%.2f", stLevelTimes.getMin());
        String totalTime = String.format(Locale.ENGLISH, "%.2f", stLevelTimes.getSum());

        resultInfo.append(date + ", ");
        resultInfo.append(Integer.toString(nCorrectLevel1) + ", ");
        resultInfo.append(Integer.toString(nCorrectLevel2) + ", ");
        resultInfo.append(Integer.toString(nCorrectLevel3) + ", ");
        resultInfo.append(Integer.toString(nCorrect) + ", ");
        resultInfo.append(Integer.toString(nCorrectSemanticL1) + ", ");
        resultInfo.append(Integer.toString(nCorrectNonSemanticL1) + ", ");
        resultInfo.append(Integer.toString(nCorrectSemanticL2) + ", ");
        resultInfo.append(Integer.toString(nCorrectNonSemanticL2) + ", ");
        resultInfo.append(Integer.toString(nCorrectSemanticL3) + ", ");
        resultInfo.append(Integer.toString(nCorrectNonSemanticL3) + ", ");
        resultInfo.append(correctSemantic + ", ");
        resultInfo.append(correctNonSemantic + ", ");
        resultInfo.append(timeLevel1 + ", ");
        resultInfo.append(timeLevel2 + ", ");
        resultInfo.append(timeLevel3 + ", ");
        resultInfo.append(meanLevelTime + ", ");
        resultInfo.append(stdLevelTime + ", ");
        resultInfo.append(maxLevelTime + ", ");
        resultInfo.append(minLevelTime + ", ");
        resultInfo.append(totalTime + "\r\n");

        results.add(String.valueOf(resultInfo));
        sendObservations(nCorrectSemanticL1, nCorrectNonSemanticL1, nCorrectSemanticL2, nCorrectNonSemanticL2,
                nCorrectSemanticL3, nCorrectNonSemanticL3, Double.parseDouble(timeLevel1), Double.parseDouble(timeLevel2), Double.parseDouble(timeLevel3));
    }

    public void sendObservations(int nCorrectSemanticL1, int nCorrectNonSemanticL1, int nCorrectSemanticL2, int nCorrectNonSemanticL2,
                                 int nCorrectSemanticL3, int nCorrectNonSemanticL3, double timeLevel1, double timeLevel2, double timeLevel3) {
        //Observations
        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();
            Observation obsVLCSL1 = new Observation(nCorrectSemanticL1, patientCode, "PDTVL_CS_L1", time);
            obsVLCSL1.PatientId = patientCode;
            Observation obsVLCNSL1 = new Observation(nCorrectNonSemanticL1, patientCode, "PDTVL_CNS_L1", time);
            obsVLCNSL1.PatientId = patientCode;
            Observation obsVLCSL2 = new Observation(nCorrectSemanticL2, patientCode, "PDTVL_CS_L2", time);
            obsVLCSL2.PatientId = patientCode;
            Observation obsVLCNSL2 = new Observation(nCorrectNonSemanticL2, patientCode, "PDTVL_CNS_L2", time);
            obsVLCNSL2.PatientId = patientCode;
            Observation obsVLCSL3 = new Observation(nCorrectSemanticL3, patientCode, "PDTVL_CS_L3", time);
            obsVLCSL3.PatientId = patientCode;
            Observation obsVLCNSL3 = new Observation(nCorrectNonSemanticL3, patientCode, "PDTVL_CNS_L3", time);
            obsVLCNSL3.PatientId = patientCode;
            Observation obsVLTimeL1 = new Observation(timeLevel1, patientCode, "PDTVL_TIME_L1", time);
            obsVLTimeL1.PatientId = patientCode;
            Observation obsVLTimeL2 = new Observation(timeLevel2, patientCode, "PDTVL_TIME_L2", time);
            obsVLTimeL2.PatientId = patientCode;
            Observation obsVLTimeL3 = new Observation(timeLevel3, patientCode, "PDTVL_TIME_L3", time);
            obsVLTimeL3.PatientId = patientCode;

            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsVLCSL1);
            observations.add(obsVLCNSL1);
            observations.add(obsVLCSL2);
            observations.add(obsVLCNSL2);
            observations.add(obsVLCSL3);
            observations.add(obsVLCNSL3);
            observations.add(obsVLTimeL1);
            observations.add(obsVLTimeL2);
            observations.add(obsVLTimeL3);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rng = new RNG();

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

        timesToAnswer = new ArrayList<Double>();

        anim = new AnimationSet(false);
        anim.addAnimation(fadeIn);
        anim.addAnimation(fadeOut);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                nextTest();
            }
        });
        infoTest();
    }

    /*private void finishTest() {
        try {

            finish();


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


        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception finishing activity: " + e.toString());
        }

    }
    */

    @Override
    protected String getTestCode() {
        return UserTaskCodes.COGN + "_" + LOGGER_TAG;
    }

    private class IconPair {
        public int card0Cat, card0Icon, card1Cat, card1Icon;

        public IconPair(int c0c, int c0i, int c1c, int c1i) {
            card0Cat = c0c;
            card0Icon = c0i;
            card1Cat = c1c;
            card1Icon = c1i;
        }
    }


}