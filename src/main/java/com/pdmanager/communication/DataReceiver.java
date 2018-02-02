package com.pdmanager.communication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pdmanager.models.Device;
import com.pdmanager.models.MedicationIntake;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.Observation;
import com.pdmanager.models.ObservationCode;
import com.pdmanager.models.Patient;
import com.pdmanager.models.PatientListResult;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by george on 25/1/2016.
 */
public class DataReceiver {
    private static final String TAG = "DataReceiver";
    private final Gson gson;
    private final String accessToken;

    public DataReceiver(String t) {
        gson = new Gson();
        accessToken = t;

    }

    public List<MedicationIntake> GetMedicationIntakes(String patientId, long datefrom, long dateto) {

        String param = "";
        try {
            param = "/find?take=0&skip=0&filter=" + URLEncoder.encode("{'patientid':'" + patientId + "','datefrom':" + datefrom + ",'dateto':" + dateto + "}", "UTF-8") + "&sort=&sortdir=false&lastmodified=";
        } catch (Exception ex) {


        }

        CommunicationManager manager = new CommunicationManager(accessToken);
        String jsonResponse = manager.Get("MedIntake", param);

        try {
            List<MedicationIntake> patients = gson.fromJson(jsonResponse, new TypeToken<List<MedicationIntake>>() {
            }.getType());
            return patients;
        } catch (Exception ex) {

            return new ArrayList<MedicationIntake>();
        }


    }

    public List<MedicationOrder> GetMedicationOrders(String patientCode) throws Exception {


        CommunicationManager manager = new CommunicationManager(accessToken);
        String param = "";
        try {
            param = "/find?take=0&skip=0&filter=" + URLEncoder.encode("{'patientid':'" + patientCode + "'}", "UTF-8") + "&sort=&sortdir=false&lastmodified=";
        } catch (Exception ex) {

        }
        String jsonResponse = manager.Get("MedicationOrder", param);
        try {
            List<MedicationOrder> meds = gson.fromJson(jsonResponse, new TypeToken<List<MedicationOrder>>() {
            }.getType());

            return meds;
        } catch (Exception e) {

            Log.e("DATARECEIVER", "Get Medication Orders", e.getCause());
            throw e;
        }

        //   MedOrderListResult res=new MedOrderListResult();
        //  res.Data=meds;


    }



    public List<com.pdmanager.models.DssInfo> GetDss(String code,String patientCode) throws Exception {


        CommunicationManager manager = new CommunicationManager(accessToken);
        String param = "";
        try {
            param = "?code="+code+"&patientid=" + patientCode;
        } catch (Exception ex) {

        }
        String jsonResponse = manager.Get("DSS", param);

        try {
            List<com.pdmanager.models.DssInfo> meds = gson.fromJson(jsonResponse, new TypeToken<List<com.pdmanager.models.DssInfo>>() {
            }.getType());

            return meds;
        } catch (Exception e) {

            Log.e("DATARECEIVER", "Get DSS Input", e.getCause());
            throw e;
        }

        //   MedOrderListResult res=new MedOrderListResult();
        //  res.Data=meds;


    }
    public PatientListResult GetPatients() {


        CommunicationManager manager = new CommunicationManager(accessToken);

        String jsonResponse = manager.Get("Patient", "/find?take=0&skip=0&filter=none&sort=none&sortdir=false&lastmodified=");
        List<Patient> patients = gson.fromJson(jsonResponse, new TypeToken<List<Patient>>() {
        }.getType());
        PatientListResult result = new PatientListResult();
        result.Data = patients;
        result.HasError = false;
        return result;


    }

    public List<ObservationCode> GetCodes() {


        CommunicationManager manager = new CommunicationManager(accessToken);

        String jsonResponse = manager.Get("ObservationCode", null);
        List<ObservationCode> patients = gson.fromJson(jsonResponse, new TypeToken<List<ObservationCode>>() {
        }.getType());

        return patients;


    }

    public List<Observation> GetObservationDistribution(String patientId, String code, Date from, Date to) {


        CommunicationManager manager = new CommunicationManager(accessToken);
        String param = "?patientId=" + patientId + "&Type=" + code;


        if (from != null)
            param = param + "&from=" + from.getTime();

        if (to != null)
            param = param + "&to=" + to.getTime();


        param = param + "&aggregate=3";

        String jsonResponse = manager.Get("Observation", param);
        List<Observation> patients = gson.fromJson(jsonResponse, new TypeToken<List<Observation>>() {
        }.getType());

        return patients;


    }


    public List<Device> GetDevices(String patientId) {


        CommunicationManager manager = new CommunicationManager(accessToken);


        String aggrs = "";


        String param = "";
        try {
            param = "/find?take=0&skip=0&filter=" + URLEncoder.encode("{'patientid':'" + patientId + "'}", "UTF-8") + "&sort=&sortdir=false&lastmodified=";
        } catch (Exception ex) {


        }


        try {


            String jsonResponse = manager.Get("Device", param);

            Log.d(TAG, jsonResponse);
            Type t = new TypeToken<List<Device>>() {
            }.getType();
            List<Device> devices = gson.fromJson(jsonResponse, t);

            return devices;
        } catch (Exception e) {

            Log.e(TAG, e.getMessage(), e.getCause());

        }

        return new ArrayList<Device>();


    }


    public List<Observation> GetObservations(String patientId, String code, long datefrom, long dateto, int aggr) {


        CommunicationManager manager = new CommunicationManager(accessToken);


        String aggrs = "";

        if (aggr == 1)
            aggrs = "day";
        else if (aggr == 2)
            aggrs = "time";
        else if (aggr == 3)
            aggrs = "hour";
        else if (aggr == 4)
            aggrs = "total";
        String param = "";
        try {
            param = "/find?take=0&skip=0&filter=" + URLEncoder.encode("{'patientid':'" + patientId + "','codeid':'" + code + "','datefrom':" + datefrom + ",'dateto':" + dateto + ",aggr:'" + aggrs + "'}", "UTF-8") + "&sort=&sortdir=false&lastmodified=";
        } catch (Exception ex) {


        }


        try {


            String jsonResponse = manager.Get("Observation", param);
            List<Observation> observations = gson.fromJson(jsonResponse, new TypeToken<List<Observation>>() {
            }.getType());

            return observations;
        } catch (Exception e) {

        }

        return new ArrayList<Observation>();


    }


   /* public List<Observation> GetCalendar(String id)
    {


        CommunicationManager manager=new CommunicationManager(accessToken);
        long datefrom=0;


        long dateto=0;

       // String param="?take=0&skip=0&filter={'patientid':'"+id+"','codeid':'DATA','datefrom':"+datefrom+",'dateto':"+dateto+"}&sort=&sortdir=false&lastmodified=";

        String param="";
        try {
            param = "?take=0&skip=0&filter=" + URLEncoder.encode("{'patientid':'" + id + "','codeid':'DATA','datefrom':" + datefrom + ",'dateto':" + dateto + "}", "UTF-8") + "&sort=&sortdir=false&lastmodified=";
        }
        catch(Exception ex)
        {


        }

        String jsonResponse=manager.Get("Observation", param);
        List<Observation> observations=(List<Observation>) gson.fromJson(jsonResponse, new TypeToken<List<Observation>>(){}.getType());

        return observations;



    }
    */

    public List<Observation> GetObservations(String patientId, String code, Date from, Date to, int aggr) {


        CommunicationManager manager = new CommunicationManager(accessToken);

        long datefrom = 0;
        if (from != null)
            datefrom = from.getTime();

        long dateto = 0;
        if (to != null)
            dateto = to.getTime();
        String aggrs = "";

        if (aggr == 1)
            aggrs = "day";
        else if (aggr == 2)
            aggrs = "time";
        else if (aggr == 3)
            aggrs = "hour";
        else if (aggr == 4)
            aggrs = "total";
        String param = "";
        try {
            param = "/find?take=0&skip=0&filter=" + URLEncoder.encode("{'patientid':'" + patientId + "','codeid':'" + code + "','datefrom':" + datefrom + ",'dateto':" + dateto + ",aggr:'" + aggrs + "'}", "UTF-8") + "&sort=&sortdir=false&lastmodified=";
        } catch (Exception ex) {


        }


        String jsonResponse = manager.Get("Observation", param);
        List<Observation> observations = gson.fromJson(jsonResponse, new TypeToken<List<Observation>>() {
        }.getType());


        return observations;


    }

    public interface IPatientReceiveCallback {


        void OnReceivePatients(List<Patient> patients);

    }

    public interface IObservationReceiveCallback {


        void OnReceiveObservations(List<Observation> patients);

    }

    public interface IObservationCodeReceiveCallback {


        void OnReceiveObservationCodes(List<ObservationCode> patients);

    }

    /*
  Patient Handler
   */
    private class ObservationCodeDataHandler implements IReceiveCallback {


        private IObservationCodeReceiveCallback mCallBack;

        public ObservationCodeDataHandler(IObservationCodeReceiveCallback callback) {

            mCallBack = callback;


        }

        @Override
        public void OnReceive(String jsonResponse) {


            ///Parse Response
            List<ObservationCode> observations = gson.fromJson(jsonResponse, new TypeToken<List<Patient>>() {
            }.getType());


            if (mCallBack != null)
                mCallBack.OnReceiveObservationCodes(observations);


        }
    }

    /*
      Patient Handler
       */
    private class ObservationDataHandler implements IReceiveCallback {


        private IObservationReceiveCallback mCallBack;

        public ObservationDataHandler(IObservationReceiveCallback callback) {

            mCallBack = callback;


        }

        @Override
        public void OnReceive(String jsonResponse) {


            ///Parse Response
            List<Observation> observations = gson.fromJson(jsonResponse, new TypeToken<List<Patient>>() {
            }.getType());


            if (mCallBack != null)
                mCallBack.OnReceiveObservations(observations);


        }
    }

    /*
Patient Handler
 */
    private class PatientDataHandler implements IReceiveCallback {


        private IPatientReceiveCallback mCallBack;

        public PatientDataHandler(IPatientReceiveCallback callback) {

            mCallBack = callback;


        }

        @Override
        public void OnReceive(String jsonResponse) {


            ///Parse Response
            List<Patient> patients = gson.fromJson(jsonResponse, new TypeToken<List<Patient>>() {
            }.getType());


            if (mCallBack != null)
                mCallBack.OnReceivePatients(patients);


        }
    }

}
