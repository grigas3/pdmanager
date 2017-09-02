package com.pdmanager.dataloggers;

import android.content.Context;
import android.location.LocationManager;

import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.interfaces.IDataLogger;
import com.pdmanager.location.PDLocationListener;
import com.pdmanager.models.LoggerStat;

import java.util.ArrayList;
import java.util.Collection;

import static com.pdmanager.logging.LogCodes.FATAL_GPS_EXCEPTION;
import static com.pdmanager.logging.LogCodes.FATAL_GPS_SECURITY;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

/**
 * Location Data Logger
 */
public class LocationDataLogger extends BaseDataLogger implements IDataLogger {
    private static final String TAG = "LocationDataLogger";
    // The minimum distance to change Updates in meters
    private static final long MIN_UPDATE_DISTANCE = 10;
    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 1000 * 5;
    private PDLocationListener locListener = null;

    public LocationDataLogger(ISensorDataHandler pHandler, Context pContext) {

        super(pHandler, pContext);

    }


    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void start() {
        registerLocationListener();
    }

    @Override
    public void pause() {
        unRegisterLocationListener();
    }

    @Override
    public void stop() {
        unRegisterLocationListener();
    }

    @Override
    public void resume() {
        registerLocationListener();
    }

    @Override
    public Collection<LoggerStat> getUsageStats() {
        return new ArrayList<LoggerStat>();
    }

    @Override
    public void resetStats() {

    }

    /**
     * Register Location Listener
     */
    private void registerLocationListener() {


        if (locListener == null)
            locListener = new PDLocationListener(getSensorHandler());

        try {
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, locListener);
            LogInfo("GPS listener enabled");

        } catch (SecurityException ex) {

            LogFatal("Cannot Register GPS listener for Security reasons", FATAL_GPS_SECURITY);
        } catch (Exception ex) {

            LogFatal("Cannot Register GPS listener", FATAL_GPS_EXCEPTION);
        }


    }


    /**
     * Un Register location Listener
     */
    private void unRegisterLocationListener() {
        if (locListener != null) {

            try {
                LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

                locationManager.removeUpdates(locListener);

                locListener = null;

            } catch (SecurityException ex) {

                LogError("Cannot Remove GPS Listener");
            } catch (Exception ex) {

                LogError("Cannot Remove GPS Listener", ex);
            }

        }

    }
}
