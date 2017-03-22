package com.pdmanager.views.patient;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;

import com.pdmanager.R;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.interfaces.IAlertFragmentManager;
import com.pdmanager.models.UserAlert;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.persistance.Preferences;
import com.pdmanager.views.patient.cognition.tools.Speak;
import com.pdmanager.views.patient.cognition.tools.Tones;

/**
 * Created by george on 15/1/2017.
 */

public abstract class AlertPDFragment extends DialogFragment {


    protected Speak speak;
    protected Tones tones;
    protected Preferences prefs;

    @Override
    public  void onPause()
    {
        super.onPause();
        speak.silence();
        tones.shutdown();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        speak = Speak.getInstance(getContext());
        tones = Tones.getInstance();
        prefs = new Preferences(getContext());
    }

    private UserAlert currentAlert;
    IAlertFragmentManager fragmentManager;
    /**
     * Release alert
     */
    public void release()
    {
      /*  if(currentAlert!=null)
        {
            UserAlertManager.newInstance(getContext()).setNotActive(currentAlert.Id);


        }
        */
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


    protected void activateMainFragment()
    {
        PatientHomeFragment patientHomeFragment = new PatientHomeFragment();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, patientHomeFragment);
        fragmentTransaction.commit();
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
