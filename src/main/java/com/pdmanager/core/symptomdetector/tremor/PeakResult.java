package com.pdmanager.core.symptomdetector.tremor;

/**
 * Created by george on 3/6/2016.
 */

import java.util.List;

/**
 * Peak Result class
 */
public class PeakResult {
    /**
     * Peaks
     */
    private final List<PeakData> __Peaks;
    /**
     * Valleys
     */
    private final List<PeakData> __Valleys;

   /* public void setPeaks(List<PeakData> Value) {
        __Peaks = Value;
    }
    */

    public PeakResult(List<PeakData> peaks, List<PeakData> valleys) {

        __Peaks = peaks;
        __Valleys = valleys;


    }

    public List<PeakData> getPeaks() {
        return __Peaks;
    }

    /*public void setValleys(List<PeakData> Value) {
        __Valleys = Value;
    }
    */

    public List<PeakData> getValleys() {
        return __Valleys;
    }

    /**
     * Dispose Results
     */
    public void dispose() throws Exception {
        if (this.getPeaks() != null)
            this.getPeaks().clear();

        if (this.getValleys() != null)
            this.getValleys().clear();

    }

}


