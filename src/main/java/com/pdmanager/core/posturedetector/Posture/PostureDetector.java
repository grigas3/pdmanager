
package com.pdmanager.core.posturedetector.Posture;

import android.util.Log;

import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.PostureEvaluation;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalFeature;

public class PostureDetector {


    private final PostureSignalProcess preprocessor;
    private final PostureFeatureExtractor fextractor;
    private final PostureEvaluator evaluator;

    public PostureDetector(int bufferSize, double fs) throws Exception {

        preprocessor = new PostureSignalProcess();
        fextractor = new PostureFeatureExtractor(bufferSize, fs);
        evaluator = new PostureEvaluator();
    }

    public PostureEvaluation Process(SignalCollection data) {
        PostureEvaluation res = null;

        try {
            NamedSignalCollection signalCollection = new NamedSignalCollection();

            ///Signal Preprocessing
            preprocessor.process(data, signalCollection);


            //Feature Extraction

            fextractor.process(signalCollection);
            SignalFeature[] features = fextractor.getFeatures();
            //foreach (var c in features)
            //    Console.WriteLine(c.Value.ToString());

            res = evaluator.evaluate(data.getStart(), data.getEnd(), features);

        } catch (Exception ex) {


            Log.d("error", "ss", ex);
        }

        return res;

    }


}
