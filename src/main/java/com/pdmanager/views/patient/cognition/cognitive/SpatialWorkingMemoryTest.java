package com.pdmanager.views.patient.cognition.cognitive;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
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
 * Cognitive Test Spatial Working Memory
 *
 * @authors: Thibaud Pacquetet, Samanta VILLANUEVA (svillanueva@lst.tfo.upm.es), Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

public class SpatialWorkingMemoryTest extends SoundFeedbackActivity {

    private final String LOGGER_TAG = "SWM test";
    private String
        test = "SWM_Results.csv",
        header = "Timestamp, " +
                "Level, " +
                "Number of blue tokens, " +
                "Number of empty boxes opened, " +
                "Number of empty boxes reopened, " +
                "Number of blue token boxes reopened, " +
                "Mean time between taps (s) , " +
                "Time between taps STD (s) , " +
                "Mean time between blue token (s), " +
                "Time between blue token STD (s), " +
                "Total time (s) " +
                "\r\n";

    private final int TIME_MILLISECONDS_TRANSITIONS = 700;
    private final int TIME_MILLISECONDS_TICK = 350;
    private final int TIME_MILLISECONDS_TASK = 480000;
    private final int NUMBER_OF_LEVELS = 4;
    private final int NUMBER_OF_BOXES = 8;
    private final int START_NUMBER_OF_TOKENS = 3;
    private final int maxErrors = 3;
    private int totalErrorCount = 0;

    private int level;
    private int nTokensFound;
    private int nTokens;

    private int nEmptyBoxesOpened;
    private int nEmptyBoxesReOpened;
    private int nTokenBoxesReOpened;

    private int[] boxArray={R.id.imageButton1,R.id.imageButton2,R.id.imageButton3,R.id.imageButton4,R.id.imageButton5,R.id.imageButton6,R.id.imageButton7,R.id.imageButton8,R.id.imageButton9,R.id.imageButton10,R.id.imageButton11,R.id.imageButton12,R.id.imageButton13,R.id.imageButton14,R.id.imageButton15,R.id.imageButton16};
    private int[] boxBisArray={R.id.imageButton1bis,R.id.imageButton2bis,R.id.imageButton3bis,R.id.imageButton4bis,R.id.imageButton5bis,R.id.imageButton6bis,R.id.imageButton7bis,R.id.imageButton8bis,R.id.imageButton9bis,R.id.imageButton10bis,R.id.imageButton11bis,R.id.imageButton12bis,R.id.imageButton13bis,R.id.imageButton14bis,R.id.imageButton15bis,R.id.imageButton16bis};
    private int[] scoreBoxArray={R.id.box1,R.id.box2,R.id.box3,R.id.box4,R.id.box5,R.id.box6,R.id.box7,R.id.box8,R.id.box0};

    private int[] boxes = new int[NUMBER_OF_BOXES];
    private int[] bisBoxes = new int[NUMBER_OF_BOXES];
    private int[] foundTokensID = new int[NUMBER_OF_BOXES];
    private int[] repeatEmptyBoxID = new int[NUMBER_OF_BOXES];

    private ArrayList<Double> timeBetweenTaps;
    private ArrayList<Double> timeBetweenTokens;

    private double startTimeLevel;

    private CountDownTimer timerTask;
    private CountDownTimer timer;

    private boolean isStarted = false;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startSWM();
    }

    private void startSWM() {
        if (!isStarted) {
            setContentView(R.layout.activity_start);
            TextView textViewToChange = (TextView) findViewById(R.id.level);
            textViewToChange.setText(getResources().getString(R.string.swm_instruction));
            speak.speakFlush(getResources().getString(R.string.swm_instruction));
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
                    results = new ArrayList<String>();
                    level = 0;
                    nTokens = START_NUMBER_OF_TOKENS;

                    setNewLevel();
                }
            });
        } else {
            setNewLevel();
        }

    }

    private void setNewLevel(){

        foundTokensID = new int[NUMBER_OF_BOXES];
        repeatEmptyBoxID = new int[NUMBER_OF_BOXES];

        if (level !=0) {
            writeFile(test, header);
        }

        results.clear();
        level++;

        if (level > NUMBER_OF_LEVELS){

            if (timerTask != null) {
                timerTask.cancel();
            }
            finishTest();
        }
        else {

            nEmptyBoxesOpened = 0;
            nEmptyBoxesReOpened = 0;
            nTokenBoxesReOpened = 0;
            timeBetweenTaps = new ArrayList<>();
            timeBetweenTokens = new ArrayList<>();

            startTimeLevel = System.currentTimeMillis();

            setContentView(R.layout.cognitive_swm_test);
            TextView textViewToChange = (TextView) findViewById(R.id.swm_info);
            textViewToChange.setText(getResources().getString(R.string.swm_levels) + " " + (level));

            setRandomBoxes();
            nTokensFound = 0;

            for (int u = 0; u < nTokens; u++) {
                ImageView imgScore = (ImageView) findViewById(scoreBoxArray[u]);
                imgScore.setImageResource(R.drawable.black_square);
            }

            startLevel();
        }
    }

    private void setRandomBoxes(){

        ArrayList<Integer> emptyBoxesIndex = new ArrayList<>();    //create list with numbers between from 0 to 15
        for(int u=0;u<boxArray.length;u++){
            emptyBoxesIndex.add(u);
        }

        ArrayList<Integer> squareBoxesIndex = new ArrayList<>();    //create list with numbers between from 0 to nEmptyBoxesShowed
        for(int u=0;u<NUMBER_OF_BOXES;u++){
            squareBoxesIndex.add(u);
        }

        Collections.shuffle(emptyBoxesIndex);  // shuffle the numbers in the list
        Collections.shuffle(squareBoxesIndex);  // shuffle the numbers in the list

        ImageButton imageButton;

        for (int i=0;i<boxArray.length;i++){

            imageButton = (ImageButton) findViewById(boxArray[i]);
            imageButton.setVisibility(View.INVISIBLE);
            imageButton = (ImageButton) findViewById(boxBisArray[i]);
            imageButton.setVisibility(View.INVISIBLE);
        }

        for (int i=0;i<NUMBER_OF_BOXES;i++){
            int index = emptyBoxesIndex.get(i);
            imageButton = (ImageButton) findViewById(boxArray[index]);
            boxes[i]=boxArray[index];
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setBackgroundResource(R.drawable.empty_square_swm);
            imageButton = (ImageButton) findViewById(boxBisArray[index]);
            bisBoxes[i]=boxBisArray[index];
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    private void startLevel()
    {

        repeatEmptyBoxID = new int[NUMBER_OF_BOXES];
        final ImageButton[] boxesButton = new ImageButton[NUMBER_OF_BOXES];
        for (int i=0; i<NUMBER_OF_BOXES; i++) {
            ImageButton btn = (ImageButton) findViewById(bisBoxes[i]);
            boxesButton[i] = btn;
        }

        final ImageButton squarePlace = (ImageButton) findViewById(boxes[nTokensFound]);
        squarePlace.setBackgroundResource(R.drawable.filled_square_swm);

        final ImageButton squareBisPlace = (ImageButton) findViewById(bisBoxes[nTokensFound]);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            double tapTime = (System.currentTimeMillis() - startTimeLevel)/1000D;
            timeBetweenTaps.add(tapTime);

                if (v.equals(squareBisPlace)) { //token

                    double tokenTime = (System.currentTimeMillis() - startTimeLevel)/1000D;
                    timeBetweenTokens.add(tokenTime);

                    for (int temp=0;temp<NUMBER_OF_BOXES;temp++){
                        boxesButton[temp].setClickable(false);
                    }

                    foundTokensID[nTokensFound] = v.getId();
                    squareBisPlace.setVisibility(View.INVISIBLE);

                    final Button boxScoreListener = (Button) findViewById(scoreBoxArray[8]);


                    View.OnClickListener clickOnBar = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (v.equals(boxScoreListener)) {
                                ImageView boxScore = (ImageView)findViewById(scoreBoxArray[nTokensFound]);
                                boxScore.setImageResource(R.drawable.blue_square);
                                squareBisPlace.setVisibility(View.VISIBLE);
                                squarePlace.setBackgroundResource(R.drawable.empty_square_swm);

                                nTokensFound++;

                                if (nTokensFound == nTokens){

                                    double tsLevel = (System.currentTimeMillis() - startTimeLevel)/1000D;
                                    double[] timeTaps = new double[timeBetweenTaps.size()];

                                    for (int i =0; i<timeBetweenTaps.size(); i++) {
                                        timeTaps[i] = timeBetweenTaps.get(i);
                                    }
                                    double[] timeTokens = new double[timeBetweenTokens.size()];
                                    for (int i =0; i<timeBetweenTokens.size(); i++) {
                                        timeTokens[i] = timeBetweenTokens.get(i);
                                    }

                                    Statistics stTaps = new Statistics(timeTaps);
                                    Statistics stTokens = new Statistics(timeTokens);

                                    addNewResult(tsLevel, stTaps.getMean(), stTaps.getStdDev(), stTokens.getMean(), stTokens.getStdDev());
                                    boxScoreListener.setOnClickListener(null);

                                    nTokens++;

                                    setNewLevel();
                                }
                                else{
                                    boxScoreListener.setOnClickListener(null);
                                    startLevel();
                                }
                            }
                        }
                    };

                    boxScoreListener.setOnClickListener(clickOnBar);

                } else { // empty square

                    for (int index = 0; index<boxesButton.length; index++) {

                        final ImageButton btn = boxesButton[index];

                        if(v.equals(btn)&& (squareBisPlace!=btn)){

                            boolean isTokenReopen = false;
                            boolean isEmptyBoxReopen = false;

                            for (int i= 0; i<nTokensFound; i++) {
                                if (v.getId() == foundTokensID[i]) {
                                    isTokenReopen = true;
                                }
                            }

                            for (int j= 0; j<repeatEmptyBoxID.length; j++) {
                                if (v.getId() == repeatEmptyBoxID[j] && !isTokenReopen) {
                                    isEmptyBoxReopen = true;
                                }
                            }

                            if (isTokenReopen) {
                                nTokenBoxesReOpened++;
                            } else {
                                if (isEmptyBoxReopen) {
                                    nEmptyBoxesReOpened++;
                                } else {
                                    nEmptyBoxesOpened++;
                                }
                            }

                            boolean isRepeated = false;
                            for (int i= 0; i<repeatEmptyBoxID.length; i++) {
                                if (repeatEmptyBoxID[i]==v.getId()) {
                                    isRepeated = true;
                                }
                            }
                            if (!isRepeated) {
                                repeatEmptyBoxID[index] = v.getId();
                            }

                            int totalErrors = nTokenBoxesReOpened + nEmptyBoxesReOpened;
                            if (totalErrors>=maxErrors)
                            {
                                totalErrorCount++;
                                if (totalErrorCount>=maxErrors) finishTest();
                                else setNewLevel();
                            }

                            timer = new CountDownTimer(TIME_MILLISECONDS_TRANSITIONS, TIME_MILLISECONDS_TICK) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    for (int temp=0;temp<NUMBER_OF_BOXES;temp++){
                                        boxesButton[temp].setClickable(false);
                                    }
                                    btn.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onFinish() {

                                    btn.setVisibility(View.VISIBLE);
                                    for (int temp=0;temp<NUMBER_OF_BOXES;temp++){
                                        boxesButton[temp].setClickable(true);
                                    }
                                }
                            }.start();
                        }
                    }
                }
            }

        };

        squareBisPlace.setOnClickListener(listener);

        for (int temp=0;temp<NUMBER_OF_BOXES;temp++){
            boxesButton[temp].setOnClickListener(listener);
        }

    }

    private void addNewResult (double tsLevel, double meanTimeBetweenTaps, double stdTimeBetweenTaps, double meanTimeBetweenTokens, double stdTimeBetweenTokens) {

        StringBuilder resultInfo = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        String date = dateFormat.format(Calendar.getInstance().getTime()) ;

        String meanTimeTaps = String.format(Locale.ENGLISH, "%.2f", meanTimeBetweenTaps);
        String stdTimeTaps = String.format(Locale.ENGLISH, "%.2f", stdTimeBetweenTaps);
        String meanTimeTokens = String.format(Locale.ENGLISH, "%.2f", meanTimeBetweenTokens);
        String stdTimeTokens = String.format(Locale.ENGLISH, "%.2f", stdTimeBetweenTokens);
        String ts = String.format(Locale.ENGLISH, "%.2f", tsLevel);

        resultInfo.append(date + ", ");
        resultInfo.append(String.valueOf(level) + ", ");
        resultInfo.append(String.valueOf(nTokens) + ", ");
        resultInfo.append(String.valueOf(nEmptyBoxesOpened) + ", ");
        resultInfo.append(String.valueOf(nEmptyBoxesReOpened) + ", ");
        resultInfo.append(String.valueOf(nTokenBoxesReOpened) + ", ");
        resultInfo.append(meanTimeTaps + ", ");
        resultInfo.append(stdTimeTaps + ", ");
        resultInfo.append(meanTimeTokens + ", ");
        resultInfo.append(stdTimeTokens + ", ");
        resultInfo.append(ts + "\r\n");

        results.add(String.valueOf(resultInfo));
    }
@Override
    protected void finishTest(){

        try {

            writeFile (test, header);

            speak.silence();

            if (timerTask != null) {
                timerTask.cancel();
            }
            if (timer != null) {
                timer.cancel();
            }

            /*setContentView(R.layout.activity_end);

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
            results = new ArrayList<String>();
            level = 0;

            nTokens = START_NUMBER_OF_TOKENS;
            setNewLevel();
            isPaused = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        speak.silence();

        if (timerTask != null) {
            timerTask.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        isPaused = true;
    }
}