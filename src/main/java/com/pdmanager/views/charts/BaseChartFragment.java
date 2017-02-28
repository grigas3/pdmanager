package com.pdmanager.views.charts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.pdmanager.R;
import com.pdmanager.models.Patient;
import com.pdmanager.views.BasePDFragment;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.visualization.behaviors.ChartSelectionBehavior;
import com.telerik.widget.chart.visualization.cartesianChart.CartesianChartGrid;
import com.telerik.widget.chart.visualization.cartesianChart.GridLineVisibility;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

/**
 * Created by George on 1/31/2016.
 */
public abstract class BaseChartFragment extends BasePDFragment {

    protected RadChartViewBase chart;
    protected Context context;
    protected View rootView;
    protected ProgressBar progressBar;
    protected Button button;
    protected Patient patient;
    ///Connect button listener
    private View.OnClickListener mButtonConnectClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {


            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                chart.setVisibility(View.INVISIBLE);

            }
            populateChart();


        }
    };

    public Patient getPatient() {

        return patient;
    }

    public void setPatient(Patient code) {

        patient = code;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        // Save the user's current game state
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    protected void restoreVariables(Bundle savedInstanceState) {

        patient = savedInstanceState.getParcelable("Patient");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (savedInstanceState != null) {


            restoreVariables(savedInstanceState);


        }


        this.context = this.getActivity();
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(this.getLayoutID(), container, false);
        this.chart = Util.getLayoutPart(this.rootView, R.id.chart, RadChartViewBase.class);


        this.button = Util.getLayoutPart(this.rootView, R.id.button, Button.class);

        button.setOnClickListener(mButtonConnectClickListener);


        this.progressBar = (ProgressBar) rootView.findViewById(R.id.busy_BusyIndicator);


        if (this.chart instanceof RadCartesianChartView) {
            CartesianChartGrid grid = new CartesianChartGrid();
            grid.setStripLinesVisibility(GridLineVisibility.Y);
            ((RadCartesianChartView) chart).setGrid(grid);
        }
        this.prepareChart();

        return this.rootView;
    }

    protected abstract int getLayoutID();

    protected abstract void populateChart();

    protected void prepareChart() {


    }

    protected void initSelectionBehavior(ChartSelectionBehavior behavior) {
    }


}
