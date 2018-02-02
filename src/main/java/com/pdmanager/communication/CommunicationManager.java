package com.pdmanager.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.pdmanager.common.Util;
import com.pdmanager.interfaces.IJsonRequestHandler;
import com.pdmanager.models.PDEntity;

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
        codemap.put("UsageStatistic", "http://pdmanager.3dnetmedical.com/api/usagestatistics");
        codemap.put("Alert", "http://pdmanager.3dnetmedical.com/api/logs");
        codemap.put("MedicationIntake", "http://pdmanager.3dnetmedical.com/api/medicationadministrations");
        codemap.put("Device", "http://pdmanager.3dnetmedical.com/api/devices");
        codemap.put("DSS", "http://195.130.121.79:8086/api/dss");

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
        codemap.put("DSS", "http://195.130.121.79/PD/api/v1/dsseval");

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

                mHandler.addRequest(new JsonStorage(json, uri, "PUT"));

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

                mHandler.addRequest(new JsonStorage(json, uri));

            }


        } catch (Exception ex) {
            Log.e("Comm Manager", ex.getMessage());
        }


    }

    public <T extends PDEntity> void SendItems(ArrayList<T> items) {


        try {


            if (items.size() > 0) {


                new SendItemsTask<T>(mHandler).execute(items);


            }


        } catch (Exception ex) {
            Log.e("Comm Manager", ex.getMessage());
        }


    }

    public <T extends PDEntity> void SendItems(ArrayList<T> items, boolean writeFile) {

        if (writeFile) {

            new WriteItemsTask<T>(mHandler).execute(items);
        }
        SendItems(items);


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
        Log.d("COMMMManager",uri);

        String response = client.Get(uri);


        Log.d("COMMMManager",response);


        return response;


    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class SendItemsTask<T extends PDEntity> extends AsyncTask<ArrayList<T>, Void, Boolean> {


        private IJsonRequestHandler tHandler;

        SendItemsTask(IJsonRequestHandler phandler) {

            this.tHandler = phandler;
        }


        @Override
        protected Boolean doInBackground(ArrayList<T>... clientParams) {

            ArrayList<T> items = null;
            try {
                items = clientParams[0];
                // if (items.size() > 0) {

                T item = items.get(0);
                String uri = getUri("POST", item.getPDType());
                String json = JsonSerializationHelper.toJson(items);
                if (tHandler != null) {

                    tHandler.addRequest(new JsonStorage(json, uri));

                }


            } catch (Exception ex) {

                Util.handleException("Send Items Async", ex);

                return false;
                // handle BandException
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {


            //TODO: Probably do something
        }
    }

    ////Connect Task
    /// The connect task asynchronously tries to connect to Microsoft Band
    private class WriteItemsTask<T extends PDEntity> extends AsyncTask<ArrayList<T>, Void, Boolean> {


        private IJsonRequestHandler tHandler;

        WriteItemsTask(IJsonRequestHandler phandler) {

            this.tHandler = phandler;
        }


        @Override
        protected Boolean doInBackground(ArrayList<T>... clientParams) {

            ArrayList<T> items = null;
            try {
                items = clientParams[0];
                // if (items.size() > 0) {


                T item = items.get(0);

                JsonSerializationHelper.toJsonFile(items, item.getPDType());


            } catch (Exception ex) {

                Util.handleException("Write Items Async", ex);

                return false;
                // handle BandException
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {


            //TODO: Probably do something
        }
    }


}
