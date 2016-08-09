package com.pdmanager.views.charts;

/**
 * Created by George on 1/31/2016.
 */

import android.view.View;

import com.pdmanager.core.R;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.ObservationCode;
import com.telerik.widget.chart.visualization.behaviors.ChartSelectionBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartSelectionMode;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public abstract class AreaFragment extends BaseChartFragment {
    protected RadCartesianChartView cartesianChart;

    public AreaFragment() {
        // Required empty public constructor

    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_area;
    }

    @Override
    protected void prepareChart() {
        super.prepareChart();
        this.cartesianChart = (RadCartesianChartView) this.chart;
        this.prepareAreaChart();
    }


    protected abstract void prepareAreaChart();


    protected void onDataReceived(List<Observation> data) {

        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
            chart.setVisibility(View.VISIBLE);

        }
    }


    @Override
    protected void initSelectionBehavior(ChartSelectionBehavior behavior) {
        behavior.setDataPointsSelectionMode(ChartSelectionMode.NONE);
        behavior.setSeriesSelectionMode(ChartSelectionMode.SINGLE);
    }


    protected List<ObservationCode> getObservationsCodes() {
        ArrayList<ObservationCode> codes = new ArrayList<ObservationCode>();
        codes.add(
                new ObservationCode("OFF.", "OFF", "UPDRS"));
        codes.add(
                new ObservationCode("Bradykinesia", "BRAD", "UPDRS"));

        codes.add(
                new ObservationCode("Dyskinesia", "LID", "UPDRS"));

        codes.add(
                new ObservationCode("Tremor", "TREMOR_C", "UPDRS"));

        codes.add(
                new ObservationCode("Gait", "GAIT", "UPDRS"));
        codes.add(
                new ObservationCode("FOG", "FOG", "UPDRS"));

        //codes.add(
        //      new ObservationCode("Standing time", "ACT_STAND", "%"));
        return codes;
    }




    /*

    */
}
