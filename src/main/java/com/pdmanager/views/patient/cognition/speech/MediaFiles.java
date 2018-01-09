package com.pdmanager.views.patient.cognition.speech;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MediaFiles {

    private static final String
            audioExt = ".3gp",
            imageExt = ".png",
            dir = "/PD_manager/AudioFiles";
    //dir = "/Android/data/";

    /**
     * Deletes the file stored under the given path.
     *
     * @param path The target file's absolute path.
     */
    public static void deleteFile(String path) {
        if (path != null) {
            File toDel = new File(path);
            toDel.delete();
        }
    }

    private static boolean isStorageReady() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Creates a new image file (.png) named with the timestamp of this call
     * under the given dirName directory.
     *
     * @param dirName the parent directory for the new file.
     * @return The absolute path of the new file or null in case of error.
     */
    public static File createImageFile(String dirName) {
        File file = null;
        if (isStorageReady()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String date = dateFormat.format(Calendar.getInstance().getTime());

            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + dir);
            if (!folder.exists()) {
                try {
                    folder.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String
                    fileName = "" + date + imageExt,
                    dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + dir + dirName,
                    path = dirPath + "/" + fileName;

            file = new File(path);
            File parent = file.getParentFile();
            if (!parent.exists()) parent.mkdirs();
        }
        return file;
    }

    /**
     * Creates a new audio file (.3gp) named with the timestamp of this call
     * under the given dirName directory.
     *
     * @param dirName the parent directory for the new file.
     * @return The absolute path of the new file or null in case of error.
     */
    public static String createNewAudioFile(String dirName) {
        String path = null;
        if (isStorageReady()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            String date = dateFormat.format(Calendar.getInstance().getTime());
            String
                    fileName = "" + date + audioExt,
                    dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + dir + dirName;
            path = dirPath + "/" + fileName;
            File file = new File(path).getParentFile();
            if (!file.exists()) file.mkdirs();
        }
        return path;
    }

}
