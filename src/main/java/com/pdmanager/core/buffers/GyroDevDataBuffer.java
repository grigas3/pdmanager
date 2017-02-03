package com.pdmanager.core.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroMData;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class GyroDevDataBuffer extends DataBuffer<GyroMData> {


    final long snano;
    private final byte[] dbytes = new byte[8];
    private String name;

    public GyroDevDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);
        snano = System.nanoTime();


    }

    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, GyroMData item) throws Exception {


        long a = item.getTimestamp().getTime();

        AccReading r = item.getValue();

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
