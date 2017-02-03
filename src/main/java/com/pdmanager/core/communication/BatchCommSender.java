package com.pdmanager.core.communication;

import android.os.AsyncTask;

import com.pdmanager.core.interfaces.INetworkStatusHandler;

/**
 * Created by george on 27/1/2016.
 */
public class BatchCommSender extends AsyncTask<ICommunicationQueue, Void, Void> {


    private INetworkStatusHandler mNetworkStatusHandler;
    private IBatchQueueSendCallback mcallback;


    public BatchCommSender(INetworkStatusHandler sender, IBatchQueueSendCallback callback) {

        mNetworkStatusHandler = sender;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (mcallback != null)
            mcallback.onComplete();

    }


    @Override
    protected Void doInBackground(ICommunicationQueue... params) {


        ICommunicationQueue mqueue = params[0];
        boolean executeNext = false;
        JsonStorage request = null;

        RESTClient mRClient = new RESTClient();

        do {

            executeNext = false;
            if (mNetworkStatusHandler.IsNetworkConnected()) {

                if (executeNext)
                    request = mqueue.poll();


                if (request != null) {

                    boolean res = true;


                    try {
                        res = mRClient.Post(request.getUri(), request.getJson());


                        if (res) {

                            executeNext = true;
                        } else {
                            mqueue.push(request);
                            executeNext = false;
                        }


                        //Sort sleep if a an entry found
                        Thread.sleep(100);
                    } catch (Exception ex) {
                        executeNext = false;

                    }

                }
            }
        } while (executeNext);


        return null;
    }

}

