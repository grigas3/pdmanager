package com.pdmanager.core.communication;


import android.content.Context;
import android.os.AsyncTask;

import com.dropbox.core.v2.DbxFiles;
import com.pdmanager.core.settings.RecordingSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Async task to upload a file to a directory
 */
public class UploadDropboxFilesTask extends AsyncTask<File[], Void, DbxFiles.FileMetadata[]> {

    private final Context mContext;
    private final DbxFiles mFilesClient;
    private Exception mException;
    private Callback mCallback;
    private String mFolder;

    public UploadDropboxFilesTask(Context context, DbxFiles filesClient, Callback callback) {
        mContext = context;
        mFilesClient = filesClient;
        mCallback = callback;


    }

    @Override
    protected void onPostExecute(DbxFiles.FileMetadata[] result) {
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
    protected DbxFiles.FileMetadata[] doInBackground(File[]... params) {

        DbxFiles.FileMetadata[] ret = null;
        File[] localFiles = params[0];
//        File localFile = UriHelpers.getFileForUri(mContext, Uri.parse(localUri));
        String dfolder = GetDropBoxFolder();
        if (localFiles != null) {


            ret = new DbxFiles.FileMetadata[localFiles.length];
            for (int i = 0; i < localFiles.length; i++) {
                File localFile = localFiles[i];

                // String remoteFolderPath = params[1];


                // Note - this is not ensuring the name is a valid dropbox file name
                String remoteFileName = dfolder + localFile.getName();

                try {
                    FileInputStream inputStream = new FileInputStream(localFile);
                    boolean success = false;
                    try {


                        ret[i] = mFilesClient.uploadBuilder(remoteFileName)
                                .mode(DbxFiles.WriteMode.overwrite)
                                .run(inputStream);

                        success = true;


                    } finally {
                        inputStream.close();
                        if (success) {

                            try {
                                localFile.delete();
                            } catch (Exception ex) {

                            }


                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mException = e;
                } catch (Exception e) {
                    e.printStackTrace();
                    mException = e;

                }
            }

        }
        return ret;
    }

    public interface Callback {
        void onUploadComplete(DbxFiles.FileMetadata[] ret);

        void onError(Exception e);
    }


}