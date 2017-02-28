//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.Core.Signals;

import com.pdmanager.posturedetector.Core.Interfaces.ISignalFeature;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Signal  Window Feature
 */
public class SignalFeature implements ISignalFeature {
    private final String _name;
    private double _value;

    /**
     * Constructor
     *
     * @param n Name
     * @param v Value
     */
    public SignalFeature(String n, double v) throws Exception {
        this._name = n;
        this._value = v;
    }

    /**
     * Feature Name
     */
    public String getName() throws Exception {
        return _name;
    }

    /**
     * Feature Value
     */
    public double getValue() {
        return _value;
    }

    public void setValue(double v) {
        _value = v;
    }
}



