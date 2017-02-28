package com.pdmanager.interfaces;

import com.pdmanager.models.ObservationResult;

/**
 * Created by George on 6/19/2016.
 */
public interface IObservationDataHandler {

    void onObservationReceived(ObservationResult obs);

}
