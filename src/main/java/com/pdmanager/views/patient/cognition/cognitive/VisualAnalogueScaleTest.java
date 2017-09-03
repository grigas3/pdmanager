package com.pdmanager.views.patient.cognition.cognitive;

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
import com.pdmanager.alerting.UserTaskCodes;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Cognitive Test Visual Analogue Scale
 *
 * @authors Jorge CANCELA (jcancela@lst.tfo.upm.es), Samanta VILLANUEVA (svillanueva@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */

    public class VisualAnalogueScaleTest extends SoundFeedbackActivity implements SeekBar.OnSeekBarChangeListener {

        public static final String FLAG = "flag";
    private final String LOGGER_TAG = "VAS_test";
    private final int TIME_MILLISECONDS_TASK = 5 * 60 * 1000;
    private final int QUESTIONS_NUMBER = 8;
        private Boolean isJustOnOff = null;
        private TextView tvYes, tvNo;
        private String
            test = "VisualAnalogueScale.csv",
            header = "Timestamp, "
                    + "Question 1 (%), "
                    + "Question 2 (%), "
                    + "Question 3 (%), "
                    + "Question 4 (%), "
                    + "Question 5 (%), "
                    + "Question 6 (%), "
                    + "Question 7 (%), "
                    + "Question 8 (%), "
                    + "\r\n";
        private int questionNumber = 0;

        private float minSize, midSize;

        private boolean isStarted = false;
        private boolean isPaused = false;


//        private CountDownTimer timerTask;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle b = getIntent().getExtras();
            if (b!=null) isJustOnOff = b.getBoolean(FLAG);
            startVAS();
        }

        private void startVAS() {

            // Introduction to the task
            if (!isStarted) {
                setContentView(R.layout.activity_start);

                TextView textViewToChange = (TextView) findViewById(R.id.level);
                textViewToChange.setText(getResources().getString(R.string.vas_instruction));
                speakFlush(getResources().getString(R.string.vas_instruction));

                Button buttonStart = (Button) findViewById(R.id.play);
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        speakerSilence();

                        isStarted = true;
                        setContentView(R.layout.cognitive_vas_test);
                        tvYes = (TextView)findViewById(R.id.seekbar_maxValue);
                        tvNo = (TextView)findViewById(R.id.seekbar_minValue);

                        midSize = tvYes.getTextSize();
                        minSize = midSize/2;

                        results = new ArrayList<String>();
                        questionNumber = 0;
                        if (isJustOnOff!=null)
                        {
                            if (!isJustOnOff) // Si F, hacemos todas las preguntas menos la primera
                            {
                                questionNumber = 1;
                                String vasScore = prefs.getVasScore();
                                prefs.clearVasScore();
                                String ts = getTimestamp();
                                results.add(ts);
                                results.add(", "+vasScore);
                            }
                        }
                        else
                        {
                            String ts = getTimestamp();
                            results.add(ts);
                        }
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

        private void getQuestion()
        {
            questionNumber++;
            if (questionNumber > QUESTIONS_NUMBER)
            {

                results.add("\r\n");
                sendObservations();
                finishTest();
            }
            else { setQuestion(); }
        }

        private void setQuestion()
        {
            TextView textQuestion = (TextView) findViewById(R.id.textView_vasQuestion);
            Button buttonNext = (Button) findViewById(R.id.buttonNext);
            final SeekBar seekbarAnswer = (SeekBar) findViewById(R.id.seekBar_vasAnswer);
            TextView seekbarMinValue = (TextView) findViewById(R.id.seekbar_minValue);
            TextView seekbarMaxValue = (TextView) findViewById(R.id.seekbar_maxValue);
            seekbarAnswer.setOnSeekBarChangeListener(this);

            Resources res = getResources();

            //set question and bounds text
            String qn = Integer.toString(questionNumber);
            String question = "vas_question" + qn;
            String minValue = "vas_minValue_Q" + qn;
            String maxValue = "vas_maxValue_Q" + qn;

            textQuestion.setText(res.getString(res.getIdentifier(question, "string", getPackageName())));
            final String minValueText = res.getString(res.getIdentifier(minValue, "string", getPackageName()));
            final String maxValueText = res.getString(res.getIdentifier(maxValue, "string", getPackageName()));
            seekbarMinValue.setText(minValueText);
            seekbarMaxValue.setText(maxValueText);

            final Long tsStart = System.currentTimeMillis();

            seekbarAnswer.setProgress(50);

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    int answerQuestion = seekbarAnswer.getProgress();
                    saveNewAnswer(answerQuestion);
                    if (isJustOnOff!=null && isJustOnOff) finish();
                    else getQuestion();
                }
            });
        }

        private String getTimestamp()
        {
            StringBuilder resultInfo = new StringBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String date = dateFormat.format(Calendar.getInstance().getTime()) ;
            return date;
        }

        private void saveNewAnswer(int answerValue)
        {
            if (isJustOnOff!=null && isJustOnOff) prefs.setVasScore(""+answerValue);
            else results.add(", "+answerValue);
        }



        @Override
        protected void finishTest()
        {
            String rtest = "";
            for (String i : results) rtest+=i;
            Log.e("Results", ""+rtest);

            try
            {

                writeFile (test, header);

                speakerSilence();

                //if (timerTask != null) { timerTask.cancel(); }

             /*   setContentView(R.layout.activity_end);

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
                buttonExit.setVisibility(View.GONE);
                */

            }catch (Exception e){
                Log.v(LOGGER_TAG, "Exception finishing activity: " + e.toString());
            }

            super.finishTest();
        }

        public void sendObservations () {
        //Observations

        try {
            RecordingSettings settings = new RecordingSettings(getApplicationContext());
            String patientCode = settings.getPatientID();
            String token = settings.getToken();

            DirectSender sender = new DirectSender(token);
            CommunicationManager mCommManager = new CommunicationManager(sender);
            Long time = Calendar.getInstance().getTimeInMillis();

            Observation obsVASQ1 = new Observation (Double.parseDouble(results.get(1).split(", ")[1]), patientCode, "PDTVAS_Q1", time);
            obsVASQ1.PatientId = patientCode;
            Observation obsVASQ2 = new Observation(Double.parseDouble(results.get(2).split(", ")[1]), patientCode, "PDTVAS_Q2", time);
            obsVASQ2.PatientId = patientCode;
            Observation obsVASQ3 = new Observation(Double.parseDouble(results.get(3).split(", ")[1]), patientCode, "PDTVAS_Q3", time);
            obsVASQ3.PatientId = patientCode;
            Observation obsVASQ4 = new Observation(Double.parseDouble(results.get(4).split(", ")[1]), patientCode, "PDTVAS_Q4", time);
            obsVASQ4.PatientId = patientCode;
            Observation obsVASQ5 = new Observation (Double.parseDouble(results.get(5).split(", ")[1]), patientCode, "PDTVAS_Q5", time);
            obsVASQ5.PatientId = patientCode;
            Observation obsVASQ6 = new Observation (Double.parseDouble(results.get(6).split(", ")[1]), patientCode, "PDTVAS_Q6", time);
            obsVASQ6.PatientId = patientCode;
            Observation obsVASQ7 = new Observation(Double.parseDouble(results.get(7).split(", ")[1]), patientCode, "PDTVAS_Q7", time);
            obsVASQ7.PatientId = patientCode;
            Observation obsVASQ8 = new Observation(Double.parseDouble(results.get(8).split(", ")[1]), patientCode, "PDTVAS_Q8", time);
            obsVASQ8.PatientId = patientCode;


            ArrayList<Observation> observations = new ArrayList<>();
            observations.add(obsVASQ1);
            observations.add(obsVASQ2);
            observations.add(obsVASQ3);
            observations.add(obsVASQ4);
            observations.add(obsVASQ5);
            observations.add(obsVASQ6);
            observations.add(obsVASQ7);
            observations.add(obsVASQ8);
            mCommManager.SendItems(observations, true);

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: " + e.toString());
        }
    }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            float
                pYes = (progress - 50)/2,
                pNo = (50 - progress)/2;

            if (pYes>=0) tvYes.setTextSize(midSize+pYes);
            else
            {
                if (midSize+pYes>minSize) tvYes.setTextSize(midSize+pYes);
                else tvYes.setTextSize(minSize);
            }
            if (pNo>=0) tvNo.setTextSize(midSize+pNo);
            else
            {
                if (midSize+pNo>minSize) tvNo.setTextSize(midSize+pNo);
                else tvNo.setTextSize(minSize);
            }

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

                speakerSilence();

                //if (timerTask != null) { timerTask.cancel(); }

                isStarted = false;
                isPaused = true;
                speakerSilence();

                isStarted = true;
                setContentView(R.layout.cognitive_vas_test);
                tvYes = (TextView)findViewById(R.id.seekbar_maxValue);
                tvNo = (TextView)findViewById(R.id.seekbar_minValue);

                midSize = tvYes.getTextSize();
                minSize = midSize/2;
                results = new ArrayList<String>();
                questionNumber = 0;
                getQuestion();
            }
        }

        @Override
        public void onPause() {
            super.onPause();

            speakerSilence();
            isPaused = true;
            //if (timerTask != null) { timerTask.cancel(); }
        }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.COGN + "_" + LOGGER_TAG;
    }
}
