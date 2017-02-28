package com.pdmanager.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.pdmanager.common.data.LocationData;
import com.pdmanager.common.data.LocationReading;
import com.pdmanager.common.interfaces.IDataHandler;

/**
 * Created by george on 27/11/2015.
 */
public class PDLocationListener implements LocationListener {


    private IDataHandler mDataHandler;


    public PDLocationListener(IDataHandler h) {


        mDataHandler = h;


    }


    @Override
    public void onLocationChanged(Location loc) {


        double log = loc.getLongitude();
        double lat = loc.getLatitude();

        LocationData locData = new LocationData();
        locData.setValue(new LocationReading(lat, log));
        locData.setTicks(loc.getTime());

        if (mDataHandler != null)
            mDataHandler.handleData(locData);

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
