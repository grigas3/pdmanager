//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.FeatureExtractors;

import com.pdmanager.posturedetector.Core.Interfaces.IWindowFeatureExtractor;
import com.pdmanager.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.posturedetector.Core.Signals.SignalFeature;
import com.pdmanager.posturedetector.SignalProcessing.SignalProcess;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Base Feature Extractor
 */
public abstract class BaseWindowFeatureExtractor extends SignalProcess implements IWindowFeatureExtractor {
    /**
     * Feature vector
     */
    protected SignalFeature[] features;

    /**
     * Void Constructor
     */
    public BaseWindowFeatureExtractor() throws Exception {
        super();
    }

    /**
     * Constructor
     *
     * @param pFN Buffer size
     */
    public BaseWindowFeatureExtractor(int pFN) throws Exception {
        super(pFN);
    }

    /**
     * Constructor
     *
     * @return
     */
    public SignalFeature[] getFeatures() throws Exception {
        return features;
    }

    /**
     * Main Process method
     *
     * @param signals Named Signal collection (after preprocessing)
     */
    public abstract void process(NamedSignalCollection signals) throws Exception;

}


