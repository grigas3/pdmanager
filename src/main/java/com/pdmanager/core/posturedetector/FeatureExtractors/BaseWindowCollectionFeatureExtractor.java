//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.FeatureExtractors;

import com.pdmanager.core.posturedetector.Core.Signals.SignalFeature;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Base Signal Collection Feature Extractor
 */
public abstract class BaseWindowCollectionFeatureExtractor extends BaseWindowFeatureExtractor {
    /**
     * Number of features
     */
    protected final int NumOfFeatures;

    /**
     * Constructor
     *
     * @param pnf Number of features
     */
    public BaseWindowCollectionFeatureExtractor(int pnf) throws Exception {
        super();
        this.NumOfFeatures = pnf;
        features = new SignalFeature[pnf];
    }

    /**
     * Constructor
     *
     * @param numf Number of features
     * @param fn   Frequency
     */
    public BaseWindowCollectionFeatureExtractor(int numf, int fn) throws Exception {
        super(fn);
        this.NumOfFeatures = numf;
        features = new SignalFeature[numf];
    }

}


