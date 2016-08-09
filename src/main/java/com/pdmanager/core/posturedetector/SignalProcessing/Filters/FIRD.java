//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:09 μμ
//

package com.pdmanager.core.posturedetector.SignalProcessing.Filters;

import com.pdmanager.core.posturedetector.Core.Signals.SignalBuffer;

/**
 * Double FIR Filter
 */
public class FIRD extends FIR<Double> {
    /**
     * Constructor
     */
    public FIRD(Double[] coefs) throws Exception {
        super(coefs);
    }

    /**
     * Clear filter
     */
    public void clear() throws Exception {
        int i;
        for (i = 0; i < 2 * length; i++) {
            pastInputs[i] = 0.0;
        }
        piOffset = length;
    }


    public int getOrder() {
        return length;
    }

    /**
     * Tick (next filter input)
     *
     * @param input Signal input
     * @return Filter response
     */
    public Double tick(Double input) throws Exception {
        int i;
        double lastOutput = input * coeffs[0];
        for (i = 1; i < length; i++)
            lastOutput += coeffs[i] * pastInputs[piOffset - i];
        pastInputs[piOffset] = input;
        pastOutputs[piOffset] = lastOutput;
        piOffset++;
        if (piOffset >= 2 * length) {
            piOffset = length;
            for (i = 0; i < length; i++) {
                pastInputs[i] = pastInputs[length + i];
                pastOutputs[i] = pastOutputs[length + i];
            }
        }

        return lastOutput;
    }

    /**
     * Initialize
     *
     * @param acc_coeff Coefficients
     */
    protected void init(Double[] acc_coeff) throws Exception {
        int i;
        length = acc_coeff.length;
        coeffs = new Double[length];
        pastInputs = new Double[2 * length];
        pastOutputs = new Double[2 * length];
        delay = length;
        for (i = 0; i < length; i++) {

            coeffs[i] = acc_coeff[i];
        }
        piOffset = length;
    }

    public void applyTo(SignalBuffer signal, SignalBuffer destsignal) throws Exception {


        //Probably unecesairy
        //destsignal.copyFrom(signal);
        //   fir.clear();
        for (int i = 0; i < signal.getSize(); i++) {
            Double v = tick(signal.get___idx(i));
            destsignal.set___idx(i, v);
        }


    }
}


