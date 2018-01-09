package com.pdmanager.communication;

import android.os.Environment;
import android.text.format.Time;

import com.pdmanager.interfaces.IJsonRequestHandler;
import com.squareup.tape.FileObjectQueue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by george on 11/1/2016.
 */
public class CommunicationQueue extends FileObjectQueue<JsonStorage> implements IJsonRequestHandler, ICommunicationQueue

{


    public CommunicationQueue(File file, Converter<JsonStorage> converter) throws IOException {
        super(file, converter);


    }


    public static File CreateQueueFile() {


        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        Calendar c = Calendar.getInstance();

        StringBuilder hrsb = new StringBuilder();
        hrsb.append("commqueue");
        hrsb.append(String.format("%04d", today.year));                // Year)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.monthDay));          // Day of the month (1-31)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.month + 1));              // Month (0-11))
        hrsb.append("_");
        hrsb.append(today.format("%k:%M:%S"));      // Current time
        hrsb.append(".txt");                      //Completed file name


        String newFileName = hrsb.toString();
        newFileName = newFileName.replaceAll(":", "_");


        //get the path to sdcard
        File pathToExternalStorage = Environment.getExternalStorageDirectory();
        //to this path add a new directory path and create new App dir (InstroList) in /documents Dir

        long unixTime = System.currentTimeMillis() / 1000L;

        File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/PD_Manager/");
        // have the object build the directory structure, if needed.
        appDirectory.mkdirs();

        File saveFilePath = new File(appDirectory, newFileName);

        return saveFilePath;

    }

    @Override
    public void addRequest(JsonStorage jsonRequest) {


        super.add(jsonRequest);
    }

    @Override
    public boolean push(JsonStorage s) {
        try {
            super.add(s);
            return true;
        } catch (Exception e) {

            return false;
        }


    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public ArrayList<JsonStorage> getLastN(int n) {
        return null;
    }

    @Override
    public JsonStorage poll() {
        JsonStorage res = super.peek();
        super.remove();
        return res;
    }
}
