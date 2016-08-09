package com.pdmanager.core.communication;

import com.pdmanager.core.interfaces.IJsonRequestHandler;

/**
 * Created by george on 15/6/2016.
 */
public class DirectSender implements IJsonRequestHandler {


    private final String accessToken;

    public DirectSender(String a) {
        accessToken = a;


    }

    @Override
    public void AddRequest(JsonStorage request) {


        RESTClient client = new RESTClient(accessToken);

        if (request != null) {

            if (request.getMethod().equals("POST")) {
                boolean res = true;
                res = client.Post(request.getUri(), request.getJson());

            } else if (request.getMethod().equals("PUT")) {
                boolean res = true;
                res = client.Put(request.getUri(), request.getJson());
            }


        }

    }
}
