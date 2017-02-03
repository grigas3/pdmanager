//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.core.posturedetector.Core.Signals;

import java.util.Date;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Collection of signals
 */
public class SignalCollection {
    private final int size;
    private final int signals;
    private final SignalBuffer[] buffers;
    private Date _start;
    private Date _end;

    /**
     * Constructor
     *
     * @param s
     * @param n
     */
    public SignalCollection(int s, int n) throws Exception {
        signals = s;
        size = n;
        buffers = new SignalBuffer[signals];
        for (int i = 0; i < signals; i++) {
            buffers[i] = new SignalBuffer(size);
        }
    }

    /**
     * Constructor
     *
     * @param s
     * @param n
     * @param st
     * @param et
     */
    public SignalCollection(int s, int n, Date st, Date et) throws Exception {
        signals = s;
        size = n;
        _start = st;
        _end = et;
        buffers = new SignalBuffer[signals];
        for (int i = 0; i < signals; i++) {
            buffers[i] = new SignalBuffer(size);
        }
    }

    public static SignalCollection sSubtract(SignalCollection source, SignalCollection dest) throws Exception {
        int signals = source.getSignals();
        int size = source.getSize();
        SignalCollection res = new SignalCollection(signals, size);
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < size; j++)
                res.get___idx(i).set___idx(j, source.get___idx(i).get___idx(j) - dest.get___idx(i).get___idx(j));
        return res;
    }

    public static SignalCollection sAdd(SignalCollection source, SignalCollection dest) throws Exception {
        int signals = source.getSignals();
        int size = source.getSize();
        SignalCollection res = new SignalCollection(signals, size);
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < size; j++)
                res.get___idx(i).set___idx(j, source.get___idx(i).get___idx(j) + dest.get___idx(i).get___idx(j));
        return res;
    }

    /**
     * Start
     */
    public Date getStart() {
        return _start;
    }

    public void setStart(Date value) {
        _start = value;
    }

    /**
     * End
     */
    public Date getEnd() {
        return _end;
    }

    public void setEnd(Date value) {
        _end = value;
    }

    /**
     * Size
     */
    public int getSize() {
        return size;
    }

    /**
     * Signals
     */
    public int getSignals() {
        return signals;
    }

    /**
     * Accessor
     *
     * @param index
     * @return
     */
    public SignalBuffer get___idx(int index) throws Exception {
        return buffers[index];
    }

    public void set___idx(int index, SignalBuffer value) throws Exception {
        buffers[index] = value;
    }


    public void setValue(int signal, int i, double value) throws Exception {

        buffers[signal].set___idx(i, value);

    }


    /**
     * Copy from other collection
     *
     * @param source
     */
    public void copyFrom(SignalCollection source) throws Exception {
        this._start = source._start;
        this._end = source._end;
        for (int i = 0; i < signals; i++)
            this.buffers[i].copyFrom(source.get___idx(i));
    }

    /**
     * Total difference
     *
     * @return
     */
    public double tCdiff() throws Exception {
        double v = 0;
        for (int i = 0; i < signals; i++)
            v += buffers[i].cdiff();
        return v;
    }

    /**
     * Set Window
     *
     * @param offset
     * @param windowLength
     * @param window
     */
    public void setWindow(int offset, int windowLength, SignalCollection window) throws Exception {
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < windowLength; j++)
                window.get___idx(i).set___idx(j, buffers[i].get___idx(offset + j));
    }

    /**
     * Get Window
     *
     * @param offset
     * @param windowLength
     * @return
     */
    public SignalCollection getWindow(int offset, int windowLength) throws Exception {
        SignalCollection window = new SignalCollection(signals, windowLength);
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < windowLength; j++)
                window.get___idx(i).set___idx(j, buffers[i].get___idx(offset + j));
        return window;
    }

    /**
     * Calculate Signal Energy
     *
     * @return Energy
     */
    public double energy() throws Exception {
        double s = 0;
        for (int i = 0; i < signals; i++)
            s += buffers[i].sum_energy();

        return s / (signals * size);
    }


    public double sum_energy() throws Exception {
        double s = 0;
        for (int i = 0; i < signals; i++) {


            s += buffers[i].sum_energy();

        }

        return s;
    }

    public double sum_diff_energy() throws Exception {
        double s = 0;
        for (int i = 0; i < signals; i++) {
            s += buffers[i].sum_diff_energy();
        }

        return s;
    }


    public void add(SignalCollection source) throws Exception {
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < size; j++)
                this.get___idx(i).set___idx(j, this.get___idx(i).get___idx(j) - source.get___idx(i).get___idx(j));
    }

    public void subtract(SignalCollection source) throws Exception {
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < size; j++)
                this.get___idx(i).set___idx(j, this.get___idx(i).get___idx(j) - source.get___idx(i).get___idx(j));
    }

    public void subtract(SignalCollection source, SignalCollection dest) throws Exception {
        for (int i = 0; i < signals; i++)
            for (int j = 0; j < size; j++)
                this.get___idx(i).set___idx(j, source.get___idx(i).get___idx(j) - dest.get___idx(i).get___idx(j));
    }

}


