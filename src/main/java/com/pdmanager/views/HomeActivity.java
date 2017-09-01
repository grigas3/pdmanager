package com.pdmanager.views;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.interfaces.IServiceStatusListener;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.common.LoginActivity;


public class HomeActivity extends ActionBarActivity implements IServiceStatusListener {


    RecordingService mService;
    boolean mBound = false;

    private ImageView mLogo;
    private TextView mErrorText;
    private TextView mErrorLabel;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RecordingService.LocalBinder binder = (RecordingService.LocalBinder) service;
            mService = binder.getService();

            // Ensure the service is not in the foreground when bound
            mService.background();
            //        Intent intent = new Intent(className, BandService.class);


            //   RecordingServiceHandler.getInstance().setService(mService);


            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


    }


    @Override
    protected void onResume() {


        notifyServiceStatusChanged();
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            intent = new Intent(this, RecordingService.class);
            getApplicationContext().startService(intent);
            // Bind to LocalService

            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        } catch (Exception ex) {
            //  Util.handleException("Start Service", ex);

        }


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {

            // If a timer is active, foreground the service, otherwise kill the service
            /*
            }
            else {
                stopService(new Intent(this, RecordingService.class));
            }
            */
            // Unbind the service
            unbindService(mConnection);
            if (mService.isSessionRunning())
                mService.foreground();

            mBound = false;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);


        mErrorText = (TextView) findViewById(R.id.errorText);
        mErrorLabel = (TextView) findViewById(R.id.errorLabel);
        mLogo = (ImageView) findViewById(R.id.splashscreen);

        notifyServiceStatusChanged();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent mainIntent = new Intent(HomeActivity.this, LoginActivity.class);
            HomeActivity.this.startActivity(mainIntent);

        }

        if (id == R.id.action_help) {

///            Intent mainIntent = new Intent(HomeActivity.this, HelpActivity.class);
            //         HomeActivity.this.startActivity(mainIntent);

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void notifyServiceStatusChanged() {


        if (mService != null) {


            boolean isRunning = mService.getSessionMustRun();

            boolean hasFatalError = mService.hasFatalError();


            if (hasFatalError) {


                if (mErrorLabel != null)
                    mErrorLabel.setVisibility(View.VISIBLE);


                if (mErrorText != null)
                    mErrorText.setVisibility(View.VISIBLE);
                if (mService.getFatalErrorCode() == 3) {
                    mErrorText.setText("An unspecified error occured while connecting to Band");


                }
                if (mService.getFatalErrorCode() == 2) {
                    mErrorText.setText("Band is not recording...please move phone close to your Band");


                }

                if (mService.getFatalErrorCode() == 1) {
                    mErrorText.setText("Band is not paired...please re-pair your Band from the band device");


                }


                if (mLogo != null)
                    mLogo.setImageResource(R.drawable.splash_error);
            } else {

                if (mErrorLabel != null)
                    mErrorLabel.setVisibility(View.INVISIBLE);


                if (mErrorText != null)
                    mErrorText.setVisibility(View.INVISIBLE);

                if (mLogo != null)
                    mLogo.setImageResource(R.drawable.splash_normal);


            }


        } else {

            if (mErrorLabel != null)
                mErrorLabel.setVisibility(View.INVISIBLE);


            if (mErrorText != null)
                mErrorText.setVisibility(View.INVISIBLE);

            if (mLogo != null)
                mLogo.setImageResource(R.drawable.splash_normal);


        }

    }
}
