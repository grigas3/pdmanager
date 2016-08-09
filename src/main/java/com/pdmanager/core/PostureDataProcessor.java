package com.pdmanager.core;

import android.util.Log;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.PedoData;
import com.pdmanager.common.data.PostureData;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.core.posturedetector.Core.PostureTypes;
import com.pdmanager.core.posturedetector.Core.Signals.PostureEvaluation;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.Posture.PostureDetector;

import java.util.Date;

/**
 * Created by admin on 16/5/2015.
 */

public class PostureDataProcessor extends BaseDataProcessor {

    private static final int MAXBUFFER = 90;
    private final PostureDetector evaluator = new PostureDetector(90, 30);
    private final SignalCollection signalData = new SignalCollection(3, 90);
    private IDataHandler mHandler;
    private int bufferCount;
    private int lastPosture;
    private int bites = 0;
    private int prevbites = 0;
    private int drinking = 0;
    private long distanceTraveled = -1;
    private long prevValue = 0;

    public PostureDataProcessor(IDataHandler handler)
            throws Exception {
        bufferCount = 0;
        lastPosture = -1;
        mHandler = handler;
    }

    public boolean requiresData(int dataType) {
        return dataType == DataTypes.ACCELEROMETER || dataType == DataTypes.PEDOMETER;
    }


    private boolean hasLargeValue(double t) {
        try {
            for (int i = 0; i < signalData.getSize(); i++) {

                double sx = signalData.get___idx(0).get___idx(i);
                double sy = signalData.get___idx(1).get___idx(i);
                double sz = signalData.get___idx(2).get___idx(i);

                double ss = (sx * sx) + sy * sy + sz * sz;

                if (ss > t)
                    return true;

            }


        } catch (Exception ex) {


        }
        return false;

    }

    public void addData(ISensorData data) {


        if (data.getDataType() == DataTypes.PEDOMETER) {

            PedoData pedData = (PedoData) data;


            if (distanceTraveled == -1)
                distanceTraveled = 0;
            else
                distanceTraveled += (pedData.getValue() - prevValue);
            prevValue = pedData.getValue();

        }

        if (data.getDataType() == DataTypes.ACCELEROMETER)
            try {
                AccData accData = (AccData) data;
                AccReading reading = accData.getValue();
                signalData.get___idx(0).set___idx(bufferCount, reading.getY());
                signalData.get___idx(1).set___idx(bufferCount, reading.getX());
                signalData.get___idx(2).set___idx(bufferCount, reading.getZ());
                bufferCount++;
                if (bufferCount == MAXBUFFER) {
                    PostureEvaluation evaluation = evaluator.Process(signalData);
                    int posture = evaluation.getPosture();



            /*        if(posture== PostureTypes.STANDING_WALKING&&lastPosture==PostureTypes.SITTING_LYING)
                    {

                            if(!hasLargeValue(2))
                                posture=PostureTypes.SITTING_LYING;

                    }


*/

                  /*  if(posture==PostureTypes.SITTING_LYING&&lastPosture==PostureTypes.STANDING_WALKING)
                    {

                        if(!hasLargeValue(2))
                            posture=PostureTypes.STANDING_WALKING;

                    }
                    */


                    if (posture == PostureTypes.OTHER)
                        posture = lastPosture;


                    if (distanceTraveled > 0)
                        posture = PostureTypes.WALKING;

                    //    bites=0;
                    drinking = 0;

                    PostureData s = new PostureData();

                    Date d = new Date();
                    s.setTimestamp(d);
                    s.setTicks(d.getTime());
                    s.setValue(posture);
                    if (mHandler != null) {

                        mHandler.handleData(s);

                    }
                    lastPosture = posture;
                    bufferCount = 0;
                    distanceTraveled = 0;
                }
            } catch (Exception ex) {
                Log.d("PostureDataProcessor", ex.getMessage());
            }
    }
}
