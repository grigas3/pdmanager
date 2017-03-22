package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.controls.CircleButton;
import com.pdmanager.models.Observation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private String medId;


    public MedAlertFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alertmed, container, false);

        title = (TextView) rootView.findViewById(R.id.alertText);
        description = (TextView) rootView.findViewById(R.id.alertDescription);

        mButtonConfirm = (CircleButton) rootView.findViewById(R.id.confirm);

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                speak.silence();
                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                //   mButtonPatients.setVisibility(View.INVISIBLE);

                new ConfirmMedTask(getPatientCode(), getAccessToken()).execute();
            }
        });

        mButtonReject = (CircleButton) rootView.findViewById(R.id.reject);

        mButtonReject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                speak.silence();
                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                //   mButtonPatients.setVisibility(View.INVISIBLE);

                new RejectMedTask(getPatientCode(), getAccessToken()).execute();
            }
        });
        busyIndicator = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);

        layout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);

        //Get Saved Variables
        if (savedInstanceState != null) {

            restoreVariables(savedInstanceState);


        }
        Bundle bundle = this.getArguments();
        speekInfo();

        return rootView;
    }


    private void speekInfo()
    {

        if(speak!=null)
        speak.speakFlush(getContext().getString(R.string.patient_med_instructions));


    }
    protected void restoreVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    /*  @Override
      public void update(UserAlert alert ) {


          this.currentAlertId = alert.getId();
          this.medId=alert.getSource();


      }
      */
    @Override
    public void onResume() {
        super.onResume();


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

                DirectSender sender = new DirectSender(accessToken, getContext());
                CommunicationManager mCommManager = new CommunicationManager(sender);

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

          /*   if(currentAlertId!=null)
            {

                UserAlertManager.newInstance(getContext()).setNotActive(currentAlertId);
            }
            */

            activateMainFragment();

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

                DirectSender sender = new DirectSender(accessToken, getContext());
                CommunicationManager mCommManager = new CommunicationManager(sender);

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

         /*   if(currentAlertId!=null) {


                UserAlertManager.newInstance(getContext()).setNotActive(currentAlertId);

            }
            */
            activateMainFragment();

            //    message.setVisibility(View.VISIBLE);
            //  mButtonPatients.setVisibility(View.VISIBLE);

        }
    }


}