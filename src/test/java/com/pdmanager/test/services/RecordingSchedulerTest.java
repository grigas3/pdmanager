package com.pdmanager.test.services;

import com.pdmanager.BuildConfig;
import com.pdmanager.services.RecordingScheduler;
import com.pdmanager.services.RecordingService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by george on 22/1/2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RecordingSchedulerTest {


    @Test
    public void testScheduledRun() {



        assertFalse(RecordingService.testSheduledRun(7,8,20));


        assertFalse(RecordingService.testSheduledRun(7,8,20));



        assertTrue(RecordingService.testSheduledRun(8,8,20));

        assertTrue(RecordingService.testSheduledRun(16,8,20));


        assertFalse(RecordingService.testSheduledRun(23,8,20));


        assertFalse(RecordingService.testSheduledRun(0,8,20));


    }

    @Test
    public void testPlannedStop() {



        //6 8 12 24
        assertTrue(RecordingScheduler.calculateSkipTest(6,6+6)==0);

//        assertTrue(Math.abs(RecordingScheduler.calculateSkipTest(6,6+8)-1.66666667*60*1000)<1000);

        assertTrue(RecordingScheduler.calculateSkipTest(6, 6 + 14) == 5 * 60 * 1000);

        //  assertTrue(RecordingScheduler.calculateSkipTest(6,6+24)==15*60*1000);
        //0    2    5.0000   15.0000

    }

}
