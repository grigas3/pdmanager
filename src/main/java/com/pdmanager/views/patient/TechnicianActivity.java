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

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;
import com.pdmanager.call.CNMessage;
import com.pdmanager.common.ConnectionResult;
import com.pdmanager.common.Util;
import com.pdmanager.app.PDApplicationContext;
import com.pdmanager.app.PDPilotAppContext;
import com.pdmanager.R;
import com.pdmanager.app.VideoApp;
import com.pdmanager.communication.BatchCommSender;
import com.pdmanager.communication.NetworkStatus;
import com.pdmanager.communication.SQLCommunicationQueue;
import com.pdmanager.interfaces.IBandTileManager;
import com.pdmanager.interfaces.INetworkStatusHandler;
import com.pdmanager.logging.LogAdapter;
import com.pdmanager.sensor.RecordingServiceHandler;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.LogEventFragment;
import com.pdmanager.views.RecordingSchedulingFragment;
import com.pdmanager.views.RecordingServiceFragment;
import com.pdmanager.views.RecordingSettingsFragment;
import com.pdmanager.views.common.LoginActivity;
import com.pdmanager.views.drawers.TechnicianDrawerFragment;
import com.telerik.common.TrackedApplication;
import com.telerik.common.contracts.TrackedActivity;
import com.telerik.common.contracts.TransitionHandler;
import com.telerik.primitives.TipsPresenter;
import com.telerik.viewmodels.MenuAction;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//import com.pdmanager.services.RegistrationIntentService;

public class TechnicianActivity extends ActionBarActivity implements TechnicianDrawerFragment.NavigationDrawerCallbacks,IBandTileManager,
        android.support.v7.app.ActionBar.OnNavigationListener, TransitionHandler, TrackedActivity, FragmentManager.OnBackStackChangedListener, INetworkStatusHandler,
        VideoApp.OperationChangeListener, VideoApp.CallNegotiationListener
{

    RecordingService mService;
    boolean mBound = false;
    FilesFragment filesFragment;
    HashMap<String, Fragment> fragmentCache = new HashMap<String, Fragment>();
    private ColorDrawable currentBgColor;
    private android.support.v7.app.ActionBar actionBar;
    private PDPilotAppContext app;
    private TechnicianDrawerFragment mTechnicianDrawerFragment;
    private TipsPresenter tipsPresenter;
    //private SensorsFragmentWithService sensorFragment;
    private int lastNavigationItemIndex = 1;
    private RecordingServiceFragment bandFragment;
    private LogEventFragment logFragment;
    private RecordingSettingsFragment recordingSettingsFragment;
    private RecordingSchedulingFragment recordingSchedulingFragment;

    private MedListFragment medAdminFragment;



    /*

     */


    private boolean isBluetoothEnabled() {


        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


            if (mBluetoothAdapter != null) {
                return mBluetoothAdapter.isEnabled();

            }

            return false;


        } catch (Exception ex) {

            return false;
        }

    }
    private void enableBluetooth() {
        boolean bluetoothEnabled = isBluetoothEnabled();

        if (!bluetoothEnabled) {


            try {
                BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
                mAdapter.enable();
                Thread.sleep(3000);

              //  LogInfo("Bluetooth activated by service");
                //LogInfo("Bluetooth activated by service");
            } catch (Exception ex) {

             //   LogError("Cannot activate bluetooth Band Sensors");
                //LogInfoError("Cannot activate bluetooth Band Sensors");


            }


            //requireBluetooth();

        }

    }

@Override
public void createTile()
    {


        enableBluetooth();
//First Remove Tile


        BandClient mClient=null;
        BandClientManager manager = BandClientManager.getInstance();
        BandInfo[] mPairedBands = manager.getPairedBands();

        if (mPairedBands.length > 0) {


            mClient = manager.create(this, mPairedBands[0]);
            new CreateTileTask(this).execute(mClient);

        }





    }





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
        if (mTechnicianDrawerFragment.isDrawerOpen()) {


            mTechnicianDrawerFragment.closeDrawer();
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
            setContentView(R.layout.activity_technician);
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

        }

        //RecordingSettingsHandler.getInstance().Init(getApplicationContext());


          app = (PDPilotAppContext) this.getApplicationContext();


    //    this.tipsPresenter = com.telerik.android.common.Util.getLayoutPart(this, R.id.tipsPresenter, TipsPresenter.class);


        try {
            this.setupNavigationDrawer(savedInstanceState);

        }
        catch (Exception e) {

        }
        try {
            // Prevents the drawer from being opened at the time of the first launch.
            //com.telerik.android.common.Util.getLayoutPart(this, R.id.drawer_layout, DrawerLayout.class).closeDrawer(Gravity.LEFT);
            this.getSupportFragmentManager().addOnBackStackChangedListener(this);
            if (savedInstanceState == null) {
                this.loadSectionFromIntent(this.getIntent(), false);
                //    this.app.trackScreenOpened(this);

            }


        }
        catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.technician, menu);
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
       /* if (id == R.id.action_mainsettings) {

            Intent mainIntent = new Intent(TechnicianActivity.this, HomeActivity.class);
            TechnicianActivity.this.startActivity(mainIntent);
            //   finish();
        }
    */

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearlog) {


            LogAdapter adapter = new LogAdapter(this);
            new ClearLog().execute(adapter);




        }
        //noinspection SimplifiableIfStatement
      else  if (id == R.id.action_syncms) {


            Intent mainIntent = new Intent(TechnicianActivity.this, MSSyncActivity.class);
            TechnicianActivity.this.startActivity(mainIntent);




        }

       else if (id == R.id.action_sendqueue) {


            Gson gson = new Gson();
            try {
                //CommunicationQueue queue = new CommunicationQueue(CommunicationQueue.CreateQueueFile(), new JsonConverter<JsonStorage>(gson, JsonStorage.class));

                SQLCommunicationQueue queue = new SQLCommunicationQueue(this);

                new BatchCommSender(this, null) {

                }.execute(queue);


            } catch (Exception ex) {

            }

        }
       else if (id == R.id.action_logout) {

            RecordingSettings settings = new RecordingSettings(this);
            settings.setLoggedIn(false);


            Intent mainIntent = new Intent(TechnicianActivity.this, LoginActivity.class);
            TechnicianActivity.this.startActivity(mainIntent);
            if (mBound) {

                mService.unregisterListener(bandFragment);
                mService.unregisterSensorListener(bandFragment);
                mService.unregisterListener(recordingSettingsFragment);

                unbindService(mConnection);
                if (!mService.getSessionMustRun())
                    getApplicationContext().stopService(intent);


                mBound = false;
            }

            finish();


        }
        else if (id == R.id.action_lock) {



            Intent mainIntent = new Intent(TechnicianActivity.this, MainActivity.class);
            TechnicianActivity.this.startActivity(mainIntent);
            if (mBound) {

                mService.unregisterListener(bandFragment);
                mService.unregisterSensorListener(bandFragment);
                mService.unregisterListener(recordingSettingsFragment);

                unbindService(mConnection);
                if (!mService.getSessionMustRun())
                    getApplicationContext().stopService(intent);


                mBound = false;
            }
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
    private AlertDialog callDialog = null;
    private Ringtone currentRingtone;

    private VideoApp application = null;
    private boolean mIsAlive = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver = null;
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

                    Intent intent = new Intent(application.getContext(), TechnicianActivity.class);
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

    public interface MenuList {
        void fill(View view, ContextMenu menu);
    }



    private void initFragments() {


        if (bandFragment == null) {
            bandFragment = new RecordingServiceFragment();

            bandFragment.setTileManager(this);
            fragmentCache.put(TechnicianDrawerFragment.NAV_DRAWER_SECTION_HOME, bandFragment);

        }
        if (recordingSettingsFragment == null) {
            recordingSettingsFragment = new RecordingSettingsFragment();
            fragmentCache.put(TechnicianDrawerFragment.NAV_DRAWER_SECTION_HOME, recordingSettingsFragment);
        }

        if (recordingSchedulingFragment == null) {
            recordingSchedulingFragment = new RecordingSchedulingFragment();
            fragmentCache.put(TechnicianDrawerFragment.NAV_DRAWER_SECTION_SCHEDULING, recordingSchedulingFragment);
        }
        if(filesFragment==null)
        {
            filesFragment=new FilesFragment();
            fragmentCache.put(TechnicianDrawerFragment.NAV_DRAWER_SECTION_FILES, filesFragment);
        }


    }

    private Fragment getSectionFragment(String section) {
        Fragment newFragment = null;


        if (fragmentCache.containsKey(section)) {
            Log.d("MainActivity", "Fragment from Cache");

            newFragment = fragmentCache.get(section);

        } else {


            if (section.equalsIgnoreCase(TechnicianDrawerFragment.NAV_DRAWER_SECTION_FILES)) {
                if (filesFragment == null) {
                    filesFragment = new FilesFragment();


                }


                newFragment = filesFragment;
            } else if (section.equalsIgnoreCase(TechnicianDrawerFragment.NAV_DRAWER_SECTION_SCHEDULING)) {

                if (recordingSchedulingFragment == null)
                    recordingSchedulingFragment = new RecordingSchedulingFragment();

                newFragment = recordingSchedulingFragment;

            } else if (section.equalsIgnoreCase(TechnicianDrawerFragment.NAV_DRAWER_SECTION_SETTINGS)) {

                if (recordingSettingsFragment == null)
                    recordingSettingsFragment = new RecordingSettingsFragment();

                newFragment = recordingSettingsFragment;

            }  else if (section.equalsIgnoreCase(TechnicianDrawerFragment.NAV_DRAWER_SECTION_LOGS)) {
                newFragment = new LogEventFragment();
            } else if (section.equalsIgnoreCase(TechnicianDrawerFragment.NAV_DRAWER_SECTION_MEDS)) {
                newFragment = new MedListFragment();
            } else {
                if (bandFragment == null) {
                    bandFragment = new RecordingServiceFragment();
                    bandFragment.setTileManager(this);



                }

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


        mTechnicianDrawerFragment = (TechnicianDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mTechnicianDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            if (!this.getIntent().hasExtra(PDApplicationContext.INTENT_SECTION_ID)) {
                String selectedSection = mTechnicianDrawerFragment.selectedSection() == null ? TechnicianDrawerFragment.NAV_DRAWER_SECTION_HOME : mTechnicianDrawerFragment.selectedSection();
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


        //    if(control.getShortFragmentName()==TechnicianDrawerFragment.NAV_DRAWER_SECTION_FILES)
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
        if (currentFragment instanceof TechnicianDrawerFragment.SectionInfoProvider) {


            mTechnicianDrawerFragment.updateSelectedSection(((TechnicianDrawerFragment.SectionInfoProvider) currentFragment).getSectionName());
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





    private void createTile(BandClient mClient)
    {




    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class CreateTileTask extends AsyncTask<BandClient, Void, ConnectionResult> {


        private Activity mActivity;

                public CreateTileTask(Activity pActivity)
                {
                    mActivity=pActivity;

                }

        @Override
        protected ConnectionResult doInBackground(BandClient... clientParams) {

            ConnectionResult result;
            BandPendingResult<ConnectionState> pendingResult = null;
            try {

                BandClient mClient = clientParams[0];
                pendingResult = clientParams[0].connect();


                ConnectionState res = pendingResult.await();
                result=new ConnectionResult(res);


                int tileCapacity = 0;
                try {


// determine the number of available tile slots on the Band
                     tileCapacity =
                            mClient.getTileManager().getRemainingTileCapacity().await();


                } catch (BandException e) {
// handle BandException
                } catch (InterruptedException e) {
// handle InterruptedException
                }
                catch (Exception e) {
                    Log.d("TECH_BANDTILE", e.getMessage());



// handle InterruptedEx  Log.d("TECH_BANDTILE", e.getMessage());ception
                }
                if (tileCapacity > 0) {
// Create the small and tile icons from writable bitmaps.
// Small icons are 24x24 pixels.
                    Bitmap smallIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pd24);
                    //Bitmap smallIconBitmap = Bitmap.createBitmap(24, 24,null );
                    BandIcon smallIcon = BandIcon.toBandIcon(smallIconBitmap);
// Tile icons are 46x46 pixels for Microsoft Band 1 and 48x48 pixels
// for Microsoft Band 2.
                    //  Bitmap tileIconBitmap = Bitmap.createBitmap(46, 46, null);

                    Bitmap tileIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pd46);
                    BandIcon tileIcon = BandIcon.toBandIcon(tileIconBitmap);

                    try {
                        UUID tileUUI=UUID.randomUUID();


                        String uuid=RecordingSettings.newInstance(getApplicationContext()).getTileUUID();

                        if(uuid!=null) {
                            tileUUI = UUID.fromString(uuid);
                        }

// get the current set of tiles
                        List<BandTile> tiles =
                                mClient.getTileManager().getTiles().await();
                        for(BandTile t : tiles) {

                            if(t.getTileId()==tileUUI||t.getTileName()=="PD")
                                if(mClient.getTileManager().removeTile(t).await()){

                                    Log.d("TECH_BANDTILE","PREVIOUS ONE REMOVED");
// do work if the tile was successfully removed
                                }
                        }
                    } catch (BandException e) {
                        Log.d("TECH_BANDTILE", e.getMessage());
// handle BandException
                    } catch (InterruptedException e) {
                        Log.d("TECH_BANDTILE", e.getMessage());
// handle InterruptedException
                    }
                    catch (Exception e) {
                        Log.d("TECH_BANDTILE", e.getMessage());



// handle InterruptedEx  Log.d("TECH_BANDTILE", e.getMessage());ception
                    }
// create a new UUID for the tile
                    UUID tileUuid = UUID.randomUUID();

                    RecordingSettings.newInstance(getApplicationContext()).setTileUUID(tileUuid);
// create a new BandTile using the builder
// add optional small icon
// enable badging (the count of unread messages)
                    BandTile tile = new BandTile.Builder(tileUuid, "PD", tileIcon)
                            .setTileSmallIcon(smallIcon).build();

                    // tile.IsBadingEnabled = true;
                    try {

                      boolean ret=  mClient.getTileManager().addTile(mActivity,
                                tile).await();
                        if (ret) {

                            Log.d("TECH_BANDTILE","Tile ok");
// do work if the tile was successfully created
                        }
                        else
                        {
                            Log.d("TECH_BANDTILE","Tile error");


                        }
                    }catch (BandIOException e) {
                        Log.d("TECH_BANDTILE", e.getMessage());
// handle BandException
                    } catch (BandException e) {
                        Log.d("TECH_BANDTILE", e.getMessage());
// handle BandException
                    } catch (InterruptedException e) {
                        Log.d("TECH_BANDTILE", e.getMessage());
// handle InterruptedException
                    }
                    catch (Exception e) {
                        Log.d("TECH_BANDTILE", e.getMessage());
// handle InterruptedException
                    }
                    finally {
                        if (mClient != null) {

                            mClient.disconnect();
                        }
                    }


                }

                }catch(InterruptedException ex){
                    Util.handleException("Connect to band", ex);
                    return new ConnectionResult(ex);
                    // handle InterruptedException
                }catch(BandException ex){

                    Util.handleException("Connect to band", ex);
                    return new ConnectionResult(ex);
                    // handle BandException
                }catch(Exception ex){

                    Util.handleException("Connect to band", ex);
                    return new ConnectionResult(ex);
                    // handle BandException
                }

            return result;

        }

        protected void onPostExecute(ConnectionResult res) {

        }
    }





}
