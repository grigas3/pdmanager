package com.pdmanager.views.patient.cognition.cognitive.LondonTowers;

public class TowerStacks {
    public PS p0, p1, p2;

    public TowerStacks(TowerStacks ts) {
        p0 = new PS(ts.p0.bottom, ts.p0.middle, ts.p0.top);
        p1 = new PS(ts.p1.bottom, ts.p1.middle, ts.p1.top);
        p2 = new PS(ts.p2.bottom, ts.p2.middle, ts.p2.top);
    }

    public TowerStacks(PS p0, PS p1, PS p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    public boolean equals(TowerStacks lts) {
        return (p0.isEqualTo(lts.p0) && p1.isEqualTo(lts.p1) && p2.isEqualTo(lts.p2));
    }

}
