//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Interfaces;

import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Signal Preprocessor Interface
 */
public interface ISignalPreprocessor {
    /**
     * Process Method
     *
     * @param source  Signal Source
     * @param signals Output signal collection
     */
    void process(SignalCollection source, NamedSignalCollection signals) throws Exception;

    /**
     * Reset Preprocessor
     */
    void reset() throws Exception;

}


