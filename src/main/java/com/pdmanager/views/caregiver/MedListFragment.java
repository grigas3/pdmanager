package com.pdmanager.views.caregiver;

/**
 * Created by George on 1/30/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pdmanager.R;
import com.pdmanager.medication.MedAdapter;
import com.pdmanager.medication.MedCursorAdapter;
import com.pdmanager.medication.MedLoader;
import com.pdmanager.medication.MedObserver;
import com.pdmanager.persistence.DBHandler;
import com.pdmanager.views.BasePDFragment;
import com.pdmanager.views.FragmentListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class MedListFragment extends BasePDFragment implements FragmentListener,LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "MedListFragment";
    AbsListView listView;
    MedCursorAdapter mAdapter;
    private Button mButtonPatients;
    private TextView emptyList;
    private ProgressBar busyIndicator;
    private MedAdapter dbQ;
    private int LOADER_ID = 2;
    public MedListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MedListFragment newInstance(int sectionNumber) {
        MedListFragment fragment = new MedListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter(getActivity());



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medlist, container, false);


        try {

            listView = (ListView) rootView.findViewById(android.R.id.list);

            if (mAdapter != null) {
                listView.setAdapter(mAdapter);
                getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
            }
        } catch (Exception ex) {




        }
        return rootView;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the Value, call this method
     * to supply the Value it should use.
     */
    public void setEmptyText(CharSequence emptyText) {

        if (listView != null) {
            View emptyView = listView.getEmptyView();

            if (emptyView instanceof TextView) {
                ((TextView) emptyView).setText(emptyText);
            }
        }
    }
    private void initAdapter(Context context) {


        try {
            dbQ = new MedAdapter(context);

            //   dbQ.open();
            // create the adapter using the cursor pointing to the desired data
            //as well as the layout information
            mAdapter = new MedCursorAdapter(context, null, false);


        } catch (Exception ex) {

            Log.d(TAG, ex.getMessage());

        }

        //  ListView listView = (ListView) view.findViewById(android.R.id.list);
        // Assign adapter to ListView
        //listView.setAdapter(dataAdapter);


    }

    @Override
    public void onFragmentSelected() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //  Log.e(TAG, ":::: onCreateLoader");

        MedLoader demoLoader = new MedLoader(this.getActivity(), dbQ);
        return demoLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        //Log.e(TAG, ":::: onLoadFinished");

        mAdapter.swapCursor(c);

        /**
         * Registering content observer for this cursor, When this cursor Value will be change
         * This will setNotified our loader to reload its data*/
        MedObserver cursorObserver = new MedObserver(new Handler(), loader);
        c.registerContentObserver(cursorObserver);
        c.setNotificationUri(getActivity().getContentResolver(), DBHandler.URI_TABLE_MEDS);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Log.e(TAG, ":::: onLoaderReset");

        mAdapter.swapCursor(null);
    }




}