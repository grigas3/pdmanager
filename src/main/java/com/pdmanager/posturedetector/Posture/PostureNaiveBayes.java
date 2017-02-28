//
// Translated by CS2J (http://www.cs2j.com): 11/5/2015 7:30:08 μμ
//

package com.pdmanager.posturedetector.Posture;

import com.pdmanager.posturedetector.Classifiers.NaiveBayes;

/*
 * This Type was developed by George Rigas
 * in 2015 for project AMI Health.
 * The copyrights of the Type belong to Medlab (UOI).
 * */

/**
 * Naive Bayes model for Tremor
 */
public class PostureNaiveBayes extends NaiveBayes {
    //Mean
    //Att1 42.8683 ,0.4228,  5.8623,  0.8455
    //Att2 0.6532,  1.6963 , 1.8303 , 1.9857
    //Cov
    //Att1 4.9886 , 0.4228 , 2.3809 , 0.5637
    //Att2 0.0869 , 0.2105,  0.3122,  0.1872

    /**
     * Constructor
     */
    public PostureNaiveBayes() throws Exception {
        super(5, 3);
        init(new double[]{1.0F / 3.0F, 1.0F / 3.0F, 1.0F / 3.0F}, new double[][]{new double[]{1, 0, 0}, new double[]{0, 1, 0}, new double[]{0, 0.0F, 1}}, new double[][]{new double[]{0.6501, 0.6696, -0.2805}, new double[]{0.4451, 0.4738, 0.8054}, new double[]{0.1653, 0.1349, 0.0935}, new double[]{-52.0693, -60.8404, 71.2187}, new double[]{22.8537, 14.6523, 23.3052}}, new double[][]{new double[]{0.4999, 0.3194, 0.2896}, new double[]{0.2023, 0.1843, 0.1787}, new double[]{0.1314, 0.0734, 0.0837}, new double[]{55.4692, 32.2543, 56.1925}, new double[]{31.0418, 14.9467, 31.54}});
    }

    /**
     * Get Index
     *
     * @param name
     * @return
     */
    protected int getIndex(String name) throws Exception {
        int index = Integer.valueOf(name);
        return index;
    }

    // var index = int.Parse(name.Split('_')[1]);
    protected double[] postProcessOutput(double[] p) throws Exception {
        return p;
    }

}


