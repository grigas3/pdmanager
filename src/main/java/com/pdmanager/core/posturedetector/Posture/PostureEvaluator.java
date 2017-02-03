//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:08 μμ
//

package com.pdmanager.core.posturedetector.Posture;

import com.pdmanager.core.posturedetector.Classifiers.NaiveBayes;
import com.pdmanager.core.posturedetector.Core.Interfaces.IPostureEvaluator;

import java.util.Date;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Main Posture Evaluation Class
 */
public class PostureEvaluator implements IPostureEvaluator {
    private NaiveBayes naive;

    /**
     * Void Constructor
     */
    public PostureEvaluator() throws Exception {
        naive = new PostureNaiveBayes();
    }

    /**
     * Evaluate Features
     *
     * @param start    Start of sequence
     * @param end      End of sequence
     * @param features Feature Vector
     * @return
     */
    public com.pdmanager.core.posturedetector.Core.Signals.PostureEvaluation evaluate(Date start, Date end, com.pdmanager.core.posturedetector.Core.Signals.SignalFeature[] features) throws Exception {
        double[] ConfAction = new double[3];
        ConfAction[0] = 0.5;
        ConfAction[1] = 0.5;
        ConfAction[2] = 0.5;
        double[] confLw = naive.getOutput(features);
        int outcomeLw = maxSev(confLw);
        return new com.pdmanager.core.posturedetector.Core.Signals.PostureEvaluation(outcomeLw, 1.0, start, end);
    }

    /**
     * Calculate the max severity
     *
     * @param s The severities buffefr
     * @return Max Severity Index
     */
    private int maxSev(double[] s) throws Exception {
        double m = -1000;
        int i = 0;
        for (int j = 0; j < s.length; j++)
            if (s[j] > m) {
                m = s[j];
                i = j;
            }

        return i;
    }

}


