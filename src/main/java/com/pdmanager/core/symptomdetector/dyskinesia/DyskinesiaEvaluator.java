package com.pdmanager.core.symptomdetector.dyskinesia;

import android.util.Log;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.LIDData;
import com.pdmanager.common.data.TremorData;
import com.pdmanager.common.data.UPDRS;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.core.communication.CommunicationManager;
import com.pdmanager.core.posturedetector.SignalProcessing.Filters.FIRD;
import com.pdmanager.core.symptomdetector.aggregators.BaseAggregator;
import com.pdmanager.core.symptomdetector.tremor.PeakData;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */
public class DyskinesiaEvaluator extends BaseAggregator implements IDataProcessor {

    private final Double[] filtLowPass0 = new Double[]{0.00686175413922559, -0.00210812809815127, -0.00140995049739125, -0.000581184068209493, 0.000347539278974443, 0.00134503392921250, 0.00235780322117217, 0.00331370037475211, 0.00411456794751656, 0.00465764956855528, 0.00484687292894300, 0.00461999462380983, 0.00396208528220915, 0.00292539653844375, 0.00162389787886795, 0.000233728190695391, -0.00104345151308655, -0.00199591632852114, -0.00245180995355913, -0.00231776973263679, -0.00155746440350502, -0.000319731936354579, 0.00121838315265730, 0.00276202713434801, 0.00396839548555625, 0.00451448814308644, 0.00415248722143433, 0.00275381676696610, 0.000340299093674319, -0.00289868588359506, -0.00661514598355649, -0.0103458673805168, -0.0135783842903753, -0.0158330931507694, -0.0167497031526762, -0.0161555615681348, -0.0141219911406636, -0.0109719946719315, -0.00725619788992864, -0.00369756992875365, -0.00106909003624467, -0.000109681343295953, -0.00136497256875669, -0.00509142899476702, -0.0111812610247046, -0.0191220631314753, -0.0280259419233949, -0.0367094463299778, -0.0438310564352571, -0.0480571855595708, -0.0482387029660367, -0.0435858114691874, -0.0338040722074129, -0.0191751582210433, -0.000567952937862047, 0.0206303446731526, 0.0426420133554704, 0.0634887345876356, 0.0812145481437397, 0.0941021745086694, 0.100885494334602, 0.100885494334602, 0.0941021745086694, 0.0812145481437397, 0.0634887345876356, 0.0426420133554704, 0.0206303446731526, -0.000567952937862047, -0.0191751582210433, -0.0338040722074129, -0.0435858114691874, -0.0482387029660367, -0.0480571855595708, -0.0438310564352571, -0.0367094463299778, -0.0280259419233949, -0.0191220631314753, -0.0111812610247046, -0.00509142899476702, -0.00136497256875669, -0.000109681343295953, -0.00106909003624467, -0.00369756992875365, -0.00725619788992864, -0.0109719946719315, -0.0141219911406636, -0.0161555615681348, -0.0167497031526762, -0.0158330931507694, -0.0135783842903753, -0.0103458673805168, -0.00661514598355649, -0.00289868588359506, 0.000340299093674319, 0.00275381676696610, 0.00415248722143433, 0.00451448814308644, 0.00396839548555625, 0.00276202713434801, 0.00121838315265730, -0.000319731936354579, -0.00155746440350502, -0.00231776973263679, -0.00245180995355913, -0.00199591632852114, -0.00104345151308655, 0.000233728190695391, 0.00162389787886795, 0.00292539653844375, 0.00396208528220915, 0.00461999462380983, 0.00484687292894300, 0.00465764956855528, 0.00411456794751656, 0.00331370037475211, 0.00235780322117217, 0.00134503392921250, 0.000347539278974443, -0.000581184068209493, -0.00140995049739125, -0.00210812809815127, 0.00686175413922559};
    double post2 = 0;
    int cpost2 = 0;
    int count = 0;
    int count1 = 0;
    int tremorCount = 0;
    int totalTremorCount = 0;
    double[] features;
    DyskinesiaDetClassifier classifier;
    IDataHandler handler;
    private double segm_duration;
    private float amp_thresh;
    private float height_thresh;
    private float dur_thresh;
    private double FS;
    private double gs1 = 0;
    private double gs2 = 0;
    private double mp = 0;
    private FIRD lowPassFilter;
    private ArrayList<PeakData> current_peaks = new ArrayList<PeakData>();
    private Object lock1 = new Object();

    /**
     * Constructor
     *
     * @param fs Frequency
     */
    public DyskinesiaEvaluator(CommunicationManager manager, String patientIdentifier, IDataHandler phandler, double fs) throws Exception {

        super(manager, patientIdentifier);

        lowPassFilter = new FIRD(filtLowPass0);
        this.FS = fs;
        lowPassFilter.clear();
        handler = phandler;
        classifier = new DyskinesiaDetClassifier();
        features = new double[2];


    }

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.ACCELEROMETER || dataType == DataTypes.TREMOR;
    }

    @Override
    public void addData(ISensorData data) {


        if (data.getDataType() == DataTypes.ACCELEROMETER) {
            try {
                double lsX = 0;
                double lsY = 0;
                double lsZ = 0;
                double accX = 0;
                double accY = 0;
                double accZ = 0;
                long ticks = 0;
                ///TEST IS THAT OK?
                //      synchronized (lock1) {
                AccData accd = (AccData) data;
                AccReading acc = accd.getValue();
                lsX = lowPassFilter.tick((double) acc.getX());
                lsY = lowPassFilter.tick((double) acc.getY());
                lsZ = lowPassFilter.tick((double) acc.getZ());
                accX = acc.getX();
                accY = acc.getY();
                accZ = acc.getZ();
                ticks = accd.getTicks();
                //   }

                double gs = Math.sqrt(lsX * lsX + lsY * lsY + lsZ * lsZ);
                gs1 = gs1 + gs;
                gs2 = gs2 + Math.sqrt(accX * accX + accY * accY + accZ * accZ);

                mp += 1 / (1 + Math.exp(-250 * (gs - 0.025)));

                count++;
                count1++;


                if (count1 == 3750) {

                    int res = 0;


                    if (((double) tremorCount / (double) totalTremorCount) < 0.1) {


                        mp = mp / 3750;
                        gs1 = gs1 / 3750;
                        gs2 = gs2 / 3750;
                        features[0] = mp;// (gs / (gs1 + 0.00000001));
                        features[1] = (gs1 / (gs2 + 0.00000001));
                        res = classifier.getMaxClass(features);
                        post2 += classifier.getOutput(features)[1];
                        cpost2++;
                    }


                    LIDData s = new LIDData();

                    Date d = new Date();
                    s.setTimestamp(d);
                    s.setTicks(d.getTime());
                    UPDRS u = new UPDRS();
                    u.setUPDRS(res);
                    s.setValue(u);

                    if (handler != null)
                        handler.handleData(s);


                    SendObservation(res, "LID", ((AccData) data).getTicks());

                    count1 = 0;
                    count = 0;
                    gs2 = 0;
                    gs1 = 0;
                    mp = 0;
                    tremorCount = 0;
                    totalTremorCount = 0;
                    cpost2 = 0;
                    post2 = 0;
                }

              /*  if (count == 18750) {

                    int res=0;

                    if(cpost2>0)
                    {

                        post2/=cpost2;


                        if(post2>0.5)
                            res=1;
                    }



                    SendObservation(res, "LID", ((AccData) data).getTicks());


                    count=0;

                    cpost2=0;
                    post2=0;


                }
            */
            } catch (Exception ex) {

                Log.d("Error", "Error");

            }
        } else

        {

            TremorData accd = (TremorData) data;
            UPDRS acc = accd.getValue();
            if (acc.getUPDRS() > 0) {

                tremorCount++;
            }
            totalTremorCount++;


        }


    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }


    public void addDataTest(double x, long tick) {

    }

    private void filterData(ISensorData data) {


    }


}
