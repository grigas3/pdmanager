package com.pdmanager.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;

import java.util.UUID;

/**
 * A class containing the recording settings
 * i.e. which devices are enabled and if we should display data
 * Created by george on 5/8/2015.
 */
public class RecordingSettings {


    public static final String PREFERENCES = "PDManagerHomePreferences";


    private Context mContext;
    private boolean mST = true;
    private boolean mbandEnabled = true;
    private boolean mdevEnabled = true;
    private boolean mLocationEnabled = true;
    private boolean mLoggedIn = false;
    private int mSensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
    private int mStartHour = 8;
    private int mStopHour = 20;
    private long mRecordingStart = 0;
    private long mlastNFG = 0;
    private String mtoken;
    private String mPatientID = "PAT01";
    private boolean mSessionRunning = false;
    private String mReminder1 = null;
    private String mReminder2 = null;
    //minutes
    private int mStopInterval = 60;
    private int mAcquisitionInterval = 30;
    private String muserName;
    private String mPassword;
    private String mDeviceId;
    private long mExpirationTick;

    private boolean mDevTokenSended = false;
    private boolean mRecordFiles = true;
    private boolean mUseDetectors = false;
    //   private boolean mbandaccEnabled=true;
    //  private boolean mbandgyroEnabled=true;
    //  private boolean mheartRateEnabled=true;

    //
    //  private boolean mBandDistanceEnabled=true;
    private String mSessionFolder = "";
    private String mEvent1;
    private String mEvent2;
    private String mRole;
    private int mCognHour1;
    private int mCognHour2;
    private int mMoodHour;



    public RecordingSettings() {


    }



    public static RecordingSettings newInstance(Context context)
    {

        return new RecordingSettings(context);

    }

    public RecordingSettings(Context context) {

        mContext = context;

        if (mContext != null) {

            try {

                SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES, 0);
                mbandEnabled = pref.getBoolean("Band", mbandEnabled);
                mLoggedIn = pref.getBoolean("LoggedIn", mLoggedIn);
                //    mheartRateEnabled = pref.getBoolean("HeartRate", mheartRateEnabled);
                mLocationEnabled = pref.getBoolean("Location", mLocationEnabled);
                //   mBandDistanceEnabled = pref.getBoolean("BandDistance", mBandDistanceEnabled);
                //    mbandaccEnabled = pref.getBoolean("BandAcc", mbandaccEnabled);
                mST = pref.getBoolean("Temperature", mST);
                //   mbandgyroEnabled = pref.getBoolean("BandGyro", mbandgyroEnabled);
                mdevEnabled = pref.getBoolean("Device", mdevEnabled);
                mStartHour = pref.getInt("StartHour", mStartHour);
                mRecordFiles = pref.getBoolean("RecordFiles", mRecordFiles);
                mUseDetectors = pref.getBoolean("UseDetectors", mUseDetectors);
                mDevTokenSended = pref.getBoolean("DevTokenSended", mDevTokenSended);

                mLang = pref.getString("Lang", mLang);

                mStopHour = pref.getInt("StopHour", mStopHour);

                mCognHour1 = pref.getInt("CognHour1", mCognHour1);
                mCognHour2 = pref.getInt("CognHour2", mCognHour2);
                mMoodHour = pref.getInt("MoodHour", mMoodHour);

                mSensorDelay = pref.getInt("SensorDelay", mSensorDelay);
                mSessionRunning = pref.getBoolean("SessionRunning", mSessionRunning);
                mRecordingStart = pref.getLong("RecordingStart", mRecordingStart);
                mSessionFolder = pref.getString("SessionFolder", mSessionFolder);
                mPatientID = pref.getString("PatientID", mPatientID);
                mReminder1 = pref.getString("Reminder1", mReminder1);
                mReminder2 = pref.getString("Reminder2", mReminder2);

                mReminder2 = pref.getString("Reminder2", mReminder2);

                mTileUUID = pref.getString("TileUUID", mTileUUID);


                muserName = pref.getString("Username", muserName);
                mPassword = pref.getString("Password", mPassword);
                mExpirationTick = pref.getLong("Expiration", mExpirationTick);
                mlastNFG = pref.getLong("LastNFG", mlastNFG);
                mEvent1 = pref.getString("Event1", mEvent1);
                mEvent2 = pref.getString("Event2", mEvent2);
                mtoken = pref.getString("access-token", mtoken);

                mDeviceId = pref.getString("DeviceId", mDeviceId);
                mRole = pref.getString("Role", mRole);


            } catch (Exception ex) {

            }
        }


    }

    public static RecordingSettings GetRecordingSettings(Context context) {


        return new RecordingSettings(context);

    }

    private void SetPref(String name, boolean value) {

        if (mContext != null) {
            SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(name, value);
            editor.commit();
        }

    }

    private void SetPref(String name, int value) {

        if (mContext != null) {
            SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(name, value);
            editor.commit();
        }

    }

    private void SetPref(String name, long value) {

        if (mContext != null) {
            SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putLong(name, value);
            editor.commit();
        }

    }

    private void SetPref(String name, String value) {

        if (mContext != null) {
            SharedPreferences pref = mContext.getSharedPreferences(PREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(name, value);
            editor.commit();
        }

    }

    public boolean isBandEnabled() {
        return mbandEnabled;
    }

    public void setBandEnabled(boolean mbandEnabled) {
        this.mbandEnabled = mbandEnabled;
        SetPref("Band", mbandEnabled);
    }

    public boolean isDevEnabled() {
        return mdevEnabled;
    }

    public void setDevEnabled(boolean mdevEnabled) {
        this.mdevEnabled = mdevEnabled;

        SetPref("Device", mdevEnabled);
    }

    /*
        public boolean isBandaccEnabled() {
            return mbandaccEnabled;
        }

        public void setBandaccEnabled(boolean mbandaccEnabled) {
            this.mbandaccEnabled = mbandaccEnabled;

            SetPref("BandAcc",mbandaccEnabled);

        }

        public boolean isBandgyroEnabled() {
            return mbandgyroEnabled;
        }

        public void setBandgyroEnabled(boolean pbandgyroEnabled) {
            this.mbandgyroEnabled = pbandgyroEnabled;

            SetPref("BandGyro",mbandgyroEnabled);
        }

        public boolean isheartRateEnabled() {
            return mheartRateEnabled;
        }

        public void setheartRateEnabled(boolean mheartRateEnabled) {
            this.mheartRateEnabled = mheartRateEnabled;
            SetPref("HeartRate",mheartRateEnabled);
        }

        public boolean isBandDistanceEnabled() {
            return mBandDistanceEnabled;
        }

        public void setBandDistanceEnabled(boolean pBandDistanceEnabled) {
            this.mBandDistanceEnabled = pBandDistanceEnabled;

            SetPref("BandDistance",mBandDistanceEnabled);
        }





    */
    public boolean isLocationEnabled() {
        return mLocationEnabled;
    }

    public void setLocationEnabled(boolean pLocationEnabled) {
        this.mLocationEnabled = pLocationEnabled;

        SetPref("Location", mLocationEnabled);
    }

    public boolean isSTEnabled() {
        return mST;
    }

    public void setSTEnabled(boolean pST) {
        this.mST = pST;

        SetPref("Temperature", mST);
    }

    public int getStartHour() {
        return mStartHour;
    }

    public void setStartHour(int h) {
        mStartHour = h;
        SetPref("StartHour", h);
    }

    public int getStopHour() {
        return mStopHour;
    }

    public void setStopHour(int h) {
        mStopHour = h;
        SetPref("StopHour", h);
    }

    public int getSensorDelay() {
        return mSensorDelay;
    }

    public void setSensorDelay(int h) {
        mSensorDelay = h;
        SetPref("SensorDelay", h);
    }

    public boolean getSessionRunning() {
        return mSessionRunning;
    }

    public void setSessionRunning(boolean flag) {


        mSessionRunning = flag;
        SetPref("SessionRunning", flag);
    }

    public long getRecordingStart() {
        return mRecordingStart;

    }

    public void setRecordingStart(long h) {
        mRecordingStart = h;
        SetPref("RecordingStart", h);
    }

    public String getToken() {
        return mtoken;

    }

    public void setToken(String h) {
        mtoken = h;
        SetPref("access-token", h);
    }

    public String getUserName() {
        return muserName;

    }

    public void setUserName(String h) {
        muserName = h;
        SetPref("Username", h);
    }

    public String getPassword() {
        return mPassword;

    }

    public void setPassword(String h) {
        mPassword = h;
        SetPref("Password", h);
    }


    private String mLang="en";
    public String getLang() {
        return mLang;

    }

    public void setLang(String h) {
        mLang = h;
        SetPref("Lang", h);
    }




    public long getExpiration() {
        return mExpirationTick;

    }


    public long getLastNFG() {
        return mlastNFG;

    }

    public void setLastNFG(long nfg) {
         mlastNFG=nfg;
        SetPref("LastNFG", nfg);
    }

   /* public String getOrganization() {


        return mDropboxFolder;
    }


    public void setOrganization(String token) {
        this.mDropboxFolder = token;
        SetPref("DropboxFolder",token);
    }
    */

    public void setExpiration(long h) {
        mExpirationTick = h;
        SetPref("Expiration", h);
    }

    public String getSessionFolder() {


        return mSessionFolder;
    }

    public void setSessionFolder(String token) {
        this.mSessionFolder = token;
        SetPref("SessionFolder", token);
    }

    public String getPatientID() {


        return mPatientID;
    }

    public void setPatientID(String token) {
        this.mPatientID = token;
        SetPref("PatientID", token);
    }

    public String getReminder1() {


        return mReminder1;
    }

    public void setReminder1(String token) {
        this.mReminder1 = token;
        SetPref("Reminder1", token);
    }

    public String getReminder2() {


        return mReminder2;
    }

    public void setReminder2(String token) {
        this.mReminder2 = token;
        SetPref("Reminder2", token);
    }

    public String getEvent1() {


        return mEvent1;
    }

    public void setEvent1(String token) {
        this.mEvent1 = token;
        SetPref("Event1", token);
    }

    public String getEvent2() {


        return mEvent2;
    }

    public void setEvent2(String token) {
        this.mEvent2 = token;
        SetPref("Event2", token);
    }

    public boolean getLoggedIn() {


        return mLoggedIn;
    }

    public void setLoggedIn(boolean token) {
        this.mLoggedIn = token;
        SetPref("LoggedIn", token);
    }

    public String getRole() {


        return mRole;
    }

    public void setRole(String token) {
        this.mRole = token;
        SetPref("Role", token);
    }


    public boolean getRecordFiles() {


        return mRecordFiles;
    }

    public void setRecordFiles(boolean token) {
        this.mRecordFiles = token;
        SetPref("RecordFiles", token);
    }


    public boolean getUseDetectors() {


        return mUseDetectors;
    }

    public void setUseDetectors(boolean token) {
        this.mUseDetectors = token;
        SetPref("UseDetectors", token);
    }


    public boolean getDevTokenSended() {


        return mDevTokenSended;
    }

    public void setDevTokenSended(boolean token) {
        this.mDevTokenSended = token;
        SetPref("DevTokenSended", token);
    }

    public String getDeviceId() {


        return mDeviceId;
    }

    public void setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
        SetPref("DeviceId", deviceId);
    }

    String mTileUUID;
    public String getTileUUID() {



        return mTileUUID;
    }

    public void setTileUUID(UUID deviceId) {
        this.mTileUUID = deviceId.toString();

        SetPref("TileUUID", deviceId.toString());
    }
    public int getCognHour1() {
        return this.mCognHour1;
    }
    public void setCognHour1(int cognHour1) {
        this.mCognHour1 = cognHour1;
    }

    public int getCognHour2() {
        return this.mCognHour2;
    }
    public void setCognHour2(int cognHour2) {
        this.mCognHour2 = cognHour2;
    }



    public int getMoodHour() {
        return this.mMoodHour;
    }
    public void setMoodHour(int moodHour) {
        this.mMoodHour = moodHour;
    }
}