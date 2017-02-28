package com.pdmanager.views.clinician;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.adapters.AllergyAdapter;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.telerik.widget.list.RadListView;

import java.util.List;

/**
 *
 * Fragment from Pient Chart showing patient Allergies
 * Created by George on 6/5/2016.
 */
public class PatientAllergyFragment extends BasePDFragment implements PatientChartFragment.OnDestinationSelectedListener {
    RadListView listView;
    ImageView image;
    //    PatientChartFragment.Destination destination;
    TextView titleView;
    TextView contentView;
    private Patient patient;


    public PatientAllergyFragment() {
        // Required empty public constructor
    }

    public void setPatient(Patient p) {

        patient = p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_allergy, container, false);

        listView = (RadListView) rootView.findViewById(R.id.listView);
        if (savedInstanceState != null) {

            patient = savedInstanceState.getParcelable("Patient");

        }

//        if(savedInstanceState != null) {
        //          destination = savedInstanceState.getParcelable("currentAttraction");
        //    }

        //  if(destination == null) {
        //    return rootView;
        // }

        int color = Color.parseColor("#555555");

        // TextView attractionsView = (TextView)headerView.findViewById(R.id.attractions);
        // attractionsView.setTextColor(color);
        // attractionsView.setText("List of clinical information by Category");


        TextView emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        List<String> clinicalInfo = patient.getAllergies();
        if (clinicalInfo == null || clinicalInfo.size() == 0) {

            if (emptyList != null)
                emptyList.setVisibility(View.VISIBLE);
        } else {

            if (emptyList != null)
                emptyList.setVisibility(View.INVISIBLE);
        }


        AllergyAdapter adapter = new AllergyAdapter(clinicalInfo);


        listView.setAdapter(adapter);


        View line = rootView.findViewById(R.id.line);
        line.setBackgroundColor(color);


        image = (ImageView) rootView.findViewById(R.id.image);
        image.setImageResource(R.drawable.allergies);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setContentScrimColor(color);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_arrow);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
        //outState.putParcelable("currentAttraction", destination);
    }

    @Override
    public void onDestinationSelected(PatientChartFragment.Destination destination) {
        //this.destination = destination;
    }


}

