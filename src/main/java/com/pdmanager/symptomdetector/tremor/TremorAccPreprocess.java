package com.pdmanager.symptomdetector.tremor;


import com.pdmanager.posturedetector.Core.Interfaces.ISignalPreprocessor;
import com.pdmanager.posturedetector.Core.Signals.NamedSignalCollection;
import com.pdmanager.posturedetector.Core.Signals.SignalCollection;
import com.pdmanager.posturedetector.SignalProcessing.Filters.FIRD;
import com.pdmanager.posturedetector.SignalProcessing.SignalDictionary;
import com.pdmanager.posturedetector.SignalProcessing.SignalProcess;

/**
 * Created by george on 3/6/2016.
 */
public class TremorAccPreprocess extends SignalProcess implements ISignalPreprocessor {


    private final Double[] numf = new Double[]{0.00168138185890348, 0.00178569886735348, 0.00200031215411487, 0.00233230916041547, 0.00278700594202078, 0.00336779831026805, 0.00407604462364871, 0.00491098293058890, 0.00586968454816950, 0.00694704549890134, 0.00813581653090907, 0.00942667172933180, 0.0108083150022778, 0.0122676230073607, 0.0137898223887340, 0.0153586985333309, 0.0169568324418030, 0.0185658617566539, 0.0201667615083817, 0.0217401397398165, 0.0232665428574801, 0.0247267653431641, 0.0261021583436533, 0.0273749316442833, 0.0285284436234633, 0.0295474739790433, 0.0304184743100781, 0.0311297920237906, 0.0316718635101968, 0.0320373730770261, 0.0322213747548369, 0.0322213747548369, 0.0320373730770261, 0.0316718635101968, 0.0311297920237906, 0.0304184743100781, 0.0295474739790433, 0.0285284436234633, 0.0273749316442833, 0.0261021583436533, 0.0247267653431641, 0.0232665428574801, 0.0217401397398165, 0.0201667615083817, 0.0185658617566539, 0.0169568324418030, 0.0153586985333309, 0.0137898223887340, 0.0122676230073607, 0.0108083150022778, 0.00942667172933180, 0.00813581653090907, 0.00694704549890134, 0.00586968454816950, 0.00491098293058890, 0.00407604462364871, 0.00336779831026805, 0.00278700594202078, 0.00233230916041547, 0.00200031215411487, 0.00178569886735348, 0.00168138185890348};
    private FIRD lpfirFilterTX = null;
    private FIRD lpfirFilterTY = null;
    private FIRD lpfirFilterTZ = null;

    public TremorAccPreprocess() throws Exception {

        try {


            lpfirFilterTX = new FIRD(numf);
            lpfirFilterTX.clear();
            lpfirFilterTY = new FIRD(numf);
            lpfirFilterTY.clear();
            lpfirFilterTZ = new FIRD(numf);
            lpfirFilterTZ.clear();
        } catch (Exception e) {


        }

    }

    private void filter0(SignalCollection source, NamedSignalCollection signals) throws Exception {
        int numofSignals = source.getSignals();
        int size = source.getSize();
        //Create the low pass filter
        SignalCollection bpSignal = signals.get___idx(SignalDictionary.TremorAccLowPass);
        if (bpSignal == null) {
            bpSignal = new SignalCollection(numofSignals, size);
            this.lpfirFilterTX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            this.lpfirFilterTY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            this.lpfirFilterTZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));
            signals.set___idx(SignalDictionary.TremorAccLowPass, bpSignal);
        } else {
            this.lpfirFilterTX.applyTo(source.get___idx(0), bpSignal.get___idx(0));
            this.lpfirFilterTY.applyTo(source.get___idx(1), bpSignal.get___idx(1));
            this.lpfirFilterTZ.applyTo(source.get___idx(2), bpSignal.get___idx(2));

        }
    }


    @Override
    public void process(SignalCollection source, NamedSignalCollection signals) throws Exception {
        SignalCollection bpSignal = signals.get___idx(SignalDictionary.OriginalAcc);
        if (bpSignal == null) {
            signals.set___idx(SignalDictionary.OriginalAcc, source);
        }

        int numofSignals = source.getSignals();
        int size = source.getSize();
        filter0(source, signals);
    }

    @Override
    public void reset() throws Exception {


    }
}
