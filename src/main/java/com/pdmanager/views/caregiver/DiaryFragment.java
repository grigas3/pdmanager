package com.pdmanager.views.caregiver;

/**
 * Created by George on 1/30/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.AlertAdapter;
import com.pdmanager.alerting.AlertCursorAdapter;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.patient.DiaryTrackingActivity;
import com.pdmanager.views.patient.MedAlertActivity;
import com.pdmanager.views.patient.MoodTrackingActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class DiaryFragment extends BasePDFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "CaregiverHomeFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    final Handler handler = new Handler();
    TextView mTextNextMed;
    TextView message;
    private Button mButtonNGG;
    private Button mButtonMood;
    //private TextView mSensorStatus;
    //private TextView mMonitoringStatus;
    private LinearLayout mDiary;
    private ImageView mDiaryImage;
    private TextView mDiaryTitle;
    private ImageView mDiaryAct;
    private TextView mDiaryText;


    private LinearLayout mMedication;
    private LinearLayout mMood;

    AbsListView listView;
    AlertCursorAdapter mAdapter;
    private AlertAdapter dbQ;
    private int LOADER_ID = 2;


    private boolean debugToggle = false;
    private RelativeLayout layout;
    public DiaryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DiaryFragment newInstance(int sectionNumber) {
        DiaryFragment fragment = new DiaryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_caregiver_diary, container, false);



        final Context context=this.getContext();

         mDiary = (LinearLayout) rootView.findViewById(R.id.patient_home_notifications);
        mDiaryImage = (ImageView) rootView.findViewById(R.id.patient_home_notifications_img);
        mDiaryTitle = (TextView) rootView.findViewById(R.id.patient_home_notifications_title);
        mDiaryAct = (ImageView) rootView.findViewById(R.id.patient_home_notifications_act);
        mDiaryText = (TextView) rootView.findViewById(R.id.patient_home_notifications_text);
      mMedication = (LinearLayout) rootView.findViewById(R.id.patient_home_meds);
        mMood = (LinearLayout) rootView.findViewById(R.id.patient_home_mood);








        mDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("LOG", "test");
                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("DIARY");


                Intent diaryTracking =
                        new Intent(getActivity(), DiaryTrackingActivity.class);
                startActivity(diaryTracking);


            }
        });




        mMood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("MOOD");

                Intent menuPALIntent =
                        new Intent(getActivity(), MoodTrackingActivity.class);
                startActivity(menuPALIntent);
              /*  MoodTrackingActivity moodTrackingFragment = new MoodTrackingActivity();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, moodTrackingFragment);
                fragmentTransaction.commit();
                */
            }
        });


        mMedication.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                UserAlertManager manager=new UserAlertManager(context);
                manager.updateAlerts("MED");
                Intent menuPALIntent =
                        new Intent(getActivity(), MedAlertActivity.class);
                startActivity(menuPALIntent);

            }
        });





        return rootView;
    }




    public void onDestroy()
    {
        super.onDestroy();

    }





    @Override
    public void onPause() {


        super.onPause();

    }

    @Override
    public void onStop() {


        super.onStop();


    }


    @Override
    public void onResume() {
        super.onResume();

    }








}