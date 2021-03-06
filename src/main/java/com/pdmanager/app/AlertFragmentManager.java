package com.pdmanager.app;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.pdmanager.alerting.IUserAlertManager;
import com.pdmanager.interfaces.IAlertFragmentManager;
import com.pdmanager.models.UserAlert;
import com.pdmanager.views.patient.AlertPDFragment;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Alert Fragment Manager implementation
 * The alert fragment manager is used by the patient activity
 * to display the proper fragment based on the user alert
 * TODO: Maybe the alert is not the proper word
 * <p>
 * Created by george on 15/1/2017.
 */

public class AlertFragmentManager implements IAlertFragmentManager {


    private static final String TAG = "AlertFragmentManager";

    FragmentActivity mainActivity;
    IUserAlertManager alertManager;
    //   AlertPDFragment defaultFragment;
    boolean onDefaultFragment = true;

    HashMap<String, AlertPDFragment> fragmentCache = new HashMap<String, AlertPDFragment>();
    private Timer timer = null;


    /*
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

    */
    private boolean timeRunning = false;// timer = null;


    /**
     * AlertFragmentManager constructor
     *
     * @param activity Activity
     */
    public AlertFragmentManager(FragmentActivity activity, IUserAlertManager pAlertManager) {

        this.alertManager = pAlertManager;
        this.mainActivity = activity;

    }
  /*  public void gotoAlertFragment(String id)
    {

        AlertPDFragment fragment=getAlertFragment(id);



        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();


    }
    */


   /* public void gotoNextFragment()
    {

        AlertPDFragment fragment=getNextFragment();
        
        
        
        FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
        
        
    }
    */

    private AlertPDFragment getNextFragment() {
        onDefaultFragment = true;
        UserAlert alert = alertManager.getFirstActive();

    /*    if (alert != null) {

            //Get Fragment based on alert
            AlertPDFragment fragment= getFragmentByAlert(alert);
              onDefaultFragment=false;

            return fragment;


        } else {


            return null;
        }
        */

        return null;
    }

    private void CheckForUserAlerts() {

        boolean hasActiveAlert = alertManager.anyActive();

        if (hasActiveAlert) {
            setFragment(getNextFragment());
        }

    }

    protected void setFragment(AlertPDFragment fragment) {

        if (fragment != null) {
            FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =
                    fragmentManager.beginTransaction();

            fragmentTransaction.addToBackStack(null);
            fragment.show(fragmentTransaction, "dialog");

        } else

        {

            Log.e("FRAGMENT_MANAGER", "Null Fragment");

        }

   /*     FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
        */
    }

    public void startAutoUpdate() {

        if (!timeRunning) {
            timeRunning = true;
            timer = new Timer();
            timer.scheduleAtFixedRate(new mainTask(), 10000, 60000);

        }
    }

    public void stopAutoUpdate() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            timeRunning = false;
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
            if (onDefaultFragment)
                CheckForUserAlerts();


        }


    }
}
