//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.Core.Signals;

import java.util.Date;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Posture Evaluation class
 */
public class PostureEvaluation {

    private int _Posture;
    private double _confidence;
    private Date _start;
    private Date _end;

    /**
     * @param posture
     * @param confidence
     * @param start
     * @param end
     */
    public PostureEvaluation(int posture, double confidence, Date start, Date end) throws Exception {
        this._Posture = posture;

        this._start = start;
        this._end = end;
        this._confidence = confidence;
    }

    /**
     * Posture
     */
    public int getPosture() throws Exception {
        return _Posture;
    }


    /**
     * Confidence
     */
    public double getConfidence() throws Exception {
        return _confidence;
    }

    /**
     * Start Time
     */
    public Date getStart() throws Exception {
        return _start;
    }

    /**
     * End Time
     */
    public Date getEnd() throws Exception {
        return _end;
    }

}


