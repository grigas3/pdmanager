package com.pdmanager.views.clinician;

/**
 * Created by George on 1/30/2016.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.adapters.EventAdapter;
import com.pdmanager.adapters.FetchObservationTask;
import com.pdmanager.adapters.ObservationParams;
import com.pdmanager.interfaces.IEventDetailNavigator;
import com.pdmanager.interfaces.IObservationDataHandler;
import com.pdmanager.models.Observation;
import com.pdmanager.models.ObservationResult;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.telerik.android.common.Function;
import com.telerik.widget.list.ListViewDataSourceAdapter;
import com.telerik.widget.list.RadListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayAssessmentFragment extends BasePDFragment implements IObservationDataHandler {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    ListViewDataSourceAdapter adapter;
    RadListView listView;
    IEventDetailNavigator navigator;
    private Patient patient;
    private long day;
    private TextView emptyList;
    private ProgressBar busyIndicator;

    public DayAssessmentFragment() {
    }

    public void setPatient(Patient p) {

        patient = p;
    }

    public void setDay(long p) {

        day = p;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        // Save the user's current game state
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    public void setNavigator(IEventDetailNavigator nav) {
        navigator = nav;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clinician, container, false);


        //   mButtonPatients=(Button) rootView.findViewById(R.id.button);

        //  mButtonPatients.setOnClickListener(mButtonConnectClickListener);

        listView = (RadListView) rootView.findViewById(R.id.listView);

        if (savedInstanceState != null) {
            patient = savedInstanceState.getParcelable("Patient");


        }


        emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);
        emptyList.setVisibility(View.INVISIBLE);


        listView.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int itemPosition, MotionEvent motionEvent) {

                Observation obs = (Observation) adapter.getItem(itemPosition);


                if (navigator != null)
                    navigator.navigate(obs.getCode());

/*
                if (obs.getCategoryCode().equals("MOTOR") || obs.getCategoryCode().equals("ACT")) {

                    DayObsChartFragment newFragment = new DayObsChartFragment();
                    newFragment.setPatient(patient);

                    Bundle args = new Bundle();

                    args.putString("selectedCode", obs.getCode());
                    newFragment.setArguments(args);


                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.container, newFragment);

                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    transaction.commit();

                }


*/
            }

            @Override
            public void onItemLongClick(int itemPosition, MotionEvent motionEvent) {

            }


        });


        ArrayList<String> codes = new ArrayList<String>();

        Date date1 = new java.util.Date(day);
        date1.setHours(0);
        date1.setMinutes(0);
        //Date date2= new java.util.Date(t2);
        Calendar cal1 = Calendar.getInstance();
        //Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);

        long day1 = cal1.getTimeInMillis();
        //cal2.setTime(date2);


        /*codes.add("LID");
        codes.add("TREMOR_C");
        codes.add("MED_ADH");
        codes.add("ACT_STAND");
        codes.add("TEST_BS11");
        codes.add("TEST_NMSS");*/
        if (patient != null)
            FetchData(patient.Id, "LID;TREMOR_C;OFF;FOG;GAIT;BRAD;MED_ADH;TEST_BIS11;TEST_NMSS;NUTR_NRS2002;NUTR_MOP", day1, day1 + 24 * 60 * 60 * 1000, 4);


        //new GetObservationsTask(getAccessToken()).execute();
        return rootView;
    }


    protected void FetchData(String patient, String code, long from, long to, int aggregate) {

        emptyList.setVisibility(View.INVISIBLE);
        busyIndicator.setVisibility(View.VISIBLE);
        new FetchObservationTask(this, getAccessToken()).execute(new ObservationParams(patient, code, from, to, aggregate));


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