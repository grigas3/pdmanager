package com.pdmanager.helpers;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

public class BluetoothHelper {


    private static final String TAG = "BluetoothHelper";


    /**
     * Check if bluetooth is enabled
     *
     * @return
     */
    public static boolean isBluetoothEnabled() {

        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter != null) {
                return mBluetoothAdapter.isEnabled();

            }
            return false;

        } catch (Exception ex) {

            return false;
        }
    }

    /***
     * Is Bluetooth enabled
     */
    public static void enableBluetooth() {
        boolean bluetoothEnabled = isBluetoothEnabled();

        if (!bluetoothEnabled) {

            try {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                mAdapter.enable();


                //LogInfo("Bluetooth activated by service");
            } catch (Exception ex) {

                Log.e(TAG, "Cannot activate bluetooth Band Sensors");
                //LogInfoError("Cannot activate bluetooth Band Sensors");

            }

            //activateBluetooth();

        }

    }

    public static void disableBluetooth() {
        boolean bluetoothEnabled = isBluetoothEnabled();

        if (bluetoothEnabled) {

            try {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                mAdapter.disable();


                //LogInfo("Bluetooth activated by service");
            } catch (Exception ex) {

                Log.e(TAG, "Cannot activate bluetooth Band Sensors");
                //LogInfoError("Cannot activate bluetooth Band Sensors");

            }

            //activateBluetooth();

        }

    }

}
