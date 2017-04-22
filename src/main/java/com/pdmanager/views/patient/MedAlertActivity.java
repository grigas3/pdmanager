package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.communication.DirectSenderTask;
import com.pdmanager.communication.IDirectSendCallback;
import com.pdmanager.controls.CircleButton;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class MedAlertActivity extends SoundFeedbackActivity implements IDirectSendCallback {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TextView description;
    TextView title;
    private LinearLayout mButtonConfirm;
    private LinearLayout mButtonReject;
    private ProgressBar busyIndicator;
    private RelativeLayout layout;
    private String medId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alertmed);
        setUp();
        speekInfo();
    }



    private void setUp()
    {

        final DirectSenderTask sender=new DirectSenderTask(RecordingSettings.GetRecordingSettings(getApplicationContext()).getToken(),this);

        title = (TextView) this.findViewById(R.id.alertText);
        description = (TextView) this.findViewById(R.id.alertDescription);

        mButtonConfirm = (LinearLayout) this.findViewById(R.id.confirm);

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                speak.silence();
                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                //   mButtonPatients.setVisibility(View.INVISIBLE);
                Date date1 = new java.util.Date();
                date1.setHours(0);
                date1.setMinutes(0);
                //Date date2= new java.util.Date(t2);
                Calendar cal1 = Calendar.getInstance();
                //Calendar cal2 = Calendar.getInstance();
                cal1.setTime(date1);
                Observation obs = new Observation(1, getPatientCode(), "PQMEDADH", cal1.getTimeInMillis());


                ArrayList<Observation> obsC = new ArrayList<>();
                obsC.add((obs));
                sender.execute(obsC);
                //new ConfirmMedTask(getPatientCode(), getAccessToken()).execute();
            }
        });

        mButtonReject = (LinearLayout) this.findViewById(R.id.reject);

        mButtonReject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                speak.silence();
                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                //   mButtonPatients.setVisibility(View.INVISIBLE);
                Date date1 = new Date();
                date1.setHours(0);
                date1.setMinutes(0);
                //Date date2= new java.util.Date(t2);
                Calendar cal1 = Calendar.getInstance();
                //Calendar cal2 = Calendar.getInstance();
                cal1.setTime(date1);
                Observation obs = new Observation(0, getPatientCode(),"PQMEDADH", cal1.getTimeInMillis());


                ArrayList<Observation> obsC = new ArrayList<>();
                obsC.add((obs));
                sender.execute(obsC);
                //new ConfirmMedTask(getPat


            }
        });
        busyIndicator = (ProgressBar) this.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);

        layout = (RelativeLayout) this.findViewById(R.id.mainLayout);


    }

    private void speekInfo()
    {

        if(speak!=null)

            speak.speakFlush(getApplicationContext().getString(R.string.patient_med_instructions));


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

    /*
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



            activateMainFragment();

        }
    }
    */

    @Override
    public void onPostDirectSend(boolean result) {
        busyIndicator.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);

        finishTest();
    }


    /*

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


            activateMainFragment();

            //    message.setVisibility(View.VISIBLE);
            //  mButtonPatients.setVisibility(View.VISIBLE);

        }
    }
            */


}