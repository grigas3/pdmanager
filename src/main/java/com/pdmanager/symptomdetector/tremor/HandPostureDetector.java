package com.pdmanager.symptomdetector.tremor;

import android.util.Log;

import com.pdmanager.common.DataTypes;
import com.pdmanager.common.data.AccData;
import com.pdmanager.common.data.AccReading;
import com.pdmanager.common.data.ISensorData;
import com.pdmanager.common.data.PostureData;
import com.pdmanager.common.interfaces.IDataHandler;
import com.pdmanager.common.interfaces.IDataProcessor;
import com.pdmanager.posturedetector.Core.PostureTypes;
import com.pdmanager.posturedetector.SignalProcessing.Filters.FIRD;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by george on 2/6/2016.
 * Tis hand posture detector detects simple hand raises
 */
public class HandPostureDetector implements IDataProcessor {

    private final Double[] filtLowPass0 = new Double[]{5.90985359413491e-20, 2.48199209872468e-05, 5.14857827834168e-05, 8.11195227318032e-05, 0.000114891835278174, 0.000154012571207948, 0.000199720578019910, 0.000253273059781340, 0.000315934539865309, 0.000388965514361833, 0.000473610887637214, 0.000571088284452969, 0.000682576335216082, 0.000809203032291309, 0.000952034255846160, 0.00111206256740908, 0.00129019636819763, 0.00148724951731936, 0.00170393150217491, 0.00194083824981714, 0.00219844366366836, 0.00247709196490009, 0.00277699091197535, 0.00309820596538655, 0.00344065545754324, 0.00380410682012889, 0.00418817391311622, 0.00459231549107150, 0.00501583483345944, 0.00545788055645514, 0.00591744861435354, 0.00639338548911831, 0.00688439255701027, 0.00738903161166120, 0.00790573151349183, 0.00843279592609397, 0.00896841209118353, 0.00951066058506141, 0.0100575259912663, 0.0106069084163378, 0.0111566357683994, 0.0117044767116752, 0.0122481542041389, 0.0127853595203002, 0.0133137666567146, 0.0138310470141967, 0.0143348842479550, 0.0148229891749746, 0.0152931146269728, 0.0157430701371508, 0.0161707363497673, 0.0165740790432602, 0.0169511626602323, 0.0173001632410752, 0.0176193806623048, 0.0179072500857866, 0.0181623525309098, 0.0183834244883564, 0.0185693665013808, 0.0187192506483802, 0.0188323268689522, 0.0189080280845241, 0.0189459740739303, 0.0189459740739303, 0.0189080280845241, 0.0188323268689522, 0.0187192506483802, 0.0185693665013808, 0.0183834244883564, 0.0181623525309098, 0.0179072500857866, 0.0176193806623048, 0.0173001632410752, 0.0169511626602323, 0.0165740790432602, 0.0161707363497673, 0.0157430701371508, 0.0152931146269728, 0.0148229891749746, 0.0143348842479550, 0.0138310470141967, 0.0133137666567146, 0.0127853595203002, 0.0122481542041389, 0.0117044767116752, 0.0111566357683994, 0.0106069084163378, 0.0100575259912663, 0.00951066058506141, 0.00896841209118353, 0.00843279592609397, 0.00790573151349183, 0.00738903161166120, 0.00688439255701027, 0.00639338548911831, 0.00591744861435354, 0.00545788055645514, 0.00501583483345944, 0.00459231549107150, 0.00418817391311622, 0.00380410682012889, 0.00344065545754324, 0.00309820596538655, 0.00277699091197535, 0.00247709196490009, 0.00219844366366836, 0.00194083824981714, 0.00170393150217491, 0.00148724951731936, 0.00129019636819763, 0.00111206256740908, 0.000952034255846160, 0.000809203032291309, 0.000682576335216082, 0.000571088284452969, 0.000473610887637214, 0.000388965514361833, 0.000315934539865309, 0.000253273059781340, 0.000199720578019910, 0.000154012571207948, 0.000114891835278174, 8.11195227318032e-05, 5.14857827834168e-05, 2.48199209872468e-05, 5.90985359413491e-20};
    RTPeakFinder posFinder;
    RTPeakFinder negFinder;
    IDataHandler handler;
    private double segm_duration;
    private float amp_thresh;
    private float height_thresh;
    private float dur_thresh;
    private double FS;
    private double gs;
    private double pp;
    private double previous_lsX;
    private FIRD lowPassFilter;
    private int count = 0;
    private int numOfPeaks = 0;
    private double pos_mean = 0;
    private double max_x = -1000;
    private double min_x = 1000;
    private long lastHandPosture = 0;
    private ArrayList<PeakData> current_peaks = new ArrayList<PeakData>();
    private Object lock1 = new Object();
    private long lastPosPeak = -1;

    /**
     * Constructor
     *
     * @param amplThreshold     Amplitude Threshold
     * @param heightThreshold   Height Threshold
     * @param durationThreshold Duration Threshold
     * @param fs                Frequency
     */
    public HandPostureDetector(IDataHandler phandler, float amplThreshold, float heightThreshold, float durationThreshold, double fs) throws Exception {
        this.height_thresh = heightThreshold;
        this.amp_thresh = amplThreshold;
        this.dur_thresh = durationThreshold;
        lowPassFilter = new FIRD(filtLowPass0);
        this.FS = fs;
        lowPassFilter.clear();
        handler = phandler;
        posFinder = new RTPeakFinder(256, 0);
        negFinder = new RTPeakFinder(256, 1);
    }

    @Override
    public boolean requiresData(int dataType) {

        return dataType == DataTypes.ACCELEROMETER;
    }

    @Override
    public void addData(ISensorData data) {

        try {
            double lsX = 0;
            long ticks = 0;
            ///TEST IS THAT OK?
            synchronized (lock1) {
                AccData accd = (AccData) data;
                AccReading acc = accd.getValue();
                lsX = lowPassFilter.tick((double) acc.getX());
                ticks = accd.getTicks();
            }
            gs = gs + lsX * lsX;
            pos_mean = pos_mean + lsX;
            count++;
            if (lsX > max_x)
                max_x = lsX;

            if (lsX < min_x)
                min_x = lsX;


            double pp1 = lsX - previous_lsX;
            double pp2 = -(lsX - previous_lsX);

            if (pp1 < 0)
                pp1 = 0;

            if (pp2 < 0)
                pp2 = 0;
            //Update previous lsx
            previous_lsX = lsX;
            //Update peak finder
            posFinder.update(pp1, ticks);
            negFinder.update(pp2, ticks);

            if (lastHandPosture > 0 && ticks - lastHandPosture > 2000) {
                lastHandPosture = 0;
                if (handler != null) {
                    PostureData s = new PostureData();

                    Date d = new Date();
                    s.setTimestamp(d);
                    s.setTicks(d.getTime());
                    s.setValue(PostureTypes.OTHER);

                    handler.handleData(s);
                }


            }
        } catch (Exception ex) {

            Log.d("Error", "Error");

        }


    }

    public int getNumOfPeaks() {

        return numOfPeaks;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }

    public void addDataTest(double x, long tick) {
        //double lsX=lowPassFilter.tick((double) x);


        //Difference between current and previous lsx
        double lsX = x;


        gs = gs + lsX * lsX;
        pos_mean = pos_mean + lsX;
        count++;
        if (lsX > max_x)
            max_x = lsX;

        if (lsX < min_x)
            min_x = lsX;


        double pp1 = lsX - previous_lsX;
        double pp2 = -(lsX - previous_lsX);

        if (pp1 < 0)
            pp1 = 0;

        if (pp2 < 0)
            pp2 = 0;
        //Update previous lsx
        previous_lsX = lsX;
        //Update peak finder
        posFinder.update(pp1, tick);
        negFinder.update(pp2, tick);

        if (tick - lastHandPosture > 10000) {

            if (handler != null) {
                PostureData s = new PostureData();

                Date d = new Date();
                s.setTimestamp(d);
                s.setTicks(d.getTime());
                s.setValue(PostureTypes.OTHER);

                handler.handleData(s);
            }


        }

    }

    private void filterData(ISensorData data) {


    }

    private void onNegativePeakFound(long i) {

        if (lastPosPeak > -1) {

            if (ClassifySegment(i)) {

                lastHandPosture = i;
                reset();

                if (handler != null) {
                    PostureData s = new PostureData();

                    Date d = new Date();
                    s.setTimestamp(d);
                    s.setTicks(d.getTime());
                    s.setValue(PostureTypes.HANDPOSTURE);

                    handler.handleData(s);
                }


            }

        }

    }


    private void reset() {
        count = 0;
        pos_mean = 0;
        gs = 0;
        max_x = -1000;
        min_x = 1000;

    }

    private void onPositivePeakFound(long i) {
        lastPosPeak = i;
        reset();
    }

    private double psigmf(double x, double a, double b, double c, double d) {


        return sigmf(x, a, b) * sigmf(x, c, d);

    }

    private double sigmf(double x, double a, double b) {
        return 1 / (1 + Math.exp(-a * (x - b)));

    }

    private boolean ClassifySegment(long t) {


        double mp1 = max_x - min_x;

        double p1 = pos_mean / count;
        double nspls = Math.sqrt(gs / count - p1 * p1);

        double dfs = t - lastPosPeak;

        //double durP=psigmf(dfs/FS,2,4,-0.2,30);
        double durP = sigmf(dfs / FS, 2, 8);
        double posP = sigmf(-p1, 20, 0.2);
        double stilP = sigmf(nspls, -10, 0.1);
        double ampP = sigmf(mp1, 10, 0.3);
        return durP * posP * stilP * ampP > 0.1;
    }

    private class RTPeakFinder {

        private double[] tmpBuffer;
        private int currentBufferPos = 0;
        private int currentBufferSize = 128;

        private double min_map_x;
        private double min_map_y;
        private int min_found = 1;
        private int max_found = 0;
        private int current_ind = 0;
        private int negPos = 0;

        RTPeakFinder(int n, int nP) {

            negPos = nP;
            currentBufferSize = n;
            this.tmpBuffer = new double[currentBufferSize];
        }

        private void clearBuffer() {
            currentBufferPos = 0;

        }

        private int updateBuffer(double x) {
            int i = 0;

            if (current_ind > 0)
                current_ind--;
            else {

                min_found = 1;
                max_found = 0;


            }

            if (currentBufferPos < currentBufferSize - 1) {
                tmpBuffer[currentBufferPos++] = x;


                if (currentBufferPos < 2)
                    return 0;

                return 1;

            } else {


                for (i = 0; i < currentBufferSize - 1; i++)
                    tmpBuffer[i] = tmpBuffer[i + 1];

                tmpBuffer[currentBufferSize - 1] = x;


                return 1;
            }


        }

        private void update(double x, long tick) {


            if (updateBuffer(x) > 0) {

                int i, j, k;


                i = currentBufferPos - 1;
                //  int start = nelements -((int)Fs)*window;
                if (tmpBuffer[i] > tmpBuffer[i + 1]) {
                    max_found = 1;
                    for (j = current_ind; j < i; j++) {
                        if (tmpBuffer[i] < tmpBuffer[j]) {
                            max_found = 0;
                            break;
                        }

                    }
                    if (max_found == 1) {
                        if (min_found != 1) {
                            if (tmpBuffer[i] - min_map_y > amp_thresh && tick - min_map_x > dur_thresh && tmpBuffer[i] > height_thresh) {

                                current_peaks.add(new PeakData(tick, tmpBuffer[i]));


                                if (negPos == 1)
                                    onPositivePeakFound(tick);
                                else
                                    onNegativePeakFound(tick);
                                //  clearBuffer();


                            }

                        }
                        current_ind = i;


                    }

                } else if (tmpBuffer[i] < tmpBuffer[i + 1]) {
                    min_found = 1;
                    for (k = current_ind; k < i; k++) {
                        if (tmpBuffer[i] > tmpBuffer[k]) {
                            min_found = 0;
                            break;
                        }

                    }
                    if (min_found == 1) {
                        min_map_x = tick;
                        min_map_y = tmpBuffer[i];
                        current_ind = i;
                    }

                }


            }

        }


    }


}
