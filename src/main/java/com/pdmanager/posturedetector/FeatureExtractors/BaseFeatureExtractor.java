//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:04 μμ
//

package com.pdmanager.posturedetector.FeatureExtractors;

import com.pdmanager.posturedetector.Core.Interfaces.IFeatureExtractor;
import com.pdmanager.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.posturedetector.Core.Signals.SignalFeature;
import com.pdmanager.posturedetector.SignalProcessing.SignalProcess;

/**
 * Base Feature Extractor
 */
public abstract class BaseFeatureExtractor extends SignalProcess implements IFeatureExtractor {
    /**
     * Feature vector
     */
    protected SignalFeature[] features;

    /**
     * Void Constructor
     */
    public BaseFeatureExtractor() throws Exception {
        super();
    }

    /**
     * Void Constructor
     */
    public BaseFeatureExtractor(int pFN) throws Exception {
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


