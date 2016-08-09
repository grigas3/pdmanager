package com.pdmanager.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.pdmanager.core.settings.RecordingSettings;

import java.util.ArrayList;

/**
 * Created by George on 1/30/2016.
 */
public abstract class BasePDFragment extends Fragment {

    private ExampleLoadedListener listener;

    public BasePDFragment() {
        // Required empty public constructor
    }

    public boolean onBackPressed() {
        return false;
    }

    public void unloadExample() {
    }

    public void onHidden() {

    }

    public void onVisualized() {

    }

    public void onExampleSuspended() {

    }

    public void onExampleResumed() {

    }

    ///Private method for get settings
    protected RecordingSettings getSettings() {


        return new RecordingSettings(this.getContext());


    }


    protected String getAccessToken() {

        RecordingSettings settings = RecordingSettings.GetRecordingSettings(this.getContext());

        if (settings != null) {
            return settings.getToken();


        }

        return null;
    }


    protected void makeToast(String text) {

        Toast.makeText(getContext(),
                text,
                Toast.LENGTH_SHORT)
                .show();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    public String getEQATECCategory() {
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.listener != null) {
            this.listener.onExampleLoaded(this.getView());
        }
    }

    public void setOnExampleLoadedListener(ExampleLoadedListener listener) {
        if (listener != null && this.listener != null) {
            throw new IllegalArgumentException("Listener already set!");
        }
        this.listener = listener;
    }


    private ArrayList<String> getClassHierarchyNames() {
        ArrayList<String> classes = new ArrayList<String>();

        for (Class c = this.getClass(); c != null; c = c.getSuperclass()) {
            if (c.getSimpleName().equals(BasePDFragment.class.getSimpleName())) {
                break;
            }

            classes.add(c.getSimpleName());
        }

        return classes;
    }


    public interface ExampleLoadedListener {
        void onExampleLoaded(View root);
    }
}
