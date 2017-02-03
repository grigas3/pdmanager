package com.pdmanager.core.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.STData;
import com.pdmanager.common.data.STReading;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class STDataBuffer extends DataBuffer<STData> {


    private String name;

    public STDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }


    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, STData item) throws Exception {

        long a = item.getTicks();

        STReading r = item.getValue();

        OutDataWriter.writeLong(a);


        OutDataWriter.writeLong(GetLong(r.getST()));


    }


    @Override
    protected long GetItemSize() {
        return long2Bytes;

    }
}
