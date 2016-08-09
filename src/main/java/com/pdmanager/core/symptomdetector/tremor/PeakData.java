
/**
 * Created by george on 2/6/2016.
 */

package com.pdmanager.core.symptomdetector.tremor;


/**
 * Peak Data
 */
public class PeakData {
    private long _x;
    private double _y;

    /**
     * Constructor
     *
     * @param px Location
     * @param py Value
     */
    public PeakData(long px, double py) {
        _x = px;
        _y = py;
    }

    /**
     * Peak Location
     */
    public long getX() {
        return _x;
    }

    /**
     * Peak Value
     */
    public double getY() {
        return _y;
    }

}


