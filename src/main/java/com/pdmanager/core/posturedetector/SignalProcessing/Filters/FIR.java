//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:09 μμ
//

package com.pdmanager.core.posturedetector.SignalProcessing.Filters;


/**
 * Abstract FIR Filter class
 * Type of signal (double,float etc)
 */
public abstract class FIR<T> {
    /**
     * Filter length
     */
    protected int length;
    /**
     * Filter coeffs
     */
    protected T[] coeffs;
    /**
     * Past Inputs
     */
    protected T[] pastInputs;
    /**
     * Past Outputs
     */
    protected T[] pastOutputs;
    /**
     * Offset
     */
    protected int piOffset;
    /**
     * Delay
     */
    protected int delay;

    /**
     * Constructor
     */
    public FIR(T[] paccF) throws Exception {
        init(paccF);
    }

    /**
     * Init
     *
     * @param acc_coeff coeffs
     */
    protected abstract void init(T[] acc_coeff) throws Exception;

    /**
     * Clear
     */
    public abstract void clear() throws Exception;

    /**
     * Tick
     *
     * @param input Input signal
     * @return Filter Response
     */
    public abstract T tick(T input) throws Exception;

    /**
     * Get Delay Val
     *
     * @return
     */
    public T getDelayVal() throws Exception {
        return pastOutputs[piOffset - 1];
    }

    /**
     * Get Delay Inverse Val
     *
     * @return
     */
    public T getDelayIVal() throws Exception {
        int v = (length / 2) + 1;
        return pastInputs[piOffset - v];
    }

    /**
     * Set coeffs
     *
     * @param theCoeffs Filter coefficients
     */
    protected void setCoeffs(T[] theCoeffs) throws Exception {
        int i;
        for (i = 0; i < length; i++) {
            coeffs[i] = theCoeffs[i];
        }
    }

    /**
     * Get Delay
     *
     * @return The delay
     */
    protected int getDelay() throws Exception {
        return delay;
    }

    /**
     * Length of filter
     *
     * @return length
     */
    protected int getLength() throws Exception {
        return length;
    }

}


