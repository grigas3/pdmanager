package com.symptomdetector.classifier; /**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */


import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.GyroData;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.TremorData;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary;
import com.pdmanager.core.symptomdetector.tremor.TremorAccPreprocess;
import com.pdmanager.core.symptomdetector.tremor.TremorDetector;
import com.pdmanager.core.symptomdetector.tremor.TremorEstimator;
import com.pdmanager.core.symptomdetector.tremor.TremorEvaluator;
import com.pdmanager.core.symptomdetector.tremor.TremorGyroPreprocess;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertTrue;


public class TremorDetectorTest {


    @Test
    public void tremorSignalPreprocess_test() {


        int n = 187;
        int i=0;
        try {
            SignalCollection acc = ReadTestFiles.ReadMoticon(n, 3, "E:\\Rigas\\PDManager\\TestSignals\\sensor_acc_tremor_test1.txt");
            SignalCollection gyro = ReadTestFiles.ReadMoticon(n, 3, "E:\\Rigas\\PDManager\\TestSignals\\sensor_gyro_tremor_test1.txt");

            TremorAccPreprocess p1 = new TremorAccPreprocess();
            TremorGyroPreprocess p2 = new TremorGyroPreprocess();
            NamedSignalCollection s=new NamedSignalCollection();
            for(i=0;i<gyro.getSignals();i++)
            {
                acc.get___idx(i).multScalar(-1);
                gyro.get___idx(i).multScalar(1/(2*Math.PI));
            }

            p1.process(acc,s);
            p2.process(gyro,s);

            SignalCollection gyroLowPass=s.get___idx(SignalDictionary.TremorGyroLowPass);
            SignalCollection gyroHighPass=s.get___idx(SignalDictionary.TremorGyroHighPass);
            SignalCollection accLowPass=s.get___idx(SignalDictionary.TremorAccLowPass);



            assertTrue(Math.abs(gyroLowPass.get___idx(0).get___idx(49)-(-1.7358))<0.001);
            assertTrue(Math.abs(gyroHighPass.get___idx(0).get___idx(41)-(2.4064))<0.001);
            assertTrue(Math.abs(gyroHighPass.get___idx(0).get___idx(100)-(29.8628))<0.001);
            assertTrue(Math.abs(gyroHighPass.get___idx(0).get___idx(150)-(-1.3026))<0.001);
            assertTrue(Math.abs(accLowPass.get___idx(0).get___idx(100)-(-0.5958))<0.01);


        }
        catch (Exception ex)
        {

        }

    }



    @Test
    public void tremorFeatureExtraction_test() {


        int n = 187;
        int i=0;
        try {
            SignalCollection acc = ReadTestFiles.ReadMoticon(n, 3, "E:\\Rigas\\PDManager\\TestSignals\\sensor_acc_tremor_test1.txt");
            SignalCollection gyro = ReadTestFiles.ReadMoticon(n, 3, "E:\\Rigas\\PDManager\\TestSignals\\sensor_gyro_tremor_test1.txt");

            TremorAccPreprocess p1 = new TremorAccPreprocess();
            TremorGyroPreprocess p2 = new TremorGyroPreprocess();
            NamedSignalCollection s=new NamedSignalCollection();

            for(i=0;i<gyro.getSignals();i++)
            {

                gyro.get___idx(i).multScalar(1/(2*Math.PI));
                acc.get___idx(i).multScalar(-1);
            }
            p1.process(acc,s);
            p2.process(gyro,s);

            TremorDetector d=new TremorDetector();



            double f1=0.6939;
            double f2=-0.4824;
            double f3=145.3622;
            double gs1=0.0265;
            double a2=19.08;
            double af1=d.testA3(s,0,n);
            double af2=d.testA19(s,0,n);
            double af3=d.testAE(s,0, n);
            double ags1=d.testGS1(s,0,n);
            double aa2=d.testA2(s,0,n);

            assertTrue(100*Math.abs((af1-f1)/f1)<5);
            assertTrue(100*Math.abs((af2-(f2))/f2)<5);
            assertTrue(100*Math.abs((af3-f3)/f3)<5);
            assertTrue(100*Math.abs((ags1-gs1)/gs1)<5);
            assertTrue(100*Math.abs((aa2-a2)/a2)<5);

        }
        catch (Exception ex)
        {

        }

    }

    @Test
    public void tremorDetector_test() {
        try{
            int n = 50*187;
        int i=0;
            SignalCollection acc = ReadTestFiles.ReadMoticon(n, 3,"E:\\Rigas\\PDManager\\TestSignals\\sensor_acc_tremor_test1.txt");
            SignalCollection gyro = ReadTestFiles.ReadMoticon(n, 3,"E:\\Rigas\\PDManager\\TestSignals\\sensor_gyro_tremor_test1.txt");


            TestTDataHandler t=new TestTDataHandler();
            TremorEvaluator tremor=new TremorEvaluator(t,62.5);
            for(i=0;i<n;i++)
            {
                AccData accData = new AccData();
                accData.setValue(new AccReading((float)acc.get___idx(0).get___idx(i),(float) acc.get___idx(1).get___idx(i),(float) acc.get___idx(2).get___idx(i)));
                accData.setTimestamp(new Date());
                accData.setTicks((int)(i*62.5));




                GyroData gyroData = new GyroData();
                gyroData.setValue(new AccReading((float)gyro.get___idx(0).get___idx(i),(float) gyro.get___idx(1).get___idx(i),(float) gyro.get___idx(2).get___idx(i)));
                gyroData.setTimestamp(new Date());
                gyroData.setTicks((int)(i*62.5));


                tremor.addData(accData);
                tremor.addData(gyroData);
            }





            assertTrue(Math.abs(t.getAverageAmp()-0.5939)<0.1);



        }
        catch(Exception ex)
        {

            assert false;
        }
    }
    @Test
    public void updrs_test() {


        try {
            TremorEstimator t = new TremorEstimator(3 * 187, 62.5);
            double f1 = 0.5000;
            double f2 = 2.0000;
            double f3 = 5.0000;
            double f4 = 8.0000;
            double f5 = 10.0000;
            double f6 = 20.0000;
            assertTrue(Math.abs(t.testUPDRS(f1) - 1) < 0.1);
            assertTrue(Math.abs(t.testUPDRS(f2) - 2) < 0.1);
            assertTrue(Math.abs(t.testUPDRS(f3) - 3) < 0.1);
            assertTrue(Math.abs(t.testUPDRS(f4) - 3) < 0.1);
            assertTrue(Math.abs(t.testUPDRS(f5) - 3.5) < 0.1);
            assertTrue(Math.abs(t.testUPDRS(f6) - 4) < 0.1);
        }
        catch (Exception ex)
        {

        }

    }

    @Test
    public void notremorDetector_test() {
        try{
            int n = 5000;
            int i=0;
            //SignalCollection acc = ReadTestFiles.ReadMoticon(n, 3,"E:\\Rigas\\PDManager\\TestSignals\\sensor_acc_no_tremor.txt");
            //SignalCollection gyro = ReadTestFiles.ReadMoticon(n, 3,"E:\\Rigas\\PDManager\\TestSignals\\sensor_gyro_no_tremor.txt");


            SignalCollection acc = ReadTestFiles.ReadMoticon(n, 3,"E:\\Data\\PDManager\\PHASEI\\Tests\\LID01\\sensor_acc.txt");
            SignalCollection gyro = ReadTestFiles.ReadMoticon(n, 3,"E:\\Data\\PDManager\\PHASEI\\Tests\\LID01\\sensor_gyro.txt");



            for(i=0;i<gyro.getSignals();i++)
            {

                gyro.get___idx(i).multScalar(1/(2*Math.PI));
            }
            TestTDataHandler t=new TestTDataHandler();
            TremorEvaluator tremor=new TremorEvaluator(t,62.5);
            for(i=0;i<n;i++)
            {
                AccData accData = new AccData();
                accData.setValue(new AccReading((float)acc.get___idx(0).get___idx(i),(float) acc.get___idx(1).get___idx(i),(float) acc.get___idx(2).get___idx(i)));
                accData.setTimestamp(new Date());
                accData.setTicks((int)(i*62.5));




                GyroData gyroData = new GyroData();
                gyroData.setValue(new AccReading((float)gyro.get___idx(0).get___idx(i),(float) gyro.get___idx(1).get___idx(i),(float) gyro.get___idx(2).get___idx(i)));
                gyroData.setTimestamp(new Date());
                gyroData.setTicks((int)(i*62.5));


                tremor.addData(accData);
                tremor.addData(gyroData);
            }





            assertTrue(t.getNumOfTremor()==25);



        }
        catch(Exception ex)
        {

            assert false;
        }
    }
    private class TestTDataHandler implements IDataHandler
    {


        private float amp=0;
        private int numOfTremor=0;
        @Override
        public void handleData(ISensorData data) {


            TremorData d=(TremorData)data;

            amp=amp+d.getValue().getUPDRS();
            numOfTremor++;
        }
        public float getAverageAmp()
        {
            return amp/numOfTremor;

        }
        public int getNumOfTremor()
        {
            return numOfTremor;

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

