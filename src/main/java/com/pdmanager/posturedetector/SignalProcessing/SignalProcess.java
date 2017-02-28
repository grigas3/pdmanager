//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:09 μμ
//

package com.pdmanager.posturedetector.SignalProcessing;

import com.pdmanager.posturedetector.Core.BaseMath;

/**
 * Base Signal Process class
 */
public class SignalProcess extends BaseMath {
    /**
     * Sampling Frequency
     */
    protected final float Fs = 62;
    /**
     * Fourier Samples
     */
    protected final int FourierSamples = 512;
    /**
     *
     */
    protected int FN;

    /**
     * Constructor
     */
    public SignalProcess() throws Exception {
        FN = 0;
    }

    /**
     * Constructir
     *
     * @param pFN Number of samples
     */
    public SignalProcess(int pFN) throws Exception {
        FN = pFN;
    }

    /**
     * Average for double
     *
     * @param buffer
     * @return
     */
    public double average(double[] buffer) throws Exception {
        double m = 0.0f;
        double v;
        for (int i = 0; i < FN; i++) {
            v = buffer[i];
            m += v;
        }
        return m / ((float) FN);
    }

    /**
     * Extract the average Value of a buffer
     *
     * @param buffer
     * @return
     */
    public float average(float[] buffer) throws Exception {
        float m = 0.0f;
        float v;
        for (int i = 0; i < FN; i++) {
            v = buffer[i];
            m += v;
        }
        return m / ((float) FN);
    }

    /**
     * Std for double
     *
     * @param buffer
     * @return
     */
    public double std(double[] buffer) throws Exception {
        double q = 0.0f;
        double m = 0.0f;
        double v;
        for (int i = 0; i < FN; i++) {
            v = buffer[i];
            m += v;
        }
        m = m / ((double) FN);
        for (int i = 0; i < FN; i++) {
            v = buffer[i];
            q += (m - v) * (m - v);
        }
        return sqrt(q / ((double) FN - 1));
    }

    /**
     * Extract the standard deviation of a buffer
     *
     * @param buffer
     * @return
     */
    public float std(float[] buffer) throws Exception {
        float q = 0.0f;
        float m = 0.0f;
        float v;
        for (int i = 0; i < FN; i++) {
            v = buffer[i];
            m += v;
        }
        m = m / ((float) FN);
        for (int i = 0; i < FN; i++) {
            v = buffer[i];
            q += (m - v) * (m - v);
        }
        return (float) sqrt(q / ((float) FN - 1));
    }

    /**
     * @param buffer
     * @return
     */
    public double[] remove_mean(double[] buffer) throws Exception {
        double[] new_buffer = new double[FN];
        double av = average(buffer);
        for (int i = 0; i < FN; i++)
            new_buffer[i] = buffer[i] - av;
        return new_buffer;
    }

    /**
     * Remove the mean Value from a buffer
     *
     * @param buffer
     * @return
     */
    public float[] remove_mean(float[] buffer) throws Exception {
        float[] new_buffer = new float[FN];
        float av = average(buffer);
        for (int i = 0; i < FN; i++)
            new_buffer[i] = buffer[i] - av;
        return new_buffer;
    }

    /**
     * Average2 for double
     *
     * @param buffer
     * @return
     */
    public double average2(double[] buffer) throws Exception {
        double m = 0.0f;
        for (int i = 0; i < FN; i++) {
            m += buffer[i] * buffer[i];
        }
        m = m / ((double) FN);
        return m;
    }

    /**
     * Extract the mean square of a buffer
     *
     * @param buffer
     * @return
     */
    public float average2(float[] buffer) throws Exception {
        float m = 0.0f;
        for (int i = 0; i < FN; i++) {
            m += buffer[i] * buffer[i];
        }
        m = m / ((float) FN);
        return m;
    }

    /**
     * Smooth function
     *
     * @param buffer buffer
     * @param window window of smoothing
     * @return
     */
    public float[] smooth(float[] buffer, int window) throws Exception {
        float[] smoothed = new float[FN];
        int w = window / 2;
        for (int i = w; i < FN - w; i++) {
            float s = 0;
            for (int j = i - w; j < i + w; j++) {
                s += buffer[j];
            }
            smoothed[i] = s / window;
        }
        return smoothed;
    }

    /**
     * SUm with doubles
     *
     * @param storage_buffer
     * @param start
     * @param end
     * @return
     */
    public double sum(double[] storage_buffer, int start, int end) throws Exception {
        double m = 0.0f;
        for (int i = start; i < end; i++) {
            m += storage_buffer[i];
        }
        return m;
    }

    /**
     * Extract the sum of a specific region of a buffer
     *
     * @param storage_buffer
     * @param start
     * @param end
     * @return
     */
    public float sum(float[] storage_buffer, int start, int end) throws Exception {
        float m = 0.0f;
        for (int i = start; i < end; i++) {
            m += storage_buffer[i];
        }
        return m;
    }

    /**
     * Extract the mean Value of a buffer
     *
     * @param buffer
     * @return
     */
    public float averageF(float[] buffer) throws Exception {
        float m = 0.0f;
        for (int i = 0; i < buffer.length; i++) {
            m += buffer[i];
        }
        m = m / ((float) buffer.length);
        return m;
    }

    /**
     * Entropy calculation
     *
     * @param buffer
     * @param iS
     * @param iE
     * @return
     */
    public float entropy(float[] buffer, int iS, int iE) throws Exception {
        double p = 0.0f;
        double s = 0;
        for (int i = iS; i < iE; i++) {
            s += buffer[i] + 1.0e-10;
        }
        float Ent = 0;
        for (int i = iS; i < iE; i++) {
            p = buffer[i] / s;
            Ent += -((float) (p * log2(p + 1.0E-12)));
        }
        return Ent;
    }

    /**
     * Make Fourier transform
     *
     *  @param buffer The signal buffer
     *  @param Fs The sampling frequency
     *  @param useRaisedCosine use Raised Cosine
     *  @return
     */


}


