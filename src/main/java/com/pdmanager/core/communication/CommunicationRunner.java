package com.pdmanager.core.communication;

import android.util.Log;

import com.pdmanager.core.interfaces.INetworkStatusHandler;
import com.pdmanager.core.interfaces.ITokenUpdater;
import com.pdmanager.core.models.LoginResult;
import com.pdmanager.core.models.PingEntity;

/**
 * Created by george on 11/1/2016.
 */
public class CommunicationRunner implements Runnable {


    private boolean queueRunning = false;

    private RESTClient mRClient;
    private CommunicationQueue mqueue;
    private INetworkStatusHandler mNetworkStatusHandler;
    private String jsonModel;
    private ITokenUpdater tokenUpdater;

    public CommunicationRunner(CommunicationQueue queue, INetworkStatusHandler networkStatusHandler, String pid, ITokenUpdater tu) {


        mNetworkStatusHandler = networkStatusHandler;
        mqueue = queue;
        this.tokenUpdater = tu;

        PingEntity ent = new PingEntity(pid);
        jsonModel = JsonSerializationHelper.toJson(ent);
        mRClient = new RESTClient(tokenUpdater.getAccessToken());

    }


    public void RegisterHandler(String identifier, IReceiveCallback callback) {


    }

    public void UnRegisterHandler(String identifier, IReceiveCallback callback) {


    }

    private boolean IsActiveCallBack(String identifier) {
        return true;

    }

    public void setQueueRunning(boolean q) {

        queueRunning = q;

    }

    private String getPingUri() {

        return "http://195.130.121.79/PD/api/Ping";

    }

    private void SendPing() {


        if (mNetworkStatusHandler != null) {
            Log.i("SENDMESSAGE", "PING");
            mRClient.Post(getPingUri(), jsonModel);
        }

        //
    }

    @Override
    public void run() {

        JsonStorage request = null;
        boolean executeNext = true;
        boolean loggedIn = true;
        while (queueRunning) {

            try {

                if (mNetworkStatusHandler.IsNetworkConnected()) {


                    if (executeNext)
                        request = mqueue.peek();


                    if (request != null) {

                        boolean res = true;
                        if (tokenUpdater.hasTokenExpired()) {

                            LoginResult lres = mRClient.Login(tokenUpdater.getLoginToken());


                            loggedIn = lres.success;

                            if (loggedIn) {

                                tokenUpdater.updateToken(lres.access_token, lres.expires_in);

                                mRClient = new RESTClient(tokenUpdater.getAccessToken());
                            }

                        }


                        if (loggedIn) {
                            res = mRClient.Post(request.getUri(), request.getJson());


                            if (res) {

                                Log.i("SENDMESSAGE", request.getUri());
                                mqueue.remove();
                                executeNext = true;
                            } else {
                                Log.e("SENDMESSAGE", "An error occured");
                                executeNext = false;
                            }


                        }

                        //Sort sleep if a an entry found
                        Thread.sleep(100);
                    } else {

                        //SendPing();
                        executeNext = true;
                        ///Longer sleep if quueu is empty
                        for (int i = 0; i < 60 && queueRunning; i++)
                            Thread.sleep(1000);
                    }

                } else {


                    ///Just Send a Ping


                    ///Very long for
                    for (int i = 0; i < 5 * 60 && queueRunning; i++)
                        Thread.sleep(1000);


                }


            } catch (Exception ex) {

                Log.e("Queue Thread", "An error occured");
            }

        }
        Log.e("WARN", "Queue Stopped");
    }
}
