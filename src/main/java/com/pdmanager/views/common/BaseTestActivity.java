package com.pdmanager.views.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.app.PDApplicationContext;
import com.pdmanager.R;
import com.pdmanager.communication.CommunicationManager;
import com.pdmanager.communication.DirectSender;
import com.pdmanager.models.Observation;
import com.pdmanager.settings.RecordingSettings;
import com.pdmanager.viewmodels.PDTestResult;
import com.pdmanager.views.BaseTestPagerAdapter;
import com.wizard.model.AbstractWizardModel;
import com.wizard.model.ModelCallbacks;
import com.wizard.model.Page;
import com.wizard.ui.BS11ReviewFragment;
import com.wizard.ui.NMSSReviewFragment;
import com.wizard.ui.PageFragmentCallbacks;
import com.wizard.ui.StepPagerStrip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by george on 22/6/2016.
 */
public abstract class BaseTestActivity extends FragmentActivity implements
        PageFragmentCallbacks,
        BS11ReviewFragment.Callbacks,
        NMSSReviewFragment.Callbacks,
        ModelCallbacks {
    //private AbstractWizardModel mWizardModel = new SandwichWizardModel(this);
    private final AbstractWizardModel mWizardModel = getWizard();
    private ViewPager mPager;
    private BaseTestPagerAdapter mPagerAdapter;
    private boolean mEditingAfterReview;
    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;

    private List<PDTestResult> results = new ArrayList<PDTestResult>();
    private StepPagerStrip mStepPagerStrip;

    protected abstract String getCode();

    private double getScore() {


        double score = 0;
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            score = score + page.getScore();

        }

        return score;

    }




    protected abstract AbstractWizardModel getWizard();


    protected abstract BaseTestPagerAdapter getPagerAdapter(FragmentManager fm, List<Page> pCurrentPageSequence);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs11);

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        final String patientId = this.getIntent().getStringExtra(PDApplicationContext.INTENT_PATIENT_CODE);
        final String accessToken = this.getIntent().getStringExtra(PDApplicationContext.INTENT_ACCESS_TOKEN);

        mWizardModel.registerListener(this);

        mPagerAdapter = getPagerAdapter(getSupportFragmentManager(), mCurrentPageSequence);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

/*
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    DialogFragment dg =DialogFragment.instantiate() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            return new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.submit_confirm_message)
                                    .setPositiveButton(R.string.submit_confirm_button, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // if this button is clicked, close
                                            // current activity
                                            //save data


                                            RecordingSettings settings = new RecordingSettings(getActivity());
                                            new SaveTestTask(accessToken, patientId).execute(new PDTestResult(getCode(), getScore()));


                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .create();
                        }
                    };
                    dg.show(getSupportFragmentManager(), "place_order_dialog");
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });
*/
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step

        mPagerAdapter.updatePageSequence(mCurrentPageSequence);
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }

    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
        mPrevButton.setBackgroundResource(R.drawable.selectable_item_background);
        TypedValue v = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.textAppearanceMedium, v, true);
        mPrevButton.setTextAppearance(this, v.resourceId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    private class SaveTestTask extends AsyncTask<PDTestResult, Void, Boolean> {

        private String accessToken;
        private String patientCode;

        public SaveTestTask(String a, String code) {

            this.patientCode = code;
            this.accessToken = a;
        }

        @Override
        protected Boolean doInBackground(PDTestResult... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {


                PDTestResult res = clientParams[0];

                DirectSender sender = new DirectSender(accessToken);
                CommunicationManager mCommManager = new CommunicationManager(sender);

                Date date1 = new java.util.Date();
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                ArrayList<Observation> obsC = new ArrayList<>();
                {
                    Observation obs = new Observation(res.getValue(), patientCode, res.getCode(), cal1.getTimeInMillis());
                    obs.PatientId = patientCode;


                    obsC.add((obs));
                }
                mCommManager.SendItems(obsC);


                return true;

            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return false;
                // handle BandException
            }
        }


        protected void onPostExecute(Boolean result) {


            //  new GetMedicationTask(patientCode,accessToken).execute();

            BaseTestActivity.this.finish();

        }
    }

}
