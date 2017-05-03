package com.pdmanager.test.alerting; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */



import com.pdmanager.BuildConfig;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.models.Alert;
import com.pdmanager.models.UserAlert;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.settings.RecordingSettings;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserAlertManagerTest {




    private int getHour(long l)
    {

        Calendar cal1= Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date date = new Date(l);
        cal1.setTime(date);
        int hour=cal1.get(Calendar.HOUR_OF_DAY);

        return hour;

    }

    @Test
    public void test_getNewExpirationDate() {
        UserAlertManager manager = new UserAlertManager(RuntimeEnvironment.application);

        RecordingSettings.GetRecordingSettings(RuntimeEnvironment.application).setStartHour(8);
        RecordingSettings.GetRecordingSettings(RuntimeEnvironment.application).setStopHour(20);
        int hour = 8;
        long h = manager.getNewExpirationDate(1000 * hour * 60*60, "diary");
        assertEquals(getHour(h), 9);

        hour = 16;
        h = manager.getNewExpirationDate(1000 * hour * 60*60, "diary");
        assertEquals(getHour(h), 17);

        hour = 20;
        h = manager.getNewExpirationDate(1000 * hour * 60*60, "diary");
        assertEquals(getHour(h), 8);

    }


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
        Alert alert=manager.getFirstActive();
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




    @Test
    public void test_hasUnNotifiedAlerts() {



        UserAlertManager manager=new UserAlertManager(RuntimeEnvironment.application);

        ///First clear all alerts
        manager.clearAll();
        ///Check for alert
        boolean h1=manager.anyActive();

        ///Assert false
        assertTrue(!h1);


        ///Add alertr
        long exp= (System.currentTimeMillis())-1000;
        manager.add("Test","TAKE YOUR MED","MED","12",exp);

        h1=manager.anyUnNotified();


        ///Assert true
        assertTrue(h1);

        ///Get alert
        UserAlert alert=manager.getUnNotified();
        //Assert correct title
        assertEquals( alert.Source,"12");

        manager.setNotified(alert);
        ///Check for alert
        h1=manager.anyUnNotified();

        ///Assert false
        assertTrue(!h1);
        //Clear allerts age
        manager.clearAll();



        assert true;

    }


    @Test
    public void test_updateAlerts() {



        UserAlertManager manager=new UserAlertManager(RuntimeEnvironment.application);

        ///First clear all alerts
        manager.clearAll();


        ///Add alertr
        long exp1= (System.currentTimeMillis())+5*60*1000;
        long exp2= (System.currentTimeMillis())+30*60*1000;
        manager.add("Test","TAKE YOUR MED","MED","12",exp1);
        manager.add("Test","TAKE YOUR MED","MED","12",exp2);



        boolean h1=manager.anyActive();

        assertTrue(h1);
        manager.updateAlerts("MED");
        boolean h2=manager.anyActive();

        assertTrue(!h2);



        assert true;

    }
}
