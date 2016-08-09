/*package com.pdmanager.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.core.R;
import com.pdmanager.core.communication.CommunicationManager;
import com.pdmanager.core.communication.DataReceiver;
import com.pdmanager.core.communication.DirectSender;
import com.pdmanager.core.communication.NetworkStatus;
import com.pdmanager.core.models.MedicationOrder;
import com.pdmanager.core.models.Patient;
import com.pdmanager.core.viewmodels.MedicationOrderVM;
import com.pdmanager.core.viewmodels.OnEditEndListener;
import com.telerik.android.common.Function2;
import com.telerik.widget.dataform.visualization.DataFormGroupLayoutManager;
import com.telerik.widget.dataform.visualization.DataFormLinearLayoutManager;
import com.telerik.widget.dataform.visualization.EditorGroup;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.CommitMode;

public class CreateClinicalInfoFragment extends BasePDFragment implements View.OnClickListener {
    private RadDataForm dataForm;
    private OnEditEndListener editEndListener;
    private View mProgressView;
    private View mLoginFormView;

    private Patient patient;


    public void setPatient(Patient p)    {

        patient=p;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {




        // Save the user's current game state
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    public CreateClinicalInfoFragment() {
        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medication_order, container, false);
        dataForm = (RadDataForm)rootView.findViewById(R.id.medorder_data_form);

        if(savedInstanceState!=null)
        {

            patient=savedInstanceState.getParcelable("Patient");

        }

        mLoginFormView = rootView.findViewById(R.id.data_form_mainlayout);
        mProgressView = rootView.findViewById(R.id.save_progress);

        DataFormGroupLayoutManager groupLayoutManager = new DataFormGroupLayoutManager(this.getActivity());

        groupLayoutManager.setCreateGroup(new Function2<Context, String, EditorGroup>() {
            @Override
            public EditorGroup apply(Context context, String groupName) {
                if (groupName.equals("TIMING DETAILS")) {
                    EditorGroup group = new EditorGroup(context, groupName, R.layout.data_form_editor_group_with_background);
                    DataFormLinearLayoutManager linearManager = new DataFormLinearLayoutManager(getActivity(), R.layout.data_form_editor_group_horizontal);
                    linearManager.setOrientation(LinearLayout.HORIZONTAL);
                    group.setLayoutManager(linearManager);
                    return group;
                }

                else {
                    return new EditorGroup(context, groupName, R.layout.data_form_editor_group_no_header);
                }
            }
        });
        dataForm.setLayoutManager(groupLayoutManager);


    /////CHECK THIS
        //dataForm.setEntity(DataFormFragment.getCurrentReservation());
        //


        dataForm.setEntity(new MedicationOrderVM());
        //DataFormNumberPickerEditor guestNumberEditor = (DataFormNumberPickerEditor) dataForm.getExistingEditorForProperty("NumberOfGuests");
        //RadNumberPicker picker = (RadNumberPicker)guestNumberEditor.getEditorView();
        //picker.setPluralFormatString("%.0f guests");
        //picker.setSingleFormatString("%.0f guest");
        //picker.setZeroFormatString(picker.getPluralFormatString());

        if(dataForm.getEntity() != null) {
            initSpinnerData(dataForm);
        }

        dataForm.setCommitMode(CommitMode.MANUAL);

        //((EntityPropertyEditor)dataForm.getExistingEditorForProperty("ReservationDate")).setValidationViewBehavior(new ValidationAnimationBehavior(this.getActivity()));

     //   if(getResources().getBoolean(R.bool.dual_pane)) {
       //     rootView.findViewById(R.id.data_form_edit_commit_bar).setVisibility(View.GONE);
        //} else {
            rootView.findViewById(R.id.data_form_done_button).setOnClickListener(this);
            rootView.findViewById(R.id.data_form_cancel_button).setOnClickListener(this);
        //}

        return rootView;
    }

    public RadDataForm dataForm() {
        return dataForm;
    }

    public void setReservation(MedicationOrderVM value) {
        if(value == null) {
            return;
        }

        this.dataForm.setEntity(value);
        initSpinnerData(dataForm);
    }

    public static void initSpinnerData(RadDataForm dataForm) {

        //DataFormSpinnerEditor origin = (DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("Origin");
        //origin.setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), new String[]{"phone", "in-person", "online", "other"}));
        //((DataFormSpinnerEditor) dataForm.getExistingEditorForProperty("TableNumber")).setAdapter(new EditorSpinnerAdapter(dataForm.getContext(), R.layout.reservation_table_spinner_item, createTableNumbers()));
    }

    public static Integer[] createTableNumbers() {
        Integer[] result = new Integer[15];
        for(Integer i = 1; i <= 15; i++) {
            result[i - 1] = i;
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.data_form_done_button) {
            dataForm.commitChanges();
            if(!dataForm.hasValidationErrors()) {

                showProgress(true);
                if(IsNetworkConnected()) {
                    new SavePatientTask(getAccessToken()).execute(MedicationOrderVM.getMedicationOrder((MedicationOrderVM) dataForm.getEditedObject(),patient.Id));
                }
                else {
                    showProgress(false);

                }

            }
        } else {
            getFragmentManager().popBackStackImmediate();
            hideKeyboard();
            notifyListener(false, dataForm.getEditedObject());
        }
    }

    @Override
    public boolean onBackPressed() {
        boolean handled = super.onBackPressed();

        if(!handled) {
            notifyListener(false, dataForm.getEditedObject());
        }

        return handled;
    }

    public void setOnEditEndListener(OnEditEndListener listener) {
        this.editEndListener = listener;
    }

    private void notifyListener(boolean success, Object editedObject) {
        if(this.editEndListener == null) {
            return;
        }

        this.editEndListener.onEditEnded(success, editedObject);
    }

    private void hideKeyboard() {
        Activity activity = (Activity)this.dataForm.getContext();
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if(activity.getCurrentFocus() != null) {
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public boolean IsNetworkConnected() {


        boolean ret=false;

        try {




            ret= NetworkStatus.IsNetworkConnected(this.getActivity());


        }
        catch(Exception ex) {



            Log.d("Error","Error while checking for network connection");

        }

        return ret;


    }


    private class SavePatientTask extends AsyncTask<Patient, Void, Boolean> {


        private final String accessToken;

        public SavePatientTask(String a)
        {
            accessToken=a;

        }
        @Override
        protected Boolean doInBackground(Patient... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver=new DataReceiver(accessToken);

                Patient params=clientParams[0];
                DirectSender sender=new DirectSender();
                CommunicationManager mCommManager=new CommunicationManager(sender);

                mCommManager.SendItem(params);

              return true;


            }
            catch (Exception ex) {

                // Util.handleException("Getting data", ex);
                return false;
                // handle BandException
            }
        }

        protected void onPostExecute(Boolean result) {

            showProgress(false);
            if(result) {


                getFragmentManager().popBackStackImmediate();
                hideKeyboard();
               // notifyListener(true, dataForm.getEditedObject());
            }
            else
            {



            }
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
*/