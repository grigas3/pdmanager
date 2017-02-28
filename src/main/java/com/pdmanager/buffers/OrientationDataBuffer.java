package com.pdmanager.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.OrientData;
import com.pdmanager.common.data.OrientReading;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class OrientationDataBuffer extends DataBuffer<OrientData> {


    private final byte[] dbytes = new byte[8];
    private String name;


    public OrientationDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }

    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, OrientData item) throws Exception {

        long a = item.getTimestamp().getTime();
        //long b=getStart().getTime();
        //double diff=Math.abs(a-b)/1000.0;
        OrientReading r = item.getValue();

        //  GetBytes(diff);
        OutDataWriter.writeLong(a);


        OutDataWriter.writeLong(GetLong(r.getX()));
        OutDataWriter.writeLong(GetLong(r.getY()));
        OutDataWriter.writeLong(GetLong(r.getZ()));


    }


    @Override
    protected long GetItemSize() {
        return long4Bytes;

    }
}
