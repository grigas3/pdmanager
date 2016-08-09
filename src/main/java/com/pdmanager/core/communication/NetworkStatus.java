package com.pdmanager.core.communication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by george on 6/1/2016.
 */
public class NetworkStatus {


    public static boolean IsNetworkConnected(Context activity) {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMan != null) {
            NetworkInfo m = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo.State mobile = NetworkInfo.State.UNKNOWN;
            if (m != null) {
                mobile = m.getState();

            }
            NetworkInfo s = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //wifi
            NetworkInfo.State wifi = NetworkInfo.State.UNKNOWN;

            if (s != null) {
                wifi = s.getState();

            }
//  and then use it like that:

            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) {
                return true;
            } else
                return wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING;
        }
        return false;
    }
}
