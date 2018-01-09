package com.pdmanager.views.patient.cognition.cognitive.LondonTowers;

import com.pdmanager.views.patient.cognition.tools.RNG;

public class LondonTowersGame extends LondonTowersGraph {
    public int minMoves;
    public String sinit, starget;
    public TowerStacks init, target;
    private RNG rand;

    public LondonTowersGame() {
        rand = new RNG();
        reset();
    }

    public void reset() {
        int
                range = graphPositions.length - 1,
                i = rand.getIntInClosedRange(0, range),
                j = rand.getIntInClosedRangeAvoiding(0, range, i);
        sinit = graphPositions[i];
        starget = graphPositions[j];
        minMoves = getPathLenDijkstra(sinit, starget);
        init = new TowerStacks(gamePositions[i]);
        target = new TowerStacks(gamePositions[j]);
    }

    public boolean isResolved() {
        return init.equals(target);
    }
}