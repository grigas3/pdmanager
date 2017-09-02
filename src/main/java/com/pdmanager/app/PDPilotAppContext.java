package com.pdmanager.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.bugfender.sdk.Bugfender;
import com.pdmanager.BuildConfig;
import com.pdmanager.settings.RecordingSettings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import com.pdmanager.logging.AndroidLog4jHelper;
//import com.pdmanager.logging.Log4jConfigure;

/**
 * Created by George on 1/30/2016.
 */
public class PDPilotAppContext extends Application implements Application.ActivityLifecycleCallbacks  {

    public static final String INTENT_CONTROL_ID = "CONTROL_ID";
    public static final String INTENT_Action_ID = "Action_ID";
    public static final String INTENT_SECTION_ID = "SECTION_ID";
    public static final String INTENT_PATIENT_CODE = "PATIENT_CODE";
    public static final String INTENT_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String INTENT_ALERT_ID = "ALERT_ID";
    public static final String INTENT_ALERT_TYPE= "ALERT_TYPE";

    public static final String INTENT_SELECTED_DAY = "DAY";
    public static final String INTENT_PATIENT_NAME = "PATIENT_NAME";
    public static final String INTENT_SOURCE_MODEL_ID = "SOURCE_MODEL";
    private static final String PREFS_NAME = "telerik_Actions_preferences";
    private static final String FAVORITES = "favorites";
    private static final String TIP_LEARNED_KEY = "tip_learned";
    private static final String ANALYTICS_LEARNED_KEY = "analytics_learned";
    private static final String ANALYTICS_ACTIVE_KEY = "analytics_active";
    private static final String OPENED_ActionS_COUNT_KEY = "opened_Actions_count";
    private static final int OPEN_ANALYTICS_PROMPT_AFTER_COUNT = 3;
    public static String PACKAGE_NAME;
    private Set<String> favorites;
    private boolean tipLearned = false;
    private boolean analyticsActive = false;
    private boolean analyticsLearned = false;
    private int openedActionsCount = 0;
    private List<FavouritesChangedListener> favouritesChangedListeners = new ArrayList<FavouritesChangedListener>();
    private Thread.UncaughtExceptionHandler defaultUEHandler;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        PACKAGE_NAME = getApplicationContext().getPackageName();
        defaultUEHandler = Thread.getDefaultUncaughtExceptionHandler();
        initBugfender();
        // Log4jConfigure.configureRollingFile();
        /*
        if (BuildConfig.DEBUG) {
            AndroidLog4jHelper.initialise(this.getApplicationContext(), R.raw.log4j_debug);
        } else {
            AndroidLog4jHelper.initialise(this.getApplicationContext(), R.raw.log4j_release);
        }
        */

        // check for a more appropriate place to call parse
        this.parseActions();

        // Restore preferences
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        favorites = new HashSet<String>(
                preferences.getStringSet(FAVORITES, new HashSet<String>())
        );
        this.tipLearned = preferences.getBoolean(TIP_LEARNED_KEY, false);
        this.analyticsLearned = preferences.getBoolean(ANALYTICS_LEARNED_KEY, false);
        this.analyticsActive = preferences.getBoolean(ANALYTICS_ACTIVE_KEY, false);
        this.openedActionsCount = preferences.getInt(OPENED_ActionS_COUNT_KEY, 0);


    }


    private void initBugfender() {

        if (RecordingSettings.GetRecordingSettings(this).getRemoteLogging()) {

            Bugfender.init(this, "awOajBW2R4nKPi4iUQcDxKjSZ6wZcXrc", BuildConfig.DEBUG);
            Bugfender.enableLogcatLogging();
            Bugfender.enableUIEventLogging(this);
        }
    }

    public void addOnFavouritesChangedListener(FavouritesChangedListener listener) {
        if (this.favouritesChangedListeners.contains(listener)) {
            return;
        }
        this.favouritesChangedListeners.add(listener);
    }

    public void removeOnFavouritesChangedListener(FavouritesChangedListener listener) {
        if (!this.favouritesChangedListeners.contains(listener)) {
            return;
        }
        this.favouritesChangedListeners.remove(listener);
    }




    private void resetActionsCounter() {
        this.openedActionsCount = 0;
        this.editor.putInt(OPENED_ActionS_COUNT_KEY, 0);
        this.editor.commit();
    }

    public void setAnalyticsLearned(boolean learned) {
        this.editor.putBoolean(ANALYTICS_LEARNED_KEY, learned);
        this.analyticsLearned = learned;
        editor.commit();
    }




    public boolean analyticsActive() {
        return this.analyticsActive;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(final Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    public String extractClassName(final String className, final int wordsFromEnd) {
        final StringBuilder stringBuilder = new StringBuilder();
        final String[] nameSeparated = className.split("\\.");

        for (int len = nameSeparated.length, i = len - wordsFromEnd; i < len; i++) {
            stringBuilder.append(String.format("%s ", nameSeparated[i]));
        }

        return stringBuilder.toString();
    }


    public void showSettings(Activity callingActivity) {
        //      Intent settingsIntent = new Intent(this, SettingsActivity.class);
        //    callingActivity.startActivity(settingsIntent);
    }


    public void navigateToSection(Activity callingActivity, String section) {

    }

    public void loadFragment(FragmentActivity callingActivity, Fragment fragment, int containerId, boolean addToBackStack) {
        this.loadFragment(callingActivity, fragment, containerId, addToBackStack, 0, 0);
    }

    public void loadFragment(FragmentActivity callingActivity, Fragment fragment, int containerId, boolean addToBackStack, int outTransition, int inTransition) {
        FragmentManager supportFragmentManager = callingActivity.getSupportFragmentManager();
        if (!addToBackStack) {
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();
            transaction.setCustomAnimations(inTransition, outTransition, inTransition, outTransition);
            transaction.replace(containerId, fragment).commit();
        } else {
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();
            transaction.setCustomAnimations(inTransition, outTransition, inTransition, outTransition);
            transaction.replace(containerId, fragment).addToBackStack(fragment.getClass().getName()).commit();
        }
    }

    public int getDrawableResource(String name) {
        return this.getResources().getIdentifier("@" + name, "drawable", this.getPackageName());
    }

    public void setBackgroundDrawableSafe(View backgroundContainer, Drawable background) {
        int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Method setBackgroundDrawableMethod = FrameLayout.class.getMethod("setBackgroundDrawable", Drawable.class);
                setBackgroundDrawableMethod.invoke(backgroundContainer, background);
            } catch (Exception e) {
                Log.e("RD", "Could not set background gradient " + android.os.Build.VERSION.SDK_INT, e);
            }
        } else {
            try {
                Method setBackgroundDrawableMethod = FrameLayout.class.getMethod("setBackground", Drawable.class);
                setBackgroundDrawableMethod.invoke(backgroundContainer, background);
            } catch (Exception e) {
                Log.e("RD", "Could not set background gradient " + android.os.Build.VERSION.SDK_INT, e);
            }
        }
    }



    private void savePreferences() {
        // Workaround for the problem where the stored set must be immutable bellow API 17
        editor.putStringSet(FAVORITES, new HashSet<String>(this.favorites));

        editor.commit();
    }

    private void parseActions() {

    }



    public void openActivity(Activity callingActivity, Class<?> newActivity) {

        Intent exampleInfoIntent = new Intent(callingActivity, newActivity);

        callingActivity.startActivity(exampleInfoIntent);

    }

    public interface FavouritesChangedListener {
        void favouritesChanged();
    }

}
