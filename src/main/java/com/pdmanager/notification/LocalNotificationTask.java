package com.pdmanager.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.pdmanager.app.PDApplicationContext;
import com.pdmanager.R;
import com.pdmanager.models.UserAlert;
import com.pdmanager.views.patient.MainActivity;


/**
 * Created by george on 30/11/2015.
 */
public class LocalNotificationTask extends AsyncTask<UserAlert, Void, Void> {

    private final Context mContext;


    private String mPhone;
    private int id = 9999;

    public LocalNotificationTask(Context context) {
        mContext = context;


    }

    public static LocalNotificationTask newInstance(Context context)
    {

        return new LocalNotificationTask(context);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

    @Override
    protected Void doInBackground(UserAlert... params) {


        UserAlert alert = params[0];
        try {


            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (soundUri == null)
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (soundUri == null)
                soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);


            if (soundUri != null)
                mBuilder.setSound(soundUri);




            if(alert.getAlertType()=="MED") {
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_pill_2));
                mBuilder.setSmallIcon(R.drawable.ic_pill_2);

            }
            else {
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.pdmanager));
                mBuilder.setSmallIcon(R.drawable.pdmanagersmall);
            }

            mBuilder.setContentTitle(alert.getTitle());
            mBuilder.setContentText(alert.getMessage());

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra(PDApplicationContext.INTENT_ALERT_TYPE, alert.getAlertType());
            intent.putExtra(PDApplicationContext.INTENT_ALERT_ID, alert.getId());
            intent.setAction(Long.toString(System.currentTimeMillis()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(pendingIntent);

            mBuilder.setAutoCancel(true);
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