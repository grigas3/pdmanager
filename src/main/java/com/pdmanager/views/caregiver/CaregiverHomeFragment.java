package com.pdmanager.views.caregiver;

/**
 * Created by George on 1/30/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.alerting.ActiveAlertLoader;
import com.pdmanager.alerting.AlertAdapter;
import com.pdmanager.alerting.AlertCursorAdapter;
import com.pdmanager.alerting.AlertObserver;
import com.pdmanager.alerting.IAlertDisplay;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.views.patient.AlertPDFragment;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class CaregiverHomeFragment extends AlertPDFragment implements IAlertDisplay,LoaderManager.LoaderCallbacks<Cursor> {
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
    public CaregiverHomeFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CaregiverHomeFragment newInstance(int sectionNumber) {
        CaregiverHomeFragment fragment = new CaregiverHomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter(getActivity());



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_caregiver_home, container, false);



        final Context context=this.getContext();

        try {

            listView = (ListView) rootView.findViewById(android.R.id.list);

            if (mAdapter != null) {
                listView.setAdapter(mAdapter);
                listView.setEmptyView( rootView.findViewById(R.id.patient_home_notasks));
                getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {

                        Log.d(TAG,Long.toString(id));

                        //
                        Cursor cursor= (Cursor)mAdapter.getItem(position);
                        UserAlertManager manager=new UserAlertManager(getContext());
                        manager.setNotified(cursor.getString(0));
                       // ListEntry entry = (ListEntry) parent.getItemAtPosition(position);


                    }
                });
            }
        } catch (Exception ex) {

            Log.e(TAG,ex.getMessage());

        }



        return rootView;
    }




    public void onDestroy()
    {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }
        super.onDestroy();

    }







    private Timer myTimer;

    @Override
    public void onPause() {

        stopTimer();
        super.onPause();

    }

    @Override
    public void onStop() {

        stopTimer();
        super.onStop();


    }

    private void stopTimer() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer.purge();
            myTimer = null;
        }


    }
    private void startTimer()
    {

        if (myTimer == null) {
            try {
                myTimer = new Timer();
                HomeTimerTask myTask = new HomeTimerTask();
                myTimer.schedule(myTask,1000, 60000);

            } catch (Exception ex) {

                Log.e("Timer", ex.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        startTimer();

    }



    @Override
    public void setAlertDisplay(final String messageTxt) {

        Activity activity = getActivity();
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(message!=null)
                    message.setText(messageTxt);

                }
            });
        }
    }

    private void showMonitoringError(String error)
    {

    }
    private void showMonitoringOK()
    {



    }
    private void showNotMonitoring()
    {

    }


    void updateUIAlerts()
    {
        final Context context=this.getContext();
       final UserAlertManager manager=new UserAlertManager((this.getContext()));
       final Activity activity = getActivity();
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {




                }



            });
        }






    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //  Log.e(TAG, ":::: onCreateLoader");

        ActiveAlertLoader demoLoader = new ActiveAlertLoader(this.getActivity(), dbQ);
        return demoLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        //Log.e(TAG, ":::: onLoadFinished");

        mAdapter.swapCursor(c);

        /**
         * Registering content observer for this cursor, When this cursor Value will be change
         * This will setNotified our loader to reload its data*/
        AlertObserver cursorObserver = new AlertObserver(new Handler(), loader);
        c.registerContentObserver(cursorObserver);
        c.setNotificationUri(getActivity().getContentResolver(), DBHandler.URI_TABLE_ALERTS);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Log.e(TAG, ":::: onLoaderReset");

        mAdapter.swapCursor(null);
    }

    private void initAdapter(Context context) {


        try {
            dbQ = new AlertAdapter(context);

            // create the adapter using the cursor pointing to the desired data
            //as well as the layout information
            mAdapter = new AlertCursorAdapter(context, null, false);


        } catch (Exception ex) {

            Log.d(TAG, ex.getMessage());

        }
    }


    /**
     * Timer for UI update (check for alerts etc.)
     */
    class HomeTimerTask extends TimerTask {
        public void run() {

//First Update UI Alerts
            updateUIAlerts();

        }
    }

}