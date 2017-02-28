package com.pdmanager.settings;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import com.oovoo.sdk.api.LoggerListener.LogLevel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class VideoSettings extends Hashtable<String, String> {

    private static final String TOKEN = "MDAxMDAxAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIpWAzdb6VuXFZ6da%2B%2BNfHpnNrAKtiTNi9q8tB0zF0NdysT3wxqFClN8sj8GPkFedtMEmqIK01yFpvaO4FHT5isd2%2FjEbccmFv9BgJYch6m%2BlzAeglFeKUNZ%2BtutOODlTwiIoY7qQkYi86Z62kWbsB"; // Put your application token here
    public static final String Token = "token";
    public static final String Username = "username";
    public static final String IsDoctor = "false";
    public static final String ResolutionLevel = "resolution_level";
    public static final String AvsSessionId = "avs_session_id";
    public static final String RandomAvsSessionId = "random_avs_session_id";
    public static final String AvsSessionDisplayName = "avs_session_display_name";
    public static final String LogLevelKey = "log_level_key";
    public static final String VideoModeKey = "video_mode_key";
    public static final String VideoOrientationLockKey = "video_orientation_lock";
    public static final String VideoOrientationAnimKey = "video_orientation_anim";
    public static final String UseCustomRender = "use_custom_render";
    public static final String SecurityState = "security_state";
    private static final long serialVersionUID = 1L;
    public static final String TAG = "VideoSettings";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PREVIEW_ID = "";
    private Context appcontext = null;

    public VideoSettings(Context appcontext) {
        this.appcontext = appcontext;
        load();

        if (get(VideoSettings.Token) == null) {
            put(VideoSettings.Token, TOKEN);
        }

        if (get(VideoSettings.LogLevelKey) == null) {
            put(VideoSettings.LogLevelKey, LogLevel.Debug.toString());
        }

        if (get(VideoSettings.VideoOrientationAnimKey) == null) {
            put(VideoSettings.VideoOrientationAnimKey, Boolean.toString(true));
        }
    }

    public void load() {
        try {



            FileInputStream stream = appcontext.openFileInput("VideoSettings");
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(stream)));
            // String val = reader.toString();
            reader.beginObject();

            while (reader.hasNext()) {
                String key = reader.nextName();
                String value = reader.nextString();
                this.put(key, value);
                Log.d(TAG, "Settings [" + key + " = " + value + "]");
            }

            reader.endObject();

            reader.close();
            stream.close();

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public void save() {
        try {
            try {
                appcontext.deleteFile("VideoSettings");
            } catch (Exception err) {
            }

            FileOutputStream stream = appcontext.openFileOutput("VideoSettings", Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(stream)));
            writer.setIndent("  ");
            writer.beginObject();
            Enumeration<String> keys = this.keys();
            while (keys.hasMoreElements()) {
                String value = keys.nextElement();
                writer.name(value).value(get(value));
            }
            writer.endObject();
            stream.flush();
            writer.close();
            stream.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
