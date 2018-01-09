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
package com.pdmanager.views.caregiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pdmanager.R;
import com.pdmanager.alerting.UserAlertManager;
import com.pdmanager.app.PDApplicationContext;
import com.pdmanager.app.VideoApp;
import com.pdmanager.call.CNMessage;
import com.pdmanager.communication.NetworkStatus;
import com.pdmanager.interfaces.INetworkStatusHandler;
import com.pdmanager.logging.LogAdapter;
import com.pdmanager.models.UserAlert;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.LogEventFragment;
import com.pdmanager.views.RecordingSchedulingFragment;
import com.pdmanager.views.RecordingServiceFragment;
import com.pdmanager.views.RecordingSettingsFragment;
import com.pdmanager.views.drawers.CaregiverDrawerFragment;
import com.telerik.common.TrackedApplication;
import com.telerik.common.contracts.TrackedActivity;
import com.telerik.common.contracts.TransitionHandler;
import com.telerik.primitives.TipsPresenter;
import com.telerik.viewmodels.MenuAction;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//import com.pdmanager.services.RegistrationIntentService;

public class CaregiverActivity extends ActionBarActivity implements CaregiverDrawerFragment.NavigationDrawerCallbacks,
        android.support.v7.app.ActionBar.OnNavigationListener, TransitionHandler, TrackedActivity, FragmentManager.OnBackStackChangedListener, INetworkStatusHandler,
        VideoApp.OperationChangeListener, VideoApp.CallNegotiationListener {

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
    HashMap<String, Fragment> fragmentCache = new HashMap<String, Fragment>();
    private ColorDrawable currentBgColor;
    private android.support.v7.app.ActionBar actionBar;
    private PDApplicationContext app;
    private CaregiverDrawerFragment mCaregiverDrawerFragment;
    private TipsPresenter tipsPresenter;
    //private SensorsFragmentWithService sensorFragment;
    private int lastNavigationItemIndex = 1;
    private RecordingServiceFragment bandFragment;
    private LogEventFragment logFragment;
    private RecordingSettingsFragment recordingSettingsFragment;
    private RecordingSchedulingFragment recordingSchedulingFragment;
    private MedListFragment medListFragment;
    private DiaryFragment diaryFragment;
    private AlertListFragment alertListFragment;



    /*

     */
    private CaregiverHomeFragment caregiverHomeFragment;
    private AlertDialog callDialog = null;
    private Ringtone currentRingtone;
    private VideoApp application = null;
    private boolean mIsAlive = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver = null;

    @Override
    public void onBackPressed() {
        if (mCaregiverDrawerFragment.isDrawerOpen()) {


            mCaregiverDrawerFragment.closeDrawer();
        } else {


            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            Resources resources = getResources();
            ColorDrawable bgColorPrimary = new ColorDrawable(resources.getColor(R.color.primary_title_background));
            ColorDrawable bgColorSecondary = new ColorDrawable(resources.getColor(R.color.secondary_title_background));
            currentBgColor = bgColorPrimary;
            setContentView(R.layout.activity_caregiver);
            Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar);
            this.setSupportActionBar(tb);
            tb.setTitleTextColor(Color.WHITE);
            actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setBackgroundDrawable(currentBgColor);
            }
            this.setupActionBar();
        } catch (Exception ex) {

        }

        //RecordingSettingsHandler.getInstance().Init(getApplicationContext());


        app = (PDApplicationContext) this.getApplicationContext();


        //    this.tipsPresenter = com.telerik.android.common.Util.getLayoutPart(this, R.id.tipsPresenter, TipsPresenter.class);


        try {
            this.setupNavigationDrawer(savedInstanceState);

        } catch (Exception e) {

        }
        try {
            // Prevents the drawer from being opened at the time of the first launch.
            //com.telerik.android.common.Util.getLayoutPart(this, R.id.drawer_layout, DrawerLayout.class).closeDrawer(Gravity.LEFT);
            this.getSupportFragmentManager().addOnBackStackChangedListener(this);
            if (savedInstanceState == null) {
                this.loadSectionFromIntent(this.getIntent(), false);
                //    this.app.trackScreenOpened(this);

            }


        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.caregiver, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service

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
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //THIS FUNCTIONALITY WILL BE USED IN THE NEXT VERSION OF THE EXAMPLES.
        Fragment controlsFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);
        this.lastNavigationItemIndex = itemPosition;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {


        }
        if (id == R.id.action_test_alert) {


            initAlerts();


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner_selection", this.lastNavigationItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    private void loadSectionFromIntent(Intent intent, boolean addToBackStack) {

    }

    private void addFragmentForSection(String section, boolean addToBackStack) {
        Fragment newFragment = this.getSectionFragment(section);

        this.manageTipsPresenter(newFragment);
        this.app.loadFragment(this, newFragment, R.id.container, addToBackStack);
        this.invalidateOptionsMenu();

//        this.invalidateOptionsMenu();
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

                    Intent intent = new Intent(application.getContext(), CaregiverActivity.class);
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

        if (medListFragment == null) {
            medListFragment = new MedListFragment();
            fragmentCache.put(CaregiverDrawerFragment.NAV_DRAWER_MEDLIST, medListFragment);
        }


        if (alertListFragment == null) {
            alertListFragment = new AlertListFragment();
            fragmentCache.put(CaregiverDrawerFragment.NAV_DRAWER_ALERTLIST, alertListFragment);
        }
        if (diaryFragment == null) {
            diaryFragment = new DiaryFragment();
            fragmentCache.put(CaregiverDrawerFragment.NAV_DRAWER_DIARY, diaryFragment);
        }
    }

    private Fragment getSectionFragment(String section) {
        Fragment newFragment = null;


        if (fragmentCache.containsKey(section)) {
            Log.d("MainActivity", "Fragment from Cache");

            newFragment = fragmentCache.get(section);

        } else {


            if (section.equalsIgnoreCase(CaregiverDrawerFragment.NAV_DRAWER_ALERTLIST)) {
                if (alertListFragment == null) {
                    alertListFragment = new AlertListFragment();
                }
                newFragment = alertListFragment;
            } else if (section.equalsIgnoreCase(CaregiverDrawerFragment.NAV_DRAWER_MEDLIST)) {

                if (medListFragment == null)
                    medListFragment = new MedListFragment();

                newFragment = medListFragment;

            } else if (section.equalsIgnoreCase(CaregiverDrawerFragment.NAV_DRAWER_DIARY)) {

                if (diaryFragment == null)
                    diaryFragment = new DiaryFragment();

                newFragment = diaryFragment;

            } else if (section.equalsIgnoreCase(CaregiverDrawerFragment.NAV_DRAWER_SECTION_HOME)) {

                if (caregiverHomeFragment == null)
                    caregiverHomeFragment = new CaregiverHomeFragment();

                newFragment = caregiverHomeFragment;

            } else {
                if (caregiverHomeFragment == null)
                    caregiverHomeFragment = new CaregiverHomeFragment();

                newFragment = caregiverHomeFragment;
            }
            fragmentCache.put(section, newFragment);

        }

        return newFragment;
    }

    @Override
    public void updateTransition(float step) {
        Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);


    }

    public void invalidateActionbar() {
        invalidateActionbarTitle();
        invalidateBackground();
        invalidateOptionsMenu();
    }

    private void setupActionBar() {
        //this.actionBar.setListNavigationCallbacks(this, this);

    }

    private void setupNavigationDrawer(Bundle savedInstanceState) {


        mCaregiverDrawerFragment = (CaregiverDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mCaregiverDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            if (!this.getIntent().hasExtra(PDApplicationContext.INTENT_SECTION_ID)) {
                String selectedSection = mCaregiverDrawerFragment.selectedSection() == null ? CaregiverDrawerFragment.NAV_DRAWER_SECTION_HOME : mCaregiverDrawerFragment.selectedSection();
                this.addFragmentForSection(selectedSection, false);
            }
        } else {
            Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);
            this.manageTipsPresenter(currentFragment);
        }
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

    @Override
    public void onNavigationDrawerSectionSelected(String section) {
        this.addFragmentForSection(section, true);
    }

    @Override
    public void onNavigationDrawerControlSelected(MenuAction control) {


        //    if(control.getShortFragmentName()==CaregiverDrawerFragment.NAV_DRAWER_SECTION_FILES)
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

    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        manageTipsPresenter(currentFragment);
        if (currentFragment instanceof CaregiverDrawerFragment.SectionInfoProvider) {


            mCaregiverDrawerFragment.updateSelectedSection(((CaregiverDrawerFragment.SectionInfoProvider) currentFragment).getSectionName());
        }

        invalidateActionbar();
    }

    private long getTimeFromHour() {
        Date date1 = new java.util.Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        return cal1.getTimeInMillis() + 24 * 60 * 60 * 1000;


    }

    private void initAlerts() {

        Date date1 = new java.util.Date();

        UserAlertManager manager = new UserAlertManager(this);
        manager.clearAll();
        String msg = this.getString(com.pdmanager.R.string.cognitiveAlertMsg);
        manager.add(new UserAlert("Alert", "Test Warn Alert", "WARN", date1.getTime(), getTimeFromHour(), "WARN"));
        manager.add(new UserAlert("Info", "Test info Alert", "INFO", date1.getTime(), getTimeFromHour(), "INFO"));


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
                adapter.clearCommQueue();

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
