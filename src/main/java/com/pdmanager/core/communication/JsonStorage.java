package com.pdmanager.core.communication;

/**
 * Created by george on 6/1/2016.
 */
public class JsonStorage {


    private final String method;
    private String mJson;
    private String mUri;
    private int mid;

    public JsonStorage(String json, String uri) {


        this.mJson = json;
        this.mUri = uri;
        this.method = "POST";


    }
    public JsonStorage(int id,String json, String uri) {


        this.mid=id;
        this.mJson = json;
        this.mUri = uri;
        this.method = "POST";


    }
    public JsonStorage(String json, String uri, String m) {


        this.mJson = json;
        this.mUri = uri;
        this.method = m;


    }



    public int getId(){ return mid;}
    public String getMethod() {
        return this.method;

    }

    public String getJson() {
        return mJson;
    }

    public void setJson(String mJson) {
        this.mJson = mJson;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }
}

