package com.pdmanager.views.common;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.oovoo.sdk.api.LogSdk;
import com.pdmanager.R;
import com.pdmanager.app.VideoApp;
import com.pdmanager.call.CNMessage;
import com.pdmanager.gcm.RegistrationIntentService;
import com.pdmanager.settings.VideoSettings;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.call.AVChatSessionFragment;
import com.pdmanager.views.call.CallNegotiationFragment;
import com.pdmanager.views.call.WaitingFragment;
import com.pdmanager.views.clinician.ClinicianActivity;
import com.pdmanager.views.patient.MainActivity;

/**
 * Created by mprasinos on 30/3/2017.
 */

public class BasePDActivity extends ActionBarActivity implements VideoApp.OperationChangeListener, VideoApp.CallNegotiationListener {

    protected static final String TAG = BasePDActivity.class.getSimpleName();
    protected static final String STATE_FRAGMENT = "current_fragment";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected BasePDFragment current_fragment = null;
    protected VideoApp application = null;
    protected MenuItem mSignalStrengthMenuItem = null;
    protected MenuItem mSecureNetworkMenuItem = null;
    private boolean mIsAlive = false;
    private boolean mNeedShowFragment = false;
    private AlertDialog callDialog = null;
    private BroadcastReceiver mRegistrationBroadcastReceiver = null;
    private Ringtone currentRingtone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        application = (VideoApp) getApplication();
        application.setContext(this);

        application.addOperationChangeListener(this);
        application.addCallNegotiationListener(this);
        //application.onMainActivityCreated();
        //fetch current Ringtone
        Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(this.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        currentRingtone = RingtoneManager.getRingtone(this, currentRintoneUri);
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

//                    Class activityClass = MainActivity.class;
//                    if (VideoSettings.IsDoctor.equals("true")) {
//                        activityClass = ClinicianActivity.class;
//                    }
//                    Intent intent = new Intent(application.getContext(), activityClass);
//                    intent.setAction(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    startActivity(intent);

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
                getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                            current_fragment = CallNegotiationFragment.newInstance();
                            break;
                        default:
                            return;
                    }
                }
                break;
                case Processing:
                    current_fragment = WaitingFragment.newInstance(state.getDescription());
                    break;
                case AVChatCall:
                    current_fragment = CallNegotiationFragment.newInstance();
                    break;
                case AVChatJoined:
                    current_fragment = AVChatSessionFragment.newInstance(mSignalStrengthMenuItem, mSecureNetworkMenuItem);
                    break;
                case Authorized:
                    //current_fragment = CallNegotiationFragment.newInstance();
                    break;
                case LoggedIn:
                    if (checkPlayServices()) {
                        // Start IntentService to register this application with GCM.
                        Intent intent = new Intent(this, RegistrationIntentService.class);
                        startService(intent);
                    }
                    //current_fragment = CallNegotiationFragment.newInstance();
                    break;
                case AVChatDisconnected:
                    if (application.isCallNegotiation()) {
                        return;
                    } else {
                        current_fragment = CallNegotiationFragment.newInstance();
                        break;
                    }

                default:
                    return;
            }

            showFragment(current_fragment);

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    protected void showFragment(Fragment newFragment) {
        if (!mIsAlive) {
            mNeedShowFragment = true;
            return;
        }

        try {
            if (newFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, newFragment);
                transaction.addToBackStack(newFragment.getClass().getSimpleName());
                transaction.commit();
            }
        } catch (Exception err) {
            LogSdk.e(TAG, "showFragment " + err);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                LogSdk.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public void finish() {
        if (current_fragment != null) {
            this.removeFragment(current_fragment);
            current_fragment = null;
        }
        application.logout();
        super.finish();
    }

    private void removeFragment(Fragment fragment) {

        try {
            if (fragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(current_fragment);
                transaction.show(fragment);
                transaction.commit();
            }
        } catch (Exception err) {
            LogSdk.e(TAG, "removeFragment " + err);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        application.removeOperationChangeListener(this);
        application.removeCallNegotiationListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (mRegistrationBroadcastReceiver == null) {
                mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        boolean sentToken = sharedPreferences.getBoolean(VideoSettings.SENT_TOKEN_TO_SERVER, false);
                        if (!sentToken) {
                            application.showErrorMessageBox(BasePDActivity.this, getString(R.string.registering_message), getString(R.string.token_error_message));
                        }
                    }
                };
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(VideoSettings.REGISTRATION_COMPLETE));
            }
        } catch (Exception err) {
            Log.e(TAG, "onResume exception: with ", err);
        }


        mIsAlive = true;

        if (mNeedShowFragment) {
            showFragment(current_fragment);
            mNeedShowFragment = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mIsAlive = false;
    }

}
