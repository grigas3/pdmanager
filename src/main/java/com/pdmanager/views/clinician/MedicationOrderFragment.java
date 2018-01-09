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

/**
 * Created by George on 6/5/2016.
 */
public class MedicationOrderFragment extends BasePDFragment implements PatientChartFragment.OnDestinationSelectedListener, IBasePatientChartFragment {
    //AbsListView listView;
    RadListView listView;
    ImageView image;
    //    PatientChartFragment.Destination destination;
    TextView titleView;
    TextView contentView;
    MedOrderAdapter adapter;
    private Patient patient;
    private TextView emptyList;
    private ProgressBar busyIndicator;

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

        //listView = (AbsListView) rootView.findViewById(R.id.listView);
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
//        if(savedInstanceState != null) {
        //          destination = savedInstanceState.getParcelable("currentAttraction");
        //    }

        //  if(destination == null) {
        //    return rootView;
        // }

      /*  View headerView = inflater.inflate(R.layout.listview_slideitem_header, listView, false);

        titleView = (TextView)headerView.findViewById(R.id.title);
        titleView.setText("Medications");
        titleView.setTextColor(Color.parseColor(("#D06744")));

        contentView = (TextView)headerView.findViewById(R.id.content);
        contentView.setText("View current prescription and past medication orders.");
        */

        //TextView attractionsView = (TextView)headerView.findViewById(R.id.attractions);
        //attractionsView.setTextColor(color);
        //attractionsView.setText("Current Prescription");

        //   listView.setHeaderView(headerView);

        int color = Color.parseColor("#D06744");
      /*  SwipeExecuteBehavior swipeExecuteBehavior = new SwipeExecuteBehavior();
        listView.addBehavior(swipeExecuteBehavior);

        SwipeExecuteBehavior.SwipeExecuteListener swipeExecuteListener =
                new SwipeExecuteBehavior.SwipeExecuteListener() {

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
        */
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        fab.setRippleColor(color);

        //   View line = rootView.findViewById(R.id.line);
        // line.setBackgroundColor(color);

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
                //Toast.makeText(getActivity(), "Enquiry sent.", Toast.LENGTH_SHORT).show();
            }
        });

        //  image = (ImageView)rootView.findViewById(R.id.image);
//        image.setImageResource(R.drawable.medication);

  /*      CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar_layout);
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
        //outState.putParcelable("currentAttraction", destination);
    }

    @Override
    public void onDestinationSelected(PatientChartFragment.Destination destination) {
        //this.destination = destination;
    }


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
}

