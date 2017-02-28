package com.pdmanager.adapters;

import android.os.AsyncTask;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.common.Util;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.interfaces.IObservationDataHandler;
import com.pdmanager.models.Observation;
import com.pdmanager.models.ObservationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by George on 6/19/2016.
 */
public class FetchObservationTask extends AsyncTask<ObservationParams, Void, ObservationResult> {

    private final String accessToken;
    IObservationDataHandler handler;

    public FetchObservationTask(IObservationDataHandler phandler, String a) {
        accessToken = a;

        handler = phandler;
    }

    @Override
    protected ObservationResult doInBackground(ObservationParams... clientParams) {

        BandPendingResult<ConnectionState> pendingResult = null;
        try {
            DataReceiver receiver = new DataReceiver(accessToken);
            ObservationParams params = clientParams[0];
            ObservationResult res = new ObservationResult();
            res.observations = new ArrayList<Observation>();
            if (params.hasManyCodes) {

                for (String code : params.obsCodes) {

                    List<Observation> tmp = receiver.GetObservations(params.patientCode, code, params.from, params.to, params.aggregate);

                    if (tmp != null) {
                        //This is fix since aggregation does not return code or value
                        for (Observation t : tmp) {
                            t.setCode(code);
                            t.setPatientId(params.patientCode);
                        }
                    }
                    res.observations.addAll(tmp);
                }


            } else {
                res.observations = receiver.GetObservations(params.patientCode, params.obsCode, params.from, params.to, params.aggregate);

            }
            res.params = params;

            return res;

        } catch (Exception ex) {

            Util.handleException("Getting data", ex);
            return null;
            // handle BandException
        }
    }

    protected void onPostExecute(ObservationResult result) {


        if (handler != null)
            handler.onObservationReceived(result);
        //new GetMedicationsTask(getAccessToken()).execute(result.params);


    }
}
