package com.pdmanager.test.medication; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */


import com.pdmanager.BuildConfig;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.medication.MedManager;
import com.pdmanager.models.Alert;
import com.pdmanager.models.MedTiming;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.UserAlert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MedManagerTest {




    @Test
    public void test_NextMedicationAlert() {



        MedManager manager=new MedManager(RuntimeEnvironment.application);
        ///First clear all alerts
        manager.clearAll();
        long exp= (System.currentTimeMillis())+5*60*1000;
        MedicationOrder order=new MedicationOrder();

        order.Timestamp=exp;
        ArrayList<MedTiming> timings=new ArrayList<MedTiming>();
        MedTiming timing=new MedTiming();
        timing.Dose="110mg";
        timing.Time=exp;


        timings.add(timing);
        order.setTimings(timings);
        ///Check for alert
        boolean h1=manager.addMedicationOrder(order);

        ///Assert false
        assertTrue(h1);


        ///Add alertr

        UserAlert a=manager.getNextMedicationTest(exp);



        ///Assert true
        assertTrue(a!=null);
        if(a!=null)
            manager.setLastMessage(a.getSource());

        a=manager.getNextMedicationTest(exp);
        assertTrue(a==null);
        if(a!=null)
            manager.setLastMessage(a.getSource());

        //Simulate check after a expirtation

        a=manager.getNextMedicationTest(exp+1*60*1000);
        assertTrue(a==null);
        if(a!=null)
            manager.setLastMessage(a.getSource());



        //Simulate next day check
        a=manager.getNextMedicationTest(exp+24*60*60*1000-5*60*1000);
        assertTrue(a!=null);

        //Clear allerts age
        manager.clearAll();



        assert true;

    }





    @Test
    public void test_PendingMedicationAlert() {



        MedManager manager=new MedManager(RuntimeEnvironment.application);
        UserAlertManager alertManager=new UserAlertManager(RuntimeEnvironment.application);
        ///First clear all alerts
        manager.clearAll();
        long exp= (System.currentTimeMillis())+5*60*1000;
        MedicationOrder order=new MedicationOrder();

        order.Timestamp=exp;
        ArrayList<MedTiming> timings=new ArrayList<MedTiming>();
        MedTiming timing=new MedTiming();
        timing.Dose="110mg";
        timing.Time=exp;


        timings.add(timing);
        order.setTimings(timings);
        ///Check for alert
        boolean h1=manager.addMedicationOrder(order);

        ///Assert false
        assertTrue(h1);


        ///Add alertr

        int ret=manager.pendingMedicationTest(exp);
        assertTrue(ret>0);
        if (ret > 0) {
            UserAlert alert = manager.getPendingMedAlert(ret);

            assertTrue(alert!=null);

            if(alert!=null)
            {
                alertManager.add(alert);
            }
          //  alertmanager.add(alert);
        }

        ret=manager.pendingMedicationTest(exp);
        assertTrue(ret<=0);


        ret=manager.pendingMedicationTest(exp+5*60*1000);
        assertTrue(ret<=0);

        alertManager.deleteExpiredTest(exp+24*60*60*1000-5*60*1000);


        ret=manager.pendingMedicationTest(exp+24*60*60*1000-5*60*1000);
        assertTrue(ret>0);



        assert true;

    }

}
