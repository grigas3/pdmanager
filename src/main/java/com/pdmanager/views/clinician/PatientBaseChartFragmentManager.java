package com.pdmanager.views.clinician;


import android.view.View;
import android.widget.TextView;

import com.pdmanager.core.R;
import com.pdmanager.core.models.Patient;
import com.pdmanager.views.drawers.IBasePatientChartFragment;

/**
 * Created by George on 6/5/2016.
 */
public class PatientBaseChartFragmentManager {

    IBasePatientChartFragment handler;

    /***
     * @param p
     */
    public PatientBaseChartFragmentManager(IBasePatientChartFragment p) {


        handler = p;

    }

    private void SafeSet(View rootView, int id, String s) {

        TextView text = ((TextView) rootView.findViewById(id));
        if (text != null)
            text.setText(s);

    }

    public void setHeader(View rootView) {

        Patient patient = handler.getPatient();
        SafeSet(rootView, (R.id.nameView), (patient.Family + " " + patient.Given));
        SafeSet(rootView, (R.id.phoneView), ("Phone: " + patient.Telecom));
        SafeSet(rootView, (R.id.mrnView), ("MRN: " + patient.MRN));
        SafeSet(rootView, (R.id.ywpView), ("YWP: " + patient.YWP));
        SafeSet(rootView, (R.id.dobView), ("DOB: " + patient.BirthDate));
        SafeSet(rootView, (R.id.lastVisitView), ("Last Visit: " + patient.LastVisitDate));
        SafeSet(rootView, (R.id.statusView), (patient.Status));


    }

}
