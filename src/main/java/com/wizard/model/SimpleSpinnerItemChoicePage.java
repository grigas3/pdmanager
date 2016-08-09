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

package com.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.wizard.ui.SimpleSpinnerChoiceFragment;

import java.util.ArrayList;

/**
 * A page offering the user a number of non-mutually exclusive choices.
 */
public class SimpleSpinnerItemChoicePage extends SingleFixedChoicePage {
    public SimpleSpinnerItemChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return SimpleSpinnerChoiceFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {

        /*
        StringBuilder sb = new StringBuilder();

        ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        if (selections != null && selections.size() > 0) {
            for (String selection : selections) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(selection);
            }
        }
        */
        //dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));

        //dest.add(new ReviewItem(getTitle(), "ante geia", getKey()));
    }

    /*
    @Override
    public boolean isCompleted() {
        ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        return selections != null && selections.size() > 0;
    }
    */
    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }


    public SimpleSpinnerItemChoicePage setDomain(String domain) {
        //mChoices.addAll(Arrays.asList(choices));
        //mChoices.add(domain);
        mNumberDomainQuestion = Integer.parseInt(domain);
        //System.out.println(domain);
        return this;
    }

    @Override
    public int getScore() {
        //String value = mData.getString(SIMPLE_DATA_KEY);

        //uncomment when add score
        return Integer.parseInt(mData.getString("Frequency")) * Integer.parseInt(mData.getString("Severity"));
        //return  0;
    }
}
