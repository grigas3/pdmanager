package com.pdmanager.views.patient;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.UserTaskCodes;
import com.pdmanager.communication.DirectSenderTask;
import com.pdmanager.communication.IDirectSendCallback;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.patient.cognition.tools.SoundFeedbackActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marko Koren on 11.1.2017.
 */
public class MoodTrackingActivity extends SoundFeedbackActivity implements IDirectSendCallback {
    private static final String LOG_TAG = MoodTrackingActivity.class.getName();

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
    private TextView chooseText;
//    private Button buttonBack;
    private RelativeLayout buttonNext;
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
    private RelativeLayout busyIndicator;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracking);
        setUp();
    }

    public void setUp (  ) {

        final DirectSenderTask sender=new DirectSenderTask(RecordingSettings.GetRecordingSettings(this).getToken(),this);

        busyIndicator = (RelativeLayout) this.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);
        layout=(RelativeLayout)  this.findViewById(R.id.mainLayout);
        selectedActivityViews = new ArrayList<>();
        moodContainer = (LinearLayout) this.findViewById(R.id.container_mood_select);
        actContainer = (LinearLayout) this.findViewById(R.id.container_act_select);
        moodExcellent = (LinearLayout) this.findViewById(R.id.mood_excellent);
        moodExcellent.setOnClickListener(moodClickListener);
        moodGood = (LinearLayout) this.findViewById(R.id.mood_good);
        moodGood.setOnClickListener(moodClickListener);
        moodOkay = (LinearLayout) this.findViewById(R.id.mood_okay);
        moodOkay.setOnClickListener(moodClickListener);
        moodBad = (LinearLayout) this.findViewById(R.id.mood_bad);
        moodBad.setOnClickListener(moodClickListener);
        moodAwfull = (LinearLayout) this.findViewById(R.id.mood_awfull);
        moodAwfull.setOnClickListener(moodClickListener);
        actHome = (LinearLayout) this.findViewById(R.id.act_home);
        actHome.setOnClickListener(activityClickListener);
        actWork = (LinearLayout) this.findViewById(R.id.act_work);
        actWork.setOnClickListener(activityClickListener);
        actRelax = (LinearLayout) this.findViewById(R.id.act_relax);
        actRelax.setOnClickListener(activityClickListener);
        actExercise = (LinearLayout) this.findViewById(R.id.act_exercise);
        actExercise.setOnClickListener(activityClickListener);
        actRest = (LinearLayout) this.findViewById(R.id.act_rest);
        actRest.setOnClickListener(activityClickListener);
        actSocial = (LinearLayout) this.findViewById(R.id.act_social);
        actSocial.setOnClickListener(activityClickListener);
        actOther = (EditText) this.findViewById(R.id.act_other);
        actOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0) {
                    buttonNext.setVisibility(View.VISIBLE);
                    chooseText.setVisibility(View.GONE);
                }
                else if (selectedActivityViews.size() < 1) {
                    buttonNext.setVisibility(View.GONE);
                    chooseText.setVisibility(View.VISIBLE);
                }
            }
        });
        chooseText = (TextView) this.findViewById(R.id.mood_act_choose);
        buttonNext = (RelativeLayout) this.findViewById(R.id.button_mood_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(moodContainer.getVisibility() == View.VISIBLE) {
                    moodContainer.setVisibility(View.GONE);
                    actContainer.setVisibility(View.VISIBLE);
                    buttonNext.setVisibility(View.GONE);
//                    buttonBack.setVisibility(View.VISIBLE);
                    chooseText.setVisibility(View.VISIBLE);
                } else {
                    Date date1 = new Date();
                    date1.setHours(0);
                    date1.setMinutes(0);
                    //Date date2= new java.util.Date(t2);
                    Calendar cal1 = Calendar.getInstance();
                    //Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date1);

                    String patientCode= RecordingSettings.GetRecordingSettings(getApplicationContext()).getPatientID();

                    ArrayList<Observation> observations=new ArrayList<Observation>();
                    selectedMood = selectedMoodView.getTag().toString();
                    observations.add( new Observation(1, patientCode, "PQMOOD", cal1.getTimeInMillis()));


                    selectedActivities = new ArrayList<String>();
                    for(View view : selectedActivityViews) {
                        observations.add(new Observation(1, patientCode,view.getTag().toString(), cal1.getTimeInMillis()));
                        selectedActivities.add(view.getTag().toString());
                    }
                    if(actOther.getText().length() > 0) {
                        observations.add(new Observation(1, patientCode,actOther.getText().toString(), cal1.getTimeInMillis()));
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
//        buttonBack = (Button) this.findViewById(R.id.button_mood_back);
//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actContainer.setVisibility(View.GONE);
//                moodContainer.setVisibility(View.VISIBLE);
//                buttonNext.setVisibility(View.VISIBLE);
//                buttonBack.setVisibility(View.GONE);
//            }
//        });
    }

    public void changeMood(View view) {
        if( selectedMoodView != null)
            selectedMoodView.setSelected(false);
        selectedMoodView = view;
        selectedMoodView.setSelected(true);
        chooseText.setVisibility(View.GONE);
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
            chooseText.setVisibility(View.GONE);
        } else if (actOther.getText().toString().length() < 1){
            buttonNext.setVisibility(View.GONE);
            chooseText.setVisibility(View.VISIBLE);
        }

    }

    public void onFragmentSelected() {
    }


    @Override
    public void onPostDirectSend(boolean result) {
        busyIndicator.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);

       finishTest();
    }

    @Override
    protected String getTestCode() {
        return UserTaskCodes.MOOD;
    }

}
