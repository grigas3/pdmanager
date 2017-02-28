/*package com.pdmanager.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.oovoo.sdk.api.LogSdk;
import com.pdmanager.app.VideoApp;
import com.pdmanager.settings.VideoSettings;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDListenerService";

    @Override
    public void onTokenRefresh() {
        LogSdk.d(TAG, "onTokenRefresh");

        try {
            VideoApp application = (VideoApp) getApplication();
            VideoSettings settings = application.getSettings();
            String username = settings.get(VideoSettings.Username);
            settings.remove(username);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        } catch (Exception e) {
            LogSdk.e(TAG, "onTokenRefresh - Failed to complete token refresh", e);
    }
}
}
*/