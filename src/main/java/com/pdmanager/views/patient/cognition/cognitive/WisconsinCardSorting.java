package com.pdmanager.views.patient.cognition.cognitive;

import android.content.Intent;
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
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.MainMenu;
import com.pdmanager.views.patient.cognition.tools.RNG;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;
import com.pdmanager.views.patient.cognition.tools.Statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * Wisconsin Card Sorting Test
 *
 * @authors Miguel Páramo (mparamo@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class WisconsinCardSorting extends SoundFeedbackActivity
{

    private final String LOGGER_TAG = "WC Sorting test";

    private String
        test = "WisconsinCardSortingTest.csv",
        header = "Timestamp, "
                + "rule, "
                + "card choosen, "
                + "correct answer, "
                + "time to answer (s)" +"\r\n";

    private int forcedErrors, totalErrors, good;
    private boolean forced;
    private RNG rng;
    private TextView tvLevel;
    private ImageView
        feedback, f0, f1, f2, f3,
        imgCard, img0, img1, img2, img3;

    private Animation fadeIn, fadeOut;
    private AnimationSet anim;

    private double lastTime;

    private String isCorrectAnswer;
    private String rule;
    private String cardChoosen;

    private double timeToAnswer;

    private int iShape, iColor, iNumber, currentSorting;
    private final int
        kShape = 0,
        kNum = 1,
        kColor = 2;

    private int[]
        cr = { R.drawable.cr1, R.drawable.cr2, R.drawable.cr3, R.drawable.cr4 },
        cg = { R.drawable.cg1, R.drawable.cg2, R.drawable.cg3, R.drawable.cg4 },
        cb = { R.drawable.cb1, R.drawable.cb2, R.drawable.cb3, R.drawable.cb4 },
        cy = { R.drawable.cy1, R.drawable.cy2, R.drawable.cy3, R.drawable.cy4 },

        tr = { R.drawable.tr1, R.drawable.tr2, R.drawable.tr3, R.drawable.tr4 },
        tg = { R.drawable.tg1, R.drawable.tg2, R.drawable.tg3, R.drawable.tg4 },
        tb = { R.drawable.tb1, R.drawable.tb2, R.drawable.tb3, R.drawable.tb4 },
        ty = { R.drawable.ty1, R.drawable.ty2, R.drawable.ty3, R.drawable.ty4 },

        pr = { R.drawable.pr1, R.drawable.pr2, R.drawable.pr3, R.drawable.pr4 },
        pg = { R.drawable.pg1, R.drawable.pg2, R.drawable.pg3, R.drawable.pg4 },
        pb = { R.drawable.pb1, R.drawable.pb2, R.drawable.pb3, R.drawable.pb4 },
        py = { R.drawable.py1, R.drawable.py2, R.drawable.py3, R.drawable.py4 },

        sr = { R.drawable.sr1, R.drawable.sr2, R.drawable.sr3, R.drawable.sr4 },
        sg = { R.drawable.sg1, R.drawable.sg2, R.drawable.sg3, R.drawable.sg4 },
        sb = { R.drawable.sb1, R.drawable.sb2, R.drawable.sb3, R.drawable.sb4 },
        sy = { R.drawable.sy1, R.drawable.sy2, R.drawable.sy3, R.drawable.sy4 };

    private int[][]
        cs = { cr, cg, cb, cy },
        ts = { tr, tg, tb, ty },
        ps = { pr, pg, pb, py },
        ss = { sr, sg, sb, sy };

    private int[][][] deck = { cs, ts, ps, ss };

    private int
        level = 0,
        maxLevel = 60;

    private void infoTest()
    {
        if (level == 0)
        {
            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.wisconsin_instruction));
            speak.speakFlush(getResources().getString(R.string.wisconsin_instruction));
            Button buttonStart = (Button) findViewById(R.id.play);
            buttonStart.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    speak.silence();
                    start();
                }
            });
        }
        else start();
    }

    private OnClickListener oclCard = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            disableButtons();
            feedback.setVisibility(View.INVISIBLE);
            int clickedS = 0, clickedN = 0, clickedC = 0;
            feedback = f0;
            if (v==img1)
            {
                clickedS = clickedN = clickedC = 1;
                feedback = f1;
            }
            else if (v==img2)
            {
                clickedS = clickedN = clickedC = 2;
                feedback = f2;
            }
            else if (v==img3)
            {
                clickedS = clickedN = clickedC = 3;
                feedback = f3;
            }
            boolean success = false;

            switch (currentSorting)
            {
                case kShape:
                {
                    success = iShape==clickedS;
                    if (success) {
                        cardChoosen = "Shape";
                    } else {
                        if ((clickedC == iColor) && (clickedN != iNumber) ) {
                            cardChoosen = "Color";
                        }else if ((clickedN == iNumber) && (clickedC != iColor)) {
                            cardChoosen = "Number";
                        } else  {
                            cardChoosen = "Number or Color";
                        }
                    }
                    break;
                }
                case kNum:
                {
                    success = iNumber==clickedN;
                    if (success) {
                        cardChoosen = "Number";
                    } else {
                        if ((clickedC == iColor) && (clickedS != iShape) ) {
                            cardChoosen = "Color";
                        }else if ((clickedS == iShape) && (clickedC != iColor) ) {
                            cardChoosen = "Shape";
                        } else {
                            cardChoosen = "Shape or Color";
                        }
                    }

                    break;
                }
                case kColor:
                {
                    success = iColor==clickedC;
                    if (success) {
                        cardChoosen = "Color";
                    } else {
                        if ((clickedN == iNumber) && (clickedS != iShape)) {
                            cardChoosen = "Number";
                        }
                        else if ((clickedS == iShape) && (clickedN != iNumber)) {
                            cardChoosen = "Shape";
                        }  else {
                            cardChoosen = "Number or Shape";
                        }
                    }
                    break;
                }
            }

            updateFeedback(success);
        }
    };

    private void updateFeedback(boolean success)
    {
        long ts = System.currentTimeMillis();
        int icon = R.drawable.red_cross;
        if (success)
        {
            good++;
            icon = R.drawable.green_tick;
            tones.ackBeep();
            isCorrectAnswer = "Yes";
        }
        else
        {
            if (forced) forcedErrors++;
            else totalErrors++;
            tones.nackBeep();
            isCorrectAnswer = "No";
        }

        double timeLevel = (ts - lastTime)/1000D;

        timeToAnswer = timeLevel;
        lastTime = ts;
        feedback.setImageResource(icon);
        feedback.setVisibility(View.VISIBLE);
        feedback.startAnimation(anim);
    }

    private void start()
    {
        setContentView(R.layout.wisconsin_cards);
        tvLevel = (TextView)findViewById(R.id.tvLevel);
        img0 = (ImageView)findViewById(R.id.imgStack0);
        img0.setOnClickListener(oclCard);
        img1 = (ImageView)findViewById(R.id.imgStack1);
        img1.setOnClickListener(oclCard);
        img2 = (ImageView)findViewById(R.id.imgStack2);
        img2.setOnClickListener(oclCard);
        img3 = (ImageView)findViewById(R.id.imgStack3);
        img3.setOnClickListener(oclCard);
        imgCard = (ImageView)findViewById(R.id.imgCard);
        f0 = (ImageView)findViewById(R.id.imgFeedback0);
        f1 = (ImageView)findViewById(R.id.imgFeedback1);
        f2 = (ImageView)findViewById(R.id.imgFeedback2);
        f3 = (ImageView)findViewById(R.id.imgFeedback3);
        feedback = f0;
        updateSorting();
        nextCard();
    }

    private void updateSorting() { currentSorting = rng.getIntInClosedRangeAvoiding(0, 2, currentSorting); }

    private void nextCard()
    {
        if (level !=0) {
            addNewResult();
            cardChoosen = "";
            timeToAnswer = 0;
        }

        level++;
        tvLevel.setText(getString(R.string.level) + ": " + level + "/" + maxLevel);

        if (level==maxLevel+1)
        {
            getStatistics (results);
            writeFile(test, header);
            finishTest();
        }
        else if (level%10==1) switchSorting();
        else dealCard();
        enableButtons();
    }

    private void getStatistics (ArrayList<String> results) {
        // get statistics
        double[] times = new double[results.size()];
        int correctColor = 0;
        int correctShape = 0;
        int correctNumber = 0;
        int errorsColor = 0;
        int errorsShape = 0;
        int errorsNumber = 0;
        int errorsColorShape = 0;
        int errorsColorNumber = 0;
        int errorsShapeNumber = 0;

        for (int i=0; i<results.size(); i++) {
            String[] lineSplit = results.get(i).split(", ");
            String rule = lineSplit[1];
            String chosen = lineSplit[2];
            String answer = lineSplit[3];
            times[i] = Double.parseDouble(lineSplit[4]);

            if (answer == "Yes") {
                if (rule == "Color") correctColor++;
                if (rule == "Shape") correctShape++;
                if (rule == "Number") correctNumber++;
            } else {
                if (chosen.startsWith("C") && chosen.endsWith("r")) errorsColor++;
                if (chosen.startsWith("S") && chosen.endsWith("e")) errorsShape++;
                if (chosen.length() == 6 && chosen.startsWith("N") && chosen.endsWith("r")) errorsNumber++;
                if (chosen.startsWith("N") && chosen.endsWith("e")) errorsShapeNumber++;
                if (chosen.length() == 15 && chosen.startsWith("N") && chosen.endsWith("r")) errorsColorNumber++;
                if (chosen.startsWith("S") && chosen.endsWith("r")) errorsColorShape++;
            }
        }

        Statistics stTimes = new Statistics(times);
        String meanTime = String.format(Locale.ENGLISH, "%.2f", stTimes.getMean());
        String maxTime = String.format(Locale.ENGLISH, "%.2f", stTimes.getMax());
        String minTime = String.format(Locale.ENGLISH, "%.2f", stTimes.getMin());

        //send observations
        sendObservations(correctColor, correctShape, correctNumber, errorsColor, errorsShape, errorsNumber,
                errorsColorShape, errorsColorNumber, errorsShapeNumber, Double.parseDouble(meanTime),
                Double.parseDouble(maxTime), Double.parseDouble(minTime));

    }

    public void sendObservations (int correctColor, int correctShape, int correctNumber,
                                  int errorsColor, int errorsShape, int errorsNumber,
                                  int errorsColorShape, int errorsColorNumber, int errorsShapeNumber,
                                  double meanTime, double maxTime, double minTime) {
        //Observations
        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();
            Observation obsWCSTCorrColor = new Observation (correctColor, patientCode, "PDTWCST_CORR_COLOR", time);
            obsWCSTCorrColor.PatientId = patientCode;
            Observation obsWCSTCorrShape = new Observation(correctShape, patientCode, "PDTWCST_CORR_SHAPE", time);
            obsWCSTCorrShape.PatientId = patientCode;
            Observation obsWCSTCorrNumber = new Observation(correctNumber, patientCode, "PDTWCST_CORR_NUMBER", time);
            obsWCSTCorrNumber.PatientId = patientCode;
            Observation obsWCSTErrorsColor = new Observation(errorsColor, patientCode, "PDTWCST_ERROR_COLOR", time);
            obsWCSTErrorsColor.PatientId = patientCode;
            Observation obsWCSTErrorsShape = new Observation (errorsShape, patientCode, "PDTWCST_ERROR_SHAPE", time);
            obsWCSTErrorsShape.PatientId = patientCode;
            Observation obsWCSTErrorsNumber = new Observation (errorsNumber, patientCode, "PDTWCST_ERROR_NUMBER", time);
            obsWCSTErrorsNumber.PatientId = patientCode;
            Observation obsWCSTErrorsColorShape = new Observation(errorsColorShape, patientCode, "PDTWCST_ERROR_COLORSHAPE", time);
            obsWCSTErrorsColorShape.PatientId = patientCode;
            Observation obsWCSTErrorsColorNumber = new Observation(errorsColorNumber, patientCode, "PDTWCST_ERROR_COLORNUMBER", time);
            obsWCSTErrorsColorNumber.PatientId = patientCode;
            Observation obsWCSTErrorsShapeNumber = new Observation(errorsShapeNumber, patientCode, "PDTWCST_ERROR_SHAPENUMBER", time);
            obsWCSTErrorsShapeNumber.PatientId = patientCode;
            Observation obsWCSTMean = new Observation(meanTime, patientCode, "PDTWCST_MEAN", time);
            obsWCSTMean.PatientId = patientCode;
            Observation obsWCSTMax = new Observation(maxTime, patientCode, "PDTWCST_MAX", time);
            obsWCSTMax.PatientId = patientCode;
            Observation obsWCSTMin = new Observation(minTime, patientCode, "PDTWCST_MIN", time);
            obsWCSTMin.PatientId = patientCode;

            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsWCSTCorrColor);
            observations.add(obsWCSTCorrShape);
            observations.add(obsWCSTCorrNumber);
            observations.add(obsWCSTErrorsColor);
            observations.add(obsWCSTErrorsShape);
            observations.add(obsWCSTErrorsNumber);
            observations.add(obsWCSTErrorsColorShape);
            observations.add(obsWCSTErrorsColorNumber);
            observations.add(obsWCSTErrorsShapeNumber);
            observations.add(obsWCSTMean);
            observations.add(obsWCSTMax);
            observations.add(obsWCSTMin);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }

    private void disableButtons()
    {
        img0.setClickable(false);
        img1.setClickable(false);
        img2.setClickable(false);
        img3.setClickable(false);
    }

    private void enableButtons()
    {
        img0.setClickable(true);
        img1.setClickable(true);
        img2.setClickable(true);
        img3.setClickable(true);
    }

    private void switchSorting()
    {
        forced = true;
        updateSorting();
        switch (currentSorting)
        {
            case kShape: { dealCardSwitchToShape(); break; }
            case kNum: { dealCardSwitchToNumber(); break; }
            case kColor: { dealCardSwitchToColor(); break; }
        }
    }

    private void dealCardSwitchToNumber()
    {
        int aux = rng.getIntInClosedRange(0, 3);
        iShape = iColor = aux;
        iNumber = rng.getIntInClosedRangeAvoiding(0, 3, iShape);
        int dealtCard = deck[iShape][iColor][iNumber];
        rule = "Number";
        imgCard.setImageResource(dealtCard);
    }

    private void dealCardSwitchToColor()
    {
        iColor = rng.getIntInClosedRange(0, 3);
        int aux = rng.getIntInClosedRangeAvoiding(0, 3, iColor);
        iShape = iNumber = aux;
        int dealtCard = deck[iShape][iColor][iNumber];
        rule = "Color";
        imgCard.setImageResource(dealtCard);
    }

    private void dealCardSwitchToShape()
    {
        iShape = rng.getIntInClosedRange(0, 3);
        int aux = rng.getIntInClosedRangeAvoiding(0, 3, iShape);
        iColor = iNumber = aux;
        int dealtCard = deck[iShape][iColor][iNumber];
        rule = "Shape";
        imgCard.setImageResource(dealtCard);
    }

    private void dealCard()
    {
        forced = false;
        iShape = rng.getIntInClosedRange(0, 3);
        iColor = rng.getIntInClosedRange(0, 3);
        int[] shapeAndColor = deck[iShape][iColor];
        iNumber = rng.getIntInClosedRangeAvoiding(0, 3, iShape);
        int dealtCard = shapeAndColor[iNumber];
        imgCard.setImageResource(dealtCard);
    }

    private void addNewResult () {
        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime()) ;

        String time = String.format(Locale.ENGLISH, "%.2f", timeToAnswer);

        resultInfo.append(date + ", ");
        resultInfo.append(rule + ", ");
        resultInfo.append(cardChoosen + ", ");
        resultInfo.append(isCorrectAnswer + ", ");
        resultInfo.append(time + "\r\n");

        results.add(String.valueOf(resultInfo));

    }

    private void getChoosenCard (int clicked) {

    }

    /*
    @Override
    protected void finishTest() {
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

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception finishing activity: " + e.toString());
        }
    }
    */

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        results = new ArrayList<String>();

        lastTime = System.currentTimeMillis();
        timeToAnswer = 0;
        isCorrectAnswer = "";
        rule = "";
        cardChoosen = "";
        rng = new RNG();
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
        anim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                feedback.setVisibility(View.INVISIBLE);
                nextCard();
            }
        });
    }

}