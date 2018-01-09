package com.pdmanager.views.patient.cognition.tools;

import java.util.Random;

public class RNG {
    private Random random;

    public RNG() {
        random = new Random();
    }

    public boolean getBoolean() {
        return random.nextBoolean();
    }

    public int getInt() {
        return random.nextInt();
    }

    public int getIntInClosedRange(int n, int m) {
        return (n + random.nextInt(m - n + 1));
    }

    public int getIntInClosedRangeAvoiding(int n, int m, int avoid) {
        int res = getIntInClosedRange(n, m);
        if (res != avoid) return res;
        else return getIntInClosedRangeAvoiding(n, m, avoid);
    }
}
