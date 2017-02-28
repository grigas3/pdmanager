//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:07 μμ
//

package com.pdmanager.posturedetector.Core.Signals;

import java.util.HashMap;

/**
 * Named Signal  Collection
 */
public class NamedSignalCollection {
    private final HashMap<String, Boolean> signalsUpdated = new HashMap<String, Boolean>();
    private final HashMap<String, SignalCollection> signals = new HashMap<String, SignalCollection>();

    /**
     * Is signal contained in collection
     *
     * @param name Signal name
     * @return True/False
     */
    public boolean isSignalUpdate(String name) throws Exception {
        return signals.containsKey(name);
    }

    /**
     * Accessor to signals
     *
     * @param name Signal Name
     * @return Signal Collection
     */
    public SignalCollection get___idx(String name) throws Exception {
        if (signals.containsKey(name))
            return signals.get(name);
        else
            return null;
    }

    public void set___idx(String name, SignalCollection value) throws Exception {
        if (signals.containsKey(name))
            signals.put(name, value);
        else
            signals.put(name, value);
    }

    /**
     * Reset collection
     */
    public void reset() throws Exception {
        signals.clear();
    }

}


