package com.pdmanager.views;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.pdmanager.common.data.LogEvent;
import com.pdmanager.core.R;
import com.pdmanager.core.logging.LogAdapter;
import com.pdmanager.core.logging.LogCursorAdapter;
import com.pdmanager.core.logging.LogLoader;
import com.pdmanager.core.logging.LogObserver;
import com.pdmanager.core.persistence.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class LogEventFragment extends Fragment implements FragmentListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "LOADER";
    List<LogEvent> logEvents = new ArrayList<LogEvent>();
    LogAdapter dbQ;
    private int LOADER_ID = 1;
    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private GridView mGridView;
    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private LogCursorAdapter mAdapter;
    private Object stateChanged = new Object();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogEventFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAdapter(getActivity());

        /*

        // TODO: Change Adapter to display your content
        mAdapter = new ArrayAdapter<LogEvent>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, logEvents){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView Value = (TextView) view.findViewById(android.R.id.text1);
                LogEvent c=(LogEvent)this.getItem(position);
                if(c!=null&&c.getLogType()==2)
                {

                    Value.setTextColor(Color.RED);
                }

                return view;
            }
        };
        */

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logevent_new, container, false);
        // Set the adapter

        //  displayListView(view);


        try {

            mListView = (ListView) view.findViewById(android.R.id.list);

            if (mAdapter != null) {
                mListView.setAdapter(mAdapter);
                getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
            }
        } catch (Exception ex) {


            /*try {
                mGridView = (GridView) view.findViewById(android.R.id.list);

                if (mAdapter != null) {
                    mGridView.setAdapter(mAdapter);
                    getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
                }
            }catch(Exception ex1) {


                Log.d("Error",ex1.toString());
            }
            */

        }


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the Value, call this method
     * to supply the Value it should use.
     */
    public void setEmptyText(CharSequence emptyText) {

        if (mListView != null) {
            View emptyView = mListView.getEmptyView();

            if (emptyView instanceof TextView) {
                ((TextView) emptyView).setText(emptyText);
            }
        } else {

            View emptyView = mGridView.getEmptyView();

            if (emptyView instanceof TextView) {
                ((TextView) emptyView).setText(emptyText);
            }

        }
    }

    private void initAdapter(Context context) {


        try {
            dbQ = new LogAdapter(context);

            //   dbQ.open();
            // create the adapter using the cursor pointing to the desired data
            //as well as the layout information
            mAdapter = new LogCursorAdapter(context, null, false);


        } catch (Exception ex) {

            Log.d("AA", "Aa");

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

        LogLoader demoLoader = new LogLoader(this.getActivity(), dbQ);
        return demoLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        //Log.e(TAG, ":::: onLoadFinished");

        mAdapter.swapCursor(c);

        /**
         * Registering content observer for this cursor, When this cursor Value will be change
         * This will notify our loader to reload its data*/
        LogObserver cursorObserver = new LogObserver(new Handler(), loader);
        c.registerContentObserver(cursorObserver);
        c.setNotificationUri(getActivity().getContentResolver(), DBHandler.URI_TABLE_USERS);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Log.e(TAG, ":::: onLoaderReset");

        mAdapter.swapCursor(null);
    }
}


