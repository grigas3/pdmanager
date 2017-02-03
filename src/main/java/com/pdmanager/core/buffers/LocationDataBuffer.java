package com.pdmanager.core.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.LocationData;
import com.pdmanager.common.data.LocationReading;

import java.io.DataOutputStream;

/**
 * Created by george on 27/11/2015.
 */
public class LocationDataBuffer extends DataBuffer<LocationData> {


    private String name;

    public LocationDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }


    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, LocationData item) throws Exception {

        long a = item.getTicks();

        LocationReading r = item.getValue();


        OutDataWriter.writeLong(a);


        OutDataWriter.writeLong(GetLong(r.getLatitude()));
        OutDataWriter.writeLong(GetLong(r.getLognitude()));


    }


    @Override
    protected long GetItemSize() {
        return long3Bytes;

    }
}
