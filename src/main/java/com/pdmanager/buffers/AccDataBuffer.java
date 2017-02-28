package com.pdmanager.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class AccDataBuffer extends DataBuffer<AccData> {


    private String name;


    public AccDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }


    @Override
    protected long GetItemSize() {
        return long4Bytes;

    }

    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, AccData item) throws Exception {

        long a = item.getTicks();
        AccReading r = item.getValue();
        OutDataWriter.writeLong(a);
        OutDataWriter.writeLong(GetLong(r.getX()));
        OutDataWriter.writeLong(GetLong(r.getY()));
        OutDataWriter.writeLong(GetLong(r.getZ()));


    }

}
