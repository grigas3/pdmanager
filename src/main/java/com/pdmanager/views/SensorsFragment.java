//Copyright (c) Microsoft Corporation All rights reserved.  
// 
//MIT License: 
// 
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
//documentation files (the  "Software"), to deal in the Software without restriction, including without limitation
//the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
//to permit persons to whom the Software is furnished to do so, subject to the following conditions: 
// 
//The above copyright notice and this permission notice shall be included in all copies or substantial portions of
//the Software. 
// 
//THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
//TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
//THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
//CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
//IN THE SOFTWARE.
package com.pdmanager.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.R;


public class SensorsFragment extends Fragment implements FragmentListener, ISensorDataHandler {

    // HR sensor controls
    private TextView mTextHeartRate;
    private TextView mTextHeartRateQuality;

    // Pedometer sensor controls
    private TextView mTextTotalSteps;
    private TextView mTextActivity;
    private TextView mTextTremor;
    private TextView mTextLID;
    private TextView mTextTremorType;


    // When pausing, turn off any active sensors.
    /*@Override
    public void onPause() {
        for (Switch sw : mSensorMap.keySet()) {
            if (sw.isChecked()) {
                sw.setChecked(false);
                mToggleSensorSection.onCheckedChanged(sw, false);
            }
        }

        super.onPause();
    }*/


    public SensorsFragment() {


    }

    public void onFragmentSelected() {
        if (isVisible()) {
            refreshControls();
        }
    }

    //
    // Sensor event handlers - each handler just writes the new sample to an atomic
    // reference where it will be read by the UI thread. Samples that arrive faster
    // than they can be processed by the UI thread overwrite older samples. Each
    // handler calls scheduleSensorHandler() which makes sure that at most one call
    // is queued to the UI thread to update all of the sensor displays.
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);


        mTextHeartRate = (TextView) rootView.findViewById(R.id.textHeartRate);
        mTextHeartRateQuality = (TextView) rootView.findViewById(R.id.textHeartRateQuality);

        mTextTremor = (TextView) rootView.findViewById(R.id.textAmplitude);
        mTextLID = (TextView) rootView.findViewById(R.id.textDyskinesia);


        mTextTotalSteps = (TextView) rootView.findViewById(R.id.textTotalSteps);
        mTextActivity = (TextView) rootView.findViewById(R.id.textActivity);


        return rootView;
    }


    @Override
    public void handleData(ISensorData data) {

        displayData(data);


    }


    //
    // Other helpers
    //

    private void refreshControls() {

    }


    private void displayData(final ISensorData data) {
        Activity activity = getActivity();

        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (data.getDataType() == DataTypes.HR) {
                            HRReading hr = ((HRData) data).getValue();
                            if (mTextHeartRate != null && mTextHeartRateQuality != null) {
                                mTextHeartRate.setText(Integer.toString(hr.getHR()));
                                int quality = hr.getQuality();
                                if (quality == 1) {
                                    mTextHeartRateQuality.setText("Tight");
                                } else {
                                    mTextHeartRateQuality.setText("Loose");
                                }
                            }
                        } else if (data.getDataType() == DataTypes.PEDOMETER) {


                            if (mTextTotalSteps != null) {
                                mTextTotalSteps.setText(data.getDisplay() + " steps");
                            }
                        } else if (data.getDataType() == DataTypes.ACTIVITY) {


                            if (mTextActivity != null)
                                mTextActivity.setText(data.getDisplay());

                        } else if (data.getDataType() == DataTypes.TREMOR) {


                            if (mTextTremor != null) {
                                mTextTremor.setText(data.getDisplay());
                            }
                        } else if (data.getDataType() == DataTypes.LID) {


                            if (mTextLID != null) {
                                mTextLID.setText(data.getDisplay());
                            }
                        }

                    } catch (Exception ex) {


                    }

                }
            });
        }


    }


}
