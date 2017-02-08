package com.pdmanager.core.interfaces;

import android.app.Fragment;

import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.patient.AlertPDFragment;

/**
 * Alert Fragment Manager Interface
 * The alert fragment manager is used by the patient activity
 * to display the proper fragment based on the user alert
 * TODO: Maybe the alert is not the proper word
 *
 * Created by george on 15/1/2017.
 */


public interface IAlertFragmentManager {

    //void registerFragment(String alertType,AlertPDFragment fragment);
    //void gotoAlertFragment(String alertId);
    //void gotoNextFragment();
    //void setDefaultFragment(AlertPDFragment pDefaultFragment);

    void startAutoUpdate();
    void stopAutoUpdate();


}
