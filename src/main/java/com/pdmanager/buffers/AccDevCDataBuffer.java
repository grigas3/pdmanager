package com.pdmanager.buffers;

import com.pdmanager.common.DataBuffer;
import com.pdmanager.common.data.AccMCData;
import com.pdmanager.common.data.AccReading;

import java.io.DataOutputStream;

/**
 * Created by admin on 2/7/2015.
 */
public class AccDevCDataBuffer extends DataBuffer<AccMCData> {


    private static long NANOSECONDS_TO_MILLISECONDS = 1000000;
    final long snano;
    private final byte[] dbytes = new byte[8];
    private String name;

    public AccDevCDataBuffer(int mBuffer, String pfolder, String pfileName) {


        super(mBuffer, pfolder, pfileName);
        snano = System.nanoTime();


    }

    private void GetBytes(double d) {
        long lng = Double.doubleToLongBits(d);
        for (int i = 0; i < 8; i++) dbytes[i] = (byte) ((lng >> ((7 - i) * 8)) & 0xff);


    }

    @Override
    protected void WriteItem(DataOutputStream OutDataWriter, AccMCData item) throws Exception {

        //long a=(item.getTicks()-snano)/NANOSECONDS_TO_MILLISECONDS;

        long a = item.getTimestamp().getTime();
        //  long b=getStart().getTime();
        //  double diff=Math.abs(a-b)/1000.0;
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
