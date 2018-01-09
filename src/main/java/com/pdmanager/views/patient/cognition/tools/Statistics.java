package com.pdmanager.views.patient.cognition.tools;

import java.util.Arrays;

/**
 * Created by jcancela on 13/7/15.
 */
public class Statistics {
    double[] data;
    int size;

    public Statistics(double[] data) {
        this.data = data;
        size = data.length;

    }

    public void setSize(int mySize) {
        this.size = mySize;
    }

    public double getMean() {
        double sum = 0.0;
        for (double a : data)
            sum += a;
        if (size == 0) {
            return 0;
        } else {
            return sum / size;
        }

    }

    public double getVariance() {
        double mean = getMean();
        double temp = 0;
        for (double a : data)
            temp += (mean - a) * (mean - a);
        if (size == 0) {
            return 0;
        } else {
            return temp / size;
        }

    }

    public double getStdDev() {
        return Math.sqrt(getVariance());
    }

    public double median() {
        Arrays.sort(data);

        if (data.length % 2 == 0) {
            return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        } else {
            return data[data.length / 2];
        }
    }

    public double getMax() {
        double max = data[0];

        for (int i = 0; i < size; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }

        return max;
    }

    public double getMin() {
        double min = data[0];

        for (int i = 0; i < size; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }

        return min;
    }

    public double getSum() {
        double sum = 0;

        for (int i = 0; i < size; i++) {
            sum = sum + data[i];
        }

        return sum;
    }
}