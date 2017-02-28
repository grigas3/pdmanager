package com.pdmanager.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pdmanager.common.data.PDAnnotData;
import com.pdmanager.common.data.PDData;
import com.pdmanager.R;
import com.pdmanager.services.RecordingService;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.logging.LogHandler;
import com.pdmanager.sensor.RecordingServiceHandler;

import java.util.Date;
import java.util.Formatter;


public class AnnotationFragment extends Fragment implements FragmentListener, IServiceStatusListener {
    //  private static int Low = 76;
    //  private static int High = 78;
    //  private static int Duration = 10;
    private Button mButtonSubmit;
    private SeekBar mLid;
    private SeekBar mTremor;
    private SeekBar mBradykinesia;
    private TextView mTremorText;
    private TextView mLidText;
    private TextView mBradText;
    //
    // Handle connect/disconnect requests.
    //
    private View.OnClickListener mButtonSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {


            RecordingService band = getService();

            if (band != null && band.isSessionRunning()) {


                PDData data = new PDData();

                data.setTimestamp(new Date());
                data.setValue(new PDAnnotData(mLid.getProgress(), mBradykinesia.getProgress(), mTremor.getProgress()));
                data.setTimestamp(new Date());
                //   long TICKS_AT_EPOCH = 621355968000000000L;
                //   long ticks = System.currentTimeMillis()*10000 + TICKS_AT_EPOCH;
                //   data.setTicks(ticks);

                band.handleData(data);

                LogHandler.getInstance().Log("Annotation added by user");


            }


        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {


            if (seekBar == mLid) {


                Formatter r = new Formatter();
                r.format("LID of UPDRS %d ", progress);
                mLidText.setText(r.toString());


            } else if (seekBar == mTremor) {

                Formatter r = new Formatter();
                r.format("Tremor of UPDRS %d ", progress);
                mTremorText.setText(r.toString());

            } else

            {

                Formatter r = new Formatter();
                r.format("Bradykinesia of UPDRS %d ", progress);
                mBradText.setText(r.toString());

            }

            // TODO Auto-generated method stub
            //  updateBackground();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }


    };

    public AnnotationFragment() {


    }

    public final void onFragmentSelected() {
        if (isVisible()) {
            refreshControls();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_annotation, container, false);


        mButtonSubmit = (Button) rootView.findViewById(R.id.buttonSubmit);
        mButtonSubmit.setOnClickListener(mButtonSubmitClickListener);

        // Heart rate setup
        mLid = (SeekBar) rootView.findViewById(R.id.lidBar);

        mLidText = (TextView) rootView.findViewById(R.id.lidText);
        mTremorText = (TextView) rootView.findViewById(R.id.tremorText);
        mTremor = (SeekBar) rootView.findViewById(R.id.tremorBar);
        mBradykinesia = (SeekBar) rootView.findViewById(R.id.bradBar);
        mBradText = (TextView) rootView.findViewById(R.id.bradText);
        mLid.setOnSeekBarChangeListener(seekBarChangeListener);
        mTremor.setOnSeekBarChangeListener(seekBarChangeListener);
        mBradykinesia.setOnSeekBarChangeListener(seekBarChangeListener);


        refreshControls();
        return rootView;
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


        refreshControls();
    }

    private RecordingService getService() {
        return RecordingServiceHandler.getInstance().getService();
    }

    private void refreshControls() {

        RecordingService band = getService();
        if (band != null && band.isSessionRunning()) {


            if (mButtonSubmit != null)
                mButtonSubmit.setEnabled(true);

        } else

        {

            if (mButtonSubmit != null)
                mButtonSubmit.setEnabled(false);


        }


    }

    @Override
    public void notifyServiceStatusChanged() {


        refreshControls();

    }


}
