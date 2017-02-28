package com.pdmanager.views.clinician;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.R;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;

import java.util.HashMap;

/**
 * Created by George on 6/18/2016.
 */
public class PatientTestFragment extends BasePDFragment implements IBasePatientChartFragment {


    protected Patient patient;
    long day;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    DOVCollectionPagerAdapter mDemoCollectionPagerAdapter;
    /**
     * The {@link ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    public void setDay(long pday) {

        day = pday;
    }

    protected void restoreVariables(Bundle savedInstanceState) {


        patient = savedInstanceState.getParcelable("Patient");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        outState.putParcelable("Patient", patient);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_day, container, false);

        if (savedInstanceState != null) {

            patient = savedInstanceState.getParcelable("Patient");

        }

        mDemoCollectionPagerAdapter = new DOVCollectionPagerAdapter(this.getActivity().getSupportFragmentManager());

        if (patient != null) {

            PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
            manager.setHeader(rootView);

        }

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);


        return rootView;
    }

    @Override
    public Patient getPatient() {
        return patient;
    }

    @Override
    public void setPatient(Patient p) {

        patient = p;
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public class DOVCollectionPagerAdapter extends FragmentStatePagerAdapter {


        HashMap<Integer, Fragment> fragmentCache = new HashMap<Integer, Fragment>();


        public DOVCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if (fragmentCache.containsKey(i))
                return fragmentCache.get(i);

            if (i == 0) {
                DayAssessmentFragment f = new DayAssessmentFragment();
                f.setPatient(patient);
                f.setDay(day);
                fragment = f;
            } else /*if(i==1)*/ {

                DayObsChartFragment f = new DayObsChartFragment();
                f.setPatient(patient);
                f.setDay(day);
                fragment = f;

            }
         /*   else {
                DayEventsFragment f = new DayEventsFragment();
                f.setPatient(patient);
                f.setDay(day);
                fragment = f;
            }
            */

            //Bundle args = new Bundle();
            //args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1); // Our object is just an integer :-P
            //fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {


            if (position == 0)
                return "Day Assessment";
            else //if(position==1)
                return "Day Observations";
            //else
            //  return "Day Observations";
        }
    }

}
