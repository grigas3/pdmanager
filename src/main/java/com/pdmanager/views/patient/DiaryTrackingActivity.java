package com.pdmanager.views.patient;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdmanager.R;
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
public class DiaryTrackingActivity extends SoundFeedbackActivity implements IDirectSendCallback {
    private static final String LOG_TAG = DiaryTrackingActivity.class.getName();

    private View selectedMoodView;
    private ArrayList<View> selectedActivityViews;

    private ArrayList<String> selectedActivities;
    private LinearLayout diaryContainer;

    private LinearLayout diaryOn;
    private LinearLayout diaryOnDys;
    private LinearLayout diaryOff;
    private LinearLayout moodAwfull;

    private TextView chooseText;

    private RelativeLayout buttonNext;
    private RelativeLayout busyIndicator;
    private RelativeLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_tracking);
        setUp();
    }

    public void setUp (  ) {

        final DirectSenderTask sender=new DirectSenderTask(RecordingSettings.GetRecordingSettings(getApplicationContext()).getToken(),this);

        busyIndicator = (RelativeLayout) this.findViewById(R.id.busy_BusyIndicator);

        busyIndicator.setVisibility(View.INVISIBLE);
        layout=(RelativeLayout)  this.findViewById(R.id.mainLayout);
        selectedActivityViews = new ArrayList<>();
        diaryContainer = (LinearLayout) this.findViewById(R.id.container_diary_select);


        diaryOn = (LinearLayout) this.findViewById(R.id.diary_on);
        diaryOn.setOnClickListener(moodClickListener);
        diaryOnDys = (LinearLayout) this.findViewById(R.id.diary_on_dys);
        diaryOnDys.setOnClickListener(moodClickListener);
        diaryOff = (LinearLayout) this.findViewById(R.id.diary_off);
        diaryOff.setOnClickListener(moodClickListener);

        chooseText = (TextView) this.findViewById(R.id.mood_act_choose);
        buttonNext = (RelativeLayout) this.findViewById(R.id.button_mood_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Date date1 = new Date();
                    date1.setHours(0);
                    date1.setMinutes(0);
                    //Date date2= new java.util.Date(t2);
                    Calendar cal1 = Calendar.getInstance();
                    //Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(date1);

                    String patientCode= RecordingSettings.GetRecordingSettings(getApplicationContext()).getPatientID();

                    ArrayList<Observation> observations=new ArrayList<Observation>();
                    int pv=Integer.parseInt( selectedMoodView.getTag().toString());
                    observations.add( new Observation(pv, patientCode, "DIARY", cal1.getTimeInMillis()));
                    busyIndicator.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.INVISIBLE);
                    sender.execute(observations);
                 //   Toast.makeText(rootView, "selectedMood="+selectedMood+", selectedActivities="+selectedActivities+"\nTODO: Send this values to server, disable toast.", Toast.LENGTH_LONG).show();
                 //   Log.d(LOG_TAG, "selectedMood=" + selectedMood + ", selectedActivities=" + selectedActivities);


                    //post selectedMood and selectedActivites to the server and open XY fragment

            }
        });
//        buttonBack = (Button) this.findViewById(R.id.button_mood_back);
//        buttonBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actContainer.setVisibility(View.GONE);
//                diaryContainer.setVisibility(View.VISIBLE);
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



    View.OnClickListener moodClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changeMood(v);
        }
    };


    public void onFragmentSelected() {
    }


    @Override
    public void onPostDirectSend(boolean result) {
        busyIndicator.setVisibility(View.INVISIBLE);
        layout.setVisibility(View.VISIBLE);

        finishTest();
    }



}
