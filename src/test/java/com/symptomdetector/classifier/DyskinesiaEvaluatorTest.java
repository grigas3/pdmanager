package com.symptomdetector.classifier; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */



import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.LIDData;
import com.pdmanager.common.interfaces.ISensorDataHandler;
import com.pdmanager.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.symptomdetector.dyskinesia.DyskinesiaEvaluator;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;


public class DyskinesiaEvaluatorTest {



    private class TestPDataHandler implements ISensorDataHandler
    {

        private int numOfPostures=0;
        @Override
        public void handleData(ISensorData data) {

            LIDData d=(LIDData)data;
            if(d.getValue().getUPDRS()>0)
            numOfPostures++;
        }

        public int getNumOfPostures()
        {
            return numOfPostures;

        }
    }

    /***
     * Test with real posture signal
     */
    @Test
    public void dyskinesia_normal_test() {

        try{
            int n=10*5*60*63;
            int i;

            TestPDataHandler handler=new TestPDataHandler();
            SignalCollection source = ReadTestFiles.ReadMoticon(n,3, "E:\\Data\\PDManager\\PHASEI\\PDmanager_Data_T4_1_IRCCS_SantaLucia\\LR\\UOI\\CTRL02\\SESSION01\\sensor_acc.txt");
            DyskinesiaEvaluator evaluator=new DyskinesiaEvaluator(null,"TEST",handler,62.5);

            for(i=0;i<n;i++)
            {

                AccData accData = new AccData();
                accData.setValue(new AccReading((float)source.get___idx(0).get___idx(i),(float) source.get___idx(1).get___idx(i), (float)source.get___idx(2).get___idx(i)));
                accData.setTimestamp(new Date());
                accData.setTicks(i);
                evaluator.addData(accData);

            }





            assertTrue(handler.getNumOfPostures()>0);

        }
        catch(Exception ex)
        {

            assert false;

        }

    }
    @Test
    public void dyskinesia_lid_test() {

        try{
            int n=10*5*60*63;
            int i;

            TestPDataHandler handler=new TestPDataHandler();
            SignalCollection source = ReadTestFiles.ReadMoticon(n,3, "E:\\Data\\PDManager\\PHASEI\\PDmanager_Data_T4_1_IRCCS_SantaLucia\\LR\\IRCSS\\PAT011\\SESSION01\\ON\\sensor_acc.txt");
            DyskinesiaEvaluator evaluator=new DyskinesiaEvaluator(null,"TEST",handler,62.5);

            for(i=0;i<n;i++)
            {

                AccData accData = new AccData();
                accData.setValue(new AccReading((float)source.get___idx(0).get___idx(i),(float) source.get___idx(1).get___idx(i), (float)source.get___idx(2).get___idx(i)));
                accData.setTimestamp(new Date());
                accData.setTicks(i);
                evaluator.addData(accData);

            }





            assertTrue(handler.getNumOfPostures()>0);

        }
        catch(Exception ex)
        {

            assert false;

        }

    }

}
