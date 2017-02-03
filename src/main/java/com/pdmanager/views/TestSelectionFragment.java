package com.pdmanager.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pdmanager.core.PDApplicationContext;
import com.pdmanager.views.clinician.BS11Activity;
import com.pdmanager.views.clinician.NMSSActivity;

/**
 * Created by george on 22/6/2016.
 */
public class TestSelectionFragment extends DialogFragment {

    final CharSequence[] items = {"BIS11", "NMSS"};

    public static TestSelectionFragment newInstance(String patientId, String accessToken) {
        TestSelectionFragment f = new TestSelectionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("patientId", patientId);
        args.putString("accessToken", accessToken);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String patient = getArguments().getString("patientId");
        final String accessToken = getArguments().getString("accessToken");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Test")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            Intent mainIntent = new Intent(getActivity(), BS11Activity.class);
                            mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_CODE, patient);
                            mainIntent.putExtra(PDApplicationContext.INTENT_ACCESS_TOKEN, accessToken);
                            //mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_NAME, p.Given + " " + p.Family);
                            getActivity().startActivity(mainIntent);
                        } else {

                            Intent mainIntent = new Intent(getActivity(), NMSSActivity.class);
                            mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_CODE, patient);
                            mainIntent.putExtra(PDApplicationContext.INTENT_ACCESS_TOKEN, accessToken);
                            //mainIntent.putExtra(PDApplicationContext.INTENT_PATIENT_NAME, p.Given + " " + p.Family);
                            getActivity().startActivity(mainIntent);
                        }
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }
}