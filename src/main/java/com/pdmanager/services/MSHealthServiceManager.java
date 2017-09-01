package com.pdmanager.services;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by george on 22/8/2017.
 */

public class MSHealthServiceManager {


    private final String msHealthService = "MS Health Service";

    public boolean isServiceInstalled(Context context) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            String serv = service.service.getClassName();
        }

        return true;
    }

}
