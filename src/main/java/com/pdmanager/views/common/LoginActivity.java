package com.pdmanager.views.common;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pdmanager.R;
import com.pdmanager.communication.RESTClient;
import com.pdmanager.models.LoginModel;
import com.pdmanager.models.LoginResult;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.clinician.ClinicianActivity;
import com.pdmanager.views.patient.TechnicianActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {
    private static final String TAG = "LOGIN";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CONTACTS = 2;
    private static final int REQUEST_RECORD = 3;
    private static final int REQUEST_PHONE_STATE = 4;
    private static final int REQUEST_VIDEO = 5;
    private static final int REQUEST_LOCATION = 6;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "doctor:123456:doctor",
            "patient:123456:patient"
    };
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,


    };
    private static String[] PERMISSIONS_CONTACTS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,


    };
    private static String[] PERMISSIONS_RECORD = {
            Manifest.permission.RECORD_AUDIO


    };
    private static String[] PERMISSIONS_PHONE = {
            Manifest.permission.READ_PHONE_STATE


    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,


    };
    private static String[] PERMISSIONS_VIDEO = {
            Manifest.permission.CAMERA,
            Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT,
            Manifest.permission.CAPTURE_VIDEO_OUTPUT
    };
    public boolean hasWriteExternalStoragePermission = false;
    public boolean hasWriteContactsPermission = false;
    public boolean hasReadPhoneStatePermissions = false;
    public boolean hasRecordAudioPermission = false;
    public boolean hasCameraPermission = false;
    public boolean hasAccessFineLocation = false;
    public boolean reqPermissions = false;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (requirePermissions(this)) {

            setContentView(R.layout.activity_permissions);

        } else {

            setContentView(R.layout.activity_login);

            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);

            redirectIfLogged();
            //}

           /* //added for firebase
            Button logTokenButton = (Button) findViewById(R.id.logTokenButton);
            logTokenButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get token
                    String token = FirebaseInstanceId.getInstance().getToken();

                    // Log and toast
                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d("Login Activity", msg);
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });

           */

        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        //Before proceed with login require permissions
        //TODOCheck if username and password are lost
        requirePermissions(this);

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        */


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        grantResults = new int[7];

        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                // permission was granted, yay! Do the
// contacts-related task you need to do.
// permission denied, boo! Disable the
// functionality that depends on this permission.
                hasWriteExternalStoragePermission = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                return;
            }
            case REQUEST_CONTACTS: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    hasWriteContactsPermission = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    hasWriteContactsPermission = false;

                    Toast.makeText(LoginActivity.this, "Permission denied to request contacts", Toast.LENGTH_SHORT).show();

                }
                return;
            }
            case REQUEST_RECORD: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    hasRecordAudioPermission = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(LoginActivity.this, "Permission denied to request record", Toast.LENGTH_SHORT).show();
                    hasRecordAudioPermission = false;

                }
                return;
            }
            case REQUEST_PHONE_STATE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    hasReadPhoneStatePermissions = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    hasReadPhoneStatePermissions = false;

                    Toast.makeText(LoginActivity.this, "Permission denied to request phone state", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_VIDEO: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    hasCameraPermission = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    hasCameraPermission = false;

                    Toast.makeText(LoginActivity.this, "Permission denied to request video", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    hasAccessFineLocation = true;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    hasAccessFineLocation = false;
                    Toast.makeText(LoginActivity.this, "Permission denied to request location", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        if (hasWriteExternalStoragePermission
                && hasWriteContactsPermission
                && hasReadPhoneStatePermissions
                && hasRecordAudioPermission
                && hasCameraPermission
                && hasAccessFineLocation) {

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            requirePermissions(this);
        }
    }

    private void redirectIfLogged() {

        RecordingSettings settings = getSettings();
        if (settings != null) {

            if (settings.getLoggedIn()) {
                String role = settings.getRole();
                long currentTime = Calendar.getInstance().getTimeInMillis();


                if (currentTime > settings.getExpiration()) {
                    RESTClient client = new RESTClient();
                    LoginModel model = new LoginModel(settings.getUserName(), settings.getPassword());
                    LoginResult lres = client.Login(model);


                    boolean loggedIn = lres.success;

                    if (loggedIn) {

                        settings.setToken(lres.access_token);
                        settings.setExpiration(Calendar.getInstance().getTimeInMillis() + lres.expires_in * 1000);

                    } else
                        return;

                }

                if (role != null) {
                    if (role.toLowerCase().equals("patients")) {
                        Intent mainIntent = new Intent(LoginActivity.this, TechnicianActivity.class);
                        LoginActivity.this.startActivity(mainIntent);


                    } else {
                        Intent mainIntent = new Intent(LoginActivity.this, ClinicianActivity.class);
                        LoginActivity.this.startActivity(mainIntent);


                    }

                }
            }
        }


    }

    protected RecordingSettings getSettings() {

        return new RecordingSettings((this));
    }

    private boolean checkPermissions(Activity activity) {

        boolean requiresPermissions = false;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            requiresPermissions = true;

        }

        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            requiresPermissions = true;
        }

        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permission3 != PackageManager.PERMISSION_GRANTED) {
            requiresPermissions = true;
        }

        return requiresPermissions;
    }

    @Override
    public void onResume() {
        super.onResume();

        requirePermissions(this);

    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public boolean requirePermissions(Activity activity) {

        if (Build.VERSION.SDK_INT >= 23) {
            // Check if we have write permission

            reqPermissions = false;

            try {
                int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permission != PackageManager.PERMISSION_GRANTED) {

                    reqPermissions = true;
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                }

                int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS);

                if (permission2 != PackageManager.PERMISSION_GRANTED) {
                    reqPermissions = true;
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_CONTACTS,
                            REQUEST_CONTACTS
                    );
                }

                int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

                if (permission3 != PackageManager.PERMISSION_GRANTED) {
                    reqPermissions = true;
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_PHONE,
                            REQUEST_PHONE_STATE
                    );
                }

                int permission4 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);

                if (permission4 != PackageManager.PERMISSION_GRANTED) {
                    reqPermissions = true;
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_RECORD,
                            REQUEST_RECORD
                    );
                }

                int permission5 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
                if (permission5 != PackageManager.PERMISSION_GRANTED) {
                    reqPermissions = true;
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_VIDEO,
                            REQUEST_VIDEO
                    );
                }

                int permission6 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

                if (permission6 != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_LOCATION,
                            REQUEST_LOCATION
                    );
                }


          /*  if (!reqPermissions&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&!Settings.canDrawOverlays(this)) {
                reqPermissions=true;
                Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(permissionIntent);
            }
            */
            } catch (Exception e) {

                Log.e(TAG, e.getMessage());

            }
        } else {
            reqPermissions = true;
        }

        return reqPermissions;
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private class LoginSucces {

        public boolean success;
        public boolean patient;

        public String error;
        public String access_token;
        public int expires_in;
        public String id;
        public String role;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, LoginSucces> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected LoginSucces doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            LoginSucces ret = new LoginSucces();
            ret.success = false;

            RESTClient client = new RESTClient();
            LoginModel model = new LoginModel(mEmail, mPassword);
            LoginResult res = client.Login(model);

            ret.error = "";

            if (res.success) {
                ret.success = true;/*
                if (res.role != null) {
                    ret.patient = (res.role.toLowerCase().equals("patients"));


                    if(!ret.patient) {
                        //ONLY FOR PILOT
                        ret.success = false;
                        ret.error = "User is not patient";
                    }

                }
                else
                {
                    ret.success=false;
                    ret.error="User has not a patient role";


                }*/
                ret.access_token = res.access_token;
                ret.expires_in = res.expires_in;
                ret.id = res.rolemapid;


                ret.role = res.role;

                Log.d(TAG, "Logged in as " + res.role);
                //client.GetUserRole("http://pdmanager.3dnetmedical.com/userusers?take=10&skip=0&filter={'name':'"+mEmail+"'}&sort=&sortdir=false&lastmodified=");

            } else {

                ret.error = "Password incorrect or no internet connection";
            }


            // TODO: register the new account here.
            return ret;
        }


        @Override
        protected void onPostExecute(final LoginSucces ret) {
            mAuthTask = null;
            showProgress(false);
            if (ret != null) {
                if (ret.success) {
                    RecordingSettings settings = getSettings();

                    settings.setToken(ret.access_token);

                    settings.setLoggedIn(true);
                    settings.setExpiration(Calendar.getInstance().getTimeInMillis() + ret.expires_in * 1000);
                    settings.setUserName(mEmail);
                    settings.setPassword(mPassword);
                    settings.setRole(ret.role);
                    settings.setUserID(ret.id);
                    if (ret.patient) {

                        settings.setPatientID(ret.id);
                        Intent mainIntent = new Intent(LoginActivity.this, TechnicianActivity.class);
                        LoginActivity.this.startActivity(mainIntent);
                    } else {
                        Intent mainIntent = new Intent(LoginActivity.this, ClinicianActivity.class);
                        LoginActivity.this.startActivity(mainIntent);

                    }

                    finish();
                } else {
                    mPasswordView.setError(ret.error);
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

