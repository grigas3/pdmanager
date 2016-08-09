
package com.pdmanager.core.posturedetector.Core;


/*
 * This Code was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Code belong to Medlab (UOI).
 * */

/**
 * Base Math Class (Mainly a wrapper of Math for better portability)
 */
public class BaseMath {
    /**
     * MAX FLOAT
     */
    public static final float MAXFLOAT = Float.MAX_VALUE;
    /**
     * MIN FLOAT
     */
    public static final float MINFLOAT = Float.MIN_VALUE;
    /**
     * PI
     */
    public static final double PI = Math.PI;

    /**
     * Abs Wrapper
     *
     * @param x
     * @return
     */
    public static double absS(double x) throws Exception {
        return Math.abs(x);
    }

    /**
     * Cosine Wrapper
     *
     * @param x Theta
     * @return Cosine
     */
    public static double cosS(double x) throws Exception {
        return Math.cos(x);
    }

    /**
     * Power Wrapper
     *
     * @param x
     * @param n
     * @return
     */
    public static double powS(double x, int n) throws Exception {
        return Math.pow(x, n);
    }

    /**
     * Sine Wrapper
     *
     * @param x
     * @return
     */
    public static double sinS(double x) throws Exception {
        return Math.sin(x);
    }

    /**
     * Log Wrapper
     *
     * @param x
     * @param e
     * @return
     */
    public static double logS(double x, double e) throws Exception {
        return (Math.log(x) / Math.log(e));
    }

    /**
     * Ceiling Wrapper
     *
     * @param x
     * @return
     */
    public static double ceilingS(double x) throws Exception {
        return Math.ceil(x);
    }

    /**
     * Power
     *
     * @param x Base
     * @param n Exponent
     * @return Value
     */
    protected double pow(double x, int n) throws Exception {
        return Math.pow(x, n);
    }

    /**
     * Cosine
     *
     * @param x Theta
     * @return Cosine
     */
    protected double cos(double x) throws Exception {
        return Math.cos(x);
    }

    /**
     * Exponential
     *
     * @param x Input
     * @return Output
     */
    protected double exp(double x) throws Exception {
        return Math.exp(x);
    }

    /**
     * @param x
     * @return
     */
    protected double acos(double x) throws Exception {
        return Math.acos(x);
    }

    /**
     * Sine
     *
     * @param x Theta
     * @return Sine
     */
    protected double sin(double x) throws Exception {
        return Math.sin(x);
    }

    /**
     * Sqrt
     *
     * @param x Input
     * @return Output
     */
    protected double sqrt(double x) throws Exception {
        return Math.sqrt(x);
    }

    /**
     * Abs
     *
     * @param x
     * @return
     */
    protected double abs(double x) throws Exception {
        return Math.abs(x);
    }

    /**
     * Atan
     *
     * @param x X
     * @param y Y
     * @return Output
     */
    protected double atan2(double x, double y) throws Exception {
        return Math.atan2(x, y);
    }

    /**
     * Log
     *
     * @param x X
     * @return Log(x)
     */
    protected double log(double x) throws Exception {
        return Math.log10(x);
    }

    /**
     * Log 2
     *
     * @param x X
     * @return Log2(x)
     */
    protected double log2(double x) throws Exception {
        return (Math.log(x) / Math.log(2));
    }

}


