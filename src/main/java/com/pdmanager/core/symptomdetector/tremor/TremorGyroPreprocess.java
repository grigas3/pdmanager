package com.pdmanager.core.symptomdetector.tremor;


import com.pdmanager.core.posturedetector.Core.Interfaces.ISignalPreprocessor;
import com.pdmanager.core.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.core.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.core.posturedetector.SignalProcessing.Filters.FIRD;
import com.pdmanager.core.posturedetector.SignalProcessing.SignalDictionary;
import com.pdmanager.core.posturedetector.SignalProcessing.SignalProcess;

/**
 * Created by george on 3/6/2016.
 */
public class TremorGyroPreprocess extends SignalProcess implements ISignalPreprocessor {

    private final Double[] numf = new Double[]{-0.000577717752484010, -0.000721063831307715, -0.000904745304397467, -0.00113757791923942, -0.00141865013073978, -0.00173535770845033, -0.00206225563481072, -0.00236087855739894, -0.00258061676719985, -0.00266066145811824, -0.00253295584471773, -0.00212601295113059, -0.00136939194700063, -0.000198567862367828, 0.00144011128354880, 0.00358470713451200, 0.00625354025499387, 0.00944191026900345, 0.0131199473696245, 0.0172317930900549, 0.0216962305776401, 0.0264087967745513, 0.0312453165250910, 0.0360667077466687, 0.0407248234603464, 0.0450690262998874, 0.0489531389207270, 0.0522423831843338, 0.0548199143678989, 0.0565925746443484, 0.0574955317661325, 0.0574955317661325, 0.0565925746443484, 0.0548199143678989, 0.0522423831843338, 0.0489531389207270, 0.0450690262998874, 0.0407248234603464, 0.0360667077466687, 0.0312453165250910, 0.0264087967745513, 0.0216962305776401, 0.0172317930900549, 0.0131199473696245, 0.00944191026900345, 0.00625354025499387, 0.00358470713451200, 0.00144011128354880, -0.000198567862367828, -0.00136939194700063, -0.00212601295113059, -0.00253295584471773, -0.00266066145811824, -0.00258061676719985, -0.00236087855739894, -0.00206225563481072, -0.00173535770845033, -0.00141865013073978, -0.00113757791923942, -0.000904745304397467, -0.000721063831307715, -0.000577717752484010};
    private final Double[] numf3 = new Double[]{-0.000602459334496390, -0.000520700962335730, -0.000430962195283354, -0.000307121115790260, -0.000117835828113695, 0.000170505457339200, 0.000590244390479560, 0.00116854629219906, 0.00192338721460169, 0.00285971050371420, 0.00396602478431984, 0.00521168848359823, 0.00654507649480568, 0.00789275285941572, 0.00915968005662886, 0.0102303802975983, 0.0109708238289486, 0.0112306437222793, 0.0108450422462853, 0.00963540839458683, 0.00740709602297441, 0.00394176610781463, -0.00102040733840635, -0.00781797093480556, -0.0169451516006107, -0.0292178366049659, -0.0461650048056063, -0.0710934374739467, -0.112706291110851, -0.203185976532873, -0.633220904331848, 0.633220904331848, 0.203185976532873, 0.112706291110851, 0.0710934374739467, 0.0461650048056063, 0.0292178366049659, 0.0169451516006107, 0.00781797093480556, 0.00102040733840635, -0.00394176610781463, -0.00740709602297441, -0.00963540839458683, -0.0108450422462853, -0.0112306437222793, -0.0109708238289486, -0.0102303802975983, -0.00915968005662886, -0.00789275285941572, -0.00654507649480568, -0.00521168848359823, -0.00396602478431984, -0.00285971050371420, -0.00192338721460169, -0.00116854629219906, -0.000590244390479560, -0.000170505457339200, 0.000117835828113695, 0.000307121115790260, 0.000430962195283354, 0.000520700962335730, 0.000602459334496390};
    private FIRD hpfirFilterTX = null, hpfirFilterTY = null, hpfirFilterTZ = null;
    private FIRD lpfirFilterTX = null, lpfirFilterTY = null, lpfirFilterTZ = null;

    public TremorGyroPreprocess() throws Exception {

        try {

            hpfirFilterTX = new FIRD(numf3);
            hpfirFilterTY = new FIRD(numf3);
            hpfirFilterTZ = new FIRD(numf3);
            lpfirFilterTX = new FIRD(numf);
            lpfirFilterTY = new FIRD(numf);
            lpfirFilterTZ = new FIRD(numf);

            lpfirFilterTX.clear();
            lpfirFilterTY.clear();
            lpfirFilterTZ.clear();
            hpfirFilterTX.clear();
            hpfirFilterTY.clear();
            hpfirFilterTZ.clear();
        } catch (Exception e) {


        }

    }

    private void filter1(SignalCollection source, NamedSignalCollection signals) throws Exception {
        int numofSignals = source.getSignals();
        int size = source.getSize();
        //Create the low pass filter
        SignalCollection bpSignal = signals.get___idx(SignalDictionary.TremorGyroLowPass);
        if (bpSignal == null) {
            bpSignal = new SignalCollection(numofSignals, size);

            this.lpfirFilterTX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            this.lpfirFilterTY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            this.lpfirFilterTZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));

            signals.set___idx(SignalDictionary.TremorGyroLowPass, bpSignal);
        } else {
            this.lpfirFilterTX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            this.lpfirFilterTY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            this.lpfirFilterTZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));

        }
    }

    private void filter0(SignalCollection source, NamedSignalCollection signals) throws Exception {
        int numofSignals = source.getSignals();
        int size = source.getSize();
        //Create the high pass filter
        SignalCollection bpSignal = signals.get___idx(SignalDictionary.TremorGyroHighPass);
        if (bpSignal == null) {
            bpSignal = new SignalCollection(numofSignals, size);


            this.hpfirFilterTX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            this.hpfirFilterTY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            this.hpfirFilterTZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));
            signals.set___idx(SignalDictionary.TremorGyroHighPass, bpSignal);
        } else {


            this.hpfirFilterTX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            this.hpfirFilterTY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            this.hpfirFilterTZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));
        }
    }


    @Override
    public void process(SignalCollection source, NamedSignalCollection signals) throws Exception {
        SignalCollection bpSignal = signals.get___idx(SignalDictionary.OriginalGyro);
        if (bpSignal == null) {
            signals.set___idx(SignalDictionary.OriginalGyro, source);
        }

        int numofSignals = source.getSignals();
        int size = source.getSize();
        filter0(source, signals);
        filter1(source, signals);
    }

    @Override
    public void reset() throws Exception {


    }
}
