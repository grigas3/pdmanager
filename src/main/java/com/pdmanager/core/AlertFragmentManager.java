package com.pdmanager.core;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.pdmanager.core.alerting.IUserAlertManager;
import com.pdmanager.core.interfaces.IAlertFragmentManager;
import com.pdmanager.core.models.UserAlert;
import com.pdmanager.views.patient.AlertPDFragment;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Alert Fragment Manager implementation
 * The alert fragment manager is used by the patient activity
 * to display the proper fragment based on the user alert
 * TODO: Maybe the alert is not the proper word
 *
 * Created by george on 15/1/2017.
 */

public class AlertFragmentManager implements IAlertFragmentManager {


    private static final String TAG="AlertFragmentManager";

    FragmentActivity mainActivity;
    IUserAlertManager alertManager;
    AlertPDFragment defaultFragment;
    boolean onDefaultFragment=true;

    HashMap<String, AlertPDFragment> fragmentCache = new HashMap<String, AlertPDFragment>();


    /**
     * AlertFragmentManager constructor
     * @param activity Activity
     */
    public AlertFragmentManager(FragmentActivity activity,IUserAlertManager pAlertManager)
    {

        this.alertManager=pAlertManager;
        this.mainActivity=activity;

    }


    @Override
    public void setDefaultFragment(AlertPDFragment pDefaultFragment) {

        this.defaultFragment=pDefaultFragment;

    }



    @Override
    public void registerFragment(String alertType, AlertPDFragment fragment) {

        if(fragment!=null) {
            fragment.setFragmentManager(this);

            if (!fragmentCache.containsKey(alertType))
                fragmentCache.put(alertType, fragment);
        }
    }


    private AlertPDFragment getFragmentByAlert(UserAlert alert)
    {
        AlertPDFragment newFragment = null;

        String section = alert.getAlertType();

        onDefaultFragment=true;

        if (fragmentCache.containsKey(section)) {
            Log.d(TAG, "Fragment from Cache");

            newFragment = fragmentCache.get(section);


            if(newFragment!=null) {
                onDefaultFragment = false;
                return newFragment;
            }

        }



    return defaultFragment;
    }


    private AlertPDFragment getAlertFragment(String alertId) {

        onDefaultFragment=true;
        UserAlert alert = alertManager.getAlert(alertId);

        if (alert != null) {


            return getFragmentByAlert(alert);

        } else
            return defaultFragment;
    }





    private AlertPDFragment getNextFragment() {
        onDefaultFragment=true;
        UserAlert alert=alertManager.getActive();

        if (alert != null) {

            //Get Fragment based on alert
            AlertPDFragment fragment= getFragmentByAlert(alert);

            fragment.update(alert);
            /*
            Bundle args = new Bundle();
            args.putString("AlertID", alert.getSource());
            fragment.setArguments(args);
            */
            //Fragment update from alert

            // Default Fragment =false
            onDefaultFragment=false;

            return fragment;
            

        } else {


            return defaultFragment;
        }
    }


    private void CheckForUserAlerts()
    {


        boolean hasActiveAlert=alertManager.anyActive();


        if(hasActiveAlert)
        {
            setFragment(getNextFragment());
        }

    }
    public void gotoAlertFragment(String id)
    {

        AlertPDFragment fragment=getAlertFragment(id);



        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();


    }


    public void gotoNextFragment()
    {

        AlertPDFragment fragment=getNextFragment();
        
        
        
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
        
        
    }
    
    protected void setFragment(AlertPDFragment fragment) {
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private Timer timer = null;
    private boolean timeRunning=false;// timer = null;

    public void startAutoUpdate() {


        if(!timeRunning) {
            timeRunning=true;
            timer = new Timer();
            timer.scheduleAtFixedRate(new mainTask(), 10000, 60000);

        }
    }

    public void stopAutoUpdate() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            timeRunning=false;
        }


    }


    /**
     * A main timer for checking for alerts to create a fragment
     */
    private class mainTask extends TimerTask {


        public mainTask() {



        }

        public void run() {



            ///Check if we running with default fragment
            if(onDefaultFragment)
                CheckForUserAlerts();


        }


    }
}