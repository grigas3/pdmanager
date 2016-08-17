package com.pdmanager.views;


import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pdmanager.core.PDApplicationContext;
import com.pdmanager.core.R;
import com.pdmanager.core.settings.RecordingSettings;
import com.pdmanager.gcm.RegistrationIntentService;
import com.pdmanager.views.drawers.ClinicianDrawerFragment;
import com.telerik.common.TelerikActivityHelper;
import com.telerik.common.TrackedApplication;
import com.telerik.common.contracts.TrackedActivity;
import com.telerik.common.contracts.TransitionHandler;
import com.telerik.primitives.TipsPresenter;
import com.telerik.viewmodels.MenuAction;

import java.util.HashMap;


public class ClinicianActivity extends ActionBarActivity implements ClinicianDrawerFragment.NavigationDrawerCallbacks,
        ActionBar.OnNavigationListener, TransitionHandler, SpinnerAdapter, TrackedActivity, FragmentManager.OnBackStackChangedListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String CPTAG = "CHECKPLAYSERVICES";
    private ColorDrawable currentBgColor;
    private int redFrom;
    private int redTo;
    private int greenFrom;
    private int greenTo;
    private int blueFrom;
    private int blueTo;
    private ActionBar actionBar;
    private PDApplicationContext app;
    private ClinicianDrawerFragment mClinicianDrawerFragment;
    private TipsPresenter tipsPresenter;
    private int lastNavigationItemIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelerikActivityHelper.updateActivityTaskDescription(this);
        Resources resources = getResources();
        ColorDrawable bgColorPrimary = new ColorDrawable(resources.getColor(R.color.primary_title_background));
        ColorDrawable bgColorSecondary = new ColorDrawable(resources.getColor(R.color.secondary_title_background));
        currentBgColor = bgColorPrimary;
        setContentView(R.layout.activity_clinician);
        Toolbar tb = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(tb);
        tb.setTitleTextColor(Color.WHITE);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setBackgroundDrawable(currentBgColor);
        }

        redFrom = Color.red(bgColorPrimary.getColor());
        redTo = Color.red(bgColorSecondary.getColor());

        greenFrom = Color.green(bgColorPrimary.getColor());
        greenTo = Color.green(bgColorSecondary.getColor());

        blueFrom = Color.blue(bgColorPrimary.getColor());
        blueTo = Color.blue(bgColorSecondary.getColor());

        app = (PDApplicationContext) this.getApplicationContext();
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doPatientSearch(query);
        }


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

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent sintent = new Intent(this, RegistrationIntentService.class);
            startService(sintent);
            // startService(intent);
        }

    }

    private void doPatientSearch(String query) {


    }

    @Override
    protected void onResume() {
        super.onResume();
        this.invalidateActionbar();
        this.gotoMainFragment();
    }

    private void gotoMainFragment() {
        Fragment newFragment = new ClinicianHomeFragment();


        this.manageTipsPresenter(newFragment);


        this.app.loadFragment(this, newFragment, R.id.container, false);

    }

    @Override
    public void onBackPressed() {
        if (mClinicianDrawerFragment.isDrawerOpen()) {


            mClinicianDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        manageTipsPresenter(currentFragment);
        if (currentFragment instanceof ClinicianDrawerFragment.SectionInfoProvider) {


            mClinicianDrawerFragment.updateSelectedSection(((ClinicianDrawerFragment.SectionInfoProvider) currentFragment).getSectionName());
        }

        invalidateActionbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner_selection", this.lastNavigationItemIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // this.lastNavigationItemIndex = savedInstanceState.getInt("spinner_selection", this.lastNavigationItemIndex);
       /* Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof BasePDFragment) {
            this.invalidateActionbar();
            this.actionBar.setSelectedNavigationItem(this.lastNavigationItemIndex);
        }
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clinician, menu);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            RecordingSettings settings = new RecordingSettings(this);
            settings.setLoggedIn(false);
            Intent mainIntent = new Intent(ClinicianActivity.this, LoginActivity.class);
            ClinicianActivity.this.startActivity(mainIntent);

            finish();


        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        //THIS FUNCTIONALITY WILL BE USED IN THE NEXT VERSION OF THE EXAMPLES.
        Fragment controlsFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);


        this.lastNavigationItemIndex = itemPosition;

        return false;
    }

    private void loadSectionFromIntent(Intent intent, boolean addToBackStack) {

    }

    private void addFragmentForSection(String section, boolean addToBackStack) {
        Fragment newFragment = this.getSectionFragment(section);
        if (newFragment == null) {
            if (section.equalsIgnoreCase(ClinicianDrawerFragment.NAV_DRAWER_SECTION_SETTINGS)) {
                this.app.showSettings(this);
            }
            return;
        }

        this.manageTipsPresenter(newFragment);

     /*   if (newFragment instanceof BasePDFragment) {
            BasePDFragment typedFragment = (BasePDFragment) newFragment;
            if (this.lastNavigationItemIndex == 0) {
                typedFragment.showAll();
            } else if (this.lastNavigationItemIndex == 1) {
                typedFragment.showHighlighted();
            }
        }
       */

        this.app.loadFragment(this, newFragment, R.id.container, addToBackStack);
    /*    if (newFragment instanceof FavoritesFragment) {
            this.app.trackEvent(TrackedApplication.HOME_SCREEN, TrackedApplication.EVENT_SHOW_FAVOURITES);
        }else if (newFragment instanceof AboutFragment){
            this.app.trackEvent(TrackedApplication.HOME_SCREEN, TrackedApplication.EVENT_SHOW_ABOUT);
        }
        */
        this.invalidateOptionsMenu();

//        this.invalidateOptionsMenu();
    }

    private void manageTipsPresenter(Fragment newFragment) {

    }

    private Fragment getSectionFragment(String section) {
        Fragment newFragment = null;


        if (section.equalsIgnoreCase(ClinicianDrawerFragment.NAV_DRAWER_SECTION_HOME)) {
            newFragment = new ClinicianHomeFragment();
        } else if (section.equalsIgnoreCase(ClinicianDrawerFragment.NAV_DRAWER_SECTION_PATIENTS)) {
            newFragment = new PatientListFragment();
        } else if (section.equalsIgnoreCase(ClinicianDrawerFragment.NAV_DRAWER_SECTION_ABOUT)) {
            newFragment = new AboutFragment();
        } else {
            newFragment = new ClinicianHomeFragment();
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
        this.actionBar.setListNavigationCallbacks(this, this);

    }

    private void setupNavigationDrawer(Bundle savedInstanceState) {


        mClinicianDrawerFragment = (ClinicianDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mClinicianDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            if (!this.getIntent().hasExtra(PDApplicationContext.INTENT_SECTION_ID)) {
                String selectedSection = mClinicianDrawerFragment.selectedSection() == null ? ClinicianDrawerFragment.NAV_DRAWER_SECTION_HOME : mClinicianDrawerFragment.selectedSection();
                this.addFragmentForSection(selectedSection, false);
            }
        } else {
            Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);
            this.manageTipsPresenter(currentFragment);
        }
    }

    private void invalidateActionbarTitle() {
        Fragment currentFragment = this.getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof PatientListFragment) {
            this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            this.actionBar.setTitle("Patient List");
        } /* else if (currentFragment instanceof PatientListFragment) {
            this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            this.actionBar.setTitle(R.string.favoritesStringPascalCase);
        }*/ else {
            this.actionBar.setTitle("Home");
            this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            //this.actionBar.setSelectedNavigationItem(this.lastNavigationItemIndex);
        }
    }

    private void invalidateBackground() {

    }

    private int primaryBgColor() {
        return Color.rgb(redFrom, greenFrom, blueFrom);
    }

    private int secondaryBgColor() {
        return Color.rgb(redTo, greenTo, blueTo);
    }

    private int calculateCurrentColor(float step) {
        return Color.rgb(
                calculateCurrentStep(redFrom, redTo, step),
                calculateCurrentStep(greenFrom, greenTo, step),
                calculateCurrentStep(blueFrom, blueTo, step)
        );
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
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        FrameLayout root = (FrameLayout) View.inflate(app, R.layout.actionbar_list_item, null);
        TextView text = (TextView) root.findViewById(R.id.actionBarTextView);
        text.setText(this.getItem(position).toString());
        return root;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return getResources().getString(R.string.allControlsStringPascalCase);
        } else if (position == 1) {
            return getResources().getString(R.string.actionbar_section_highlighted);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView root = (TextView) View.inflate(app, R.layout.actionbar_spinner_main_item, null);
        root.setText(this.getItem(position).toString());
        return root;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
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

        this.addFragmentForSection(control.getFragmentName(), true);

        //  this.app.openAction(this, control);
    }

    @Override
    public void onNavigationDrawerOpened() {

    }

    @Override
    public void onNavigationDrawerClosed() {

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(CPTAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}

