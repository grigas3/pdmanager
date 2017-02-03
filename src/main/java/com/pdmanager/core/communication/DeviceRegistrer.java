package com.pdmanager.core.communication;

import android.os.Build;

import com.pdmanager.core.models.Device;

/**
 * Created by george on 17/8/2016.
 */
public class DeviceRegistrer {

    String accessToken;
    String patientId;

    public DeviceRegistrer(String pid, String paccessToken) {
        this.patientId = pid;
        this.accessToken = paccessToken;

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private Device getDevice(String id) {

        Device dev = new Device(patientId, id, "ANDROID", getDeviceName());

        return dev;


    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void Register(String id) {


        DirectSender sender = new DirectSender(accessToken);
        CommunicationManager mCommManager = new CommunicationManager(sender);
        mCommManager.SendItem(getDevice(id));
    }
}
