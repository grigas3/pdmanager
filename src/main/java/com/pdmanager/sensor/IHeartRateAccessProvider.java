package com.pdmanager.sensor;

import com.microsoft.band.sensors.BandSensorManager;

/**
 * Created by George on 5/22/2015.
 */
public interface IHeartRateAccessProvider {

    void requestHeartRateConsent(BandSensorManager sensorManager);

}
