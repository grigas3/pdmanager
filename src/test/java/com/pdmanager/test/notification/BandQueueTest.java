package com.pdmanager.test.notification;

import com.pdmanager.BuildConfig;
import com.pdmanager.notification.BandMessage;
import com.pdmanager.notification.BandMessageQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Created by george on 28/1/2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BandQueueTest {

    @Test
    public void testBandQueue1()
    {


        BandMessage message=new BandMessage("Test","test",0,false);


        BandMessageQueue queue=new BandMessageQueue();

        queue.add(message);

        BandMessage message2=queue.poll();

        assertTrue(message.getTimestamp()==message2.getTimestamp());





    }


}
