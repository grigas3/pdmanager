package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.controls.CircleButton;
import com.pdmanager.core.PDApplicationContext;
import com.pdmanager.core.R;
import com.pdmanager.core.alerting.IUserAlertManager;
import com.pdmanager.core.alerting.UserAlertManager;
import com.pdmanager.core.communication.CommunicationManager;
import com.pdmanager.core.communication.DirectSender;
import com.pdmanager.core.interfaces.IAlertFragmentManager;
import com.pdmanager.core.medication.MedManager;
import com.pdmanager.core.models.MedicationIntake;
import com.pdmanager.core.models.MedicationOrder;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.PendingMedication;
import com.pdmanager.core.models.UserAlert;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.views.BasePDFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MedAlertFragment extends AlertPDFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TextView description;
    TextView title;
    private CircleButton mButtonConfirm;
    private CircleButton mButtonReject;
    private ProgressBar busyIndicator;
    private RelativeLayout layout;
    private String currentAlertId=null;
    private PendingMedication currentMedOrder=null;

    public MedAlertFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alertmed, container, false);

        title = (TextView) rootView.findViewById(R.id.alertText);
        description = (TextView) rootView.findViewById(R.id.alertDescription);

        mButtonConfirm=(CircleButton) rootView.findViewById(R.id.confirm);

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
             //   mButtonPatients.setVisibility(View.INVISIBLE);

                new ConfirmMedTask(getPatientCode(), getAccessToken()).execute();
            }
        });


        mButtonReject=(CircleButton) rootView.findViewById(R.id.reject);

        mButtonReject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                //   mButtonPatients.setVisibility(View.INVISIBLE);

                new RejectMedTask(getPatientCode(), getAccessToken()).execute();
            }
        });
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);

        layout= (RelativeLayout) rootView.findViewById(R.id.mainLayout);





        //Get Saved Variables
        if (savedInstanceState != null) {


            restoreVariables(savedInstanceState);


        }
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            medId = bundle.getString(PDApplicationContext.INTENT_ALERT_ID, null);
            currentAlertId = bundle.getString(PDApplicationContext.INTENT_ALERT_SOURCE, null);

            //Get From Bundle

            if (medId != null) {
                MedManager manager = new MedManager(this.getContext());
                currentMedOrder = manager.getPendingMedication(medId);

            }
        }

        if(title!=null)
        {
            if(currentMedOrder!=null) {
                title.setText(getString(R.string.medalertpart1) + " " + currentMedOrder.Medication + " " +getString(R.string.medalertpart2) + " " + currentMedOrder.getTime());

            }
            else
            {
                title.setText("The allert is not valid");

            }
        }




        return rootView;
    }


    protected void restoreVariables(Bundle savedInstanceState) {


        currentAlertId=savedInstanceState.getString("CurrentAlertID");
        currentMedOrder = savedInstanceState.getParcelable("CurrentMedOrder");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CurrentAlertID", currentAlertId);
        outState.putParcelable("CurrentMedOrder", currentMedOrder);
        // Always call the superclass so it can save the view hierarchy state
        //super.onSaveInstanceState(outState);
        //outState.putParcelable("currentAttraction", destination);
    }


    private String medId;
  /*  @Override
    public void update(UserAlert alert ) {


        this.currentAlertId = alert.getId();
        this.medId=alert.getSource();


    }
    */
    @Override
    public void onResume()
    {
        super.onResume();

        if(title!=null)
        {
            if(currentMedOrder!=null) {
                title.setText(getString(R.string.medalertpart1) + " " + currentMedOrder.Medication + " " +getString(R.string.medalertpart2) + " " + currentMedOrder.getTime());

            }
            else
            {
                title.setText("The allert is not valid");

            }
        }

    }

    private class ConfirmMedTask extends AsyncTask<Void, Void, Boolean> {

        private String accessToken;
        private String patientCode;

        public ConfirmMedTask(String code, String a) {

            this.patientCode = code;
            this.accessToken = a;
        }



        @Override
        protected Boolean doInBackground(Void... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {



                if(currentMedOrder!=null) {
                    DirectSender sender = new DirectSender(accessToken);
                    CommunicationManager mCommManager = new CommunicationManager(sender);


                    {

                        Date date1 = new java.util.Date();
                        Date date2 = new java.util.Date(currentMedOrder.Time);
                        Calendar cal1 = Calendar.getInstance();
                        date1.setHours(date2.getHours());
                        date1.setMinutes(date2.getMinutes());
                        cal1.setTime(date1);
                        MedicationIntake intake = new MedicationIntake(getPatientCode(), currentMedOrder.MedOrderId, currentMedOrder.Medication, "Oral", currentMedOrder.Dose, currentMedOrder.Id + ";taken", cal1.getTimeInMillis());

                        mCommManager.SendItem(intake);
                    }


                    Date date1 = new java.util.Date();
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
                    mCommManager.SendItems(obsC, true);


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
            layout.setVisibility(View.VISIBLE);
            //TODO PROPERLY CHECK CONNECTION

            if(currentAlertId!=null)
            {

                UserAlertManager.newInstance(getContext()).setNotActive(currentAlertId);
            }


            notifyFragmentManager();

        }
    }

    private class RejectMedTask extends AsyncTask<Void, Void, Boolean> {

        private String accessToken;
        private String patientCode;

        public RejectMedTask(String code, String a) {

            this.patientCode = code;
            this.accessToken = a;
        }

        @Override
        protected Boolean doInBackground(Void... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {






                DirectSender sender = new DirectSender(accessToken);
                CommunicationManager mCommManager = new CommunicationManager(sender);




                    Date date1 = new Date();
                    date1.setHours(0);
                    date1.setMinutes(0);
                    //Date date2= new java.util.Date(t2);
                    Calendar cal1 = Calendar.getInstance();
                    //Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date1);
                    Observation obs = new Observation(1, patientCode, "NFG", cal1.getTimeInMillis());
                    obs.PatientId = patientCode;

                    ArrayList<Observation> obsC = new ArrayList<>();
                    obsC.add((obs));
                    mCommManager.SendItems(obsC);


                return true;

            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return false;
                // handle BandException
            }
        }


        protected void onPostExecute(Boolean result) {

            busyIndicator.setVisibility(View.INVISIBLE);
            layout.setVisibility(View.VISIBLE);
            //TODO PROPERLY CHECK CONNECTION

            if(currentAlertId!=null) {


                UserAlertManager.newInstance(getContext()).setNotActive(currentAlertId);

            }

                notifyFragmentManager();
        //    message.setVisibility(View.VISIBLE);
         //  mButtonPatients.setVisibility(View.VISIBLE);

        }
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


                    Date date1 = new java.util.Date();
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


                    Date date1 = new java.util.Date();
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
            layout.setVisibility(View.VISIBLE);
            //TODO PROPERLY CHECK CONNECTION


           // new MedAdminFragment.GetMedicationTask(patientCode, accessToken).execute();


        }
    }


    private class PatientMedicationResult

    {


        List<MedicationOrder> orders;
        List<MedicationIntake> intakes;
        private boolean error;

        public void setError(boolean b) {

            error = b;
        }

        public boolean hasError() {

            return error;
        }

    }

}