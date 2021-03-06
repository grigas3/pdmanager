package com.pdmanager.services;/*package com.pdmanager.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.oovoo.sdk.api.LogSdk;
import com.oovoo.sdk.oovoosdksampleshow.R;
import com.pdmanager.app.PDManagerVideoApp;
import com.pdmanager.ui.CallActivity;


public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String property = data.getString("property");
        String body = data.getString("body");

        LogSdk.d(TAG, "GcmListenerService :: From     : " + from);
        LogSdk.d(TAG, "GcmListenerService :: full     : " + data.toString());
        LogSdk.d(TAG, "GcmListenerService :: property : " + property);
        LogSdk.d(TAG, "GcmListenerService :: body     : " + body);

        PDManagerVideoApp application = (PDManagerVideoApp) getApplication();

        Intent intent = new Intent(application.getContext(), CallActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(application.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(application.getContext());

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(property != null ? property : from)
                .setContentTitle(property != null ? property : from)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) application.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }
}
*/