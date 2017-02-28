package com.pdmanager.models;

import com.pdmanager.adapters.ObservationParams;

import java.util.List;

/**
 * Created by George on 6/19/2016.
 */
public class ObservationResult {

    public List<Observation> observations;
    public ObservationParams params;

    public long getmaxt() {

        long max = -10000;
        if (observations == null || observations.size() == 0)
            return 1;
        if (observations != null) {

            for (Observation o : observations) {

                if (o.getTimestamp() > max)
                    max = o.getTimestamp();

            }
        }

        return max;

    }

    public long getmint() {
        long min = Long.MAX_VALUE;
        if (observations == null || observations.size() == 0)
            return 0;

        if (observations != null) {


            for (Observation o : observations) {


                if (o.getTimestamp() < min)
                    min = o.getTimestamp();

            }
        }

        return min;

    }

    public double getmax() {

        double max = -10000;
        if (observations == null || observations.size() == 0)
            return 1;
        if (observations != null) {

            for (Observation o : observations) {

                if (o.getValue() > max)
                    max = o.getValue();

            }
        }

        return max;

    }

    public double getmin() {
        double min = 10000;

        if (observations == null || observations.size() == 0)
            return 0;
        if (observations != null) {


            for (Observation o : observations) {


                if (o.getValue() < min)
                    min = o.getValue();

            }
        }

        return min;

    }
}

