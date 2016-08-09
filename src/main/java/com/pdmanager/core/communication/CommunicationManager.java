package com.pdmanager.core.communication;

import android.util.Log;

import com.pdmanager.core.interfaces.IJsonRequestHandler;
import com.pdmanager.core.models.PDEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by george on 6/1/2016.
 */
public class CommunicationManager {


    private final String accessToken;
    Map<String, String> codemap = new HashMap<String, String>();
    //  private JsonStorageAdapter mJAdapter=null;
    private IJsonRequestHandler mHandler = null;
    private int countOfflineJsons = 0;

    public CommunicationManager() {


        accessToken = null;
        initB3DCodeMap();
    }

    public CommunicationManager(String a) {


        accessToken = a;
        initB3DCodeMap();
    }


    public CommunicationManager(IJsonRequestHandler handler) {


        //  mRClient=new RESTClient();
        mHandler = handler;
        accessToken = null;
        initB3DCodeMap();

    }

    public CommunicationManager(IJsonRequestHandler handler, String a) {


        //  mRClient=new RESTClient();
        mHandler = handler;
        accessToken = a;

        initB3DCodeMap();

    }

    private void initB3DCodeMap() {

        codemap.put("Patient", "http://pdmanager.3dnetmedical.com/api/patients");
        codemap.put("MedIntake", "http://pdmanager.3dnetmedical.com/api/medicationadministrations");
        codemap.put("MedicationOrder", "http://pdmanager.3dnetmedical.com/api/medicationorders");
        codemap.put("PatientCalendar", "http://pdmanager.3dnetmedical.com/api/observations");
        codemap.put("Observation", "http://pdmanager.3dnetmedical.com/api/observations");

        //MedicationIntake
        codemap.put("MedicationIntake", "http://pdmanager.3dnetmedical.com/api/medicationadministrations");
    }

    private void initCodeMap() {

        codemap.put("ObservationCode", "http://195.130.121.79/PD/api/ObservationCode");
        codemap.put("Patient", "http://pdmanager.3dnetmedical.com/api/Patient");
        codemap.put("MedIntake", "http://195.130.121.79/PD/api/MedicationIntake");
        codemap.put("MedicationOrder", "http://195.130.121.79/PD/api/MedicationPlan");
        codemap.put("PatientCalendar", "http://195.130.121.79/PD/api/PatientCalendar");
        codemap.put("Observation", "http://195.130.121.79/PD/api/Observation");
        codemap.put("Log", "http://195.130.121.79/PD/api/Log");
        codemap.put("Alert", "http://195.130.121.79/PD/api/Alert");
        codemap.put("UsageStatistic", "http://195.130.121.79/PD/api/UsageStatistic");


    }

    public void setContext(IJsonRequestHandler handler) {


        mHandler = handler;
        //   mJAdapter=new JsonStorageAdapter(c);
    }

    private String getUri(String method, String code) {

        String baseUri = codemap.get(code);


        return baseUri;

    }

    public void SendQueue() {


    }

    public <T extends PDEntity> void UpdateItem(T item) {


        try {


            String uri = getUri("PUT", item.getPDType());
            uri = uri + "/" + item.Id;
            String json = JsonSerializationHelper.toJson(item);
            if (mHandler != null) {

                mHandler.AddRequest(new JsonStorage(json, uri, "PUT"));

            }


        } catch (Exception ex) {
            Log.e("Comm Manager", ex.getMessage());
        }


    }

    public <T extends PDEntity> void SendItem(T item) {


        try {


            String uri = getUri("POST", item.getPDType());
            String json = JsonSerializationHelper.toJson(item);
            if (mHandler != null) {

                mHandler.AddRequest(new JsonStorage(json, uri));

            }


        } catch (Exception ex) {
            Log.e("Comm Manager", ex.getMessage());
        }


    }

    public <T extends PDEntity> void SendItems(ArrayList<T> items) {


        try {
            if (items.size() > 0) {


                T item = items.get(0);
                String uri = getUri("POST", item.getPDType());
                String json = JsonSerializationHelper.toJson(items);
                if (mHandler != null) {

                    mHandler.AddRequest(new JsonStorage(json, uri));

                }

            }


        } catch (Exception ex) {
            Log.e("Comm Manager", ex.getMessage());
        }


    }


    public void Get(String itemCode, String params, IReceiveCallback callback) {

        RESTClient client = new RESTClient(accessToken);


        String uri = codemap.get(itemCode);
        if (params != null)
            uri = uri + params;//"UsageStatistic", "http://195.130.121.79/PD/api/UsageStatistic");


        String response = client.Get(uri);


        if (callback != null)
            callback.OnReceive(response);


    }

    public String Get(String itemCode, String params) {

        RESTClient client = new RESTClient(accessToken);


        String uri = codemap.get(itemCode);
        if (params != null)
            uri = uri + params;//"UsageStatistic", "http://195.130.121.79/PD/api/UsageStatistic");


        String response = client.Get(uri);


        return response;


    }


}
