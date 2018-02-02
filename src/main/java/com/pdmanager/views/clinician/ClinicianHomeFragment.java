package com.pdmanager.views.clinician;

/**
 * Created by George on 1/30/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.views.BasePDFragment;

/**
 * A placeholder fragment containing a simple view.
 */
public class ClinicianHomeFragment extends BasePDFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private Button mButtonPatients;


    ///Connect button listener
    private View.OnClickListener mButtonConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {


            PatientListFragment newFragment = new PatientListFragment();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();


        }
    };

    public ClinicianHomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_clinician_home, container, false);


        mButtonPatients = (Button) rootView.findViewById(R.id.button);

        mButtonPatients.setOnClickListener(mButtonConnectClickListener);
        TextView libLink = (TextView) rootView.findViewById(R.id.textView5);
        libLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://mobmed.cs.uoi.gr/resourcespace/pages/home.php"));
                getActivity().startActivity(intent);
            }
        });


        return rootView;
    }


}