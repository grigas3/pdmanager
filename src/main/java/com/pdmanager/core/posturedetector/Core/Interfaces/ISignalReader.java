//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Interfaces;

import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Reader Interface
 */
public interface ISignalReader {
    /**
     * Num of samples
     */
    int getSamples() throws Exception;

    /**
     * Read Signal
     *
     * @param bufferSize Buffer size
     * @return Signal Collection
     */
    SignalCollection read(int bufferSize) throws Exception;

    /**
     * Read All
     *
     * @return Signal Collection
     */
    SignalCollection readAll() throws Exception;

    /**
     * Has other data
     *
     * @return
     */
    boolean hasNext() throws Exception;

    /**
     * Reset
     */
    void reset() throws Exception;

}


