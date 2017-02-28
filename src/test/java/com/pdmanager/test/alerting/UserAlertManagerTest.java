package com.pdmanager.test.alerting; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */



import com.pdmanager.BuildConfig;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.models.Alert;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserAlertManagerTest {




    @Test
    public void test_hasActiveAlerts() {



        UserAlertManager manager=new UserAlertManager(RuntimeEnvironment.application);

        ///First clear all alerts
        manager.clearAll();
        ///Check for alert
        boolean h1=manager.anyActive();

        ///Assert false
        assertTrue(!h1);


        ///Add alertr
       long exp= (System.currentTimeMillis())+5*60*1000;
        manager.add("Test","TAKE YOUR MED","MED","12",exp);

        h1=manager.anyActive();


        ///Assert true
        assertTrue(h1);

        ///Get alert
        Alert alert=manager.getActive();
        //Assert correct title
        assertEquals( alert.Source,"12");

        manager.setNotActive(alert.Id);
        ///Check for alert
        h1=manager.anyActive();

        ///Assert false
        assertTrue(!h1);
        //Clear allerts age
        manager.clearAll();



        assert true;

    }



}
