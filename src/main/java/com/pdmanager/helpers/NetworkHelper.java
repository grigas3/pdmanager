package com.pdmanager.helpers;

import android.content.Context;
import android.util.Log;

import com.pdmanager.communication.NetworkStatus;

/**
 * Created by george on 4/9/2017.
 */

public class NetworkHelper {


    /**
     * Check if network is connected
     *
     * @param context
     * @return
     */
    public static boolean IsNetworkConnected(Context context) {
        boolean ret = false;

        try {

            ret = NetworkStatus.IsNetworkConnected(context);


        } catch (Exception ex) {

            Log.e("Error", "Error while checking for network connection");

        }

        return ret;

    }
}
