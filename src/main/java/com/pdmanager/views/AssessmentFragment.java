package com.pdmanager.views;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.core.R;
import com.pdmanager.core.adapters.EventAdapter;
import com.pdmanager.core.communication.DataReceiver;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.ObservationResult;
import com.pdmanager.core.models.Patient;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.android.common.Function;
import com.telerik.widget.list.ListViewDataSourceAdapter;
import com.telerik.widget.list.RadListView;

import java.util.List;

/**
 * Created by George on 6/5/2016.
 */
public class AssessmentFragment extends BasePDFragment implements PatientChartFragment.OnDestinationSelectedListener, IBasePatientChartFragment {
    RadListView listView;
    ListViewDataSourceAdapter adapter;
    private Patient patient;
    private TextView emptyList;
    private ProgressBar busyIndicator;

    public AssessmentFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_patient_assessment, container, false);

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


        int color = Color.parseColor("#D06744");

        emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.VISIBLE);
        emptyList.setVisibility(View.INVISIBLE);
        /*

        View headerView = inflater.inflate(R.layout.listview_slideitem_header, listView, false);

        titleView = (TextView)headerView.findViewById(R.id.title);
        titleView.setText("Patient Assessment");
        titleView.setTextColor(Color.parseColor(("#D06744")));

        contentView = (TextView)headerView.findViewById(R.id.content);
        contentView.setText("View patient overral assessment");

        //TextView attractionsView = (TextView)headerView.findViewById(R.id.attractions);
        //attractionsView.setTextColor(color);
        //attractionsView.setText("Current Prescription");

        listView.setHeaderView(headerView);
        View line = rootView.findViewById(R.id.line);
        line.setBackgroundColor(color);


        image = (ImageView)rootView.findViewById(R.id.image);
        image.setImageResource(R.drawable.image004);

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
        */
        /**/

        listView.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int itemPosition, MotionEvent motionEvent) {

                Observation obs = (Observation) adapter.getItem(itemPosition);


                //     if(obs.getCategoryCode().equals("MOTOR")||obs.getCategoryCode().equals("ACT")) {

                RObservationChartFragment newFragment = new RObservationChartFragment();
                newFragment.setPatient(patient);

                Bundle args = new Bundle();

                args.putString("selectedCode", obs.getCode());
                newFragment.setArguments(args);


                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.container, newFragment);

                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                transaction.commit();

                //     }


            }

            @Override
            public void onItemLongClick(int itemPosition, MotionEvent motionEvent) {

            }


        });


        new GetAssessmentTask(patient.Id, getAccessToken()).execute();


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


    private class GetAssessmentTask extends AsyncTask<Void, Void, ObservationResult> {


        private String code;
        private String accessToken;

        public GetAssessmentTask(String pcode, String a) {


            this.code = pcode;
            this.accessToken = a;

        }


        @Override
        protected ObservationResult doInBackground(Void... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);


                //  List<Observation> total=new ArrayList<>();

                List<Observation> total = receiver.GetObservations(code, "LID;TREMOR_C;OFF;FOG;GAIT;BRAD;MED_ADH;TEST_BIS11;TEST_NMSS;NUTR_NRS2002;NUTR_MOP", 0, 0, 4);
/*
for(Observation o:lid)
{
    o.setPatientId(code);
    o.setCode("LID");

}

                List<Observation> tremor=receiver.GetObservations(code,"TREMOR_C",0,0,4);

                for(Observation o:tremor)
                {
                    o.setPatientId(code);
                    o.setCode("TREMOR_C");

                }
                List<Observation> medadh=receiver.GetObservations(code,"MED_ADH",0,0,4);

                for(Observation o:medadh)
                {
                    o.setPatientId(code);
                    o.setCode("MED_ADH");

                }


                List<Observation> stand=receiver.GetObservations(code,"ACT_STAND",0,0,4);

                for(Observation o:stand)
                {
                    o.setPatientId(code);
                    o.setCode("ACT_STAND");

                }


                List<Observation> test=receiver.GetObservations(code,"TEST_BS11",0,0,4);

                for(Observation o:test)
                {
                    o.setPatientId(code);
                    o.setCode("TEST_BS11");

                }     List<Observation> test2=receiver.GetObservations(code,"TEST_NMSS",0,0,4);

                for(Observation o:test2)
                {
                    o.setPatientId(code);
                    o.setCode("TEST_NMSS");

                }

                total.addAll(tremor);
                total.addAll(lid);
                total.addAll(medadh);
                total.addAll(stand);
                total.addAll(test2);
                total.addAll(test);
*/
                ObservationResult res = new ObservationResult();
                res.observations = total;

                return res;
            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }


        protected void onPostExecute(ObservationResult result) {
            busyIndicator.setVisibility(View.INVISIBLE);
            if (result != null && result.observations != null) {


                if (result.observations.size() == 0) {

                    if (emptyList != null)
                        emptyList.setVisibility(View.VISIBLE);

                }
                adapter = new EventAdapter(result.observations);

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


}

