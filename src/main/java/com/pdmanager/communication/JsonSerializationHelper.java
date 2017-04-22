package com.pdmanager.communication;

import android.os.Build;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.pdmanager.models.PDEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

import static com.pdmanager.BuildConfig.DEBUG;

/**
 * Created by george on 6/1/2016.
 */
public class JsonSerializationHelper {


    static <T extends PDEntity> String toJson(T item) {

        Gson gson = new Gson();
        String json = gson.toJson(item);
        return json.toString();


    }
    static <T extends PDEntity> String toJson(T[] items) {

        Gson gson = new Gson();
        String json = gson.toJson(items);
        return json.toString();


    }

    static <T extends PDEntity> String toJson(ArrayList<T> items) {

        Gson gson = new Gson();
        String json = gson.toJson(items);
        return json.toString();


    }
    static File CreateObsFile(String type) {


        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        Calendar c = Calendar.getInstance();

        StringBuilder hrsb = new StringBuilder();
        hrsb.append(type);
        hrsb.append(String.format("%04d", today.year));                // Year)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.monthDay));          // Day of the month (1-31)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.month + 1));              // Month (0-11))
        hrsb.append("_");
        hrsb.append(today.format("%k:%M:%S"));      // Current time
        hrsb.append(".json");                      //Completed file name


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

    static  <T extends PDEntity> void toJsonFile(T items,String type) {

        Gson gson = new Gson();

        FileOutputStream outputStream = null;
        File file = CreateObsFile(type);
        try {
            outputStream = new FileOutputStream(file);
            BufferedWriter bufferedWriter;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,
                        StandardCharsets.UTF_8));
            } else {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            }

            gson.toJson(items, bufferedWriter);
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (DEBUG) Log.e("SAVE JSON", "saveUserData, FileNotFoundException e: '" + e + "'");
        } catch (IOException e) {
            e.printStackTrace();
            if (DEBUG) Log.e("SAVE JSON", "saveUserData, IOException e: '" + e + "'");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    if (DEBUG) Log.e("SAVE JSON", "saveUserData, finally, e: '" + e + "'");
                }
            }
        }
    }

    static  <T extends PDEntity> void toJsonFile(ArrayList<T> items,String type) {

        Gson gson = new Gson();

        FileOutputStream outputStream = null;
        File file = CreateObsFile(type);
        try {
            outputStream = new FileOutputStream(file);
            BufferedWriter bufferedWriter;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,
                        StandardCharsets.UTF_8));
            } else {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            }

            gson.toJson(items, bufferedWriter);
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (DEBUG) Log.e("SAVE JSON", "saveUserData, FileNotFoundException e: '" + e + "'");
        } catch (IOException e) {
            e.printStackTrace();
            if (DEBUG) Log.e("SAVE JSON", "saveUserData, IOException e: '" + e + "'");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    if (DEBUG) Log.e("SAVE JSON", "saveUserData, finally, e: '" + e + "'");
                }
            }
        }
    }


}
