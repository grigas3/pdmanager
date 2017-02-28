package com.pdmanager.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroData;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class GyroDataBuffer extends DataBuffer<GyroData> {

    private final byte[] dbytes = new byte[8];

    private String name;

    public GyroDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);


    }

    private void GetBytes(double d) {
        long lng = Double.doubleToLongBits(d);
        for (int i = 0; i < 8; i++) dbytes[i] = (byte) ((lng >> ((7 - i) * 8)) & 0xff);


    }

    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, GyroData item) throws Exception {

        long a = item.getTicks();

        AccReading r = item.getValue();
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
