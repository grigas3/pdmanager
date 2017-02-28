package com.pdmanager.views.patient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lnikkila.oidc.OIDCAccountManager;
import com.lnikkila.oidc.authenticator.OIDCClientConfigurationActivity;
import com.lnikkila.oidc.security.UserNotAuthenticatedWrapperException;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.common.exceptions.TokenDecryptionException;
import com.pdmanager.R;
import com.pdmanager.logging.ILogHandler;
import com.pdmanager.monitoring.MSHealthDataHandler;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.services.RecordingService;
import com.pdmanager.views.APIUtility;
import com.pdmanager.views.HomeActivity;

import java.io.IOException;
import java.util.Map;


public class MSSyncActivity extends Activity implements ILogHandler {

    private OIDCAccountManager accountManager;
    private Account availableAccounts[];
    private static final int RENEW_REFRESH_TOKEN = 2016;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String protectedResUrl = "https://api.microsofthealth.net/v1/me/Activities/";

    protected String userInfoEndpoint;

    private int selectedAccountIndex;
    RecordingService mService;
    boolean mBound = false;
    private TextView progressText;
    private ProgressBar progressBar;
    private Button mssyncButton;


    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mssync);
        accountManager = new OIDCAccountManager(getActivity());
        progressText=(TextView) this.findViewById(R.id.textView6);
        progressBar=(ProgressBar) this.findViewById(R.id.progressBar);
        mssyncButton=(Button) this.findViewById(R.id.mssyncButton);
        userInfoEndpoint = getString(R.string.op_userInfoEndpoint);

        mssyncButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // do something
                refreshAvailableAccounts();
                doLogin(v);
            }
        });


    }


    @Override
    protected void onResume() {



        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();



    }



    //endregion

    //region Account Utilities

    protected void refreshAvailableAccounts() {
        // Grab all our accounts
        availableAccounts = accountManager.getAccounts();
    }

    //endregion

    //region Buttons ClickListeners

    /**
     * Called when the user taps the big yellow button.
     */
    public void doLogin(final View view) {


        switch (availableAccounts.length) {
            // No account has been created, let's create one now
            case 0://this to this.getActivity()

                accountManager.createAccount(this, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> futureManager) {
                        // Unless the account creation was cancelled, try logging in again
                        // after the account has been created.
                        if (!futureManager.isCancelled()) {
                            refreshAvailableAccounts();

                            // if we have an user endpoint we try to get userinfo with the receive token
                            if (!TextUtils.isEmpty(userInfoEndpoint)) {
                                new LoginTask().execute(availableAccounts[0]);
                            }
                        }
                    }
                });
                break;

            // There's just one account, let's use that
            case 1:
                // if we have an user endpoint we try to get userinfo with the receive token
                if (!TextUtils.isEmpty(userInfoEndpoint)) {
                    new LoginTask().execute(availableAccounts[0]);
                }
                break;

            // Multiple accounts, let the user pick one
            default:
                String name[] = new String[availableAccounts.length];

                for (int i = 0; i < availableAccounts.length; i++) {
                    name[i] = availableAccounts[i].name;
                }
                //this to this.getActivity
                new AlertDialog.Builder(this)
                        .setTitle("Choose an account")
                        .setAdapter(new ArrayAdapter<>(this,//this to this.getActivity
                                        android.R.layout.simple_list_item_1, name),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedAccount) {
                                        selectedAccountIndex = selectedAccount;

                                        // if we have an user endpoint we try to get userinfo with the receive token
                                        if (!TextUtils.isEmpty(userInfoEndpoint)) {
                                            new LoginTask().execute(availableAccounts[selectedAccountIndex]);
                                        }
                                    }
                                })
                        .create()
                        .show();
        }
    }

    public void doConfEdit(View view) {
        // Never use this on a release. The OpenId Connect client configuration should be stored in
        // a "secure" way (not on user preferences), if possible obfuscated, and not be allow to be edited.
        // Use it on dev or to test your OpenId Provider only.
        //this to this.getActivity
        Intent intent = new Intent(this, OIDCClientConfigurationActivity.class);
        startActivity(intent);
    }

    public void doRequest(View view) {
        new ProtectedResTask().execute(availableAccounts[selectedAccountIndex]);
    }

    public void doLogout(View view) {
        new LogoutTask(false).execute(availableAccounts[selectedAccountIndex]);
    }


    private void showSyncing()
    {

        progressText.setText("Syncing with Microsoft Health....");
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        mssyncButton.setVisibility(View.INVISIBLE);

    }

    private void showSyncing(String message)
    {
        progressText.setText(message);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        mssyncButton.setVisibility(View.INVISIBLE);

    }
    private void hideSyncing()
    {
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
        mssyncButton.setVisibility(View.VISIBLE);

    }

    private Activity getActivity()
    {

        return this;
    }

    @Override
    public void ProcessLog(String logType, String message) {

        SQLiteDatabase sqlDB=null;

        DBHandler handler=null;
        try {


            long unixTime = System.currentTimeMillis() / 1000L;

             handler =  DBHandler.getInstance(this);

             sqlDB = handler.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(DBHandler.COLUMN_LOGTYPE, logType);
            values.put(DBHandler.COLUMN_LOGMESSAGE, message);
            values.put(DBHandler.COLUMN_TIMESTAMP, unixTime);
            sqlDB.insert(DBHandler.TABLE_LOGS, null, values);



            //mCtx.getContentResolver().notifyChange(DBHandler.URI_TABLE_USERS, null);

        } catch (Exception ex) {
            Log.d("Error", ex.getMessage());

        }
        finally {

            if(sqlDB!=null)
                sqlDB.close();

            if(handler!=null)
                handler.close();



        }
    }
    //endregion

    //region Background tasks

    private class LoginTask extends AsyncTask<Account, Void, Map> {

        @Override
        protected void onPreExecute() {


            showSyncing();
        }

        /**
         * Makes the API request. We could use the OIDCRequestManager.getUserInfo() method, but we'll do it
         * like this to illustrate making generic API requests after we've logged in.
         */

        @Override
        protected Map doInBackground(Account... args) {
            Account account = args[0];

            try {
                return APIUtility.getJson(accountManager, userInfoEndpoint, account, null);
            } catch (IOException e) {
                Log.w(TAG, "We couldn't fetch userinfo from server", e);
                handleTokenExpireException(account, e);
            } catch (AuthenticatorException | OperationCanceledException e) {
                Log.w(TAG, "Coudln't get access token from accountmanager", e);
            } catch (UserNotAuthenticatedWrapperException e) {
                //FIXME: we gotta handle this somehow

            }
            catch (TokenDecryptionException e) {
                Log.w(TAG, "Token Decryption error", e);

            }
            catch (Exception e) {
                Log.w(TAG, "Another error occured", e);


            }
            return null;
        }

        /**
         * Processes the API's response.
         */
        @Override
        protected void onPostExecute(Map result) {




            if (result == null) {
                hideSyncing();
                Toast.makeText(getActivity(),
                        "Login failed",
                        Toast.LENGTH_SHORT).show();

                new LogoutTask(false).execute(availableAccounts[selectedAccountIndex]);
            }
            else {
                new ProtectedResTask().execute(availableAccounts[selectedAccountIndex]);

            }
           /* progressBar.setVisibility(View.INVISIBLE);

            if (result == null) {
                loginButton.setText("Couldn't get user info");
            } else {
                //loginButton.setText("Logged in as " + result.get("given_name"));
                loginButton.setText("Logged in as " + result.get("firstName"));
                Log.i(TAG, "We manage to login user to server");


            }
            */
        }

        private void handleTokenExpireException(Account account, IOException e){
            if (e.getMessage().contains("Access Token not valid")) {
                accountManager.invalidateAllAccountTokens(account);
                Log.i(TAG, "User should authenticate one more");
                launchExpiredTokensIntent(account);
            }
        }

        private void launchExpiredTokensIntent(Account account) {
            // See https://github.com/kalemontes/OIDCAndroidLib/issues/4
            try {
                accountManager.getAccessToken(account, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                            if (launch != null) {
                                launch.setFlags(0);
                                //HomeActivity.this.startActivityForResult(launch, RENEW_REFRESH_TOKEN);
                                getActivity().startActivityForResult(launch, RENEW_REFRESH_TOKEN);
                            }
                        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                            Log.e(TAG, "Coudn't extract AuthenticationActivity lauch intent", e);
                        }
                    }
                });
            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                Log.e(TAG, "Couldn't renew tokens", e);
            } catch (UserNotAuthenticatedWrapperException e) {
                //FIXME: we gotta handle this somehow
            }
        }
    }

    private class ProtectedResTask extends AsyncTask<Account, Void, Map> {

        @Override
        protected void onPreExecute() {

            showSyncing();

        }

        /**
         * Makes the API request to an SP.
         */
        @Override
        protected Map doInBackground(Account... args) {
            Account account = args[0];

            try {

                Log.v("test",APIUtility.getJson(accountManager, protectedResUrl, account, null).toString());
                return APIUtility.getJson(accountManager, protectedResUrl, account, null);
            } catch (AuthenticatorException | OperationCanceledException|TokenDecryptionException |IOException e) {
                e.printStackTrace();
            } catch (UserNotAuthenticatedWrapperException e) {
                //FIXME: we gotta handle this somehow
            }
            return null;
        }

        /**
         * Processes the API's response.
         */
        @Override
        protected void onPostExecute(Map result) {


            hideSyncing();

            if (result == null) {

                //TODO: Add warning message
                Toast.makeText(getActivity(),
                        "Syncing failed",
                        Toast.LENGTH_SHORT).show();

            } else {

                //TODO: Add success message

                SyncData(result);



            }
        }
    }


    ///Sync Data
    private void SyncData(Map result)
    {


        RecordingSettings settings=new RecordingSettings(this);
        new SyncHealthDataTask(settings.getPatientID(),settings.getToken()).execute(result);

    }

    private class SyncHealthDataTask extends AsyncTask<Map, Void, Boolean> {
        @Override
        protected void onPreExecute() {

            showSyncing("Sending to PD Manager server...");

        }

        private String accessToken;
        private String patientCode;

        public SyncHealthDataTask(String code, String a) {

            this.patientCode = code;
            this.accessToken = a;
        }

        @Override
        protected Boolean doInBackground(Map... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {


                Map result = clientParams[0];

                MSHealthDataHandler  mhandler = new MSHealthDataHandler(accessToken,patientCode);

                mhandler.handleData(result);



                return true;

            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return false;
                // handle BandException
            }
        }


        protected void onPostExecute(Boolean result) {


            hideSyncing();
            // MSHealthManager manager=new MSHealthManager()
            Toast.makeText(getActivity(),
                    "Syncing succeeded",
                    Toast.LENGTH_SHORT).show();


        }
    }

    private class LogoutTask extends AsyncTask<Account, Void, Boolean> {

        private boolean requestServerLogout;

        public LogoutTask(boolean requestServerLogout){
            this.requestServerLogout = requestServerLogout;
        }

        @Override
        protected void onPreExecute() {


            showSyncing();

        }

        @Override
        protected Boolean doInBackground(Account... args) {
            Account account = args[0];
            return !requestServerLogout || requestServerLogout(account);
        }

        /**
         * Processes the API's response.
         */
        @Override
        protected void onPostExecute(Boolean result) {
            hideSyncing();

            if (result) {
                boolean removed = accountManager.removeAccount(availableAccounts[0]);
                if (removed) {

                    hideSyncing();

                    refreshAvailableAccounts();
                    //HomeActivity.this to getActivity
                    Toast.makeText(getActivity(),
                            "Session closed",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    //TODO: show error message "Couldn't remove account"
                }
            } else {
                //TODO: show error message "Couldn't logout"
            }
        }

        private boolean requestServerLogout(Account account) {
            //TODO: make a request to the OP's revoke endpoint to invalidate the current tokens
            //See https://github.com/kalemontes/OIDCAndroidLib/issues/5 discution
            return false;
        }
    }

}
