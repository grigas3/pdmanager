package com.pdmanager.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdmanager.core.R;
import com.pdmanager.core.interfaces.IEventDetailNavigator;
import com.pdmanager.core.models.Patient;
import com.pdmanager.views.drawers.IBasePatientChartFragment;

import java.util.HashMap;

/**
 * Created by George on 6/18/2016.
 */
public class DayOverViewFragment extends BasePDFragment implements IBasePatientChartFragment, IEventDetailNavigator {


    protected Patient patient;
    long day;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    DOVCollectionPagerAdapter mDemoCollectionPagerAdapter;
    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    public void setDay(long pday) {

        day = pday;
    }

    protected void restoreVariables(Bundle savedInstanceState) {


        day = savedInstanceState.getLong("Day");
        patient = savedInstanceState.getParcelable("Patient");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putLong("Day", day);
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
    public void navigate(String code) {


        Log.d("INFO", "Navigate");

        mViewPager.setCurrentItem(1);

    }


    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public class DOVCollectionPagerAdapter extends FragmentStatePagerAdapter implements IEventDetailNavigator {


        HashMap<Integer, Fragment> fragmentCache = new HashMap<Integer, Fragment>();
        private DayObsChartFragment chartFragment;
        private String selectedCode;


        public DOVCollectionPagerAdapter(FragmentManager fm) {
            super(fm);

        }


        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            if (fragmentCache.containsKey(i)) {
                fragment = fragmentCache.get(i);

                Bundle args = new Bundle();

                args.putString("selectedCode", selectedCode);
                fragment.setArguments(args);
                return fragment;
            }

            if (i == 0) {
                DayAssessmentFragment f = new DayAssessmentFragment();


                f.setPatient(patient);
                f.setNavigator(this);
                f.setDay(day);


                fragment = f;
            } else /*if(i==1)*/ {

                DayObsChartFragment f = new DayObsChartFragment();
                f.setPatient(patient);
                f.setDay(day);

                Bundle args = new Bundle();

                args.putString("selectedCode", selectedCode);
                f.setArguments(args);
                fragment = f;
                chartFragment = f;

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

        @Override
        public void navigate(String code) {


            selectedCode = code;
            try {
                mViewPager.setCurrentItem(1);

                if (chartFragment != null) {


                    chartFragment.updateChart(selectedCode);
                }
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }
        }
    }

}
