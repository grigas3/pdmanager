package com.pdmanager.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.models.Observation;

import java.util.ArrayList;

/**
 * Created by george on 31/1/2017.
 */

public class DirectSenderTask extends AsyncTask<ArrayList<Observation>, Void, Boolean> {

    private final String accessToken;
    private final IDirectSendCallback callback;

    public DirectSenderTask(String a, IDirectSendCallback c) {


        this.callback=c;
        this.accessToken = a;
    }

    @Override
    protected Boolean doInBackground(ArrayList<Observation>... clientParams) {

        BandPendingResult<ConnectionState> pendingResult = null;
        try {






            DirectSender sender = new DirectSender(accessToken);
            CommunicationManager mCommManager = new CommunicationManager(sender);

            ArrayList<Observation> obsC = clientParams[0];
            mCommManager.SendItems(obsC,true);
            return true;

        } catch (Exception ex) {


            Log.e("DIRECTSENDER",ex.getMessage(),ex.getCause());
            //Util.handleException("Getting data", ex);
            return false;
            // handle BandException
        }
    }




    protected void onPostExecute(Boolean result) {


        if(callback!=null) {

            callback.onPostDirectSend(result);
        }

    }
}