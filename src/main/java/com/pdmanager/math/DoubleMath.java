package com.pdmanager.math;

/**
 * Created by george on 14/11/2015.
 */


public final class DoubleMath {

    private static long multiplier = 1000000000;

    private DoubleMath() {
    }

    /*
     * This method returns a Value y such that rounding y DOWN (towards zero) gives the same result
     * as rounding x according to the specified mode.
     */
    public static long doubleToLong(double x) {


        // return Double.doubleToLongBits(x);


        return (long) (x * multiplier);

    }
}