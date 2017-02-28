/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wizard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.pdmanager.R;
import com.wizard.model.Page;
import com.wizard.model.SimpleSpinnerItemChoicePage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleSpinnerChoiceFragment extends ListFragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private List<String> mChoices;
    private Page mPage;
    private Spinner spinnerSeverity;
    private Spinner spinnerFrequency;

    public SimpleSpinnerChoiceFragment() {
    }

    public static SimpleSpinnerChoiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        SimpleSpinnerChoiceFragment fragment = new SimpleSpinnerChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        SimpleSpinnerItemChoicePage spinnerChoicePage = (SimpleSpinnerItemChoicePage) mPage;
        //mChoices = new ArrayList<String>();
        //for (int i = 0; i < spinnerChoicePage.getOptionCount(); i++) {
        //    mChoices.add(spinnerChoicePage.getOptionAt(i));
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_spinner, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());


        //for future use domain name
        // ((TextView) rootView.findViewById(R.id.ddomain)).setText(Integer.toString(mPage.getmNumberDomainQuestion()));


        final List<String> severitylist = new ArrayList<String>();
        /*
        severitylist.add("0 = None");
        severitylist.add("1 = Mild:symptoms present but causes little distress or disturbance");
        severitylist.add("2 = Moderate: some distress or disturbance to patient");
        severitylist.add("3 = Severe: major sourcwe of distress or disturbance to patient");
        */

        //set only numbers
        severitylist.add("0 = None");
        severitylist.add("1 = Mild");
        severitylist.add("2 = Moderate");
        severitylist.add("3 = Severe");


        spinnerSeverity = (Spinner) rootView.findViewById(R.id.spinnerSeverity);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                //  android.R.layout.simple_spinner_item, severitylist);
                //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                R.layout.myspinner, severitylist);
        dataAdapter.setDropDownViewResource(R.layout.myspinner);

        spinnerSeverity.setAdapter(dataAdapter);

        spinnerSeverity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();

                mPage.getData().putString("Severity", Integer.toString(index));
                mPage.notifyDataChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int index = adapterView.getSelectedItemPosition();

                mPage.getData().putString("Severity", Integer.toString(index));
                mPage.notifyDataChanged();
            }
        });

        final List<String> frequencylist = new ArrayList<String>();

        /*
        frequencylist.add("1 = Rarely(<1/wk)");
        frequencylist.add("2 = Often(1/wk)");
        frequencylist.add("3 = Frequent(several times per week)");
        frequencylist.add("4 = Very Frequent(daily or all the time)");
        */

        //set only numbers
        frequencylist.add("1 = Rarely(<1/wk)");
        frequencylist.add("2 = Often(1/wk)");
        frequencylist.add("3 = Frequent(several times per week)");
        frequencylist.add("4 = Very Frequent(daily or all the time)");

        spinnerFrequency = (Spinner) rootView.findViewById(R.id.spinnerFrequency);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                // android.R.layout.simple_spinner_dropdown_item, frequencylist);
                //dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                R.layout.myspinner, frequencylist);
        dataAdapter2.setDropDownViewResource(R.layout.myspinner);
        spinnerFrequency.setAdapter(dataAdapter2);


        spinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = adapterView.getSelectedItemPosition();

                mPage.getData().putString("Frequency", Integer.toString(index));
                mPage.notifyDataChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                int index = adapterView.getSelectedItemPosition();

                mPage.getData().putString("Frequency", Integer.toString(index));
                mPage.notifyDataChanged();
            }
        });


        // Pre-select currently selected items.

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> selectedItems = mPage.getData().getStringArrayList(
                        Page.SIMPLE_DATA_KEY);
                if (selectedItems == null || selectedItems.size() == 0) {
                    return;
                }

                Set<String> selectedSet = new HashSet<String>(selectedItems);

                for (int i = 0; i < mChoices.size(); i++) {
                    if (selectedSet.contains(mChoices.get(i))) {
                        //false spinnerSeverity.setItemChecked(i, true);

                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    /* no use

    public void setOnItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ArrayList<String> selections = new ArrayList<String>();
        Spinner spinner = (Spinner) parent;
        getSelectedItemId();

        if(spinner.getId() == R.id.spinnerSeverity)
        {
            //do this
            selections.add("Severity: "+spinner.getSelectedItem().toString());
        }
        else if(spinner.getId() == R.id.spinnerFrequency)
        {
            //do this
            selections.add("Frequency: "+spinner.getSelectedItem().toString());
        }

        //mPage.getData().putStringArrayList(Page.SIMPLE_DATA_KEY, selections);
        mPage.getData().putString("Score", "2");
        mPage.notifyDataChanged();
    }


*/
}
