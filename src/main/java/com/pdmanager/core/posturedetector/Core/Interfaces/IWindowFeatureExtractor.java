//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Interfaces;

import com.pdmanager.core.posturedetector.Core.Signals.SignalFeature;

/**
 * Window Feature Extractor
 */
public interface IWindowFeatureExtractor extends IFeatureExtractor {
    /**
     * Get Features
     *
     * @return Feature Array
     */
    SignalFeature[] getFeatures() throws Exception;

}


