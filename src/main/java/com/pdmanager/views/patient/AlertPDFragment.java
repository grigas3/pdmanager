package com.pdmanager.views.patient;

import com.pdmanager.core.alerting.IUserAlertManager;
import com.pdmanager.core.alerting.UserAlertManager;
import com.pdmanager.core.interfaces.IAlertFragmentManager;
import com.pdmanager.core.models.UserAlert;
import com.pdmanager.views.BasePDFragment;

/**
 * Created by george on 15/1/2017.
 */

public abstract class AlertPDFragment extends BasePDFragment{




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
        if(this.fragmentManager!=null)
            this.fragmentManager.gotoNextFragment();;

    }

    public abstract void update(UserAlert alert);


}
