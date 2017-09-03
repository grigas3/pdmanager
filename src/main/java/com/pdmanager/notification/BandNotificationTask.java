package com.pdmanager.notification;

import android.os.AsyncTask;

import com.microsoft.band.BandClient;
import com.microsoft.band.notifications.VibrationType;

/**
 * Notification through Microsoft Band
 * Created by george on 23/1/2017.
 */

public class BandNotificationTask extends AsyncTask<Void, Void, Void> {



    private BandClient mClient;
    private String mTileUUID;
    public BandNotificationTask(BandClient client)
    {
        this.mClient=client;



    }

    public static BandNotificationTask newInstance(BandClient client)
    {

        return new BandNotificationTask(client);

    }


    @Override
    protected Void doInBackground(Void... params) {



        try {


            if (mClient != null) {

                mClient.getNotificationManager().vibrate(VibrationType.NOTIFICATION_ALARM);
            }


        } catch (Exception e) {

            e.printStackTrace();


        }


        return null;
    }


}
