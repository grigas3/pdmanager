package com.pdmanager.communication;


import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.v2.DbxFiles;
import com.pdmanager.settings.RecordingSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Async task to upload a file to a directory
 */
public class UploadDropboxFileTask extends AsyncTask<File, Void, DbxFiles.FileMetadata> {

    private final Context mContext;
    private final DbxFiles mFilesClient;
    private Exception mException;
    private Callback mCallback;


    public UploadDropboxFileTask(Context context, DbxFiles filesClient, Callback callback) {
        mContext = context;
        mFilesClient = filesClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(DbxFiles.FileMetadata result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    private String GetDropBoxFolder() {
        RecordingSettings settings = RecordingSettings.GetRecordingSettings(mContext);
        if (settings != null) {

            return "/" + settings.getPatientID() + "/" + settings.getSessionFolder() + "/";


        } else {
            return "/tmp/";
        }


    }

    @Override
    protected DbxFiles.FileMetadata doInBackground(File... params) {

        DbxFiles.FileMetadata ret = null;
        File localFile = params[0];
//        File localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri));

        if (localFile != null) {
            // String remoteFolderPath = params[1];


            // Note - this is not ensuring the name is a valid dropbox file name
            String remoteFileName = GetDropBoxFolder() + localFile.getName();

            try {
                FileInputStream inputStream = new FileInputStream(localFile);
                try {


                    ret = mFilesClient.uploadBuilder(remoteFileName)
                            .mode(DbxFiles.WriteMode.overwrite)
                            .run(inputStream);


                } finally {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                mException = e;
            } catch (Exception e) {
                e.printStackTrace();
                mException = e;

            }
        }

        return ret;
    }

    public interface Callback {
        void onUploadComplete(DbxFiles.FileMetadata result);

        void onError(Exception e);
    }
}