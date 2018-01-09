package com.pdmanager;

import android.text.format.Time;

import com.pdmanager.buffers.AccDataBuffer;
import com.pdmanager.buffers.AccDevCDataBuffer;
import com.pdmanager.buffers.AccDevDataBuffer;
import com.pdmanager.buffers.DistanceDataBuffer;
import com.pdmanager.buffers.GyroDataBuffer;
import com.pdmanager.buffers.GyroDevDataBuffer;
import com.pdmanager.buffers.HRDataBuffer;
import com.pdmanager.buffers.LocationDataBuffer;
import com.pdmanager.buffers.OrientationDataBuffer;
import com.pdmanager.buffers.STDataBuffer;
import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.ISensorData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 18/5/2015.
 */
public class FileDataProcessor extends BaseDataProcessor {


    private static int s4bufferSize = 16384;
    private static int s2bufferSize = 32768;
    private static int s4MbufferSize = 196608;
    private static int s2MbufferSize = 393216;
    Map<Integer, DataBuffer> bufferMap = new HashMap<Integer, DataBuffer>();
    ArrayList<DataBuffer> bufferMapList = new ArrayList<DataBuffer>();

    public FileDataProcessor() {
        // InitBuffers();

    }

    private String CreateFolder(Time today) {

        StringBuilder hrsb = new StringBuilder();
        hrsb.append("PDManager/");
        hrsb.append(String.format("%04d", today.year));                // Year)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.monthDay));          // Day of the month (1-31)
        hrsb.append("_");
        hrsb.append(String.format("%02d", today.month + 1));              // Month (0-11))
        hrsb.append("_");
        hrsb.append(today.format("%k:%M:%S"));      // Current time
        hrsb.append("/");                      //Completed file name


        String newFileName = hrsb.toString();
        newFileName = newFileName.replaceAll(":", "_");

        return newFileName;

    }

    private String CreateFileName(Time today, String prefix) {

        StringBuilder hrsb = new StringBuilder();
        hrsb.append(prefix);
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

        return newFileName;

    }


    /**
     * Initialize File Processor
     */
    public void initialize() {
        bufferMap.clear();
        bufferMapList.clear();
        InitBuffers();
        setStartTime();

    }


    /**
     * Finalize File Processor
     * Save files (remaining data)
     */
    public void finalize() {

        for (int i = 0; i < bufferMapList.size(); i++)
            bufferMapList.get(i).finalize();

    }

    public void setStartTime() {


        for (int i = 0; i < bufferMapList.size(); i++)
            bufferMapList.get(i).setStartTime();


    }

    private void InitBuffers() {

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

       // String recfolder = CreateFolder(today);


        String recfolder = "PD_Manager";

        String accNewFileName = "sensor_acc";
        String gyroNewFileName = "sensor_gyro";
        String accmNewFileName = "sensor_acc_mobile";
        String accmcNewFileName = "sensor_acc_corr_mobile";
        String gyromNewFileName = "sensor_gyro_mobile";
        String orientNewFileName = "sensor_orient_mobile";
        String hrNewFileName = "sensor_hr";
        String stNewFileName = "sensor_st";
        String locationNewFileName = "location";
        String distanceNewFileName = "distance";


        AccDataBuffer b1 = new AccDataBuffer(64 * 1024, recfolder, accNewFileName);
        bufferMapList.add(b1);
        bufferMap.put(DataTypes.ACCELEROMETER, b1);

        GyroDataBuffer b2 = new GyroDataBuffer(64 * 1024, recfolder, gyroNewFileName);
        bufferMap.put(DataTypes.GYRO, b2);
        bufferMapList.add(b2);

        AccDevDataBuffer b3 = new AccDevDataBuffer(32 * 1024, recfolder, accmNewFileName);
        bufferMap.put(DataTypes.ACCELEROMETER_DEVICE, b3);
        bufferMapList.add(b3);


        HRDataBuffer b4 = new HRDataBuffer(64, recfolder, hrNewFileName);
        bufferMap.put(DataTypes.HR, b4);
        bufferMapList.add(b4);


        OrientationDataBuffer b6 = new OrientationDataBuffer(32 * 1024, recfolder, orientNewFileName);
        bufferMap.put(DataTypes.ORIENTATION, b6);
        bufferMapList.add(b6);


        GyroDevDataBuffer b7 = new GyroDevDataBuffer(32 * 1024, recfolder, gyromNewFileName);
        bufferMap.put(DataTypes.GYRO_DEVICE, b7);
        bufferMapList.add(b7);

        AccDevCDataBuffer b8 = new AccDevCDataBuffer(32 * 1024, recfolder, accmcNewFileName);
        bufferMap.put(DataTypes.CORRECTED_ACCELEROMETER_DEVICE, b8);
        bufferMapList.add(b8);


        LocationDataBuffer b9 = new LocationDataBuffer(64, recfolder, locationNewFileName);
        bufferMap.put(DataTypes.GPS, b9);
        bufferMapList.add(b9);


        DistanceDataBuffer b10 = new DistanceDataBuffer(64, recfolder, distanceNewFileName);
        bufferMap.put(DataTypes.PEDOMETER, b10);
        bufferMapList.add(b10);


        STDataBuffer b11 = new STDataBuffer(64, recfolder, stNewFileName);
        bufferMap.put(DataTypes.ST, b11);
        bufferMapList.add(b11);


    }

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.ST || dataType == DataTypes.PEDOMETER || dataType == DataTypes.GPS || dataType == DataTypes.ACCELEROMETER || dataType == DataTypes.CORRECTED_ACCELEROMETER_DEVICE || dataType == DataTypes.HR || dataType == DataTypes.ANNOTATION || dataType == DataTypes.ACCELEROMETER_DEVICE || dataType == DataTypes.GYRO || dataType == DataTypes.ORIENTATION || dataType == DataTypes.GYRO_DEVICE;

/*

        if(dataType == DataTypes.ACCELEROMETER)
            return true;
        else
            return false;
            */
    }

    @Override
    public void addData(ISensorData data) {

        int dataType = data.getDataType();
        bufferMap.get(dataType).addItem(data);


    }


    public void Stop() {


        for (int i = 0; i < bufferMapList.size(); i++)
            bufferMapList.get(i).finalize();


    }


}
