package com.pdmanager.views.patient.cognition.tools;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import com.pdmanager.views.patient.cognition.persistance.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class LoggingTestActivity extends Activity {
    protected ArrayList<String> results;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences prefs = new Preferences(getApplicationContext());
        username = prefs.getUsername();
        if (username == null) username = "";
        results = new ArrayList<String>();
    }

    protected void writeFile(String test, String header) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PD_manager/" + username);
            if (!folder.exists()) {
                try {
                    folder.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PD_manager/" + username + "/" + test);

            if (!file.exists()) {
                file.createNewFile();

                FileOutputStream fOut = new FileOutputStream(file, true);
                OutputStreamWriter outputStream = new OutputStreamWriter(fOut);

                outputStream.append(header);
                outputStream.close();
            }

            FileOutputStream fOut = new FileOutputStream(file, true);
            OutputStreamWriter outputStream = new OutputStreamWriter(fOut);
            for (String i : results) {
                outputStream.append(String.valueOf(i));
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
