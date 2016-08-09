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

package com.pdmanager.views;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.pdmanager.core.viewmodels.NMSSWizardModel;
import com.wizard.model.AbstractWizardModel;
import com.wizard.model.Page;
import com.wizard.ui.NMSSReviewFragment;

import java.util.List;

public class NMSSActivity extends BaseTestActivity {


    @Override
    protected String getCode() {
        return "TEST_NMSS";
    }

    @Override
    protected AbstractWizardModel getWizard() {
        return new NMSSWizardModel(this);
    }

    @Override
    protected BaseTestPagerAdapter getPagerAdapter(FragmentManager fm, List<Page> pCurrentPageSequence) {
        return new NMSSPager(fm, pCurrentPageSequence);
    }


    public class NMSSPager extends BaseTestPagerAdapter {


        public NMSSPager(FragmentManager fm, List<Page> pCurrentPageSequence) {
            super(fm, pCurrentPageSequence);
        }

        @Override
        protected Fragment getReviewFragment() {
            return new NMSSReviewFragment();
        }
    }
}