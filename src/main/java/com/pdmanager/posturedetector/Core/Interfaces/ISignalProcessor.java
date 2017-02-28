//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.Core.Interfaces;

import com.pdmanager.posturedetector.Core.Signals.SignalBuffer;
import com.pdmanager.posturedetector.Core.Signals.SignalCollection;
/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Signal Processor Interface
 */
public interface ISignalProcessor {
    /**
     * Apply filter in signal
     *
     * @param signal Signal
     */
    void applyInPlace(SignalBuffer signal) throws Exception;

    /**
     * Apply filter to dest signal
     *
     * @param source Source Signal
     * @param to     Destination signal
     */
    void applyTo(SignalBuffer source, SignalBuffer to) throws Exception;

    /**
     * Apply filter in signal collection
     *
     * @param signal Signal Collection
     */
    void applyInPlace(SignalCollection signal) throws Exception;

    /**
     * Apply filter to source and write to destination signal
     *
     * @param source Source signal Collection
     * @param to     Destination signal Collection
     */
    void applyTo(SignalCollection source, SignalCollection to) throws Exception;

}


