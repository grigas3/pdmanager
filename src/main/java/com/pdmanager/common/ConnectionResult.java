package com.pdmanager.common;

import com.microsoft.band.BandClient;
import com.microsoft.band.ConnectionState;

/**
 * Created by george on 22/1/2017.
 */

///Connection Result used by Connect Task
public class ConnectionResult {


    private BandClient mClient;
    private ConnectionState mState;
    private Exception ex;


    public ConnectionResult(ConnectionState pState) {

        mState = pState;
        this.ex = null;

    }

    public ConnectionResult(Exception ex) {

        mState = null;
        this.ex = ex;

    }

    public BandClient getClient() {
        return mClient;
    }

    public void setClient(BandClient client) {
        mClient = client;
    }

    public ConnectionState getState() {
        return mState;
    }

    public Exception getException() {
        return ex;
    }

    public boolean hasException() {
        return ex != null;
    }
}
