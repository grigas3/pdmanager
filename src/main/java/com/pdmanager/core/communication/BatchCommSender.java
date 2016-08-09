package com.pdmanager.core.communication;

import android.os.AsyncTask;

import com.pdmanager.core.interfaces.INetworkStatusHandler;

/**
 * Created by george on 27/1/2016.
 */
public class BatchCommSender extends AsyncTask<CommunicationQueue, Void, Void> {


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
    protected Void doInBackground(CommunicationQueue... params) {


        CommunicationQueue mqueue = params[0];
        boolean executeNext = false;
        JsonStorage request = null;

        RESTClient mRClient = new RESTClient();

        do {

            executeNext = false;
            if (mNetworkStatusHandler.IsNetworkConnected()) {

                if (executeNext)
                    request = mqueue.peek();


                if (request != null) {

                    boolean res = true;


                    try {
                        res = mRClient.Post(request.getUri(), request.getJson());


                        if (res) {
                            mqueue.remove();
                            executeNext = true;
                        } else {

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

