//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:08 μμ
//

package com.pdmanager.core.posturedetector.Posture;

import com.pdmanager.core.posturedetector.Core.Logging;
import com.pdmanager.core.posturedetector.Core.Signals.SignalBuffer;
import com.pdmanager.core.posturedetector.Core.Signals.SignalFeature;
import com.pdmanager.core.posturedetector.FeatureExtractors.BaseWindowCollectionFeatureExtractor;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Main Feature Extractor Class for tremor
 */
public class PostureFeatureExtractor extends BaseWindowCollectionFeatureExtractor {
    private double FS;

    /**
     * Constructor of Posture Test
     *
     * @param bufferSize Buffer size
     * @param fs
     */
    public PostureFeatureExtractor(int bufferSize, double fs) throws Exception {
        super(5, bufferSize);
        this.FS = fs;
    }

    private PitchFeatures calculatePitch(com.pdmanager.core.posturedetector.Core.Signals.SignalCollection signal) throws Exception {
        SignalBuffer z = signal.get___idx(2);
        SignalBuffer y = signal.get___idx(1);
        SignalBuffer x = signal.get___idx(0);
        double m = 0;
        double s = 0;
        int n = x.getSize();
        for (int i = 0; i < n; i++) {
            double pitch = atan2(-y.get___idx(i), x.get___idx(i)) * 180 / Math.PI;
            if (pitch >= 0)
                pitch = (180 - pitch);
            else
                pitch = (-180 - pitch);
            m += pitch;
        }
        m = m / n;
        for (int i = 0; i < n; i++) {
            double pitch = atan2(-y.get___idx(i), x.get___idx(i)) * 180 / Math.PI;
            if (pitch >= 0)
                pitch = (180 - pitch);
            else
                pitch = (-180 - pitch);
            s += (pitch - m) * (pitch - m);
        }
        s = s / (n - 1);
        return new PitchFeatures(m, sqrt(s));
    }

    /**
     * Main Process Method
     *
     * @param signals Signals
     */
    public void process(com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection signals) throws Exception {
        try {
            com.pdmanager.core.posturedetector.Core.Signals.SignalCollection bpFreqSignal;
            bpFreqSignal = signals.get___idx(com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary.FiltLowPass30);
            PitchFeatures p = calculatePitch(bpFreqSignal);
            features[0] = new SignalFeature("0", bpFreqSignal.get___idx(1).average());
            features[1] = new SignalFeature("1", bpFreqSignal.get___idx(2).average());
            features[2] = new SignalFeature("2", bpFreqSignal.get___idx(2).std());
            features[3] = new SignalFeature("3", p.getMean());
            features[4] = new SignalFeature("4", p.getStd());
        } catch (Exception e) {
            Logging.writeMethodExecutionIntoLog(e.getMessage(), "Feature Extraction");
            throw e;
        }

    }

    private class PitchFeatures {
        private double __Mean;
        private double __Std;

        public PitchFeatures(double m, double s) {

            __Mean = m;
            __Std = s;

        }

        public double getMean() {
            return __Mean;
        }

        public void setMean(double value) {
            __Mean = value;
        }

        public double getStd() {
            return __Std;
        }

        public void setStd(double value) {
            __Std = value;
        }

    }

}


