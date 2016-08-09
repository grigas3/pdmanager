package com.pdmanager.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by george on 8/1/2016.
 */
public class PDBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, RecordingService.class);
        context.startService(service);

    }
}
