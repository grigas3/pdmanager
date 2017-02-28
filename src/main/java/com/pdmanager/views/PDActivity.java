package com.pdmanager.views;

import android.app.Activity;

import com.pdmanager.settings.RecordingSettings;

/**
 * Created by George on 6/18/2016.
 */
public abstract class PDActivity extends Activity {


    boolean isLoggedIn() {


        RecordingSettings settings = new RecordingSettings(this);
        if (settings != null) {

            return settings.getLoggedIn();

        }

        return false;

    }
}
