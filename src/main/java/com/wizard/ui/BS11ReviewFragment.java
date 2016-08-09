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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pdmanager.core.R;
import com.wizard.model.AbstractWizardModel;
import com.wizard.model.ModelCallbacks;
import com.wizard.model.Page;
import com.wizard.model.ReviewItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BS11ReviewFragment extends ListFragment implements ModelCallbacks {
    ITestResultHandler handler;
    private Callbacks mCallbacks;
    private AbstractWizardModel mWizardModel;
    private List<ReviewItem> mCurrentReviewItems;
    private ReviewAdapter mReviewAdapter;

    public BS11ReviewFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReviewAdapter = new ReviewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page, container, false);

        TextView titleView = (TextView) rootView.findViewById(android.R.id.title);
        titleView.setText("Review");
        titleView.setTextColor(Color.parseColor("#33ff33"));

        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(mReviewAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        onPageTreeChanged();
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;

        mWizardModel.unregisterListener(this);
    }


    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        //jpjp
        int score = 0;
        int numberQuestion = 0;
        //int numberDomainQuestion=0;
        //fo mean first order
        int attention_fo = 0;
        int cognitiveInstability_fo = 0;
        int motor_fo = 0;
        int perseverance_fo = 0;
        int selfControl_fo = 0;
        int cognitiveComplexity_fo = 0;
        //so mean second order
        int attentional_so = 0;
        int motor_so = 0;
        int nonplanning_so = 0;
        int domainArray[] = new int[9];

        String scoretext = "";
        String domainTextArray[] = new String[9];
        domainTextArray[0] = "Cardiovascular including falls";
        domainTextArray[1] = "Sleep/fatigue";
        domainTextArray[2] = "Mood/Cognition";
        domainTextArray[3] = "Peceptual problems/hallucinations";
        domainTextArray[4] = "Attention/Memory";
        domainTextArray[4] = "Gastrointestinal track";
        domainTextArray[6] = "Urinary";
        domainTextArray[7] = "Sexual function";
        domainTextArray[8] = "Miscellaneous";

        for (Page page : mWizardModel.getCurrentPageSequence()) {
            //dont show question
            //page.getReviewItems(reviewItems);
            //jpjp

            score = score + page.getScore();
            numberQuestion = page.getmNumberQuestion();
        }
        if (score < 52) {
            reviewItems.add(new ReviewItem("Score", Integer.toString(score), "Final Score"));
            scoretext = "Individual is either extremely over-controlled  or has not honestly completed the questionnaire  ";
            reviewItems.add(new ReviewItem("Result", scoretext, "Result"));
        } else if (score < 72) {
            reviewItems.add(new ReviewItem("Score", Integer.toString(score), "Final Score"));
            scoretext = "normal limits for impulsiveness ";
            reviewItems.add(new ReviewItem("Result", scoretext, "Result"));
        } else {
            reviewItems.add(new ReviewItem("Score", Integer.toString(score), "Final Score"));
            scoretext = " individual highly impulsive  ";
            reviewItems.add(new ReviewItem("Result", scoretext, "Result"));
        }

        //jpjp

        //reviewItems.add(new ReviewItem("NumberQuestion",Integer.toString(numberQuestion),"dsfdas") );


        for (Page page : mWizardModel.getCurrentPageSequence()) {
            if (page.getmNumberDomainQuestion() == 0) {
                //page.getReviewItems(reviewItems);
                //jpjp
                switch (page.getmNumberQuestion()) {
                    case 5:
                    case 9:
                    case 11:
                    case 20:
                    case 28:
                        attentional_so = attentional_so + page.getScore();
                        break;
                    case 6:
                    case 24:
                    case 26:
                        cognitiveInstability_fo = cognitiveInstability_fo + page.getScore();
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 17:
                    case 19:
                    case 22:
                    case 25:
                        motor_so = motor_so + page.getScore();
                        break;
                    case 16:
                    case 21:
                    case 23:
                    case 30:
                        perseverance_fo = perseverance_fo + page.getScore();
                        break;
                    case 1:
                    case 7:
                    case 8:
                    case 12:
                    case 13:
                    case 14:
                        selfControl_fo = selfControl_fo + page.getScore();
                        break;
                    case 10:
                    case 15:
                    case 18:
                    case 27:
                    case 29:
                        cognitiveComplexity_fo = cognitiveComplexity_fo + page.getScore();
                }


                if (page.getmNumberQuestion() == 30) {
                    reviewItems.add(new ReviewItem("1st Order Factors", " ", "1st Order Factors"));
                    reviewItems.add(new ReviewItem("Attention (avg: 10.4)", Integer.toString(attentional_so), "attention"));
                    reviewItems.add(new ReviewItem("Cognitive Instability (avg: 6.4)", Integer.toString(cognitiveInstability_fo), "cognitiveInstability"));
                    reviewItems.add(new ReviewItem("Motor (avg: 15.0)", Integer.toString(motor_so), "motor"));
                    reviewItems.add(new ReviewItem("Perseverance (avg: 6.9)", Integer.toString(perseverance_fo), "perseverance"));
                    reviewItems.add(new ReviewItem("Self-Control (avg: 12.1)", Integer.toString(selfControl_fo), "selfControl"));
                    reviewItems.add(new ReviewItem("Cognitive Complexity (avg: 11.5)", Integer.toString(cognitiveComplexity_fo), "cognitiveComplexity"));

                    reviewItems.add(new ReviewItem("2nd Order Factors", " ", "2st Order Factors"));
                    attentional_so = attention_fo + cognitiveInstability_fo;
                    reviewItems.add(new ReviewItem("Attentional (avg: 16.7)", Integer.toString(attentional_so), "attentional"));
                    motor_so = motor_fo + perseverance_fo;
                    reviewItems.add(new ReviewItem("Motor (avg: 22.0)", Integer.toString(motor_so), "motor"));
                    nonplanning_so = selfControl_fo + cognitiveComplexity_fo;
                    reviewItems.add(new ReviewItem("Non-planning (avg: 23.6)", Integer.toString(nonplanning_so), "nonplanning"));

                }
            } else {

                domainArray[page.getmNumberDomainQuestion() - 1] = domainArray[page.getmNumberDomainQuestion() - 1] + page.getScore();
                if (page.getmNumberQuestion() == 30) {
                    for (int k = 0; k < 9; k++) {
                        reviewItems.add(new ReviewItem("Domain " + Integer.toString(k + 1) + ": " + domainTextArray[k], Integer.toString(domainArray[k]), "Domain:" + Integer.toString(k + 1)));
                    }
                }
            }

        }


        //if(numberQuestion==30) {
        // reviewItems.add(new ReviewItem("Score",Integer.toString(score),"dsfdas") );}


        //jpjp


        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });
        mCurrentReviewItems = reviewItems;

        if (mReviewAdapter != null) {
            mReviewAdapter.notifyDataSetInvalidated();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallbacks.onEditScreenAfterReview(mCurrentReviewItems.get(position).getPageKey());
    }

    public interface Callbacks {
        AbstractWizardModel onGetModel();

        void onEditScreenAfterReview(String pageKey);
    }

    private class ReviewAdapter extends BaseAdapter {
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public Object getItem(int position) {
            return mCurrentReviewItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mCurrentReviewItems.get(position).hashCode();
        }

        @Override
        public View getView(int position, View view, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View rootView = inflater.inflate(R.layout.list_item_review, container, false);

            ReviewItem reviewItem = mCurrentReviewItems.get(position);
            String value = reviewItem.getDisplayValue();
            if (TextUtils.isEmpty(value)) {
                value = "(None)";
            }
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(reviewItem.getTitle());
            ((TextView) rootView.findViewById(android.R.id.text2)).setText(value);

            if (reviewItem.getTitle().equals("1st Order Factors")) {
                View rootViewOther = inflater.inflate(R.layout.list_item_review_small_title, container, false);
                ((TextView) rootViewOther.findViewById(R.id.text3)).setText(reviewItem.getTitle());

                return rootViewOther;
            } else if (reviewItem.getTitle().equals("2nd Order Factors")) {
                View rootViewOther = inflater.inflate(R.layout.list_item_review_small_title, container, false);
                ((TextView) rootViewOther.findViewById(R.id.text3)).setText(reviewItem.getTitle());

                return rootViewOther;
            }


            return rootView;
        }

        @Override
        public int getCount() {
            return mCurrentReviewItems.size();
        }
    }
}
