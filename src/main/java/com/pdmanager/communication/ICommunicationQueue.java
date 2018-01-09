package com.pdmanager.communication;

import java.util.ArrayList;

/**
 * Created by george on 5/1/2017.
 */

public interface ICommunicationQueue {
    JsonStorage poll();

    boolean push(JsonStorage s);

    boolean delete(String id);

    void close();

    ArrayList<JsonStorage> getLastN(int n);

}
