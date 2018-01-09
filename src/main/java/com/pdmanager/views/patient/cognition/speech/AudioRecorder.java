package com.pdmanager.views.patient.cognition.speech;


import android.content.Context;
import android.media.MediaRecorder;

/**
 * Records audio from the microphone.
 * Requires the following permissions in the manifest file:
 * <uses-permission android:name="android.permission.RECORD_AUDIO"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 */
public class AudioRecorder {

    private static AudioRecorder instance = null;
    private MediaRecorder mr;
    private String
            storageDir = null,
            path = null;
    private boolean isRecording = false;

    private AudioRecorder(String dirName) {
        mr = new MediaRecorder();
        storageDir = dirName;
    }

    private AudioRecorder(Context ctx) {
        mr = new MediaRecorder();
        storageDir = ctx.getPackageName();
    }

    /**
     * Instances the AudioRecorder singleton.
     *
     * @param dirName Directory name to store the file, recommended the name of the app.
     *                The recordings will be stored under a directory referenced by the directory name.
     */
    public static AudioRecorder getInstance(String dirName) {
        if (instance == null) instance = new AudioRecorder(dirName);
        return instance;
    }

    /**
     * Instances the AudioRecorder singleton.
     *
     * @param ctx The context of the app.
     *            The recordings will be stored under a directory referenced by the app name.
     */
    public static AudioRecorder getInstance(Context ctx) {
        if (instance == null) instance = new AudioRecorder(ctx);
        return instance;
    }

    /**
     * Starts a voice capture from the device's micro.
     * Creates a file under the external storage directory and the directory
     * name given in the constructor. The name of the file is the timestamp (system ms)
     * when this method was called. The absolute path of the file is retrieved when
     * stopRecording is called.
     * If this call exists with error, it is probably due to not having mounted an SD card or
     * because the AudioRecorder is already recording and for a second record a call to
     * stopRecording has to be made.
     *
     * @return The absolute path of the file being recorded or to be recorded
     * or <code>null</code> in case of error.
     */
    public String startRecording() {
        if (isRecording) return path;
        else {
            // Configure the recorder. Don't put these lines on the class constructor.
            // These settings must be set before EACH recording.
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);
            mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            path = MediaFiles.createNewAudioFile(storageDir);
            if (path != null) {
                try {
                    mr.setOutputFile(path);
                    mr.prepare();
                } catch (Exception e) {
                    return null;
                }
                mr.start();
                isRecording = true;
            }
            return path;
        }
    }

    /**
     * Stops the current recording. After that, another audio record can be performed.
     *
     * @return The absolute path of the file being recorded or to be recorded
     * or <code>null</code> in case of error.
     */
    public String stopRecording() {
        isRecording = false;
        try {
            mr.stop();
            mr.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Frees the Audio recorder resources.
     */
    public void release() {
        mr.release();
    }

    /**
     * Tells if the AudioRecorder engine is currently recording.
     */
    public boolean isRecording() {
        return isRecording;
    }


} // AudioRecorder

