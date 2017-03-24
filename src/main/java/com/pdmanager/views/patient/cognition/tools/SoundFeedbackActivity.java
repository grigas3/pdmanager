package com.pdmanager.views.patient.cognition.tools;

import android.os.Bundle;

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
        speak.silence();
        tones.shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        speak = Speak.getInstance(getApplicationContext());
        tones = Tones.getInstance();
        prefs = new Preferences(getApplicationContext());
    }

    /**
     * Use this method to finish your test
     */
    protected void finishTest(){

        //TODO: Add Usage Statistics?

        //Finish Test mainly finish the activity
        finish();


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


}
