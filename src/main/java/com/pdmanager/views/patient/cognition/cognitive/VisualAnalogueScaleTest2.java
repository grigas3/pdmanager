package com.pdmanager.views.patient.cognition.cognitive;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.views.patient.cognition.MainMenu;
import com.pdmanager.views.patient.cognition.tools.CustomSeekBar;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * Cognitive Test Visual Analogue Scale
 *
 * @authors Jorge CANCELA (jcancela@lst.tfo.upm.es), Samanta VILLANUEVA (svillanueva@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

    public class VisualAnalogueScaleTest2 extends SoundFeedbackActivity implements SeekBar.OnSeekBarChangeListener {

        private TextView tv0, tv1, tv2, tv3, tv4;
        private CustomSeekBar seekbarAnswer;
        private final String LOGGER_TAG = "LOGGER_TAG: VAS test";
        private String
                test = "VAS_Results.csv",
                header = "Timestamp, " +
                        "Question number, " +
                        "Min value - Max Value, " +
                        "Answer (%) " +
                        "Time (s), " +
                        "\r\n";

        private final int TIME_MILLISECONDS_TASK = 5 * 60 * 1000;
        private final int QUESTIONS_NUMBER = 8;

        private int questionNumber = 0;

        private boolean isStarted = false;
        private boolean isPaused = false;

        private ArrayList<String> results;

        //private CountDownTimer timerTask;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            startVAS();
        }

        private void startVAS() {

            // Introduction to the task
            if (!isStarted) {
                setContentView(R.layout.activity_start);

                TextView textViewToChange = (TextView) findViewById(R.id.level);
                textViewToChange.setText(getResources().getString(R.string.vas_instruction));
                speak.speakFlush(getResources().getString(R.string.vas_instruction));

                Button buttonStart = (Button) findViewById(R.id.play);
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        speak.silence();
                        /*
                        timerTask = new CountDownTimer(TIME_MILLISECONDS_TASK, TIME_MILLISECONDS_TASK)
                        {
                            @Override public void onTick(long millisUntilFinished) {}
                            @Override public void onFinish() {
                                finishTest();
                            }
                        }.start();
                        */

                        isStarted = true;
                        setContentView(R.layout.cognitive_vas_test_agreement);
                        startGUI();

                        results = new ArrayList<String>();
                        questionNumber = 0;
                        getQuestion();
                    }
                });
            }else{
                getQuestion();
            }
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

    private void startGUI()
    {

        final View.OnClickListener oclSelection = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final int nSteps = 4;
                int
                    half = seekbarAnswer.getMax()/2,
                    step = seekbarAnswer.getMax()/nSteps;
                if (v==tv0) seekbarAnswer.setProgress(0);
                else if (v==tv1) seekbarAnswer.setProgress(step);
                else if (v==tv2) seekbarAnswer.setProgress(half);
                else if (v==tv3) seekbarAnswer.setProgress(3*step);
                else if (v==tv4) seekbarAnswer.setProgress(seekbarAnswer.getMax());
            }
        };

        tv0 = (TextView)findViewById(R.id.seekbar_minValue);
        tv0.setOnClickListener(oclSelection);
        tv1 = (TextView)findViewById(R.id.seekbar_minMidValue);
        tv1.setOnClickListener(oclSelection);
        tv2 = (TextView)findViewById(R.id.seekbar_midValue);
        tv2.setOnClickListener(oclSelection);
        tv3 = (TextView)findViewById(R.id.seekbar_maxMidValue);
        tv3.setOnClickListener(oclSelection);
        tv4 = (TextView)findViewById(R.id.seekbar_maxValue);
        tv4.setOnClickListener(oclSelection);

        /*
        iv0 = (ImageView)findViewById(R.id.ag0);
        iv0.setOnClickListener(oclSelection);
        iv1 = (ImageView)findViewById(R.id.ag1);
        iv0.setOnClickListener(oclSelection);
        iv2 = (ImageView)findViewById(R.id.ag2);
        iv0.setOnClickListener(oclSelection);
        iv3 = (ImageView)findViewById(R.id.ag3);
        iv0.setOnClickListener(oclSelection);
        iv4 = (ImageView)findViewById(R.id.ag4);
        iv0.setOnClickListener(oclSelection);
        */
    }

        private void getQuestion() {

            if (questionNumber != 0) {  writeFile(test, header); }

            results.clear();
            questionNumber++;

            if (questionNumber > QUESTIONS_NUMBER)
            {
                //if (timerTask != null) { timerTask.cancel(); }
                finishTest();
            }
            else { setQuestion(); }
        }

        private void setQuestion()
        {
            TextView textQuestion = (TextView) findViewById(R.id.textView_vasQuestion);

            Button buttonNext = (Button) findViewById(R.id.buttonNext);
            seekbarAnswer = (CustomSeekBar)findViewById(R.id.seekBar_vasAnswer);
            seekbarAnswer.setOnSeekBarChangeListener(this);

            Resources res = getResources();

            //set question and bounds text
            String question = "vas_question" + Integer.toString(questionNumber);

            textQuestion.setText(res.getString(res.getIdentifier(question, "string", getPackageName())));
            final String minValueText = res.getString(R.string.vas2_s0);
            final String maxValueText = res.getString(R.string.vas2_s4);

            final Long tsStart = System.currentTimeMillis();

            seekbarAnswer.setProgress(50);

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Long tsEnd = System.currentTimeMillis();
                    Double ts = ((tsEnd - tsStart)/1000D);
                    int answerQuestion = seekbarAnswer.getProgress();
                    saveNewAnswer(questionNumber, ts, minValueText, maxValueText, answerQuestion);
                    getQuestion();
                }
            });
        }

        private void saveNewAnswer(int number, double ts, String minValue, String maxValue, int answer) {
            StringBuilder resultInfo = new StringBuilder();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String date = dateFormat.format(Calendar.getInstance().getTime()) ;

            String time = String.format(Locale.ENGLISH, "%.2f", ts);

            resultInfo.append(date + ", ");
            resultInfo.append(String.valueOf(number) + ", ");
            resultInfo.append(minValue + " - " + maxValue + ", ");
            resultInfo.append(String.valueOf(answer) + ", ");
            resultInfo.append(time + "\r\n");

            results.add(String.valueOf(resultInfo));
        }

        private void finishTest(){

            try {
                writeFile (test, header);

                speak.silence();

                //if (timerTask != null) { timerTask.cancel(); }

                setContentView(R.layout.activity_end);

                Button buttonRepeat=(Button) findViewById(R.id.buttonFTTEndRepeat);
                buttonRepeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Intent cognitiveMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
                        //startActivity(cognitiveMenuIntent);
                        finish();
                    }
                });

                Button buttonExit=(Button) findViewById(R.id.buttonFTTEndExit);
                buttonExit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent MainMenuIntent = new Intent(getApplicationContext(), MainMenu.class);
                        MainMenuIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainMenuIntent.putExtra("EXIT", true);
                        startActivity(MainMenuIntent);
                        finish();
                    }
                });

            }catch (Exception e){
                Log.v(LOGGER_TAG, "Exception finishing activity: " + e.toString());
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            float
                pYes = (progress - 50)/2,
                pNo = (50 - progress)/2;

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onResume() {
            super.onResume();

            if (isPaused) {

                speak.silence();

                //if (timerTask != null) { timerTask.cancel(); }

                isStarted = false;
                isPaused = true;
                speak.silence();

                /*
                        timerTask = new CountDownTimer(TIME_MILLISECONDS_TASK, TIME_MILLISECONDS_TASK)
                        {
                            @Override public void onTick(long millisUntilFinished) {}
                            @Override public void onFinish() {
                                finishTest();
                            }
                        }.start();
                */

                isStarted = true;
                setContentView(R.layout.cognitive_vas_test_agreement);
                startGUI();

                results = new ArrayList<String>();
                questionNumber = 0;
                getQuestion();
            }
        }

        @Override
        public void onPause() {
            super.onPause();

            speak.silence();
            isPaused = true;
            //if (timerTask != null) { timerTask.cancel(); }
        }
}
