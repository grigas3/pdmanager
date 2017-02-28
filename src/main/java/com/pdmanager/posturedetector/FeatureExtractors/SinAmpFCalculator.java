//
// Translated by Medlab: 23/1/2015 10:45:16 πμ
//

package com.pdmanager.posturedetector.FeatureExtractors;



/*
 * This Type was developed by George Rigas
 * in 2014 for project PD-fusion.
 * The copyrights of the Type belong to Micrel.
 * */

import com.pdmanager.posturedetector.Core.BaseMath;
import com.pdmanager.posturedetector.Core.Signals.SignalBuffer;

/**
 * Sin Amp Calculator
 */
public class SinAmpFCalculator extends BaseMath {
    private final int N;
    private final double FS;
    private final double[] x_n_minus_1;
    private final double[] x_n_plus_1;
    private final double[] x3;
    private final double[] x_s;
    private final double[] x;

    /**
     * Constructor
     *
     * @param p  Buffer size
     * @param fs Frequency
     */
    public SinAmpFCalculator(int p, double fs) throws Exception {
        this.N = p;
        this.FS = fs;
        //Init buffers
        x_n_minus_1 = new double[N - 2];
        x_n_plus_1 = new double[N - 2];
        x3 = new double[N - 2];
        x_s = new double[N - 2];
        x = new double[N];
    }

    /**
     * Reset Buffers
     */
    void resetBuffers() throws Exception {
        for (int i = 0; i < N - 2; i++) {
            x_n_minus_1[i] = 0;
            x_n_plus_1[i] = 0;
            x3[i] = 0;
            x_s[i] = 0;
        }
    }

    /**
     * Calculate Sin Amp
     *
     * @param buffer Signal
     * @return A SinAmpFRes class with the results
     */
    public SinAmpFRes calculate(SignalBuffer buffer) throws Exception {
        resetBuffers();
        double freq = 0;
        double amp = 0;
        buffer.copyTo(x);
        freq = 0;
        amp = 0;
        double Fs1 = this.FS / 2;
        double ave = 0;
        for (int i = 0; i < N; i++)
            ave += x[i];
        ave /= N;
        for (int i = 1; i < N - 1; i++) {
            //n   = 2:N-1;
            x_n_minus_1[i - 1] = x[i + 1];
            x_n_plus_1[i - 1] = x[i - 1];
        }
        for (int i = 0; i < N - 2; i++)
            x_s[i] = x_n_minus_1[i] + x_n_plus_1[i];
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;
        double s4 = 0;
        for (int i = 0; i < N - 2; i++) {
            s1 += x_s[i] * x[i + 1];
            s2 += x[i + 1] * x[i + 1];
            s3 += x[i + 1] * x_n_plus_1[i];
            s4 += x_n_plus_1[i] * x_n_plus_1[i];
        }
        if (abs(s2) > 1.0e-10) {
            double C = s1 / (2 * s2);
            freq = 0.5F * acos(C) * Fs1 / PI;
            amp = sqrt((s2 - 2 * s3 * C + s4) / ((1 - C * C) * (N - 2)));
        }

        return new SinAmpFRes(freq, amp);
    }

}


