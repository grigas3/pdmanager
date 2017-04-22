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
package com.pdmanager.views.patient;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pdmanager.call.CNMessage;
import com.pdmanager.app.PDApplicationContext;
import com.pdmanager.app.PDPilotAppContext;
import com.pdmanager.R;
import com.pdmanager.app.VideoApp;
import com.pdmanager.communication.NetworkStatus;
import com.pdmanager.interfaces.INetworkStatusHandler;
import com.pdmanager.logging.LogAdapter;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.call.AVChatSessionFragment;
import com.pdmanager.views.caregiver.MedListFragment;
import com.telerik.common.TrackedApplication;
import com.telerik.common.contracts.TrackedActivity;
import com.telerik.common.contracts.TransitionHandler;
import com.telerik.primitives.TipsPresenter;

import java.util.HashMap;
import java.util.Locale;

//import com.pdmanager.services.RegistrationIntentService;

public class MainActivity extends AppCompatActivity implements /*PatientDrawerFragment.NavigationDrawerCallbacks,*/
        /*android.support.v7.app.ActionBar.OnNavigationListener, */TransitionHandler, TrackedActivity,
        /*FragmentManager.OnBackStackChangedListener,*/
        INetworkStatusHandler,
        VideoApp.OperationChangeListener, VideoApp.CallNegotiationListener {




    /*

     */


    private final Handler toastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            try {
                String message = (String) msg.obj;

                //   LogInfo(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

            }
        }
    };
    RecordingService mService;
    boolean mBound = false;


    //IAlertFragmentManager alertFragmentManager;
    HashMap<String, Fragment> fragmentCache = new HashMap<String, Fragment>();
    private ColorDrawable currentBgColor;
    private android.support.v7.app.ActionBar actionBar;
    private PDPilotAppContext app;
    //private PatientDrawerFragment mPatientDrawerFragment;
    private TipsPresenter tipsPresenter;
    //private SensorsFragmentWithService sensorFragment;
    private int lastNavigationItemIndex = 1;
    private MedListFragment medAdminFragment;
    private PatientHomeFragment patientHomeFragment;
    private AVChatSessionFragment chatFragment;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RecordingService.LocalBinder binder = (RecordingService.LocalBinder) service;
            mService = binder.getService();
            //        Intent intent = new Intent(className, BandService.class);


            if(patientHomeFragment!=null)
            mService.registerListener(patientHomeFragment);


            RecordingServiceHandler.getInstance().setService(mService);

            initFragments();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private Intent intent;
    private AlertDialog callDialog = null;
    private Ringtone currentRingtone;
    private VideoApp application = null;
    private boolean mIsAlive = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver = null;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */


    @Override
    public void onBackPressed() {


        if(patientHomeFragment!=null&&patientHomeFragment.isInLayout()) {

           // return false;
        }
        else
            super.onBackPressed();
    }

    public static void setOverflowButtonColor(final Toolbar toolbar, final int color) {
        Drawable drawable = toolbar.getOverflowIcon();
        if(drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
            toolbar.setOverflowIcon(drawable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    //    application = (VideoApp) getApplication();
      //  application.setContext(this);


        // application.addOperationChangeListener(this);
        //  application.addCallNegotiationListener(this);


        //  TelerikActivityHelper.updateActivityTaskDescription(this);
        try {
            String languageToLoad  = RecordingSettings.newInstance(this.getApplicationContext()).getLang(); // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            Resources resources = getResources();
            ColorDrawable bgColorPrimary = new ColorDrawable(resources.getColor(R.color.primary_title_background));
            ColorDrawable bgColorSecondary = new ColorDrawable(resources.getColor(R.color.secondary_title_background));
            currentBgColor = bgColorSecondary;
            setContentView(R.layout.activity_main);
            Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar);
            this.setSupportActionBar(tb);
            if( tb != null) {
                tb.setTitleTextColor(Color.WHITE);
                setOverflowButtonColor(tb,Color.WHITE);
            }
            actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setBackgroundDrawable(currentBgColor);
            }
            this.setupActionBar();

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


            ///Set Brightness
            float SysBackLightValue = 255f;


          //  android.provider.Settings.System.putInt(BatteryBoosterActivity.this.getContentResolver(),   android.provider.Settings.System.SCREEN_BRIGHTNESS,(int) SysBackLightValue);
          //  Window myWindow =BatteryBoosterActivity.this. getWindow();
          //  WindowManager.LayoutParams winParams = myWindow.getAttributes();                                    winParams.screenBrightness = 255f;
          //  myWindow.setAttributes(winParams);
        }
        catch (Exception ex)
        {
            Log.e("MAINACTIVITY","RES",ex.getCause());

        }

        //RecordingSettingsHandler.getInstance().Init(getApplicationContext());






    /*    try {
            this.setupAlertFragmentManager();
        }
        catch (Exception ex)
        {
            Log.e("MAINACTIVITY","RES",ex.getCause());

        }
        */


        if (savedInstanceState == null) {

        }
        try {
        Bundle extras=this.getIntent().getExtras();

        if(extras!=null) {

            String alertType = extras.getString(PDApplicationContext.INTENT_ALERT_TYPE);
            String alertId =extras.getString(PDApplicationContext.INTENT_ALERT_ID);



            if (alertType != null && alertId != null) {

                Log.d("MAINACTIVITY",alertType);

            }
            else
            {
                Log.d("MAINACTIVITY","NULL Alert type");
            }
        }
        }
        catch (Exception ex)
        {
            Log.e("MAINACTIVITY","INTENT",ex.getCause());

        }


        initFragment();
    }


    private void initFragment()
    {
        patientHomeFragment=new PatientHomeFragment();
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, patientHomeFragment);
        fragmentTransaction.commit();
    }
    private void setupAlertFragmentManager() {

      //  this.alertFragmentManager = new AlertFragmentManager(this, new UserAlertManager(this));

    }

    @Override
    public void onDestroy() {


        if (mBound) {

            if(mService!=null)
                mService.unregisterListener(patientHomeFragment);
            unbindService(mConnection);
            if (!mService.getSessionMustRun())
                getApplicationContext().stopService(intent);


            mBound = false;
        }



        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    protected void onResume() {
        super.onResume();

       // if (alertFragmentManager != null)
         //   alertFragmentManager.startAutoUpdate();
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

      //  alertFragmentManager.stopAutoUpdate();


    }

    @Override
    public boolean IsNetworkConnected() {
        boolean ret = false;

        try {


            ret = NetworkStatus.IsNetworkConnected(this);


        } catch (Exception ex) {


            Log.d("Error", "Error while checking for network connection");

        }

        return ret;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_technician) {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("PASSWORD");
            alertDialog.setMessage("Enter Password");

            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.pdmanager);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String pass = "pdmanager";
                            String password = input.getText().toString();
                            if (password.compareTo("") != 0) {
                                if (pass.equals(password)) {
                                    Toast.makeText(getApplicationContext(),
                                            "Password Matched", Toast.LENGTH_SHORT).show();


                                    Intent mainIntent = new Intent(MainActivity.this, TechnicianActivity.class);
                                    MainActivity.this.startActivity(mainIntent);
                                    finish();


                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

            alertDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putInt("spinner_selection", this.lastNavigationItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    private void manageTipsPresenter(Fragment newFragment) {

    }

    @Override
    public void onMessageReceived(final CNMessage cnMessage) {
        if (application.getUniqueId().equals(cnMessage.getUniqueId())) {
            return;
        }

        if (cnMessage.getMessageType() == CNMessage.CNMessageType.Calling) {

            if (application.isInConference()) {
                application.sendCNMessage(cnMessage.getFrom(), CNMessage.CNMessageType.Busy, null);
                return;
            }

            callDialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = getLayoutInflater();
            View incomingCallDialog = inflater.inflate(R.layout.incoming_call_dialog, null);
            incomingCallDialog.setAlpha(0.5f);
            callDialog.setView(incomingCallDialog);

            TextView caller = (TextView) incomingCallDialog.findViewById(R.id.caller);
            caller.setText(cnMessage.getDisplayName());

            Button answerButton = (Button) incomingCallDialog.findViewById(R.id.answer_button);
            answerButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    application.setConferenceId(cnMessage.getConferenceId());
                    application.sendCNMessage(cnMessage.getFrom(), CNMessage.CNMessageType.AnswerAccept, null);
                    callDialog.hide();
                    currentRingtone.stop();

                    Intent intent = new Intent(application.getContext(), MainActivity.class);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);

                    application.join(application.getConferenceId(), true);
                }
            });

            Button declineButton = (Button) incomingCallDialog.findViewById(R.id.decline_button);
            declineButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    application.sendCNMessage(cnMessage.getFrom(), CNMessage.CNMessageType.AnswerDecline, null);
                    currentRingtone.stop();
                    callDialog.hide();
                }
            });

            callDialog.setCancelable(false);
            callDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            //play current Ringtone
            currentRingtone.play();
            callDialog.show();
        } else if (cnMessage.getMessageType() == CNMessage.CNMessageType.Cancel) {
            currentRingtone.stop();
            callDialog.hide();
        } else if (cnMessage.getMessageType() == CNMessage.CNMessageType.EndCall) {
            if (application.leave()) {
                int count = getFragmentManager().getBackStackEntryCount();
                String name = getFragmentManager().getBackStackEntryAt(count - 2).getName();
                getFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }


    @Override
    public void onOperationChange(VideoApp.Operation state) {
        try {
            switch (state) {
                case Error: {
                    switch (state.forOperation()) {
                        case AVChatJoined:
                            application.showErrorMessageBox(this, getString(R.string.join_session), state.getDescription());
                            //current_fragment = CallNegotiationFragment.newInstance();
                            break;
                        default:
                            return;
                    }
                }
                break;
                case Processing:
                    //current_fragment = WaitingFragment.newInstance(state.getDescription());
                    break;
                case AVChatCall:
                    //current_fragment = CallNegotiationFragment.newInstance();
                    break;
                case AVChatJoined:
                    //current_fragment = AVChatSessionFragment.newInstance(mSignalStrengthMenuItem, mSecureNetworkMenuItem);
                    break;
                case Authorized:
                    //current_fragment = CallNegotiationFragment.newInstance();
                    break;
                case LoggedIn:
                    //if (checkPlayServices()) {
                    //  // Start IntentService to register this application with GCM.
                    //Intent intent = new Intent(this, RegistrationIntentService.class);
                    //startService(intent);
                    // }
                    // current_fragment = CallNegotiationFragment.newInstance();
                    break;
                case AVChatDisconnected:
                    if (application.isCallNegotiation()) {
                        return;
                    } else {
                        //   current_fragment = CallNegotiationFragment.newInstance();
                        break;
                    }

                default:
                    return;
            }

            //showFragment(current_fragment);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void initFragments() {


    }

    @Override
    public void updateTransition(float step) {

        // Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);


    }

    public void invalidateActionbar() {
        invalidateActionbarTitle();
        invalidateBackground();
        invalidateOptionsMenu();
    }

    private void setupActionBar() {
        //this.actionBar.setListNavigationCallbacks(this, this);

    }

    private void invalidateActionbarTitle() {

    }

    private void invalidateBackground() {

    }

    private int calculateCurrentStep(int from, int to, float step) {
        int max = Math.max(from, to);
        int min = Math.min(from, to);
        int calculatedStep = ((int) ((max - min) * step));
        if (from > to)
            return from - calculatedStep;
        else
            return from + calculatedStep;
    }

    @Override
    public String getScreenName() {
        return TrackedApplication.HOME_SCREEN;
    }

    @Override
    public HashMap<String, Object> getAdditionalParameters() {
        return null;
    }

    public interface MenuList {
        void fill(View view, ContextMenu menu);
    }


    public class ClearLog extends AsyncTask<LogAdapter, Void, Boolean> {


        ClearLog() {

        }

        @Override
        protected Boolean doInBackground(LogAdapter... params) {
            // TODO: attempt authentication against a network service.

            try {

                LogAdapter adapter = params[0];

                adapter.clearLog();

            } catch (Exception ex) {


            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {


            Message s = new Message();
            s.obj = "Log cleared";
            toastHandler.sendMessage(s);
            //   Toast.makeText(getParent(),"Log cleared", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {

            Message s = new Message();
            s.obj = "Log clear cancelled";
            toastHandler.sendMessage(s);
            //Toast.makeText(getParent(),"Log clear cancelled", Toast.LENGTH_SHORT).show();
        }


    }

}

