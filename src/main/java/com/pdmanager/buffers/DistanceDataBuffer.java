package com.pdmanager.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.PedoData;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class DistanceDataBuffer extends DataBuffer<PedoData> {


    private String name;

    public DistanceDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }


    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, PedoData item) throws Exception {

        long a = item.getTicks();

        long r = item.getValue();


        OutDataWriter.writeLong(a);


        OutDataWriter.writeLong(r);


    }


    @Override
    protected long GetItemSize() {
        return long2Bytes;

    }
}
