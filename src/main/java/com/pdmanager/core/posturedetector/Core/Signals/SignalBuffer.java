//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Signals;

import com.pdmanager.core.posturedetector.Core.BaseMath;

/*
 * This Code was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Code belong to Medlab (UOI).
 * */

/**
 * Main Signal Buffer
 */
public class SignalBuffer extends BaseMath {
    private int size;
    private double[] buffer;

    /**
     * Constructor
     *
     * @param s Size
     */
    public SignalBuffer(int s) throws Exception {
        size = s;
        buffer = new double[s];
    }

    /**
     * Signal Size
     */
    public int getSize() throws Exception {
        return size;
    }

    /**
     * Accessor
     *
     * @param index Index
     * @return Signal Value
     */
    public double get___idx(int index) throws Exception {
        return buffer[index];
    }

    public void set___idx(int index, double value) throws Exception {
        buffer[index] = value;
    }

    /**
     * Copy from array
     *
     * @param data source array
     */
    public void copyFrom(double[] data) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = data[i];
    }

    /**
     * Copy to data array
     *
     * @param data Array
     */
    public void copyTo(double[] data) throws Exception {
        for (int i = 0; i < size; i++)
            data[i] = buffer[i];
    }

    /**
     * Copy from source signal
     *
     * @param source Source signal
     */
    public void copyFrom(SignalBuffer source) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = source.get___idx(i);
    }

    /**
     * Signal Average
     *
     * @return Average Value
     */
    public double average() throws Exception {
        double m = 0.0f;
        double v;
        for (int i = 0; i < size; i++) {
            v = buffer[i];
            m += v;
        }
        return m / ((double) size);
    }


    /**
     * Signal Standard Deviation
     *
     * @return Std Value
     */
    public double std() throws Exception {
        double q = 0.0f;
        double m = 0.0f;
        double v;
        for (int i = 0; i < size; i++) {
            v = buffer[i];
            m += v;
        }
        m = m / ((double) size);
        for (int i = 0; i < size; i++) {
            v = buffer[i];
            q += (m - v) * (m - v);
        }
        return sqrt(q / ((double) size - 1));
    }

    /**
     * Add Scalar to signal
     *
     * @param v Scalar Value
     */
    public void addScalar(double v) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = buffer[i] + v;
    }

    /**
     * Mult Scalar to signal
     *
     * @param v Scalar Value
     */
    public void multScalar(double v) throws Exception {
        for (int i = 0; i < size; i++)
            buffer[i] = buffer[i] * v;
    }

    /**
     * Absolute differences
     *
     * @return
     */
    public double cdiff() throws Exception {
        double s = 0;
        for (int i = 1; i < size; i++)
            s += abs(buffer[i] - buffer[i - 1]);
        return s;
    }

    public double sum_energy() {

        double s = 0;
        for (int j = 0; j < size; j++)
            s += buffer[j] * buffer[j];
        return s;
    }

    public double sum_diff_energy() {

        double s = 0;
        for (int i = 0; i < size - 1; i++)
            s += (buffer[i + 1] - buffer[i]) * (buffer[i + 1] - buffer[i]);

        return s;
    }

}


