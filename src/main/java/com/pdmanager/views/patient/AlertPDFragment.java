package com.pdmanager.views.patient;

import android.support.v4.app.DialogFragment;

import com.pdmanager.core.alerting.IUserAlertManager;
import com.pdmanager.core.alerting.UserAlertManager;
import com.pdmanager.core.interfaces.IAlertFragmentManager;
import com.pdmanager.core.models.UserAlert;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.views.BasePDFragment;

/**
 * Created by george on 15/1/2017.
 */

public abstract class AlertPDFragment extends DialogFragment {




    private UserAlert currentAlert;
    IAlertFragmentManager fragmentManager;
    /**
     * Release alert
     */
    public void release()
    {
        if(currentAlert!=null)
        {
            UserAlertManager.newInstance(getContext()).setNotActive(currentAlert.Id);


        }
    }
    public void setFragmentManager(IAlertFragmentManager pfragmentManager)
    {
        this.fragmentManager=pfragmentManager;

    }


    public void notifyFragmentManager()
    {
       // if(this.fragmentManager!=null)
         //   this.fragmentManager.gotoNextFragment();;

    }


    ///Private method for get settings
    protected RecordingSettings getSettings() {

        return new RecordingSettings(this.getContext());

    }

    protected String getPatientCode() {

        RecordingSettings settings = getSettings();

        return settings.getPatientID();

    }

    protected String getAccessToken() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this.getContext());

        if (settings != null) {
            return settings.getToken();


        }

        return null;
    }

}
