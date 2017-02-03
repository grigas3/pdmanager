package com.pdmanager.views.patient;

/**
 * Created by George on 1/30/2016.
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.controls.CircleButton;
import com.pdmanager.core.R;
import com.pdmanager.core.alerting.UserAlertManager;
import com.pdmanager.core.communication.CommunicationManager;
import com.pdmanager.core.communication.DirectSender;
import com.pdmanager.core.medication.MedManager;
import com.pdmanager.core.models.MedTiming;
import com.pdmanager.core.models.MedicationOrder;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.UserAlert;
import com.pdmanager.core.notification.LocalNotificationTask;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.views.BasePDFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class NotFeelingGoodFragment extends DialogFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    TextView mTextNotEnabled;
    TextView message;
    private CircleButton mButtonPatients;
    private Button mButtonCancel;
    private RelativeLayout busyIndicator;
    private RelativeLayout layout;
    public NotFeelingGoodFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotFeelingGoodFragment newInstance(int sectionNumber) {
        NotFeelingGoodFragment fragment = new NotFeelingGoodFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notfeelingood, container, false);


        message = (TextView) rootView.findViewById(R.id.textManagement);

        mButtonPatients=(CircleButton) rootView.findViewById(R.id.buttonNotFeelingGood);
        mButtonCancel=(Button) rootView.findViewById(R.id.cancel_btn);

        mButtonPatients.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                busyIndicator.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);


                new SaveNotFeelingGoodTask(getPatientCode(), getAccessToken()).execute();
            }
        });


        mButtonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                    dismiss();
            }
        });

        layout=(RelativeLayout)  rootView.findViewById(R.id.mainLayout);

        mTextNotEnabled=(TextView) rootView.findViewById(R.id.textNotEnabled);

        busyIndicator = (RelativeLayout) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);


      //  fragment.update(alert);

        updateLayout();



        return rootView;
    }

    ///Private method for get settings
    protected RecordingSettings getSettings() {


        return new RecordingSettings(this.getContext());


    }
    protected String getPatientCode() {

        RecordingSettings settings = getSettings();

        return settings.getPatientID();

    }

    protected String getAccessToken() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this.getContext());

        if (settings != null) {
            return settings.getToken();


        }

        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateLayout();


    }



    @Override
    public void onDestroy()
    {

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            timeRunning=false;
        }

        super.onDestroy();

    }

private boolean timeRunning=false;

    Timer timer=null;


    private void updateLayout()
    {


        //Update on UI Thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                long t=(System.currentTimeMillis());

                if((t- RecordingSettings.newInstance(getContext()).getLastNFG())>5/**60*/*1000) {

                    if(mButtonPatients!=null) {
                        mButtonPatients.setEnabled(true);
                        mButtonPatients.setColor(Color.RED);


                    }

                    if(mTextNotEnabled!=null)
                        mTextNotEnabled.setVisibility(View.INVISIBLE);

                }
                else

                {
                    if(mButtonPatients!=null) {
                        mButtonPatients.setEnabled(false);
                        mButtonPatients.setColor(Color.GRAY);
                    }

                    if(mTextNotEnabled!=null)
                        mTextNotEnabled.setVisibility(View.VISIBLE);
                    if(!timeRunning) {
                        timeRunning=true;
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new mainTask(), 30000, 30000);

                    }

                }
            }
        });



    }


    private class mainTask extends TimerTask {


        public mainTask() {


        }

        public void run() {

            updateLayout();
        }
    }
    private class SaveNotFeelingGoodTask extends AsyncTask<Void, Void, Boolean> {

        private String accessToken;
        private String patientCode;

        public SaveNotFeelingGoodTask(String code, String a) {

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
                    mCommManager.SendItems(obsC,true);




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

            long t=(System.currentTimeMillis());
            //TODO PROPERLY CHECK CONNECTION


/*
           UserAlertManager amanager=new UserAlertManager(getContext());
            MedManager manager=new MedManager(getContext());
           amanager.clearAll();
          manager.clearMedOrders();
            MedicationOrder order=new MedicationOrder();
            order.Instructions="Test";
            order.PatientId="01";
            order.MedicationId="LEVODOPA";
            order.Reason="Test";

            ArrayList<MedTiming> timings=new ArrayList<MedTiming>();
            MedTiming timing=new MedTiming();
            timing.Dose="100mg";

            timing.Time=t+1*60*1000;
            timings.add(timing);
            order.setTimings(timings);

            manager.addMedicationOrder(order);

            //LocalNotificationTask.newInstance(getContext()).execute(new UserAlert("Test","Test","MED",0,0,"MED"));

*/

            RecordingSettings.newInstance(getContext()).setLastNFG(t);
            updateLayout();

            //Dismiss
            dismiss();

        }
    }




}