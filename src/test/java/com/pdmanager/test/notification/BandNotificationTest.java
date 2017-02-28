package com.pdmanager.test.notification;

import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandInfo;
import com.pdmanager.notification.BandMessage;
import com.pdmanager.notification.BandMessageTask;

import org.robolectric.RuntimeEnvironment;

/**
 * Created by george on 24/2/2017.
 */

public class BandNotificationTest {


    private static final String TAG="BANDNOTIFICATIONTEST";

    void testNotification()
    {

        BandClient client;
        try {
            BandClientManager manager = BandClientManager.getInstance();
            BandInfo[] mPairedBands = manager.getPairedBands();

            if (mPairedBands.length > 0) {

                client = manager.create(RuntimeEnvironment.application, mPairedBands[0]);
                BandMessageTask.newInstance(client,"Hello").execute(new BandMessage("test","test",0,true));
            } else {



                return;


            }

        } catch (Exception ex) {



        }

    }
}
