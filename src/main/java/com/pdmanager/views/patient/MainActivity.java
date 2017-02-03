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
import android.media.Ringtone;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
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
import com.pdmanager.core.AlertFragmentManager;
import com.pdmanager.core.PDApplicationContext;
import com.pdmanager.core.PDPilotAppContext;
import com.pdmanager.core.R;
import com.pdmanager.core.VideoApp;
import com.pdmanager.core.alerting.UserAlertManager;
import com.pdmanager.core.communication.NetworkStatus;
import com.pdmanager.core.interfaces.IAlertFragmentManager;
import com.pdmanager.core.interfaces.INetworkStatusHandler;
import com.pdmanager.core.logging.LogAdapter;
import com.pdmanager.core.sensor.RecordingServiceHandler;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.call.AVChatSessionFragment;
import com.pdmanager.views.drawers.PatientDrawerFragment;
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


    IAlertFragmentManager alertFragmentManager;
    HashMap<String, Fragment> fragmentCache = new HashMap<String, Fragment>();
    private ColorDrawable currentBgColor;
    private android.support.v7.app.ActionBar actionBar;
    private PDPilotAppContext app;
    //private PatientDrawerFragment mPatientDrawerFragment;
    private TipsPresenter tipsPresenter;
    //private SensorsFragmentWithService sensorFragment;
    private int lastNavigationItemIndex = 1;
    private MedAdminFragment medAdminFragment;
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


        super.onBackPressed();
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
            currentBgColor = bgColorPrimary;
            setContentView(R.layout.activity_main);
            Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar);
            this.setSupportActionBar(tb);
            tb.setTitleTextColor(Color.WHITE);
            actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setBackgroundDrawable(currentBgColor);
            }
            this.setupActionBar();
        }
        catch (Exception ex)
        {
            Log.e("MAINACTIVITY","RES",ex.getCause());

        }

        //RecordingSettingsHandler.getInstance().Init(getApplicationContext());






        try {
            this.setupAlertFragmentManager();
        }
        catch (Exception ex)
        {
            Log.e("MAINACTIVITY","RES",ex.getCause());

        }

        // this.setupNavigationDrawer(savedInstanceState);


        // Prevents the drawer from being opened at the time of the first launch.
        //com.telerik.android.common.Util.getLayoutPart(this, R.id.drawer_layout, DrawerLayout.class).closeDrawer(Gravity.LEFT);
        //   this.getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {

        }
        try {
        Bundle extras=this.getIntent().getExtras();

        if(extras!=null) {

            String alertType = extras.getString(PDApplicationContext.INTENT_ALERT_TYPE);
            String alertId =extras.getString(PDApplicationContext.INTENT_ALERT_ID);



            if (alertType != null && alertId != null) {
                Log.d("MAINACTIVITY",alertType);
                this.alertFragmentManager.gotoAlertFragment(alertId);

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


    }

    private void setupAlertFragmentManager() {

        patientHomeFragment=new PatientHomeFragment();
        this.alertFragmentManager = new AlertFragmentManager(this, new UserAlertManager(this));
        this.alertFragmentManager.setDefaultFragment(patientHomeFragment);
        this.alertFragmentManager.registerFragment("MED",new MedAlertFragment());
        //this.alertFragmentManager.registerFragment("TEST",R.id.fragment_alert_test);
        this.alertFragmentManager.gotoNextFragment();
        //  this.alertFragmentManager.startAutoUpdate();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /*
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //THIS FUNCTIONALITY WILL BE USED IN THE NEXT VERSION OF THE EXAMPLES.
        Fragment controlsFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);
        this.lastNavigationItemIndex = itemPosition;
        return false;
    }
    */

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

        if (alertFragmentManager != null)
            alertFragmentManager.startAutoUpdate();
    }

   /* private void loadSectionFromIntent(Intent intent, boolean addToBackStack) {

    }

    private void addFragmentForSection(String section, boolean addToBackStack) {
        Fragment newFragment = this.getSectionFragment(section);

        this.manageTipsPresenter(newFragment);
        this.app.loadFragment(this, newFragment, R.id.container, addToBackStack);
        this.invalidateOptionsMenu();

//        this.invalidateOptionsMenu();
    }
    */

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

        alertFragmentManager.stopAutoUpdate();
        if (mBound) {

            if(mService!=null)
            mService.unregisterListener(patientHomeFragment);
            unbindService(mConnection);
            if (!mService.getSessionMustRun())
                getApplicationContext().stopService(intent);


            mBound = false;
        }

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




         /*   RecordingSettings settings = new RecordingSettings(this);
            settings.setLoggedIn(false);


            Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(mainIntent);
            finish();
            */


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
        /*this.lastNavigationItemIndex = savedInstanceState.getInt("spinner_selection", this.lastNavigationItemIndex);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof BasePDFragment) {
            this.invalidateActionbar();
            this.actionBar.setSelectedNavigationItem(this.lastNavigationItemIndex);
        }
        */

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

    /* private void setupNavigationDrawer(Bundle savedInstanceState) {


         mPatientDrawerFragment = (PatientDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
         // Set up the drawer.
         mPatientDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

         if (savedInstanceState == null) {
             if (!this.getIntent().hasExtra(PDApplicationContext.INTENT_SECTION_ID)) {
                 String selectedSection = mPatientDrawerFragment.selectedSection() == null ? PatientDrawerFragment.NAV_DRAWER_SECTION_HOME : mPatientDrawerFragment.selectedSection();
                 this.addFragmentForSection(selectedSection, false);
             }
         } else {
             Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);
             this.manageTipsPresenter(currentFragment);
         }
     }

 */
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

    public static interface MenuList {
        public void fill(View view, ContextMenu menu);
    }

 /*   @Override
    public void onNavigationDrawerSectionSelected(String section) {
        this.addFragmentForSection(section, true);
    }

    @Override
    public void onNavigationDrawerControlSelected(MenuAction control) {


        //    if(control.getShortFragmentName()==PatientDrawerFragment.NAV_DRAWER_SECTION_FILES)
        //      this.app.openActivity(this, FilesActivity.class);
//        else {
        this.addFragmentForSection(control.getShortFragmentName(), true);
        //      }

        //
    }

    @Override
    public void onNavigationDrawerOpened() {

    }

    @Override
    public void onNavigationDrawerClosed() {

    }
    */

   /* @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        manageTipsPresenter(currentFragment);
        if (currentFragment instanceof PatientDrawerFragment.SectionInfoProvider) {


            mPatientDrawerFragment.updateSelectedSection(((PatientDrawerFragment.SectionInfoProvider) currentFragment).getSectionName());
        }

        invalidateActionbar();
    }
    */

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

