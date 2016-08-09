package com.pdmanager.core.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;


/**
 * Created by george on 30/11/2015.
 */
public class LocalNotificationTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;


    private String mPhone;
    private int id = 9999;

    public LocalNotificationTask(Context context) {
        mContext = context;


    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

    @Override
    protected Void doInBackground(String... params) {


        String message = params[0];
        try {


            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (soundUri == null)
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (soundUri == null)
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);


            if (soundUri != null)
                mBuilder.setSound(soundUri);


            mBuilder.setSmallIcon(com.pdmanager.core.R.drawable.pdmanager);
            mBuilder.setContentTitle("PDManager Alert");
            mBuilder.setContentText(message);

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(id, mBuilder.build());
            //    SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(mPhone, null, message, null, null);

        } catch (Exception e) {

            e.printStackTrace();


        }


        return null;
    }


    public interface Callback {
        void onNotificationComplete();

        void onError(Exception e);
    }


}