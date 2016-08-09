//
// Translated by Medlab: 23/1/2015 10:45:14 πμ
//

package com.pdmanager.core.posturedetector.Classifiers;

import com.pdmanager.core.posturedetector.Core.BaseMath;
import com.pdmanager.core.posturedetector.Core.Logging;
import com.pdmanager.core.posturedetector.Core.Signals.SignalFeature;

/*
 * This Code was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Code belong to Medlab (UOI).
 * */

/**
 * Abstract Naive Bayes class
 */
public abstract class NaiveBayes extends BaseMath {
    private final int _nclasses;
    private final int _nattributes;
    private final double[] predictions;
    /**
     * Prior matrix
     */
    protected double[] prior;
    /**
     * Confusion Matrix
     */
    protected double[][] conf;
    private double[] fvector;
    private double[][] Mean;
    private double[][] Std;

    /**
     * Constructor
     *
     * @param na
     * @param nc
     */
    public NaiveBayes(int na, int nc) throws Exception {
        _nattributes = na;
        _nclasses = nc;
        predictions = new double[nc];
    }

    /**
     * Number of classes
     */
    protected int getNClasses() throws Exception {
        return _nclasses;
    }

    /**
     * Number of Attributes
     */
    protected int getNAttributes() throws Exception {
        return _nattributes;
    }

    /**
     * Initializer
     *
     * @param ppriors    Priors
     * @param pconfusion Confusion matrix
     * @param means      Means
     * @param covs       Covs
     */
    protected void init(double[] ppriors, double[][] pconfusion, double[][] means, double[][] covs) throws Exception {
        this.prior = ppriors;
        this.conf = pconfusion;
        fvector = new double[_nattributes];
        Mean = new double[getNAttributes()][];
        Std = new double[getNAttributes()][];
        try {
            for (int k = 0; k < getNAttributes(); k++) {
                //String s = meansS;
                Mean[k] = new double[_nclasses];
                for (int i = 0; i < _nclasses; i++)
                    Mean[k][i] = means[k][i];
            }
            for (int k = 0; k < getNAttributes(); k++) {
                Std[k] = new double[_nclasses];
                for (int i = 0; i < _nclasses; i++)
                    Std[k][i] = covs[k][i];
            }
        } catch (Exception e) {
            Logging.writeMethodExecutionIntoLog(e.getMessage(), "Error reading Naive Bayes Model");
        }

    }

    //   MessageBox.Show(" Error reading Naive Bayes Model");

    /**
     * Get Index of attribute given its name
     *
     * @param name Attribute name
     * @return Index
     */
    protected abstract int getIndex(String name) throws Exception;

    /**
     * Get Output
     *
     * @param sf
     * @return class probabilities
     */
    public double[] getOutput(SignalFeature[] sf) throws Exception {
        for (int i = 0; i < _nattributes; i++)
            fvector[getIndex(sf[i].getName())] = sf[i].getValue();
        return getOutput(fvector);
    }


    /**
     * Get Output
     *
     * @param x feature vector
     * @return class probabilities
     */
    public double[] getOutput(double[] x) throws Exception {
        //Need probably an optimization
        // double[] p = new double[getNClasses()];
        for (int i = 0; i < getNClasses(); i++) {
            predictions[i] = 1;
            for (int j = 0; j < getNAttributes(); j++)
                predictions[i] = predictions[i] * (gaussianPDF(x[j], Mean[j][i], Std[j][i]) + 1.0e-12);
        }
        double s1 = 0;
        for (int i = 0; i < getNClasses(); i++)
            s1 += (predictions[i]);
        for (int i = 0; i < getNClasses(); i++)
            predictions[i] = (predictions[i]) / s1;
        return predictions;
    }


    public int getMaxClass(double[] x) throws Exception {
        //Need probably an optimization

        for (int i = 0; i < getNClasses(); i++) {
            predictions[i] = 1;
            for (int j = 0; j < getNAttributes(); j++)
                predictions[i] = predictions[i] * (gaussianPDF(x[j], Mean[j][i], Std[j][i]) + 1.0e-12);
        }
        double s1 = 0;
        for (int i = 0; i < getNClasses(); i++)
            s1 += (predictions[i]);
        double maxP = 0;
        int maxI = 0;
        for (int i = 0; i < getNClasses(); i++) {
            if (predictions[i] > maxP) {
                maxI = i;
                maxP = predictions[i];
            }

        }

        return maxI;

    }

    /**
     * Gaussian PDF
     *
     * @param x X
     * @param m M
     * @param s S
     * @return Prob
     */
    private double gaussianPDF(double x, double m, double s) throws Exception {
        return ((1.0 / (sqrt(2 * Math.PI) * s)) * exp(-0.5 * (x - m) * (x - m) / (s * s)));
    }

}


