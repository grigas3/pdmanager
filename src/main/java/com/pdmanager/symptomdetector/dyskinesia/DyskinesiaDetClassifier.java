package com.pdmanager.symptomdetector.dyskinesia;

import com.pdmanager.posturedetector.Classifiers.NaiveBayes;

/**
 * Created by george on 6/6/2016.
 */
public class DyskinesiaDetClassifier extends NaiveBayes {

    public DyskinesiaDetClassifier() throws Exception {
        super(2, 2);
        init(
                new double[]{0.7F, 0.3F},
                new double[][]{new double[]{1.000000F, 0.000000F},
                        new double[]{0.000000F, 1.000000F},
                }, new double[][]{new double[]{0.25F, 0.74F}, new double[]{0.0321F, 0.1364F}
                }, new double[][]{new double[]{0.22F, 0.238F}, new double[]{0.0352F, 0.0828F}});


    }

    @Override
    protected int getIndex(String name) throws Exception {
        return 0;
    }


}
