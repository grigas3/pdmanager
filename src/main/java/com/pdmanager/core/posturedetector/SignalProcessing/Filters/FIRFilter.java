//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:09 μμ
//

package com.pdmanager.core.posturedetector.SignalProcessing.Filters;

import com.pdmanager.core.posturedetector.Core.Interfaces.ISignalProcessor;
import com.pdmanager.core.posturedetector.Core.Signals.SignalBuffer;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;

/*
 * This Code was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Code belong to Medlab (UOI).
 * */

/**
 * FIR filter for Signal Buffers implement ISignalProcessor
 */
public class FIRFilter implements ISignalProcessor {
    private final FIRD fir;

    /**
     * Constructor
     *
     * @param coeffs Filter coefficients
     */
    public FIRFilter(Double[] coeffs) throws Exception {
        fir = new FIRD(coeffs);
    }

    /**
     * Apply In Place
     *
     * @param signal Input signal
     */
    public void applyInPlace(SignalBuffer signal) throws Exception {
        //fir.clear();
        for (int i = 0; i < signal.getSize(); i++) {
            Double v = fir.tick(signal.get___idx(i));
            signal.set___idx(i, v);
        }
    }

    /**
     * Apply to source buffer
     *
     * @param source Source signal buffer
     * @param to     Dest signal buffer
     */
    public void applyTo(SignalBuffer source, SignalBuffer to) throws Exception {
        //TODO Assert
        //   fir.clear();
        to.copyFrom(source);
        for (int i = 0; i < source.getSize(); i++) {
            Double v = fir.tick(source.get___idx(i));
            to.set___idx(i, v);
        }
    }

    /**
     * Apply In Place for signal collection
     *
     * @param signals Input Signal Collection
     */
    public void applyInPlace(SignalCollection signals) throws Exception {
        for (int j = 0; j < signals.getSignals(); j++) {
            SignalBuffer signal = signals.get___idx(j);
            //   fir.clear();
            for (int i = 0; i < signal.getSize(); i++) {
                Double v = fir.tick(signal.get___idx(i));
                signal.set___idx(i, v);
            }
        }
    }

    /**
     * Apply to destination signal collection
     *
     * @param source Source signal buffer
     * @param to     Destination signal Buffer
     */
    public void applyTo(SignalCollection source, SignalCollection to) throws Exception {
        for (int j = 0; j < source.getSignals(); j++) {
            SignalBuffer signal = source.get___idx(j);
            SignalBuffer destsignal = to.get___idx(j);
            //Probably unecesairy
            destsignal.copyFrom(signal);
            //   fir.clear();
            for (int i = 0; i < signal.getSize(); i++) {
                Double v = fir.tick(signal.get___idx(i));
                destsignal.set___idx(i, v);
            }
        }
    }

    /**
     * Reset filter
     */
    public void reset() throws Exception {
        fir.clear();
    }

}


