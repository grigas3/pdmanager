package com.pdmanager.views;

/**
 * Created by George on 1/30/2016.
 */

import android.os.Bundle;
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
import com.pdmanager.core.models.ObservationResult;
import com.pdmanager.core.models.Patient;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.RadListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayEventsFragment extends BasePDFragment implements IObservationDataHandler {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RadListView listView;
    ListViewAdapter adapter;
    private Patient patient;
    private long day;
    private TextView emptyList;
    private ProgressBar busyIndicator;

    public DayEventsFragment() {
    }

    public void setPatient(Patient p) {

        patient = p;
    }

    public void setDay(long p) {

        day = p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clinician, container, false);


        //   mButtonPatients=(Button) rootView.findViewById(R.id.button);

        //  mButtonPatients.setOnClickListener(mButtonConnectClickListener);

        listView = (RadListView) rootView.findViewById(R.id.listView);


        emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);
        emptyList.setVisibility(View.INVISIBLE);
        //FetchData(patient.Id,"EVENT_TEST", day-1*60*60*1000, day + 24 * 60 * 60 * 1000,0);

        FetchData(patient.Id, "EVENT_TEST", 0, 0, 0);
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


            listView.setAdapter(adapter);
        }
    }
}