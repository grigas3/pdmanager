package com.pdmanager.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxFiles;
import com.pdmanager.core.R;
import com.pdmanager.core.communication.DropboxClient;
import com.pdmanager.core.communication.UploadDropboxFileTask;
import com.pdmanager.core.communication.UploadDropboxFilesTask;
import com.pdmanager.core.settings.RecordingSettings;

import java.io.File;
import java.util.ArrayList;

public class FilesActivity extends ActionBarActivity {

    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter<String> itemsAdapter;
    //  final static private String APP_KEY = "gorgg475srkv0v7";
    //  final static private String APP_SECRET = "qn0qlnqn9idgycu";
    private String mPath;

    //  private DropboxAPI<AndroidAuthSession> mDBApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);


        ReadFiles();

        mPath = "PDManager";

    }


    private void ReadFiles() {


        //to this path add a new directory path and create new App dir (InstroList) in /documents Dir


// lists all the files into an array


        ListView listView = (ListView) findViewById(R.id.filesList);

        RefreshFiles();
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

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

            ListView listView = (ListView) findViewById(R.id.filesList);

            ArrayList<String> items = new ArrayList<String>();

            uploadFiles(dirFiles);

            /*if (dirFiles.length != 0) {
                for (int ii = 0; ii < dirFiles.length; ii++) {



                    File file=dirFiles[ii];
                    //File file = new File("working-draft.txt");
                    //FileInputStream inputStream = new FileInputStream(file);

                    uploadFile(file);
                    //DropboxAPI.Entry response = mDBApi.putFile("/magnum-opus.txt", inputStream,
                      //      file.length(), null, null);
                    //Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                }

            }
            */

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

            ListView listView = (ListView) findViewById(R.id.filesList);

            ArrayList<String> items = new ArrayList<String>();
            if (dirFiles.length != 0) {
                for (int ii = 0; ii < dirFiles.length; ii++) {


                    File file = dirFiles[ii];

                    try {


                        file.delete();
                    } catch (Exception ex) {

                    }
                }

            }

        } catch (Exception ex) {


        }


    }

    private void uploadFile(File file) {


       /* final ProgressDialog dialog = new ProgressDialog(this);
      dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();
*/
        new UploadDropboxFileTask(this, DropboxClient.files(), new UploadDropboxFileTask.Callback() {
            @Override
            public void onUploadComplete(DbxFiles.FileMetadata result) {
                // dialog.dismiss();

                Toast.makeText(FilesActivity.this,
                        result.name + " size " + result.size + " modified " + result.clientModified.toGMTString(),
                        Toast.LENGTH_SHORT)
                        .show();

                // Reload the folder
                //  loadData();
            }

            @Override
            public void onError(Exception e) {
                // dialog.dismiss();

                Toast.makeText(FilesActivity.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(file);
    }

    private void uploadFiles(File[] files) {


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setMessage("Uploading");
        dialog.show();

        new UploadDropboxFilesTask(this, DropboxClient.files(), new UploadDropboxFilesTask.Callback() {
            @Override
            public void onUploadComplete(DbxFiles.FileMetadata[] result) {
                dialog.dismiss();


                RefreshFiles();

                Toast.makeText(FilesActivity.this,
                        "Upload completed successfully",
                        Toast.LENGTH_SHORT)
                        .show();

                // Reload the folder
                //  loadData();
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();

                Toast.makeText(FilesActivity.this,
                        "An error has occurred",
                        Toast.LENGTH_SHORT)
                        .show();

                RefreshFiles();
            }
        }).execute(files);
    }

    private void SyncFiles() {


        if (hasToken()) {
            RecordingSettings settings = RecordingSettings.GetRecordingSettings(this.getApplicationContext());
            String accessToken = settings.getToken();

            DoSyncFiles(accessToken);
        } else {
            Auth.startOAuth2Authentication(FilesActivity.this, getString(R.string.app_key));
        }


       /* AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
       mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(FilesActivity.this);
        */
// In the class declaration section:

    }

    @Override
    protected void onResume() {
        super.onResume();


        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this.getApplicationContext());

        if (settings != null) {
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
    }

    private void initDropbox(String accessToken) {


        DropboxClient.init(accessToken);
        //   PicassoClient.init(getApplicationContext(), DropboxClient.files());
        //   loadData();
    }


    protected boolean hasToken() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this.getApplicationContext());
        if (settings != null) {
            String accessToken = settings.getToken();
            return accessToken != null;

        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_files, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_main) {
            Intent mainIntent = new Intent(FilesActivity.this, MainActivity.class);
            FilesActivity.this.startActivity(mainIntent);
        }
        if (id == R.id.action_syncFiles) {
            SyncFiles();
        }
        if (id == R.id.action_refreshFiles) {
            RefreshFiles();
        }
        if (id == R.id.action_clearFiles) {
            ClearFiles();
            RefreshFiles();
        }
        return super.onOptionsItemSelected(item);
    }
}
