package com.pdmanager.views;

/**
 * Created by George on 1/30/2016.
 */

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pdmanager.core.R;
import com.pdmanager.core.adapters.EventAdapter;
import com.pdmanager.core.adapters.FetchObservationTask;
import com.pdmanager.core.adapters.ObservationParams;
import com.pdmanager.core.interfaces.IObservationDataHandler;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.ObservationResult;
import com.pdmanager.core.models.Patient;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.android.common.Function;
import com.telerik.widget.list.RadListView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TestAssessmentFragment extends BasePDFragment implements IObservationDataHandler, IBasePatientChartFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RadListView listView;
    private Patient patient;
    private TextView emptyList;
    private ProgressBar busyIndicator;

    public TestAssessmentFragment() {
    }

    @Override
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient p) {

        patient = p;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        // Save the user's current game state
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_test, container, false);


        //   mButtonPatients=(Button) rootView.findViewById(R.id.button);

        //  mButtonPatients.setOnClickListener(mButtonConnectClickListener);

        listView = (RadListView) rootView.findViewById(R.id.listView);


        if (patient != null) {

            PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
            manager.setHeader(rootView);

        }
        if (savedInstanceState != null) {
            patient = savedInstanceState.getParcelable("Patient");
        }


        emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);
        emptyList.setVisibility(View.INVISIBLE);
        ArrayList<String> codes = new ArrayList<String>();
        int color = Color.parseColor("#D06744");

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        fab.setRippleColor(color);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.


                TestSelectionFragment newFragment = TestSelectionFragment.newInstance(patient.Id, getAccessToken());

                newFragment.show(ft, "dialog");


/*
                CreateMedicationOrderFragment newFragment=new CreateMedicationOrderFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                newFragment.setPatient(patient);
                transaction.replace(R.id.container, newFragment);

                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                transaction.commit();*/
                //Toast.makeText(getActivity(), "Enquiry sent.", Toast.LENGTH_SHORT).show();
            }
        });


        //cal2.setTime(date2);


        if (patient != null)
            FetchData(patient.Id, "TEST_BIS11;TEST_NMSS", 0, 0, 1);
        //new GetObservationsTask(getAccessToken()).execute();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        ArrayList<String> codes = new ArrayList<String>();

        if (patient != null)
            FetchData(patient.Id, "TEST_BIS11;TEST_NMSS", 0, 0, 0);
    }

    protected void FetchData(String patient, String codes, long from, long to, int aggregate) {

        emptyList.setVisibility(View.INVISIBLE);
        busyIndicator.setVisibility(View.VISIBLE);
        new FetchObservationTask(this, getAccessToken()).execute(new ObservationParams(patient, codes, from, to, aggregate));


    }


    @Override
    public void onObservationReceived(ObservationResult result) {
        //TODO PROPERLY CHECK CONNECTION
        busyIndicator.setVisibility(View.INVISIBLE);
        if (result != null && result.observations != null) {

            if (result.observations.size() == 0) {


                emptyList.setVisibility(View.VISIBLE);
                listView.setVisibility(View.INVISIBLE);
            } else {

                emptyList.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);


            }
            EventAdapter adapter = new EventAdapter(result.observations);
            Function<Object, Object> groupDescriptor = new Function<Object, Object>() {
                @Override
                public Object apply(Object object) {
                    return ((Observation) object).getCategory();
                }
            };


            adapter.addGroupDescriptor(groupDescriptor);


            listView.setAdapter(adapter);
        }
    }
}