package com.pdmanager.views.patient.cognition.tools;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.pdmanager.R;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.persistance.Preferences;

public class SoundFeedbackActivity extends LoggingTestActivity
{
    protected Speak speak;
    protected Tones tones;
    protected Preferences prefs;

    @Override
    protected  void onPause()
    {
        super.onPause();
        if (getSettings().getUseSpeech()) {

            speak.silence();
            tones.shutdown();
        }
    }

    protected void speakerSilence() {

        if (getSettings().getUseSpeech()) {
            speak.silence();
        }

    }

    protected void speakFlush(String message) {
        if (getSettings().getUseSpeech()) {
            speak.speakFlush(message);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        speak = Speak.getInstance(getApplicationContext());
        tones = Tones.getInstance();
        prefs = new Preferences(getApplicationContext());


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    /**
     * Use this method to finish your test
     */
    protected void finishTest(){

        //TODO: Add Usage Statistics?
        setContentView(R.layout.fragment_thanks);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                finish();
            }
        }, 5000);
        //Finish Test mainly finish the activity



    }
    ///Private method for get settings
    protected RecordingSettings getSettings() {

        return new RecordingSettings(this);

    }

    protected String getPatientCode() {

        RecordingSettings settings = getSettings();

        return settings.getPatientID();

    }

    protected String getAccessToken() {

        RecordingSettings settings =  getSettings();

        if (settings != null) {
            return settings.getToken();


        }

        return null;
    }

    @Override
    public void onBackPressed() {
    }


}
