package com.pdmanager.views.patient.cognition.tools;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.util.Locale;

public class Speak implements OnInitListener
{

	private String utteranceID = getClass().getName();
	private TextToSpeech tts = null;
	private static Speak sp = null;
	private static boolean enabled = false;
	private static String toSpeak = null;
	private static final float
		pitch = 1.0f,
		speed = 1.0f;
	
	
	public static Speak getInstance(Context ctx)
	{
		if (sp==null) sp = new Speak(ctx);
		return sp;
	}
	
	private Speak(Context ctx)
	{
		tts = new TextToSpeech(ctx.getApplicationContext(), this);
		tts.setPitch(pitch);
		tts.setSpeechRate(speed);
	}
	
	public void shutdown()
	{
		sp = null;
		enabled = false;
		if (tts!=null)
		{
			tts.stop();
			tts.shutdown();
		}
	}

	@SuppressWarnings("deprecation")
	public void speakAdd(String s)
	{
		if (s!=null)
		{
			if (enabled)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{ tts.speak(s, TextToSpeech.QUEUE_ADD, null, utteranceID); }
				else tts.speak(s, TextToSpeech.QUEUE_ADD, null);
			}
			else toSpeak = s;
		}
	}

	@SuppressWarnings("deprecation")
	public void speakFlush(String s)
	{
		if (s!=null)
		{
			if (enabled)
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{ tts.speak(s, TextToSpeech.QUEUE_FLUSH, null, utteranceID); }
				else tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
			}
			else toSpeak = s;
		}
	}

	public void silence() { speakFlush(""); }

	@Override
	public void onInit(int status)
	{
		enabled = true;
		tts.setLanguage(Locale.getDefault());
		if (toSpeak!=null) speakAdd(toSpeak);
	}
}
