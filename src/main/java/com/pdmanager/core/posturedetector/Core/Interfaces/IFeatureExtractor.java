//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Interfaces;

import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;

/*
 * This Code was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Code belong to Medlab (UOI).
 * */

/**
 * Feature Extractor Interface
 */
public interface IFeatureExtractor {
    /**
     * Process Signal
     *
     * @param signalCollection Signal Collection
     */
    void process(NamedSignalCollection signalCollection) throws Exception;

}


