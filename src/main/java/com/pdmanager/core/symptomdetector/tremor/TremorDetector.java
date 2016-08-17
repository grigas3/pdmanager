package com.pdmanager.core.symptomdetector.tremor;

import android.util.Log;

import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary;

/**
 * Created by george on 2/6/2016.
 */
public class TremorDetector {


    private double extractSDSEnergy(SignalCollection gyro) throws Exception {

        return gyro.sum_energy() / gyro.getSize();

    }


    private double extractGS1Feature(SignalCollection ads) throws Exception {
        return ads.sum_diff_energy();


    }

    private double extractA19Feature(SignalCollection ads) throws Exception {

        double a19 = ads.get___idx(0).average();


        return a19;

    }


    private double extractHomogenity(SignalCollection ads) throws Exception {

        int i, j;
        int k = ads.getSize() / 5;

        double minE = 10000;
        double maxE = -10000;
        double s = 0;

        for (i = 0; i < 5; i++) {

            s = ads.sum_energy(i * k, (i + 1) * k - 1);
            if (s > maxE)
                maxE = s;
            if (s < minE)
                minE = s;

        }


        return (maxE - minE) / (maxE + minE + 0.0000001);


    }


    /**
     * First decision for tremor
     *
     * @param a3  Feature A3
     * @param aE  Feature AE
     * @param gs1 Feature GS1
     * @param a19 Feature A19
     * @return
     */
    private boolean firstDecision(double a3, double aE, double gs1, double a19) {


        boolean res = true;
        ///Changed to abs in order to avoid
        //Probably remove
        res = (a3 > 0.167) && aE > 0.2 && ((gs1 < 0.02 || a3 > 0.8) && Math.abs(a19) < 0.8);
        return res;

    }

    private boolean secondDecision(double a3, double a2) {

        return ((a3 >= 0.82) || (a3 <= 0.82 && a2 <= 0.285));

    }

    public double testGS1(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractGS1Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testA2(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroLowPass).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testAE(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractSDSEnergy(signalCollection.get___idx(SignalDictionary.OriginalGyro).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testA19(NamedSignalCollection signalCollection, int o, int l) {
        try {
            return extractA19Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass).getWindow(o, l));
        } catch (Exception ex) {


        }

        return 0;
    }


    public double testA3(NamedSignalCollection signalCollection, int o, int l) {
        try {
            double sds = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass).getWindow(o, l));
            double sds4 = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.OriginalGyro).getWindow(o, l));

            return sds / (sds4 + 0.0000001);
        } catch (Exception ex) {


        }

        return 0;
    }

    public double testSDS3(NamedSignalCollection signalCollection, int o, int l) {
        try {
            double sds3 = extractHomogenity(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass).getWindow(o, l));


            return sds3;
        } catch (Exception ex) {


        }

        return 0;
    }

    public int detectTremor(NamedSignalCollection signalCollection) {


        boolean hasTremor = false;

        try {


            double sds4 = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.OriginalGyro));
            double sds = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass));
            double sds3 = extractHomogenity(signalCollection.get___idx(SignalDictionary.TremorGyroHighPass));
            double sds2 = extractSDSEnergy(signalCollection.get___idx(SignalDictionary.TremorGyroLowPass));
            double gs1 = extractGS1Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass));
            double a19 = extractA19Feature(signalCollection.get___idx(SignalDictionary.TremorAccLowPass));
            double aE = sds4;
            double a3 = sds / (sds4 + 0.0000001);
            double a2 = sds2;


            if (((gs1 > 0.02 && a3 < 0.8) || Math.abs(a19) > 0.7))
                return -1;
            else {

                hasTremor = firstDecision(a3, aE, gs1, a19) && secondDecision(a3, a2) & sds3 < 0.5;


                return hasTremor ? 1 : 0;
            }
        } catch (Exception ex) {


            Log.d("Error", "detectTremor: ");
        }


        return -1;

    }


}
