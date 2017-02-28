//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.Core.Interfaces;


/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Signal Feature Interface
 */
public interface ISignalFeature {
    /**
     * Feature Name
     */
    String getName() throws Exception;

    /**
     * FValue
     */
    double getValue() throws Exception;

}


