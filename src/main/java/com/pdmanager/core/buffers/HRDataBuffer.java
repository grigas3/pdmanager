package com.pdmanager.core.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.HRData;
import com.pdmanager.common.data.HRReading;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class HRDataBuffer extends DataBuffer<HRData> {


    private final byte[] dbytes = new byte[8];
    private String name;

    public HRDataBuffer(long mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }


    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, HRData item) throws Exception {

        long a = item.getTicks();

        HRReading r = item.getValue();


        OutDataWriter.writeLong(a);


        OutDataWriter.writeLong((long) (r.getHR() * 1000 + r.getQuality()));


    }


    @Override
    protected long GetItemSize() {
        return long2Bytes;

    }
}
