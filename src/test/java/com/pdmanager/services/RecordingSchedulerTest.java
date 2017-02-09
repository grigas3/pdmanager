package com.pdmanager.services;

import com.dropbox.core.DbxOAuth1AccessToken;
import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.core.BuildConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.assertTrue;

/**
 * Created by george on 22/1/2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecordingSchedulerTest {




    @Test
    public void testPlannedStop() {



        //6 8 12 24
        assertTrue(RecordingScheduler.calculateSkipTest(6,6+6)==0);


        assertTrue(Math.abs(RecordingScheduler.calculateSkipTest(6,6+8)-1.66666667*60*1000)<1000);



        assertTrue(RecordingScheduler.calculateSkipTest(6,6+12)==5*60*1000);


        assertTrue(RecordingScheduler.calculateSkipTest(6,6+24)==15*60*1000);
        //0    2    5.0000   15.0000

    }

}
