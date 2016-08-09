//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Interfaces;

import com.pdmanager.core.posturedetector.Core.Signals.PostureEvaluation;
import com.pdmanager.core.posturedetector.Core.Signals.SignalFeature;

import java.util.Date;

/*
 * This Code was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Code belong to Medlab (UOI).
 * */

/**
 * Posture Evaluator Interface
 */
public interface IPostureEvaluator {
    /**
     * Evaluate Postures based on signal Features
     *
     * @param start    Start time
     * @param end      End time
     * @param features Features
     * @return Posture Evaluation
     */
    PostureEvaluation evaluate(Date start, Date end, SignalFeature[] features) throws Exception;

}


