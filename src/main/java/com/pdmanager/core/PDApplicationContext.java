package com.pdmanager.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tagmanager.ContainerHolder;
import com.pdmanager.views.clinician.ClinicianActivity;
import com.pdmanager.views.patient.MainActivity;
import com.telerik.common.google.ContainerHolderSingleton;
import com.telerik.viewmodels.MenuAction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by George on 1/30/2016.
 */
public class PDApplicationContext extends VideoApp implements Thread.UncaughtExceptionHandler {

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
        Thread.setDefaultUncaughtExceptionHandler(this);

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


    public void setAnalyticsActive(Activity callingActivity, boolean active, boolean showPrompt) {
        if (!this.analyticsLearned && active && showPrompt) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(callingActivity);
            dialogBuilder.setView(View.inflate(callingActivity, R.layout.analytics_message, null));
            dialogBuilder.setTitle(R.string.analytics_message_title);
            dialogBuilder.setPositiveButton(R.string.analytics_message_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setAnalyticsActive(true);
                    setAnalyticsLearned(true);
                }
            });

            dialogBuilder.setNegativeButton(R.string.analytics_message_dont_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setAnalyticsActive(false);
                    setAnalyticsLearned(true);
                }
            });

            AlertDialog dialog = dialogBuilder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    resetActionsCounter();
                }
            });
            dialog.show();
        } else {
            this.setAnalyticsActive(active);
        }
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

    public void setAnalyticsActive(boolean active) {
        this.analyticsActive = active;
        this.editor.putBoolean(ANALYTICS_ACTIVE_KEY, active);
        this.editor.commit();
        if (active && this.canStartAnalytics()) {
            this.startMonitor();
        } else if (this.canStopAnalytics()) {
            this.stopMonitor();
            if (active) {
                this.analyticsActive = false;
                this.editor.putBoolean(ANALYTICS_ACTIVE_KEY, false);
                this.editor.commit();
                Toast.makeText(this, R.string.could_not_change_setting, Toast.LENGTH_SHORT).show();
            }
        } else if (active) {
            this.analyticsActive = false;
            this.editor.putBoolean(ANALYTICS_ACTIVE_KEY, false);
            this.editor.commit();
            Toast.makeText(this, R.string.could_not_change_setting, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected boolean canStartAnalytics() {
        return super.canStartAnalytics() && this.analyticsActive;
    }

    public boolean analyticsActive() {
        return this.analyticsActive;
    }

    @Override
    public void onActivityStarted(final Activity activity) {
        super.onActivityStarted(activity);
        if (!this.analyticsLearned) {

        }
    }

    public boolean getTipLearned() {
        ContainerHolder containerHolder = ContainerHolderSingleton.getContainerHolder();
        if (containerHolder != null && containerHolder.getContainer() != null) {
            String messageId = containerHolder.getContainer().getString(ContainerHolderSingleton.ANALYTICS_GOT_IT_ID);
            if (messageId != null) {
                String preferencesMessageId = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(ContainerHolderSingleton.ANALYTICS_GOT_IT_ID, null);
                if (!messageId.equals(preferencesMessageId)) {
                    editor.putString(ContainerHolderSingleton.ANALYTICS_GOT_IT_ID, messageId);
                    editor.commit();
                    this.tipLearned = false;
                }
            }
        }
        return this.tipLearned;
    }

    public void setTipLearned(boolean tipLearned) {
        this.editor.putBoolean(TIP_LEARNED_KEY, tipLearned);
        this.editor.commit();
        this.tipLearned = tipLearned;
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
        Intent mainActivity = new Intent(this, ClinicianActivity.class);
//        if (section.equals(NavigationDrawerFragment.NAV_DRAWER_SECTION_HOME)) {
//            mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
        mainActivity.putExtra(PDApplicationContext.INTENT_SECTION_ID, section);
        callingActivity.startActivity(mainActivity);
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

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {


       // trackException(throwable);

        defaultUEHandler.uncaughtException(thread, throwable);
    }

    private void savePreferences() {
        // Workaround for the problem where the stored set must be immutable bellow API 17
        editor.putStringSet(FAVORITES, new HashSet<String>(this.favorites));

        editor.commit();
    }

    private void parseActions() {

    }

    public void openAction(Activity callingActivity, MenuAction example) {

        Intent exampleInfoIntent = new Intent(callingActivity, ClinicianActivity.class);
        exampleInfoIntent.putExtra(PDApplicationContext.INTENT_CONTROL_ID, example.getFragmentName());
        callingActivity.startActivity(exampleInfoIntent);

    }

    public void openPatientAction(Activity callingActivity, String section) {

        Intent exampleInfoIntent = new Intent(callingActivity, MainActivity.class);
        exampleInfoIntent.putExtra(PDApplicationContext.INTENT_CONTROL_ID, section);
        callingActivity.startActivity(exampleInfoIntent);

    }

    public void openPatientAction(Activity callingActivity, MenuAction example) {

        Intent exampleInfoIntent = new Intent(callingActivity, MainActivity.class);
        exampleInfoIntent.putExtra(PDApplicationContext.INTENT_CONTROL_ID, example.getFragmentName());
        callingActivity.startActivity(exampleInfoIntent);

    }

    public void openActivity(Activity callingActivity, Class<?> newActivity) {

        Intent exampleInfoIntent = new Intent(callingActivity, newActivity);

        callingActivity.startActivity(exampleInfoIntent);

    }

    public interface FavouritesChangedListener {
        void favouritesChanged();
    }

}
