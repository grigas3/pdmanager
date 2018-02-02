package com.pdmanager.views.clinician;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.adapters.DssInfoAdapter;
import com.pdmanager.adapters.MedOrderAdapter;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.models.DssInfo;
import com.pdmanager.models.DssResult;
import com.pdmanager.models.MedOrderListResult;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.android.common.Function;
import com.telerik.widget.list.RadListView;

import java.util.Collections;
import java.util.List;

/**
 * Created by IP on 9/10/2017.
 */
public class DssFragment extends BasePDFragment implements PatientChartFragment.OnDestinationSelectedListener, IBasePatientChartFragment {
    RadListView listView;
    private Patient patient;
    private TextView emptyList;
    private ProgressBar busyIndicator;
    private DssInfoAdapter adapter;
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
        emptyList = (TextView) rootView.findViewById(R.id.busy_EmptyIndicator);
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.VISIBLE);
        emptyList.setVisibility(View.INVISIBLE);

        busyIndicator.setVisibility(View.VISIBLE);
        emptyList.setVisibility(View.INVISIBLE);
        new GetDSSTask(getAccessToken()).execute("MedicationChange",patient.Id);


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



    //region Remote methods to get and cancel a medication
    /**
     * Get Medications Task
     */
    private class GetDSSTask extends AsyncTask<String, Void, DssResult> {


        private final String accessToken;

        public GetDSSTask(String a) {
            accessToken = a;

        }

        @Override
        protected DssResult doInBackground(String... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);
                String code = clientParams[0];
                String patientId = clientParams[1];

                DssResult res = new DssResult();
                res.Data = receiver.GetDss(code,patientId);


                return res;

            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }

        protected void onPostExecute(DssResult result) {

            busyIndicator.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            //TODO PROPERLY CHECK CONNECTION
            if (result != null && result.Data != null) {

                if (result.Data == null || result.Data.size() == 0) {

                    if (emptyList != null)
                        emptyList.setVisibility(View.VISIBLE);
                } else {

                    if (emptyList != null)
                        emptyList.setVisibility(View.INVISIBLE);
                }

                //Sort Data ???
                //Collections.sort(result.Data);
                adapter = new DssInfoAdapter(result.Data);
                listView.setAdapter(adapter);


            }


        }
    }


}

