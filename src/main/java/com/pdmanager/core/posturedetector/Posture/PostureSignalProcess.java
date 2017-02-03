//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:08 μμ
//

package com.pdmanager.core.posturedetector.Posture;

import com.pdmanager.core.posturedetector.Core.Interfaces.ISignalPreprocessor;
import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.SignalProcessing.Filters.FIRFilter;
import com.pdmanager.core.posturedetector.SignalProcessing.SignalProcess;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Series Process main class
 */
public class PostureSignalProcess extends SignalProcess implements ISignalPreprocessor {
    private final Double[] filtLowPass30 = new Double[]{0.00784820443777341, 0.0187278603562200, 0.00520546178825486, -0.0189369221254849, -0.0467628022798716, -0.0376235995986642, 0.0296864821547635, 0.144351827606412, 0.254022653899080, 0.299489442097382, 0.254022653899080, 0.144351827606412, 0.0296864821547635, -0.0376235995986642, -0.0467628022798716, -0.0189369221254849, 0.00520546178825486, 0.0187278603562200, 0.00784820443777341};
    private final FIRFilter bpfirFilterX;
    private final FIRFilter bpfirFilterY;
    private final FIRFilter bpfirFilterZ;

    /**
     * Void Constructor
     */
    public PostureSignalProcess() throws Exception {
        bpfirFilterX = new FIRFilter(filtLowPass30);
        bpfirFilterY = new FIRFilter(filtLowPass30);
        bpfirFilterZ = new FIRFilter(filtLowPass30);
    }

    /**
     * Process Method
     *
     * @param source  Source signal
     * @param signals Extracted signals
     */
    public void process(com.pdmanager.core.posturedetector.Core.Signals.SignalCollection source, NamedSignalCollection signals) throws Exception {
        int numofSignals = source.getSignals();
        int size = source.getSize();
        //   signals[SignalDictionary.FiltLowPass30] = source;
        /**
         * /Create band pass filter
         */
        SignalCollection bpSignal = signals.get___idx(com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary.FiltLowPass30);
        if (bpSignal == null) {
            bpSignal = new com.pdmanager.core.posturedetector.Core.Signals.SignalCollection(numofSignals, size);
            bpfirFilterX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            bpfirFilterY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            bpfirFilterZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));
            // rmFilter.ApplyInPlace(bpSignal);
            signals.set___idx(com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary.FiltLowPass30, bpSignal);
        } else {
            bpfirFilterX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            bpfirFilterY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            bpfirFilterZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));
        }
    }

    //   bpfirFilter.ApplyTo(source, bpSignal);
    // rmFilter.ApplyInPlace(bpSignal);

    /**
     * Reset
     */
    public void reset() throws Exception {
        bpfirFilterX.reset();
        bpfirFilterY.reset();
        bpfirFilterZ.reset();
    }

}


