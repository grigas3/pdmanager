package com.pdmanager.core.symptomdetector.tremor;


import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.FeatureExtractors.SinAmpFCalculator;
import com.pdmanager.core.posturedetector.FeatureExtractors.SinAmpFRes;
import com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary;

/**
 * Created by george on 2/6/2016.
 */
public class TremorEstimator {

    SinAmpFRes currentRes;
    SinAmpFCalculator sinAmpFCalculator;

    public TremorEstimator(int n, double fs) throws Exception {

        sinAmpFCalculator = new SinAmpFCalculator(n, fs);

    }


    public void update(NamedSignalCollection collection) {

        double res_amp = 0;
        try {
            int i = 0;
            int max_i = 0;
            double max_e = 0;
            SignalCollection signals = collection.get___idx(SignalDictionary.TremorGyroHighPass);
            for (i = 0; i < signals.getSignals(); i++) {


                double s = signals.get___idx(i).std();

                if (s > max_e) {
                    max_e = s;
                    max_i = i;
                }
            }

            signals.get___idx(max_i).addScalar(-signals.get___idx(max_i).average());
            SinAmpFRes res = sinAmpFCalculator.calculate(signals.get___idx(max_i));

            currentRes = res;

        } catch (Exception ex) {

            currentRes = null;

        }


    }

    private double trapmf(double x, double a, double b, double c, double d) {


        double y1, y2;

        if (x > b)
            y1 = 1;
        else if (x < a)
            y1 = 0;
        else
            y1 = (x - a) / (b - a);

        if (x <= c)
            y2 = 1;
        else if (x > d)
            y2 = 0;
        else y2 = (d - x) / (d - c);

        return Math.min(y1, y2);
    }

    public double getUPDRS() {

        double updrs2 = getAmplitude();
        return trapmf(updrs2, 0.01, 0.1, 1, 1.4) + 2 * trapmf(updrs2, 1, 1.4, 3, 4) + 3 * trapmf(updrs2, 3, 4, 8, 12) + 4 * trapmf(updrs2, 8, 12, 100, 100);


    }

    public double testUPDRS(double updrs2) {


        return trapmf(updrs2, 0.01, 0.5, 1, 2) + 2 * trapmf(updrs2, 1, 2, 3, 4) + 3 * trapmf(updrs2, 3, 4, 8, 12) + 4 * trapmf(updrs2, 8, 12, 100, 100);


    }

    public double getAmplitude() {


        if (currentRes != null) {
            double amp = currentRes.getAmp();
            double frq = currentRes.getF();
            double f = amp / (2 * Math.PI * frq);

            return 0.813 * 20 * Math.sin(f / 2);

        }

        return 0;

    }

}
