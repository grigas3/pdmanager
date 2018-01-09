package com.pdmanager.monitoring;

import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.logging.ILogHandler;
import com.pdmanager.models.Observation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by george on 25/11/2016.
 */

public class MSHealthDataHandler {
    CommunicationManager mCommManager;
    private String patientIdentifier;
    private ILogHandler mLogHandler = null;

    public MSHealthDataHandler(String accessToken, String pid, ILogHandler logHandler) {

        patientIdentifier = pid;
        DirectSender sender = new DirectSender(accessToken);
        mCommManager = new CommunicationManager(sender);
        mLogHandler = logHandler;

    }

    public MSHealthDataHandler(String accessToken, String pid) {

        patientIdentifier = pid;
        DirectSender sender = new DirectSender(accessToken);
        mCommManager = new CommunicationManager(sender);


    }

    private double parseDuration(Map data, String key) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (data.containsKey(key)) {

            String dur = (String) data.get(key);

            String ad = dur.substring(2);
            StringBuilder str = new StringBuilder();

            for (int i = 0; i < ad.length(); i++) {

                if (!Character.isDigit(ad.charAt(i))) {
                    if (ad.charAt(i) == 'H') {

                        hours = Integer.parseInt(str.toString());

                        str.setLength(0);
                    }

                    if (ad.charAt(i) == 'M') {

                        minutes = Integer.parseInt(str.toString());
                        str.setLength(0);

                    }

                    if (ad.charAt(i) == 'S') {

                        seconds = Integer.parseInt(str.toString());

                        str.setLength(0);
                    }

                } else {

                    str.append(ad.charAt(i));
                }


            }

        }

        return hours * 60 * 60 + minutes * 60 + seconds;

    }

    private double parseDouble(Map data, String key) {

        if (data.containsKey(key)) {
            return (Double) data.get(key);
            //  return Double.parseDouble((Double)data.get(key));

        }


        return 0;

    }

    private Date parseDate(Map data, String key) {
        Date d = new Date();
        if (data.containsKey(key)) {


            try {
                d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) data.get(key));

            } catch (ParseException e) {


            }

        }
        return d;


    }

    private void handleSleep(ArrayList<Observation> tmpObservations, ArrayList<Map> sleepData) {

        for (Map s : sleepData
                ) {

            Date d = parseDate(s, "dayId");
            double totalRestfulSleepDuration = parseDuration(s, "totalRestfulSleepDuration");
            tmpObservations.add(new Observation(totalRestfulSleepDuration, "SLPTRESFULL", d.getTime(), patientIdentifier));

            double totalRestlesSleepDuration = parseDuration(s, "totalRestlessSleepDuration");
            tmpObservations.add(new Observation(totalRestlesSleepDuration, "SLPRESTLESS", d.getTime(), patientIdentifier));

            double sleepDuration = parseDuration(s, "sleepDuration");
            tmpObservations.add(new Observation(sleepDuration, "SLPDUR", d.getTime(), patientIdentifier));

            double awakeDuration = parseDuration(s, "awakeDuration");
            tmpObservations.add(new Observation(awakeDuration, "SLPAWAKE", d.getTime(), patientIdentifier));

            double fallAsleepDuration = parseDuration(s, "fallAsleepDuration");
            tmpObservations.add(new Observation(fallAsleepDuration, "SLPFASDUR", d.getTime(), patientIdentifier));

            //   double totalsleepDuration=parseDuration(s,"duration");
            //  tmpObservations.add(new Observation(totalsleepDuration, "SLPNWUPS",d.getTime(), patientIdentifier));

            double numberOfWakeups = parseDouble(s, "numberOfWakeups");
            tmpObservations.add(new Observation(numberOfWakeups, "SLPNWUPS", d.getTime(), patientIdentifier));

            double restingHeartRate = parseDouble(s, "restingHeartRate");
            tmpObservations.add(new Observation(restingHeartRate, "SLPRESTHR", d.getTime(), patientIdentifier));


        }


    }

    private void handleActivity(ArrayList<Observation> tmpObservations, ArrayList<Map> activityData) {


        for (Map s : activityData
                ) {
            Date d = parseDate(s, "dayId");
            double totalDuration = parseDuration(s, "duration");
            tmpObservations.add(new Observation(totalDuration, "ACTWDUR", d.getTime(), patientIdentifier));
            if (s.containsKey("distanceSummary")) {
                Map s1 = (Map) s.get("distanceSummary");
                double totalDistance = parseDouble(s1, "totalDistance");
                tmpObservations.add(new Observation(totalDistance, "ACTWDIST", d.getTime(), patientIdentifier));

            }


        }
    }

    public void ClearObservations() {

        String codesToDelete = "TRESFULLSD;TRESLESSSD;SLEEPDUR;AWAKEDUR;FASLEEPDUR;TSLEEPDUR;NWAKEUPS;SRESTHR;RUNDUR;RUNDIST";
        //   ArrayList<String> codesToClear = new ArrayList<String>(){ "TRESFULLSD","RUNDUR","RUNDIST","SRESTHR","NWAKEUPS"};

    }

    public void handleData(Map result) {

        try {
            ArrayList<Observation> tmpObservations = new ArrayList<Observation>();
            double value;


            if (result.containsKey("runActivities")) {

                ArrayList<Map> runActivities = (ArrayList<Map>) result.get("runActivities");
                handleActivity(tmpObservations, runActivities);
            }
            if (result.containsKey("sleepActivities")) {
                ArrayList<Map> sleepActivities = (ArrayList<Map>) result.get("sleepActivities");

                handleSleep(tmpObservations, sleepActivities);
            }

            if (mCommManager != null)
                mCommManager.SendItems(tmpObservations);
        } catch (Exception e) {

            if (mLogHandler != null)
                mLogHandler.ProcessLog("ERROR", e.getMessage());

        }


    }


}
