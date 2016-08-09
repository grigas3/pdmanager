package com.pdmanager.core.notification;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.SmsManager;


/**
 * Created by george on 30/11/2015.
 */
public class SMSNotificationTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;
    private Exception mException;
    private Callback mCallback;
    private String mPhone;

    public SMSNotificationTask(Context context, String phone, Callback callback) {
        mContext = context;
        mPhone = phone;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete();
        }
    }

    @Override
    protected Void doInBackground(String... params) {


        String message = params[0];
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mPhone, null, message, null, null);

        } catch (Exception e) {

            e.printStackTrace();
            mException = e;


        }


        return null;
    }


    public interface Callback {
        void onUploadComplete();

        void onError(Exception e);
    }


}