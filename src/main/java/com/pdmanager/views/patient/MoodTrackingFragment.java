package com.pdmanager.views.patient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pdmanager.R;
import com.pdmanager.communication.DirectSenderTask;
import com.pdmanager.communication.IDirectSendCallback;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.FragmentListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marko Koren on 11.1.2017.
 */
public class MoodTrackingFragment extends DialogFragment implements FragmentListener,IDirectSendCallback {
    private static final String LOG_TAG = MoodTrackingFragment.class.getName();

    private View selectedMoodView;
    private ArrayList<View> selectedActivityViews;
    private String selectedMood;
    private ArrayList<String> selectedActivities;
    private LinearLayout moodContainer;
    private LinearLayout actContainer;
    private LinearLayout moodExcellent;
    private LinearLayout moodGood;
    private LinearLayout moodOkay;
    private LinearLayout moodBad;
    private LinearLayout moodAwfull;
    private LinearLayout actHome;
    private LinearLayout actWork;
    private LinearLayout actRelax;
    private LinearLayout actExercise;
    private LinearLayout actRest;
    private LinearLayout actSocial;
    private EditText actOther;
    private Button buttonBack;
    private Button buttonNext;
    private RelativeLayout busyIndicator;
    private LinearLayout layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mood_tracking, container, false);

        setUp(rootView);
        return rootView;
    }


    public void setUp (  View rootView ) {

        final DirectSenderTask sender=new DirectSenderTask(RecordingSettings.GetRecordingSettings(getContext()).getToken(),this);

        busyIndicator = (RelativeLayout) rootView.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);
        layout=(LinearLayout)  rootView.findViewById(R.id.mainLayout);
        selectedActivityViews = new ArrayList<>();
        moodContainer = (LinearLayout) rootView.findViewById(R.id.container_mood_select);
        actContainer = (LinearLayout) rootView.findViewById(R.id.container_act_select);
        moodExcellent = (LinearLayout) rootView.findViewById(R.id.mood_excellent);
        moodExcellent.setOnClickListener(moodClickListener);
        moodGood = (LinearLayout) rootView.findViewById(R.id.mood_good);
        moodGood.setOnClickListener(moodClickListener);
        moodOkay = (LinearLayout) rootView.findViewById(R.id.mood_okay);
        moodOkay.setOnClickListener(moodClickListener);
        moodBad = (LinearLayout) rootView.findViewById(R.id.mood_bad);
        moodBad.setOnClickListener(moodClickListener);
        moodAwfull = (LinearLayout) rootView.findViewById(R.id.mood_awfull);
        moodAwfull.setOnClickListener(moodClickListener);
        actHome = (LinearLayout) rootView.findViewById(R.id.act_home);
        actHome.setOnClickListener(activityClickListener);
        actWork = (LinearLayout) rootView.findViewById(R.id.act_work);
        actWork.setOnClickListener(activityClickListener);
        actRelax = (LinearLayout) rootView.findViewById(R.id.act_relax);
        actRelax.setOnClickListener(activityClickListener);
        actExercise = (LinearLayout) rootView.findViewById(R.id.act_exercise);
        actExercise.setOnClickListener(activityClickListener);
        actRest = (LinearLayout) rootView.findViewById(R.id.act_rest);
        actRest.setOnClickListener(activityClickListener);
        actSocial = (LinearLayout) rootView.findViewById(R.id.act_social);
        actSocial.setOnClickListener(activityClickListener);
        actOther = (EditText) rootView.findViewById(R.id.act_other);
        actOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) buttonNext.setVisibility(View.VISIBLE);
                else buttonNext.setVisibility(View.GONE);
            }
        });
        buttonNext = (Button) rootView.findViewById(R.id.button_mood_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(moodContainer.getVisibility() == View.VISIBLE) {
                    moodContainer.setVisibility(View.GONE);
                    actContainer.setVisibility(View.VISIBLE);
                    buttonNext.setVisibility(View.GONE);
                    buttonBack.setVisibility(View.VISIBLE);
                } else {
                    Date date1 = new Date();
                    date1.setHours(0);
                    date1.setMinutes(0);
                    //Date date2= new java.util.Date(t2);
                    Calendar cal1 = Calendar.getInstance();
                    //Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date1);

                    String patientCode= RecordingSettings.GetRecordingSettings(getContext()).getPatientID();

                    ArrayList<Observation> observations=new ArrayList<Observation>();
                    selectedMood = selectedMoodView.getTag().toString();
                    observations.add( new Observation(1, patientCode, "MOOD", cal1.getTimeInMillis()));


                    selectedActivities = new ArrayList<String>();
                    for(View view : selectedActivityViews) {
                        observations.add(new Observation(1, patientCode,view.getTag().toString(), cal1.getTimeInMillis()));
                        selectedActivities.add(view.getTag().toString());
                    }
                    if(actOther.getText().length() > 0) {
                        observations.add(new Observation(1, patientCode,actOther.getTag().toString(), cal1.getTimeInMillis()));
                        selectedActivities.add(actOther.getText().toString());
                    }
                    busyIndicator.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    sender.execute(observations);
                 //   Toast.makeText(rootView, "selectedMood="+selectedMood+", selectedActivities="+selectedActivities+"\nTODO: Send this values to server, disable toast.", Toast.LENGTH_LONG).show();
                 //   Log.d(LOG_TAG, "selectedMood=" + selectedMood + ", selectedActivities=" + selectedActivities);


                    //post selectedMood and selectedActivites to the server and open XY fragment
                }
            }
        });
        buttonBack = (Button) rootView.findViewById(R.id.button_mood_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actContainer.setVisibility(View.GONE);
                moodContainer.setVisibility(View.VISIBLE);
                buttonNext.setVisibility(View.VISIBLE);
                buttonBack.setVisibility(View.GONE);
            }
        });
    }

    public void changeMood(View view) {
        if( selectedMoodView != null)
            selectedMoodView.setSelected(false);
        selectedMoodView = view;
        selectedMoodView.setSelected(true);
        buttonNext.setVisibility(View.VISIBLE);
    }

    public void activityClicked (View view) {
        if(selectedActivityViews.size() > 0) {
            if(selectedActivityViews.remove(view)) {
                view.setSelected(false);
            } else {
                selectedActivityViews.add(view);
                view.setSelected(true);
            }
        } else {
            selectedActivityViews.add(view);
            view.setSelected(true);
        }
        if(selectedActivityViews.size() > 0) {
            buttonNext.setVisibility(View.VISIBLE);
        } else {
            buttonNext.setVisibility(View.GONE);
        }

    }

    View.OnClickListener moodClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeMood(v);
        }
    };

    View.OnClickListener activityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            activityClicked(v);
        }
    };

    public void onFragmentSelected() {
    }


    @Override
    public void onPostDirectSend(boolean result) {
        busyIndicator.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);

        long t=(System.currentTimeMillis());
        dismiss();
    }
}
