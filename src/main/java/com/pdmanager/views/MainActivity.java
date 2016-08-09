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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pdmanager.core.PDApplicationContext;
import com.pdmanager.core.R;
import com.pdmanager.core.RecordingService;
import com.pdmanager.core.communication.BatchCommSender;
import com.pdmanager.core.communication.CommunicationQueue;
import com.pdmanager.core.communication.JsonConverter;
import com.pdmanager.core.communication.JsonStorage;
import com.pdmanager.core.communication.NetworkStatus;
import com.pdmanager.core.interfaces.INetworkStatusHandler;
import com.pdmanager.core.logging.LogAdapter;
import com.pdmanager.core.sensor.RecordingServiceHandler;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.views.drawers.PatientDrawerFragment;
import com.telerik.common.TelerikActivityHelper;
import com.telerik.common.TrackedApplication;
import com.telerik.common.contracts.TrackedActivity;
import com.telerik.common.contracts.TransitionHandler;
import com.telerik.primitives.TipsPresenter;
import com.telerik.viewmodels.MenuAction;

import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements PatientDrawerFragment.NavigationDrawerCallbacks,
        android.support.v7.app.ActionBar.OnNavigationListener, TransitionHandler, TrackedActivity, FragmentManager.OnBackStackChangedListener, INetworkStatusHandler {




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
    SensorsFragment displayFragment;
    HashMap<String, Fragment> fragmentCache = new HashMap<String, Fragment>();
    private ColorDrawable currentBgColor;
    private android.support.v7.app.ActionBar actionBar;
    private PDApplicationContext app;
    private PatientDrawerFragment mPatientDrawerFragment;
    private TipsPresenter tipsPresenter;
    //private SensorsFragmentWithService sensorFragment;
    private int lastNavigationItemIndex = 1;
    private RecordingServiceFragment bandFragment;
    private LogEventFragment logFragment;
    private RecordingSettingsFragment recordingSettingsFragment;
    private MedAdminFragment medAdminFragment;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            RecordingService.LocalBinder binder = (RecordingService.LocalBinder) service;
            mService = binder.getService();
            //        Intent intent = new Intent(className, BandService.class);


            RecordingServiceHandler.getInstance().setService(mService);

            initFragments();

            mService.registerHRAccessProvider(bandFragment);

            mService.registerHandler(displayFragment);
            mService.registerListener(bandFragment);
            mService.registerSensorListener(bandFragment);
            mService.registerListener(recordingSettingsFragment);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    private Intent intent;

    @Override
    public void onBackPressed() {
        if (mPatientDrawerFragment.isDrawerOpen()) {


            mPatientDrawerFragment.closeDrawer();
        } else {


            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TelerikActivityHelper.updateActivityTaskDescription(this);
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


        //RecordingSettingsHandler.getInstance().Init(getApplicationContext());


        app = (PDApplicationContext) this.getApplicationContext();


        this.tipsPresenter = com.telerik.android.common.Util.getLayoutPart(this, R.id.tipsPresenter, TipsPresenter.class);

        this.setupNavigationDrawer(savedInstanceState);
        this.setupActionBar();

        // Prevents the drawer from being opened at the time of the first launch.
        com.telerik.android.common.Util.getLayoutPart(this, R.id.drawer_layout, DrawerLayout.class).closeDrawer(Gravity.LEFT);
        this.getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            this.loadSectionFromIntent(this.getIntent(), false);
            this.app.trackScreenOpened(this);

        }


    }

    @Override
    public void onDestroy() {

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

        if (mService != null)
            mService.registerHandler(displayFragment);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {

            mService.unregisterListener(bandFragment);
            mService.unregisterSensorListener(bandFragment);
            mService.unregisterListener(recordingSettingsFragment);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mainsettings) {

            Intent mainIntent = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(mainIntent);
            //   finish();
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearlog) {


            LogAdapter adapter = new LogAdapter(this);
            new ClearLog().execute(adapter);


        }


        if (id == R.id.action_sendqueue) {


            Gson gson = new Gson();
            try {
                CommunicationQueue queue = new CommunicationQueue(CommunicationQueue.CreateQueueFile(), new JsonConverter<JsonStorage>(gson, JsonStorage.class));
                new BatchCommSender(this, null) {

                }.execute(queue);


            } catch (Exception ex) {

            }

        }
        if (id == R.id.action_logout) {

            RecordingSettings settings = new RecordingSettings(this);
            settings.setLoggedIn(false);


            Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(mainIntent);
            finish();


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
        /*this.lastNavigationItemIndex = savedInstanceState.getInt("spinner_selection", this.lastNavigationItemIndex);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof BasePDFragment) {
            this.invalidateActionbar();
            this.actionBar.setSelectedNavigationItem(this.lastNavigationItemIndex);
        }
        */

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

    private void initFragments() {


        if (bandFragment == null) {
            bandFragment = new RecordingServiceFragment();
            fragmentCache.put(PatientDrawerFragment.NAV_DRAWER_SECTION_HOME, bandFragment);

        }
        if (recordingSettingsFragment == null) {
            recordingSettingsFragment = new RecordingSettingsFragment();
            fragmentCache.put(PatientDrawerFragment.NAV_DRAWER_SECTION_HOME, recordingSettingsFragment);
        }

        if (displayFragment == null) {
            displayFragment = new SensorsFragment();
            fragmentCache.put(PatientDrawerFragment.NAV_DRAWER_SECTION_DISPLAY, displayFragment);

        }
    }

    private Fragment getSectionFragment(String section) {
        Fragment newFragment = null;


        if (fragmentCache.containsKey(section)) {
            Log.d("MainActivity", "Fragment from Cache");

            newFragment = fragmentCache.get(section);

        } else {


            if (section.equalsIgnoreCase(PatientDrawerFragment.NAV_DRAWER_SECTION_DISPLAY)) {
                if (displayFragment == null) {
                    displayFragment = new SensorsFragment();


                }
                if (mService != null)
                    mService.registerHandler(displayFragment);

                newFragment = displayFragment;
            } else if (section.equalsIgnoreCase(PatientDrawerFragment.NAV_DRAWER_SECTION_SETTINGS)) {

                if (recordingSettingsFragment == null)
                    recordingSettingsFragment = new RecordingSettingsFragment();

                newFragment = recordingSettingsFragment;

            } else if (section.equalsIgnoreCase(PatientDrawerFragment.NAV_DRAWER_MEDADMIN)) {

                if (medAdminFragment == null)
                    medAdminFragment = new MedAdminFragment();

                newFragment = medAdminFragment;

            } else if (section.equalsIgnoreCase(PatientDrawerFragment.NAV_DRAWER_SECTION_LOGS)) {
                newFragment = new LogEventFragment();
            } else {
                if (bandFragment == null)
                    bandFragment = new RecordingServiceFragment();

                newFragment = bandFragment;
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

    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        manageTipsPresenter(currentFragment);
        if (currentFragment instanceof PatientDrawerFragment.SectionInfoProvider) {


            mPatientDrawerFragment.updateSelectedSection(((PatientDrawerFragment.SectionInfoProvider) currentFragment).getSectionName());
        }

        invalidateActionbar();
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
