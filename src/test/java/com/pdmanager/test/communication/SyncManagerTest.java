package com.pdmanager.test.communication; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */


import com.pdmanager.BuildConfig;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.persistence.SyncManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SyncManagerTest {




    @Test
    public void test_syncAlerts() {



        SyncManager manager=new SyncManager(RuntimeEnvironment.application);

        ///First clear all alerts

        ///Check for alert
        manager.syncTable(DBHandler.TABLE_ALERTS);

        long ret1= manager.syncTable(DBHandler.TABLE_ALERTS);


        ///Assert false
        assertTrue(ret1>0);


        long ret2= manager.syncTable(DBHandler.TABLE_ALERTS);


        ///Assert false
        assertTrue(ret2<=0);





        assert true;

    }



}
