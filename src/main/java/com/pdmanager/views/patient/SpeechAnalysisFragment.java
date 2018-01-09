package com.pdmanager.views.patient;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pdmanager.R;
import com.pdmanager.views.FragmentListener;


/**
 * Created by Marko Koren on 16.2.2017.
 */

public class SpeechAnalysisFragment extends AlertPDFragment implements FragmentListener {
    private static final String LOG_TAG = SpeechAnalysisFragment.class.getName();
    private RelativeLayout buttonRecord;
    private RelativeLayout buttonReRecord;
    private RelativeLayout buttonDone;
    private RelativeLayout buttonPlayPause;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speech_analysis, container, false);

        setUp(rootView);
        return rootView;
    }


    public void setUp(View rootView) {
        buttonRecord = (RelativeLayout) rootView.findViewById(R.id.button_record);
        buttonReRecord = (RelativeLayout) rootView.findViewById(R.id.button_re_record);
        buttonDone = (RelativeLayout) rootView.findViewById(R.id.button_rec_done);
        buttonPlayPause = (RelativeLayout) rootView.findViewById(R.id.button_play_pause);

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRecord.setVisibility(View.GONE);
                buttonReRecord.setVisibility(View.VISIBLE);
                buttonDone.setVisibility(View.VISIBLE);
                buttonPlayPause.setVisibility(View.VISIBLE);
            }
        });

        buttonReRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRecord.setVisibility(View.VISIBLE);
                buttonReRecord.setVisibility(View.GONE);
                buttonDone.setVisibility(View.GONE);
                buttonPlayPause.setVisibility(View.GONE);
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientHomeFragment patientHomeFragment = new PatientHomeFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, patientHomeFragment);
                fragmentTransaction.commit();
            }
        });
    }

    public void onFragmentSelected() {
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
