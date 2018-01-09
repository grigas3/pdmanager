/*package com.pdmanager.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.oovoo.sdk.api.LogSdk;
import com.pdmanager.app.VideoApp;
import com.pdmanager.settings.VideoSettings;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";
    
    private static final String SENDER_ID = "522796524817";

    public RegistrationIntentService() {
        super(TAG);

        LogSdk.i(TAG, "RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            VideoApp application = (VideoApp) getApplication();
            VideoSettings settings = application.getSettings();
            String username = settings.get(VideoSettings.Username);
            String token = settings.get(username);

            if (token == null) {
                InstanceID instanceID = InstanceID.getInstance(this);
                token = instanceID.getToken(SENDER_ID/, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                settings.put(username, token);
                settings.save();
                sendRegistrationToServer(token);
                LogSdk.i(TAG, "GCM Registration Token: " + token);
            }

            sharedPreferences.edit().putBoolean(VideoSettings.SENT_TOKEN_TO_SERVER, true).apply();
        } catch (Exception e) {
            LogSdk.e(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(VideoSettings.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed
        Intent registrationComplete = new Intent(VideoSettings.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
*/
/**
 * Persist registration to third-party servers.
 * <p>
 * Modify this method to associate the user's GCM registration token with any server-side account
 * maintained by your application.
 *
 * @param token The new token.
 */
    /*private void sendRegistrationToServer(String token) {

        VideoApp application = (VideoApp) getApplication();

        application.subscribe(token);
    }
}

*/