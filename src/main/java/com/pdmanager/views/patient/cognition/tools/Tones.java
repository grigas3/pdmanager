package com.pdmanager.views.patient.cognition.tools;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class Tones
{
    private int
        volume = ToneGenerator.MAX_VOLUME,
        duration = 500; // in millis
    private static Tones tones = null;
    private ToneGenerator tg;

    public static Tones getInstance()
    {
        if (tones==null) tones = new Tones();
        return tones;
    }

    private Tones() { tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume); }

    public void makeTone(int type, int millis) { tg.startTone(type, millis); }
    public void beep() { tg.startTone(ToneGenerator.TONE_DTMF_6, duration); }
    public void ackBeep() { tg.startTone(ToneGenerator.TONE_PROP_ACK, duration); }
    public void nackBeep() { tg.startTone(ToneGenerator.TONE_PROP_NACK, duration); }
    public void stopTone() { tg.stopTone(); }
    public void shutdown()
    {
        if (tg!=null)
        {
            tg.stopTone();
            tg.release();
            tg = null;
        }
        tones = null;
    }
}
