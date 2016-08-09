package com.pdmanager.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.wizard.model.Page;

import java.util.List;

/**
 * Created by George on 6/25/2016.
 */

public abstract class BaseTestPagerAdapter extends FragmentStatePagerAdapter {
    private int mCutOffPage;
    private Fragment mPrimaryItem;

    private List<Page> mCurrentPageSequence;

    public BaseTestPagerAdapter(FragmentManager fm, List<Page> pCurrentPageSequence) {
        super(fm);

        this.mCurrentPageSequence = pCurrentPageSequence;
    }


    public void updatePageSequence(List<Page> pCurrentPageSequence) {

        mCurrentPageSequence = pCurrentPageSequence;
    }

    protected abstract Fragment getReviewFragment();

    @Override
    public Fragment getItem(int i) {
        if (i >= mCurrentPageSequence.size()) {
            return getReviewFragment();
        }

        return mCurrentPageSequence.get(i).createFragment();
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO: be smarter about this
        if (object == mPrimaryItem) {
            // Re-use the current fragment (its position never changes)
            return POSITION_UNCHANGED;
        }

        return POSITION_NONE;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mPrimaryItem = (Fragment) object;
    }

    @Override
    public int getCount() {
        if (mCurrentPageSequence == null) {
            return 0;
        }
        return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
    }

    public int getCutOffPage() {
        return mCutOffPage;
    }

    public void setCutOffPage(int cutOffPage) {
        if (cutOffPage < 0) {
            cutOffPage = Integer.MAX_VALUE;
        }
        mCutOffPage = cutOffPage;
    }
}