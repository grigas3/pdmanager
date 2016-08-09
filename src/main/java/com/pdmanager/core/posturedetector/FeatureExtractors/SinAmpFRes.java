//
// Translated by Medlab: 23/1/2015 10:45:16 πμ
//

package com.pdmanager.core.posturedetector.FeatureExtractors;


/**
 * Class used for results of SinAmpF method
 * Maybe a struct instead could be used for memory
 */
public class SinAmpFRes {
    private double _f;
    private double _amp;

    /**
     * Constructor
     *
     * @param f   Frequency
     * @param amp Amplitude
     */
    public SinAmpFRes(double f, double amp) {
        _f = f;
        _amp = amp;
    }

    /**
     * Frequency
     */
    public double getF() {
        return _f;
    }

    /**
     * Amplitude
     */
    public double getAmp() {
        return _amp;
    }

}


