//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.Evaluator;


/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Base Evaluator Class
 */
public abstract class BaseEvaluator {
    /**
     * Buffer size
     */
    protected final int bufferSize;
    /**
     * Frequency
     */
    protected final double fs;

    /**
     * Constructor
     *
     * @param pbufferSize Buffer Size
     * @param pfs         Frequency
     */
    public BaseEvaluator(int pbufferSize, double pfs) throws Exception {
        this.bufferSize = pbufferSize;
        this.fs = pfs;
    }

    /**
     * Abstract method Process
     */
    public abstract void process() throws Exception;

}


