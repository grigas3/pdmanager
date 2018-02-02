package com.pdmanager.views.clinician;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.adapters.MedOrderAdapter;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.MedOrderListResult;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.android.common.Function;
import com.telerik.widget.list.RadListView;
import com.telerik.widget.list.SwipeExecuteBehavior;

import java.util.Collections;

/**
 * Created by George on 6/5/2016.
 */
public class MedicationOrderFragment extends BasePDFragment implements PatientChartFragment.OnDestinationSelectedListener, IBasePatientChartFragment {



    //region Variable declaration
    private RadListView listView;
    private MedOrderAdapter adapter;
    private Patient patient;
    private TextView emptyList;
    private ProgressBar busyIndicator;
    //endregion

    public MedicationOrderFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_patient_medicationorder, container, false);


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

        int color = Color.parseColor("#D06744");
        SwipeExecuteBehavior swipeExecuteBehavior = new SwipeExecuteBehavior();
        listView.addBehavior(swipeExecuteBehavior);
        //region    Swipe
        SwipeExecuteBehavior.SwipeExecuteListener swipeExecuteListener;
        swipeExecuteListener = new SwipeExecuteBehavior.SwipeExecuteListener() {

            @Override
            public void onSwipeStarted(int position) {
            }

            @Override
            public void onSwipeProgressChanged(int position, int offset, View swipeContent) {
            }

            @Override
            public void onSwipeEnded(int position, int offset) {
                int absOffset = Math.abs(offset);
                if (absOffset > 300) {

                    MedicationOrder order = (MedicationOrder) adapter.getItem(position);
                    if (order.getStatus().toLowerCase().equals("active")) {
                        busyIndicator.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        new CancelMedicationOrderTask(getAccessToken()).execute(order);


                    }

                    //   cityAdapter.remove(position);
                }
                if (adapter != null)
                    adapter.notifySwipeExecuteFinished();
            }

            @Override
            public void onExecuteFinished(int position) {
            }
        };

        swipeExecuteBehavior.addListener(swipeExecuteListener);
        //endregion

        //region Floating action to create a new Medication

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        fab.setRippleColor(color);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateMedicationOrderFragment newFragment = new CreateMedicationOrderFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                newFragment.setPatient(patient);
                transaction.replace(R.id.container, newFragment);

                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                transaction.commit();

            }
        });
        //endregion

        busyIndicator.setVisibility(View.VISIBLE);
        emptyList.setVisibility(View.INVISIBLE);
        new GetMedicationsTask(getAccessToken()).execute(patient.Id);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("Patient", patient);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestinationSelected(PatientChartFragment.Destination destination) {
        //this.destination = destination;
    }


    //region Remote methods to get and cancel a medication
    /**
     * Get Medications Task
     */
    private class GetMedicationsTask extends AsyncTask<String, Void, MedOrderListResult> {


        private final String accessToken;

        public GetMedicationsTask(String a) {
            accessToken = a;

        }

        @Override
        protected MedOrderListResult doInBackground(String... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);
                String code = clientParams[0];

                MedOrderListResult res = new MedOrderListResult();
                res.Data = receiver.GetMedicationOrders(code);


                return res;

            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }

        protected void onPostExecute(MedOrderListResult result) {

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

                //Sort Data by status
                //We want active to be the first in the list
                Collections.sort(result.Data);
                adapter = new MedOrderAdapter(result.Data);

                Function<Object, Object> groupDescriptor = new Function<Object, Object>() {
                    @Override
                    public Object apply(Object object) {
                        return ((MedicationOrder) object).getStatus();
                    }
                };

                adapter.addGroupDescriptor(groupDescriptor); // to be added again if group description
                listView.setAdapter(adapter);


            }


        }
    }

    /**
     * Cancel Medication Task
     */
    private class CancelMedicationOrderTask extends AsyncTask<MedicationOrder, Void, Boolean> {


        private final String accessToken;

        public CancelMedicationOrderTask(String a) {
            accessToken = a;


        }

        @Override
        protected Boolean doInBackground(MedicationOrder... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {

                MedicationOrder params = clientParams[0];
                DirectSender sender = new DirectSender(getAccessToken());
                CommunicationManager mCommManager = new CommunicationManager(sender);

                params.setStatus("canceled");

                mCommManager.UpdateItem(params);

                return true;


            } catch (Exception ex) {

                // Util.handleException("Getting data", ex);
                return false;
                // handle BandException
            }
        }

        protected void onPostExecute(Boolean result) {

            busyIndicator.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.INVISIBLE);

            new GetMedicationsTask(getAccessToken()).execute(patient.Id);
        }
    }
    //endregion

}


