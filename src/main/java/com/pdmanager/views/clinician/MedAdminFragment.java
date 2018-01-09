package com.pdmanager.views.clinician;

/**
 * Created by George on 1/30/2016.
 */

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.adapters.PendingMedOrderAdapter;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.MedTiming;
import com.pdmanager.models.MedicationIntake;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.Observation;
import com.pdmanager.models.PatientMedicationResult;
import com.pdmanager.models.PendingMedication;
import com.pdmanager.views.BasePDFragment;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.RadListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MedAdminFragment extends BasePDFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RadListView listView;
    ListViewAdapter adapter;
    private Button mButtonPatients;
    private TextView emptyList;
    private ProgressBar busyIndicator;

    public MedAdminFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MedAdminFragment newInstance(int sectionNumber) {
        MedAdminFragment fragment = new MedAdminFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
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
        listView.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int itemPosition, MotionEvent motionEvent) {


                if (adapter != null) {
                    final PendingMedication pmed = (PendingMedication) adapter.getItem(itemPosition);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// Add the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Date date1 = new Date();
                            Date date2 = new Date(pmed.Time);
                            Calendar cal1 = Calendar.getInstance();
                            date1.setHours(date2.getHours());
                            date1.setMinutes(date2.getMinutes());
                            cal1.setTime(date1);
                            MedicationIntake intake = new MedicationIntake(getPatientCode(), pmed.MedOrderId, pmed.Medication, "Oral", pmed.Dose, pmed.Id + ";taken", cal1.getTimeInMillis());
                            new SaveMedicationTask(getPatientCode(), getAccessToken()).execute(intake);


                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                            Date date1 = new Date();
                            Date date2 = new Date(pmed.Time);
                            Calendar cal1 = Calendar.getInstance();
                            date1.setHours(date2.getHours());
                            date1.setMinutes(date2.getMinutes());
                            cal1.setTime(date1);

                            MedicationIntake intake = new MedicationIntake(getPatientCode(), pmed.MedOrderId, pmed.Medication, "Oral", pmed.Dose, pmed.Id + ";missed", cal1.getTimeInMillis());
                            new SaveMedicationTask(getPatientCode(), getAccessToken()).execute(intake);


                        }
                    });
                    builder.setMessage("Did you take your pill?")
                            .setTitle("Medication");
                    AlertDialog dialog = builder.create();

                    dialog.show();


                }


            }

            @Override
            public void onItemLongClick(int itemPosition, MotionEvent motionEvent) {

            }
        });


        emptyList.setVisibility(View.INVISIBLE);
        busyIndicator.setVisibility(View.VISIBLE);


        new GetMedicationTask(getPatientCode(), getAccessToken()).execute();
        return rootView;
    }


    private class SaveMedicationTask extends AsyncTask<MedicationIntake, Void, Boolean> {

        private String accessToken;
        private String patientCode;

        public SaveMedicationTask(String code, String a) {

            this.patientCode = code;
            this.accessToken = a;
        }

        @Override
        protected Boolean doInBackground(MedicationIntake... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {


                MedicationIntake params = clientParams[0];


                DirectSender sender = new DirectSender(accessToken);
                CommunicationManager mCommManager = new CommunicationManager(sender);
                mCommManager.SendItem(params);


                if (params.Note != null && params.getTaken()) {


                    Date date1 = new Date();
                    date1.setHours(0);
                    date1.setMinutes(0);
                    //Date date2= new java.util.Date(t2);
                    Calendar cal1 = Calendar.getInstance();
                    //Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date1);
                    Observation obs = new Observation(1, patientCode, "MED_ADH", cal1.getTimeInMillis());
                    obs.PatientId = patientCode;

                    ArrayList<Observation> obsC = new ArrayList<>();
                    obsC.add((obs));
                    mCommManager.SendItems(obsC);
                } else

                {


                    Date date1 = new Date();
                    date1.setHours(0);
                    date1.setMinutes(0);
                    //Date date2= new java.util.Date(t2);
                    Calendar cal1 = Calendar.getInstance();
                    //Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date1);
                    Observation obs = new Observation(0, patientCode, "MED_ADH", cal1.getTimeInMillis());
                    obs.PatientId = patientCode;
                    ArrayList<Observation> obsC = new ArrayList<>();
                    obsC.add((obs));
                    mCommManager.SendItems(obsC);


                }


                return true;

            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return false;
                // handle BandException
            }
        }


        protected void onPostExecute(Boolean result) {

            busyIndicator.setVisibility(View.INVISIBLE);
            //TODO PROPERLY CHECK CONNECTION

            new GetMedicationTask(patientCode, accessToken).execute();


        }
    }


    private class GetMedicationTask extends AsyncTask<Void, Void, PatientMedicationResult> {

        private String accessToken;
        private String patientCode;

        public GetMedicationTask(String code, String a) {

            this.patientCode = code;
            this.accessToken = a;
        }

        @Override
        protected PatientMedicationResult doInBackground(Void... clientParams) {
            PatientMedicationResult res = new PatientMedicationResult();
            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);


                res.orders = receiver.GetMedicationOrders(patientCode);

                Date date1 = new Date();
                date1.setHours(0);
                date1.setMinutes(0);
                //Date date2= new java.util.Date(t2);
                Calendar cal1 = Calendar.getInstance();
                //Calendar cal2 = Calendar.getInstance();
                cal1.setTime(date1);
                long from = date1.getTime();
                res.intakes = receiver.GetMedicationIntakes(patientCode, from, from + 24 * 60 * 60 * 1000);

                res.setError(false);
                return res;

            } catch (Exception ex) {
                res.setError(true);
                //Util.handleException("Getting data", ex);

                // handle BandException
            }
            return res;
        }


        private boolean timelyTaken(MedicationIntake i, MedicationOrder o) {


            return true;

        }


        private String getStatus(long t) {


            Date date1 = new Date(t);
            Date date2 = new Date();
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);
            int m1 = cal1.get(Calendar.HOUR_OF_DAY) * 60 + cal1.get(Calendar.MINUTE);
            int m2 = cal2.get(Calendar.HOUR_OF_DAY) * 60 + cal2.get(Calendar.MINUTE);
            if (m1 < m2) {


                if (m1 + 30 < m2)
                    return "delay";
                else
                    return "get";


            }
            return "pending";

        }

        private boolean shouldTake(MedTiming o) {


            Date date1 = new Date(o.Time);
            Date date2 = new Date();
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);
            return cal1.get(Calendar.HOUR_OF_DAY) * 60 + cal1.get(Calendar.MINUTE) < cal2.get(Calendar.HOUR_OF_DAY) * 60 + cal2.get(Calendar.MINUTE);
//return true;


        }

        private List<PendingMedication> filterCurrentOrders(PatientMedicationResult res) {

            List<PendingMedication> pmeds = new ArrayList<>();
            if (!res.hasError()) {


                for (MedicationOrder o : res.orders) {
                    if (o.getStatus().toLowerCase().equals("active")) {
                        for (MedTiming t : o.getTimings()) {

                            boolean found = false;
                            for (MedicationIntake i : res.intakes) {
                                if (i.MedOrderId.equals(o.Id) && t.Id.equals(i.getTimingId())) {


                                    found = true;
                                    break;
                                }
                            }


                            if (!found) {

                                if (shouldTake(t)) {
                                    pmeds.add(new PendingMedication(t.Id, o.Id, o.MedicationId, t.Time, t.Dose, o.Instructions, getStatus(t.Time)));
                                }

                            }

                        }


                    }
                }

            }
            return pmeds;


        }

        protected void onPostExecute(PatientMedicationResult result) {

            busyIndicator.setVisibility(View.INVISIBLE);

            List<PendingMedication> res = filterCurrentOrders(result);
            //TODO PROPERLY CHECK CONNECTION
            if (res != null) {
                if (res.size() == 0) {


                    emptyList.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                } else {

                    emptyList.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.VISIBLE);
                }
                adapter = new PendingMedOrderAdapter(res);


                listView.setAdapter(adapter);
            }


        }
    }


}