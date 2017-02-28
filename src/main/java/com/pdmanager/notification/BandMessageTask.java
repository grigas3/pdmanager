package com.pdmanager.notification;

import android.os.AsyncTask;

import com.microsoft.band.BandClient;
import com.microsoft.band.notifications.MessageFlags;
import com.microsoft.band.notifications.VibrationType;
import com.pdmanager.models.UserAlert;

import java.util.UUID;

/**
 * Notification through Microsoft Band
 * Created by george on 23/1/2017.
 */

public class BandMessageTask extends AsyncTask<BandMessage, Void, Void> {



    private BandClient mClient;
    private String mTileUUID;
    public BandMessageTask(BandClient client, String tileUID)
    {
        this.mClient=client;
        this.mTileUUID=tileUID;


    }

    public static BandMessageTask newInstance(BandClient client, String tileUID)
    {

        return new BandMessageTask(client,tileUID);

    }


    @Override
    protected Void doInBackground(BandMessage... params) {



        try {

            if(mTileUUID!=null) {
                BandMessage alert = params[0];


                if(alert!=null) {
                    if (alert.isMessage()) {
                        mClient.getNotificationManager().sendMessage(UUID.fromString(mTileUUID),
                                alert.getTitle(), alert.getMessage(), new java.util.Date(alert.getTimestamp()), MessageFlags.NONE).await();

                    } else {
                        mClient.getNotificationManager().showDialog(UUID.fromString(mTileUUID),
                                alert.getTitle(), alert.getMessage()).await();
                    }
                }
                mClient.getNotificationManager().vibrate(VibrationType.NOTIFICATION_ALARM);

            }

        } catch (Exception e) {

            e.printStackTrace();


        }


        return null;
    }


}
