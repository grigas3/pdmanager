package com.pdmanager.core.symptomdetector.dyskinesia;

import com.pdmanager.core.posturedetector.Classifiers.NaiveBayes;

/**
 * Created by george on 6/6/2016.
 */
public class DyskinesiaClassifier extends NaiveBayes {

    public DyskinesiaClassifier() throws Exception {
        super(1, 3);
        init(
                new double[]{0.33F, 0.33F, 0.33F},
                new double[][]{new double[]{1.000000F, 0.000000F, 0.000000F},
                        new double[]{0.000000F, 1.000000F, 0.000000F},
                        new double[]{0.000000F, 0.000000F, 1.000000F},

                }, new double[][]{new double[]{0.0202F, 0.0622F, 0.1735F}
                }, new double[][]{new double[]{0.0205F, 0.0350F, 0.0751F}});


    }

    @Override
    protected int getIndex(String name) throws Exception {
        return 0;
    }


}
