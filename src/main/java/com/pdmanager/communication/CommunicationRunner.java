package com.pdmanager.communication;

import android.util.Log;

import com.pdmanager.interfaces.INetworkStatusHandler;
import com.pdmanager.interfaces.ITokenUpdater;
import com.pdmanager.models.LoginResult;
import com.pdmanager.models.PingEntity;

import java.util.ArrayList;

/**
 * Created by george on 11/1/2016.
 */
public class CommunicationRunner implements Runnable {


    private boolean queueRunning = false;

    private RESTClient mRClient;
    private ICommunicationQueue mqueue;
    private INetworkStatusHandler mNetworkStatusHandler;
    private String jsonModel;
    private ITokenUpdater tokenUpdater;

    public CommunicationRunner(ICommunicationQueue queue, INetworkStatusHandler networkStatusHandler, String pid, ITokenUpdater tu) {


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

    private void runWithQueue()
    {

        JsonStorage request = null;
        boolean executeNext = true;
        boolean loggedIn = true;
        while (queueRunning) {

            try {

                if (mNetworkStatusHandler.IsNetworkConnected()) {


                    if (executeNext)
                        request = mqueue.poll();


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
                                //  mqueue.remove();
                                executeNext = true;
                            } else {
                                Log.e("SENDMESSAGE", "An error occured");
                                executeNext = false;

                                mqueue.push(request);
                            }


                        }

                        //Sort sleep if a an entry found then sleep for 5 second before sending the next item
                        //For production this may be less
                        Thread.sleep(5000);
                    } else {

                        //SendPing();
                        executeNext = true;
                        ///Longer sleep if quueu is empty
                        for (int i = 0; i < 10*60 && queueRunning; i++)
                            Thread.sleep(1000);
                    }

                } else {


                    ///Just Send a Ping


                    ///Very long sleep if no internet connections
                    for (int i = 0; i <  5*60 && queueRunning; i++)
                        Thread.sleep(1000);


                }


            } catch (Exception ex) {

                Log.e("Queue Thread", "An error occured");



            }

        }
        Log.e("WARN", "Queue Stopped");


    }

    private void runWithList()
    {
        ArrayList<JsonStorage> request = new ArrayList<>();
        boolean executeNext = true;
        boolean loggedIn = true;
        int n=5;
        while (queueRunning) {

            try {

                if (mNetworkStatusHandler.IsNetworkConnected()) {


                    if (executeNext)
                        request.addAll(mqueue.getLastN(n));


                    if (request.size()>0) {

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


                            for(JsonStorage req:request) {

                                res = mRClient.Post(req.getUri(), req.getJson());

                                mqueue.delete(Integer.toString(req.getId()));


                                if (res) {

                                    Log.i("SENDMESSAGE", req.getUri());
                                    //  mqueue.remove();
                                    executeNext = true;
                                } else {
                                    Log.e("SENDMESSAGE", "An error occured");
                                    executeNext = false;

                                    //mqueue.push(request);
                                }

                            }






                        }

                        //Clear requests
                        request.clear();

                        //Sort sleep if a an entry found then sleep for 5 second before sending the next item
                        //For production this may be less
                        Thread.sleep(5000);
                    } else {

                        //SendPing();
                        executeNext = true;
                        ///Longer sleep if quueu is empty
                        for (int i = 0; i < 10*60 && queueRunning; i++)
                            Thread.sleep(1000);
                    }

                } else {


                    ///Just Send a Ping


                    ///Very long sleep if no internet connections
                    for (int i = 0; i <  5*60 && queueRunning; i++)
                        Thread.sleep(1000);


                }


            } catch (Exception ex) {

                Log.e("Queue Thread", "An error occured");



            }

        }
        Log.e("WARN", "Queue Stopped");
    }
    @Override
    public void run() {



        runWithList();
    }



}
