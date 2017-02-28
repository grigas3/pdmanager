package com.pdmanager.views.patient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dropbox.core.android.Auth;
import com.pdmanager.R;
import com.pdmanager.communication.DropboxClient;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.FragmentListener;

import java.io.File;
import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * interface.
 */
public class FilesFragment extends BasePDFragment implements FragmentListener {

    private static String TAG = "LOADER";
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter<String> itemsAdapter;
    private int LOADER_ID = 1;
    private Object stateChanged = new Object();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        // Set the adapter


        //  displayListView(view);
        ReadFiles(view);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onFragmentSelected() {

    }

    private void ReadFiles(View view) {

        ListView listView = (ListView) view.findViewById(R.id.filesList);

        RefreshFiles();
        itemsAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, items);

        listView.setAdapter(itemsAdapter);

    }


    private void RefreshFiles() {

        try {

            items.clear();
            String recfolder = "PDManager";
            File pathToExternalStorage = Environment.getExternalStorageDirectory();
            File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/" + recfolder + "/");
            File[] dirFiles = appDirectory.listFiles();

            if (dirFiles.length != 0) {
                // loops through the array of files, outputing the name to console
                for (int ii = 0; ii < dirFiles.length; ii++) {
                    String fileOutput = dirFiles[ii].getName();
                    items.add(fileOutput);
                }
            }

            itemsAdapter.notifyDataSetChanged();

        } catch (Exception ex) {


        }


    }

    // And later in some initialization function:


    private void DoSyncFiles(String accessToken) {


        try {
            String recfolder = "PDManager";

            File pathToExternalStorage = Environment.getExternalStorageDirectory();
            //to this path add a new directory path and create new App dir (InstroList) in /documents Dir


            File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/" + recfolder + "/");

// lists all the files into an array
            File[] dirFiles = appDirectory.listFiles();


            ArrayList<String> items = new ArrayList<String>();

            uploadFiles(dirFiles);


        } catch (Exception ex) {


        }


    }

    private void ClearFiles() {


        try {
            String recfolder = "PDManager";

            File pathToExternalStorage = Environment.getExternalStorageDirectory();
            //to this path add a new directory path and create new App dir (InstroList) in /documents Dir


            File appDirectory = new File(pathToExternalStorage.getAbsolutePath() + "/" + recfolder + "/");

// lists all the files into an array
            File[] dirFiles = appDirectory.listFiles();


            ArrayList<String> items = new ArrayList<String>();
            if (dirFiles.length != 0) {
                for (int ii = 0; ii < dirFiles.length; ii++) {


                    File file = dirFiles[ii];

                    try {


                        file.delete();
                    } catch (Exception ex) {

                        Log.d(TAG, "Error Deleting files");

                    }
                }

            }

        } catch (Exception ex) {


        }


    }

    private void uploadFile(File file) {


    }

    private void uploadFiles(File[] files) {


        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();

        RecordingSettings settings = getSettings();
        String folder = "/" + settings.getPatientID() + "/" + settings.getSessionFolder() + "/";
      /*  new UploadDropboxFilesTask(getContext(),folder, DropboxClient.files(), new UploadDropboxFilesTask.Callback() {
            @Override
            public void onUploadComplete(DbxFiles.FileMetadata[] result) {
                dialog.dismiss();


                RefreshFiles();
                makeToast("Upload completed successfully");


                // Reload the folder
                //  loadData();
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                makeToast("An error has occurred");


                RefreshFiles();
            }
        }).execute(files);
        */
    }

    private void SyncFiles() {


        if (hasToken()) {
            String accessToken = getSettings().getToken();

            DoSyncFiles(accessToken);
        } else {
            Auth.startOAuth2Authentication(getContext(), getString(R.string.app_key));
        }


       /* AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
       mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(FilesActivity.this);
        */
// In the class declaration section:

    }

    @Override
    public void onResume() {
        super.onResume();


        RecordingSettings settings = getSettings();
        String accessToken = settings.getToken();


        if (accessToken == null) {
            accessToken = Auth.getOAuth2Token();
            if (accessToken != null) {
                settings.setToken(accessToken);

                initDropbox(accessToken);
            }
        } else {
            initDropbox(accessToken);
        }
    }

    private void initDropbox(String accessToken) {


        DropboxClient.init(accessToken);
        //   PicassoClient.init(getApplicationContext(), DropboxClient.files());
        //   loadData();
    }


    protected boolean hasToken() {
        String accessToken = getSettings().getToken();
        return accessToken != null;
    }


}


