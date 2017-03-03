package com.pdmanager.views.patient.cognition.fingertapping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.pdmanager.R;
import com.pdmanager.views.patient.cognition.MainMenu;
import com.pdmanager.views.patient.cognition.persistance.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 *
 * Finger Tapping Test last screen
 *
 * @authors Quentin DELEPIERRE, Jorge CANCELA (jcancela@lst.tfo.upm.es)
 * @copyright: LifeSTech
 * @license: GPL3
 */
public class FingerTappingEnd extends Activity {
    private String LOGGER_TAG = "LOGGER_TAG FingerTappingEnd:";

    private int tapsTestOne = 0;
    private Double meanTimeTestOne = new Double(0);
    private Double stdTimeTestOne = new Double(0);
    private Double medianTimeTestOne = new Double(0);
    private Double maxTimeTestOne = new Double(0);
    private Double minTimeTestOne = new Double(0);

    private int tapsTestTwo = 0;
    private int errorTapsTestTwo = 0;
    private Double meanTimeTestTwo = new Double(0);
    private Double stdTimeTestTwo = new Double(0);
    private Double medianTimeTestTwo = new Double(0);
    private Double maxTimeTestTwo = new Double(0);
    private Double minTimeTestTwo = new Double(0);


    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Intent intent = getIntent();

        try {

            tapsTestOne = intent.getIntExtra(FingerTappingTestOne.INTENT_TAPS_NUMBER_ONE, 0);
            meanTimeTestOne = intent.getDoubleExtra(FingerTappingTestOne.INTENT_MEAN_ONE, 0);
            stdTimeTestOne = intent.getDoubleExtra(FingerTappingTestOne.INTENT_STD_ONE, 0);
            medianTimeTestOne = intent.getDoubleExtra(FingerTappingTestOne.INTENT_MEDIAN_ONE, 0);
            maxTimeTestOne = intent.getDoubleExtra(FingerTappingTestOne.INTENT_MAX_ONE, 0);
            minTimeTestOne = intent.getDoubleExtra(FingerTappingTestOne.INTENT_MIN_ONE, 0);

            tapsTestTwo = intent.getIntExtra(FingerTappingTestTwo.INTENT_TAPS_NUMBER_TWO, 0);
            errorTapsTestTwo = intent.getIntExtra(FingerTappingTestTwo.INTENT_TAPS_ERROR_TWO, 0);
            meanTimeTestTwo = intent.getDoubleExtra(FingerTappingTestTwo.INTENT_MEAN_TWO, 0);
            stdTimeTestTwo = intent.getDoubleExtra(FingerTappingTestTwo.INTENT_STD_TWO, 0);
            medianTimeTestTwo = intent.getDoubleExtra(FingerTappingTestTwo.INTENT_MEDIAN_TWO, 0);
            maxTimeTestTwo = intent.getDoubleExtra(FingerTappingTestTwo.INTENT_MAX_TWO, 0);
            minTimeTestTwo = intent.getDoubleExtra(FingerTappingTestTwo.INTENT_MIN_TWO, 0);

        }catch (Exception e){
            Log.v(LOGGER_TAG, e.toString());
        }

        Preferences prefs = new Preferences(getApplicationContext());
        username = prefs.getUsername();
        if (username==null) username = "";

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PD_manager/"+username);

        if (!folder.exists()) {

            try{
                folder.mkdir();

            } catch (Exception e) {
                Log.v(LOGGER_TAG, "Exception creating folder: " + e.toString());
            }

        }

        File fileFTTSimple = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/PD_manager/" + username + "/FingerTappingSimpleTest.csv");

        File fileFTTAlternate = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/PD_manager/" + username + "/FingerTappingAlternateTest.csv");

        try {

            if (!fileFTTSimple.exists()) {
                fileFTTSimple.createNewFile();
                FileOutputStream fOut = new FileOutputStream(fileFTTSimple, true);
                OutputStreamWriter file = new OutputStreamWriter(fOut);

                String header = "Timestamp, total taps, mean time between taps (ms), " +
                        "STD time between taps (ms), median time between taps (ms)" +
                        "max. time between taps (ms), min. time between taps (ms)" + "\r\n";

                file.append(header);
                file.close();
            }

            FileOutputStream fOut = new FileOutputStream(fileFTTSimple, true);
            OutputStreamWriter file = new OutputStreamWriter(fOut);

            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String timeStamp = timeStampFormat.format(Calendar.getInstance().getTime());
            String meanTimeSimpleTest = String.format(Locale.ENGLISH, "%.2f", meanTimeTestOne);
            String stdTimeSimpleTest = String.format(Locale.ENGLISH, "%.2f", stdTimeTestOne);
            String medianTimeSimpleTest = String.format(Locale.ENGLISH, "%.2f", medianTimeTestOne);
            String maxTimeSimpleTest = String.format(Locale.ENGLISH, "%.2f", maxTimeTestOne);
            String minTimeSimpleTest = String.format(Locale.ENGLISH, "%.2f", minTimeTestOne);

            file.append(timeStamp + ", "
                    + String.valueOf(tapsTestOne) + ", "
                    + meanTimeSimpleTest + ", "
                    + stdTimeSimpleTest + ", "
                    + medianTimeSimpleTest + ", "
                    + maxTimeSimpleTest + ", "
                    + minTimeSimpleTest + " "
                    + "\r\n");

            file.close();

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception on: " + fileFTTSimple + " " + e.toString());
        }

        try {

            if (!fileFTTAlternate.exists()) {
                fileFTTAlternate.createNewFile();

                FileOutputStream fOut = new FileOutputStream(fileFTTAlternate, true);
                OutputStreamWriter file = new OutputStreamWriter(fOut);

                String header = "Timestamp, " +
                        "total taps, " +
                        "total error taps, " +
                        "mean time between taps (ms), " +
                        "STD time between taps (ms), " +
                        "median time between taps (ms), " +
                        "max time between taps (ms), " +
                        "min time between taps (ms)" + "\r\n";

                file.append(header);
                file.close();
            }

            FileOutputStream fOut = new FileOutputStream(fileFTTAlternate, true);
            OutputStreamWriter file = new OutputStreamWriter(fOut);

            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String timeStamp = timeStampFormat.format(Calendar.getInstance().getTime());
            String meanTimeAlternateTest = String.format(Locale.ENGLISH, "%.2f", meanTimeTestTwo);
            String stdTimeAlternateTest = String.format(Locale.ENGLISH, "%.2f", stdTimeTestTwo);
            String medianTimeAlternateTest = String.format(Locale.ENGLISH, "%.2f", medianTimeTestTwo);
            String maxTimeAlternateTest = String.format(Locale.ENGLISH, "%.2f", maxTimeTestTwo);
            String minTimeAlternateTest = String.format(Locale.ENGLISH, "%.2f", minTimeTestTwo);



            file.append(timeStamp + ", "
                    + String.valueOf(tapsTestTwo) + ", "
                    + String.valueOf(errorTapsTestTwo) + ", "
                    + meanTimeAlternateTest + ", "
                    + stdTimeAlternateTest + ", "
                    + medianTimeAlternateTest + ", "
                    + maxTimeAlternateTest + ", "
                    + minTimeAlternateTest + " "
                    + "\r\n");

            file.close();

        } catch (Exception e) {
            Log.v(LOGGER_TAG, "Exception: on: " + fileFTTAlternate + " " + e.toString());
        }

        Button buttonRepeat=(Button) findViewById(R.id.buttonFTTEndRepeat);
        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent menu1Intent = new Intent(FingerTappingEnd.this, MainMenu.class);
                //startActivity(menu1Intent);
                finish();
            }
        });

        Button buttonExit=(Button) findViewById(R.id.buttonFTTEndExit);
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
    }


}
