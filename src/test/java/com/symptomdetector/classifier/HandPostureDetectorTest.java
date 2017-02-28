package com.symptomdetector.classifier; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.symptomdetector.tremor.HandPostureDetector;


import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class HandPostureDetectorTest {


//
//    @Test
//    public void handPostureDetector_filter() {
//        try{
//            int n = 1000;
//
//            SignalCollection source = ReadTestFiles.ReadMoticon(n, 3,"E:\\Rigas\\PDManager\\TestSignals\\posture_sensor_acc_test1.txt");
//            SignalCollection target_y9 = ReadTestFiles.Read(n,3, "E:\\Rigas\\PDManager\\TestSignals\\test_posture_filter_ls.txt");
//
//
//
//            NamedSignalCollection signals = new NamedSignalCollection(); // TODO: Initialize to an appropriate value
//
//            TremorPreprocess preprocess=new TremorPreprocess();
//            preprocess.process(source,signals);
//
//            SignalCollection y9 = signals.get___idx(SignalDictionary.TremorFiltLowPass);
//
//
//
//            double t1=target_y9.get___idx(0).get___idx(100);
//            double a1=y9.get___idx(0).get___idx(100);
//
//
//            assertTrue(Math.abs(a1-t1) < 0.0001);
//
//
//
//        }
//        catch(Exception ex)
//        {
//
//            assert false;
//        }
//    }

   /* @Test
    public void FindPeakTest() {

        try{
            int n=128*10;
            int i;
            HandPostureDetector detector=new HandPostureDetector(0.5F, 0.5F, 1, 128);

            double[] x=CreateSignalD(n,128);
            for(i=0;i<n;i++)
            detector.addDataTest(x[i],i);






            assertTrue(detector.getNumOfPeaks()> 1);

        }
        catch(Exception ex)
        {

            assert false;

        }

    }
*/

    private class TestPDataHandler implements IDataHandler
    {

        private int numOfPostures=0;
        @Override
        public void handleData(ISensorData data) {
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
    public void FindPeakTestRS() {

        try{
            int n=3500;
            int i;

            TestPDataHandler handler=new TestPDataHandler();
            SignalCollection target_y9 = ReadTestFiles.Read(n,3, "E:\\Rigas\\PDManager\\TestSignals\\test_posture_filter_ls.txt");
            HandPostureDetector detector=new HandPostureDetector(handler,0.003F, 0.005F, 20, 62.5);

            for(i=0;i<1000;i++)
                detector.addDataTest(0,i);
            for(i=0;i<n;i++)
                detector.addDataTest(target_y9.get___idx(0).get___idx(i),i);






            assertTrue(handler.getNumOfPostures()==3);

        }
        catch(Exception ex)
        {

            assert false;

        }

    }

    private double[] CreateSignalD(int n,int FS) throws Exception
    {




        double[] x = new double[n];


        for (int i = 0; i < n; i++)
            x[i] = (5 * Math.cos(2.0 * Math.PI * i / (n - 1) * FS));

        return x;

    }

}
