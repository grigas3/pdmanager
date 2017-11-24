package com.pdmanager.views.clinician;

import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.adapters.ClinicalInfoAdapter;
import com.pdmanager.adapters.DssInfoAdapter;
import com.pdmanager.models.ClinicalInfo;
import com.pdmanager.models.DssInfo;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.android.common.Function;
import com.telerik.widget.list.ListViewDataSourceAdapter;
import com.telerik.widget.list.RadListView;

import java.util.List;

/**
 * Created by IP on 9/10/2017.
 */
public class DssFragment extends BasePDFragment implements PatientChartFragment.OnDestinationSelectedListener, IBasePatientChartFragment {
    RadListView listView;
    ImageView image;
    //    PatientChartFragment.Destination destination;
    TextView titleView;
    TextView contentView;
    private Patient patient;


    public DssFragment() {
        // Required empty public constructor
    }

    @Override
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient p) {

        patient = p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_patient_dss, container, false);

        listView = (RadListView) rootView.findViewById(R.id.listView);
        if (savedInstanceState != null) {

            patient = savedInstanceState.getParcelable("Patient");

        }

        if (patient != null) {

            PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
            manager.setHeader(rootView);

        }


//        if(savedInstanceState != null) {
        //          destination = savedInstanceState.getParcelable("currentAttraction");
        //    }

        //  if(destination == null) {
        //    return rootView;
        // }

    /*   View headerView = inflater.inflate(R.layout.listview_slideitem_header, listView, false);

        titleView = (TextView)headerView.findViewById(R.id.title);
        titleView.setText("Clinical information");
        titleView.setTextColor(Color.parseColor(("#D06744")));
        int color=Color.parseColor("#555555");
        contentView = (TextView)headerView.findViewById(R.id.content);
        contentView.setText("View patient clinical inforrmation");
        */

        //TextView attractionsView = (TextView)headerView.findViewById(R.id.attractions);
        //attractionsView.setTextColor(color);
        //attractionsView.setText("List of clinical information by Category");

        //    listView.setHeaderView(headerView);
        TextView emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        List<DssInfo> dssInfo = patient.getDssInfo(); //Clinical Info list to change
        if (dssInfo == null || dssInfo.size() == 0) {

            if (emptyList != null)
                emptyList.setVisibility(View.VISIBLE);
        } else {

            if (emptyList != null)
                emptyList.setVisibility(View.INVISIBLE);
        }



        DssInfoAdapter adapter = new DssInfoAdapter(dssInfo);






/*
        Function<Object, Object> groupDescriptor = new Function<Object, Object>() {
            @Override
            public Object apply(Object object) {
                return ((DssInfo) object).getPriority();
            }
        };
*/

        listView.setAdapter(adapter);
//        adapter.addGroupDescriptor(groupDescriptor);

 /*
        TextView patientProfile = (TextView) rootView.findViewById(R.id.patientProfile);
        patientProfile.append("Activity: Patient's daily activity; \"high\" applies to relatively young or employed patients, \"low\" to older and retired ones\n" +
                "Impulsivity: \"mild\" if BIS<52, \"moderate\" when BIS in 52-71, \"severe\" if BIS>71\n" +
                "Non-motor: \"mild\" if NMSS<=20, \"moderate\": NMSS in 21-40, \"severe: if NMSS>70\n" +
                "Cognition: MOCA; \"severe\" when <18, \"moderate\" when between 18 and 22, and \"mild\" when >22.\n" +
                "Mood: \"moderate\" when mood is “sad” or “angry”, \"severe\" when mood is “awful”, and \"mild\" otherwise \n" +
                "UPDR Items: \"mild\" is UPDRS 0 or 1, \"moderate\" is UPDRS=2, and \"severe\" is UPDRS 3 or 4");
*/

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

