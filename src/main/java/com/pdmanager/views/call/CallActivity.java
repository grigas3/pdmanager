package com.pdmanager.views.call;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.oovoo.sdk.api.ooVooClient;
import com.pdmanager.R;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.common.BasePDActivity;


public class CallActivity extends BasePDActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_activity);
        if (savedInstanceState != null) {
            current_fragment = (BasePDFragment) getSupportFragmentManager().getFragment(savedInstanceState, STATE_FRAGMENT);
            showFragment(current_fragment);
        } else {
            Fragment newFragment = new SplashScreen();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.host_activity, newFragment).commit();

            if (!ooVooClient.isDeviceSupported()) {
                return;
            }

            try {
                application.onMainActivityCreated();
            } catch (Exception e) {
                Log.e(TAG, "onCreate exception: ", e);
            }
        }
    }
}
