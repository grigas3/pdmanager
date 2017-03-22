package com.pdmanager.common;

import android.os.Environment;

import com.pdmanager.common.data.BaseSensorData;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.math.DoubleMath;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by admin on 14/5/2015.
 */
public abstract class DataBuffer<T> {
    protected final int multiplier = 10000000;
    protected final int longBytes = 8;
    protected final long long4Bytes = 4 * longBytes;
    protected final long long2Bytes = 2 * longBytes;
    protected final long long3Bytes = 3 * longBytes;
    private final ArrayList<T> items;
    private final long MAXBUFFER;
    private final String fileName;
    private final String folder;
    private final long maxTotalBuffer;
    private final long rowSize;
    File saveFilePath = null;
    private Object lock1 = new Object();
    private long count = 0;
    private Date start;
    private long byteWritten = 0;
    private boolean mHeaderCreated = false;
    private boolean needSave = false;
    private final  String newline = "\r\n";
    private int itemCount=0;


    public DataBuffer(long mBuffer, long mTBuffer, String pfolder, String pfileName) {
        MAXBUFFER = mBuffer;
        maxTotalBuffer = mTBuffer;
        items = new ArrayList<T>();

        folder = pfolder;

        fileName = pfileName;


        rowSize = GetItemSize();

        start = new Date();

    }


    public DataBuffer(long mBuffer, String pfolder, String pfileName) {
        MAXBUFFER = mBuffer;

        //maxTotalBuffer=4*1024*1024;

        maxTotalBuffer = 4 * 1024 * 1024;
        items = new ArrayList<T>();

        folder = pfolder;

        fileName = pfileName;
        rowSize = GetItemSize();

        start = new Date();

    }

    protected abstract long GetItemSize();

    public void finalize() {

        if (count > 0) {
            //final ArrayList<T> tmpItems = new ArrayList<>();

            //tmpItems.addAll(items);

            final ArrayList<T> tmpItems=(ArrayList<T>)items.clone();
            count = 0;
            items.clear();

            if (saveFilePath == null) {


                saveFilePath = CreateTmpFile();
                byteWritten = 0;
            }

            SyncData(tmpItems);

        }


    }

    public void setStartTime() {
        start = new Date();

    }

    protected Date getStart() {
        return start;

    }

    /***
     * Get Long from double without loosing presicion
     ***/
    protected long GetLong(double x) {
        return DoubleMath.doubleToLong(x);
    }

    protected abstract void WriteItem(DataOutputStream OutDataWriter, T item) throws Exception;

    private File CreateTmpFile() {


        //get the path to sdcard
        File pathToExternalStorage = Environment.getExternalStorageDirectory();
        //to this path add a new directory path and create new App dir (InstroList) in /documents Dir

        long unixTime = System.currentTimeMillis() / 1000L;

        File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/" + folder + "/");
        // have the object build the directory structure, if needed.
        appDirectory.mkdirs();

        File saveFilePath = new File(appDirectory, fileName + "_" + unixTime + ".dat");

        return saveFilePath;

    }

    private void SyncData(ArrayList<T> tmpItems) {


        //Adds the textbox data to the file
        try {

            FileOutputStream fos = new FileOutputStream(saveFilePath, true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DataOutputStream OutDataWriter = new DataOutputStream(bos);


            for (int j = 0; j < tmpItems.size(); j++) {

                WriteItem(OutDataWriter, tmpItems.get(j));
                byteWritten += rowSize;


            }


            OutDataWriter.close();

            fos.flush();
            fos.close();
            bos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void addItem(T item) {

        final ArrayList<T> tmpItems = new ArrayList<>();

        synchronized (lock1) {

            count += rowSize;
            items.add(item);
            if (count >= MAXBUFFER) {


                needSave = true;
                //tmpItems=new ArrayList<>();
                //  tmpItems=(ArrayList<T>)items.clone();
                tmpItems.addAll(items);
                items.clear();
                count = 0;


            }

            if (saveFilePath == null || byteWritten >= maxTotalBuffer) {


                saveFilePath = CreateTmpFile();
                byteWritten = 0;
            }


        }


        if (needSave) {
            needSave = false;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    SyncData(tmpItems);

                }
            });


            t.start();


        }

    }


}
