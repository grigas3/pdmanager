package com.pdmanager.views.clinician;

/**
 * Created by George on 1/30/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pdmanager.app.PDApplicationContext;
import com.pdmanager.R;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.telerik.widget.list.ListViewAdapter;
import com.telerik.widget.list.RadListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PatientMedicationFragment extends BasePDFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    RadListView listView;
    ListViewAdapter adapter;
    private Button mButtonPatients;
    ///Connect button listener
    private View.OnClickListener mButtonConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {


            //new GetObservationsTask().execute(new ObservationParams("TEST01","001"));

            //new GetCodesTask().execute();
            //    new GetPatientsTask().execute();


        }
    };

    public PatientMedicationFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PatientMedicationFragment newInstance(int sectionNumber) {
        PatientMedicationFragment fragment = new PatientMedicationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medication, container, false);


        //   mButtonPatients=(Button) rootView.findViewById(R.id.button);

        //  mButtonPatients.setOnClickListener(mButtonConnectClickListener);

        listView = (RadListView) rootView.findViewById(R.id.listView);


        listView.addItemClickListener(new RadListView.ItemClickListener() {
            @Override
            public void onItemClick(int itemPosition, MotionEvent motionEvent) {


                if (adapter != null) {
                    Patient p = (Patient) adapter.getItem(itemPosition);


                    Intent mainIntent = new Intent(getActivity(), PatientChartActivity.class);
                    mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_CODE, p);
                    //mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_NAME, p.Given + " " + p.Family);
                    getActivity().startActivity(mainIntent);


                }


            }

            @Override
            public void onItemLongClick(int itemPosition, MotionEvent motionEvent) {

            }
        });


        // new GetPatientsTask().execute();
        return rootView;
    }



/*
    private class GetPatientsTask extends AsyncTask<Void, Void, PatientListResult> {


        @Override
        protected PatientListResult doInBackground(Void... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver=new DataReceiver();


                return receiver.GetPatients();



            }
            catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }



        protected void onPostExecute(PatientListResult result) {

            //TODO PROPERLY CHECK CONNECTION
if(result!=null&&result.Data!=null) {
    adapter = new PatientAdapter(result.Data);


    listView.setAdapter(adapter);
}



        }
    }


*/

}