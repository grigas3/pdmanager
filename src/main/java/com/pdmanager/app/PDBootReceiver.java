package com.pdmanager.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pdmanager.services.RecordingService;

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
