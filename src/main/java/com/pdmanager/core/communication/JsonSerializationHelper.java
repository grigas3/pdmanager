package com.pdmanager.core.communication;

import com.google.gson.Gson;
import com.pdmanager.core.models.PDEntity;

import java.util.ArrayList;

/**
 * Created by george on 6/1/2016.
 */
public class JsonSerializationHelper {


    static <T extends PDEntity> String toJson(T item) {

        Gson gson = new Gson();
        String json = gson.toJson(item);
        return json.toString();


    }

    static <T extends PDEntity> String toJson(ArrayList<T> items) {

        Gson gson = new Gson();
        String json = gson.toJson(items);
        return json.toString();


    }
}
