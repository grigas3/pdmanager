package com.pdmanager.views.clinician;

/**
 * Created by George on 1/31/2016.
 */

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.adapters.ObservationParams;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.models.Observation;
import com.pdmanager.views.charts.AreaFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.axes.common.DateTimeComponent;
import com.telerik.widget.chart.engine.databinding.FieldNameDataPointBinding;
import com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartPanZoomMode;
import com.telerik.widget.chart.visualization.cartesianChart.axes.DateTimeCategoricalAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.LinearAxis;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.BollingerBandsIndicator;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.MovingAverageIndicator;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.LineSeries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RObservationChartFragment extends AreaFragment implements IBasePatientChartFragment {
    private static final float VERTICAL_AXIS_SINGLE_STEP = 1;
    private static final float VERTICAL_AXIS_SINGLE_MIN = 60;
    private static final float VERTICAL_AXIS_SINGLE_MAX = 90;
    LinearAxis vAxis;
    DateTimeCategoricalAxis hAxis;
    BollingerBandsIndicator bollingerBands;
    MovingAverageIndicator movingAverage;
    Spinner mAggSpinner;
    Spinner mCodeSpinner;
    boolean enableSpinnerListener = false;
    DatePickerDialog fromDatePickerDialog;
    DatePickerDialog toDatePickerDialog;
    Date from = null;
    Date to = null;
    private LineSeries series;
    private SimpleDateFormat dateFormatter;
    private int selectedAggr = 1;
    private String selectedCode = "001";

    public RObservationChartFragment() {


    }

    protected int getLayoutID() {
        return R.layout.fragment_line_simple;
    }

    @Override
    protected void populateChart() {


        if (patient != null)
            FetchData(patient.Id, selectedCode, from, to, selectedAggr);

    }

    protected void FetchData(String patient, String code, Date from, Date to, int aggregate) {

        new GetObservationsTask(getAccessToken()).execute(new ObservationParams(patient, code, from, to, aggregate));

    }

    @Override
    protected void prepareAreaChart() {


        if (patient != null) {

            PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
            manager.setHeader(rootView);

        }

        try {

            Bundle args = getArguments();
            if (args != null)
                selectedCode = args.getString("selectedCode");

        } catch (Exception e) {


        }


        vAxis = new LinearAxis();
        //   vAxis.setMinimum(VERTICAL_AXIS_SINGLE_MIN);
        //    vAxis.setMaximum(VERTICAL_AXIS_SINGLE_MAX);
        // vAxis.setMajorStep(VERTICAL_AXIS_SINGLE_STEP);

        hAxis = new DateTimeCategoricalAxis();
        hAxis.setLabelFitMode(AxisLabelFitMode.ROTATE);


        FieldNameDataPointBinding p1 = new FieldNameDataPointBinding("Date");
        FieldNameDataPointBinding p2 = new FieldNameDataPointBinding("Value");
        series = new LineSeries();
        //series.setStrokeThickness(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2));
        series.setCategoryBinding(p1);
        series.setValueBinding(p2);
/*
        series.setCategoryBinding(new Function<Observation, Calendar>() {
            @Override
            public Calendar apply(Observation argument) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(argument.getTimestamp()));

                return calendar;

            }
        });
        */


        bollingerBands = new BollingerBandsIndicator();
        bollingerBands.setCategoryBinding(p1);
        bollingerBands.setValueBinding(p2);
        bollingerBands.setPeriod(5);
        bollingerBands.setStandardDeviations(2);


        movingAverage = new MovingAverageIndicator();
        movingAverage.setCategoryBinding(p1);
        movingAverage.setValueBinding(p2);
        movingAverage.setPeriod(5);


        //  series.setDataPointIndicatorRenderer(new SphericalDataPointIndicatorRenderer(series));
        cartesianChart.setVerticalAxis(vAxis);
        cartesianChart.setHorizontalAxis(hAxis);
        cartesianChart.getSeries().add(series);
        cartesianChart.getSeries().add(bollingerBands);
        cartesianChart.getSeries().add(movingAverage);

        ChartPanAndZoomBehavior panZoom = new ChartPanAndZoomBehavior();
        panZoom.setPanMode(ChartPanZoomMode.BOTH);
        panZoom.setZoomMode(ChartPanZoomMode.BOTH);
        this.chart.getBehaviors().add(panZoom);


        if (selectedCode != null) {


            TextView header = (TextView) rootView.findViewById(R.id.headerTextView);

            header.setText(selectedCode);


            FetchData(patient.Id, selectedCode, from, to, selectedAggr);

        }
    }

    @Override
    protected void onDataReceived(List<Observation> data) {

        setData(data);
        super.onDataReceived(data);
    }

    public void setData(List<Observation> observations) {

        try {

            if (series != null) {

                for (Observation o : observations) {
                    o.calcDate();
                }

                if (selectedAggr == 1) {
                    hAxis.setDateTimeFormat(new SimpleDateFormat("MM/dd/yy"));
                    hAxis.setDateTimeComponent(DateTimeComponent.DATE);


                } else

                {
                    hAxis.setDateTimeFormat(new SimpleDateFormat("HH:mm"));
                    hAxis.setDateTimeComponent(DateTimeComponent.TIME_OF_DAY);


                }
                series.setData(observations);
                movingAverage.setData(observations);
                bollingerBands.setData(observations);
            }
            //cartesianChart.invalidate();
            // cartesianChart.invalidateOutline();


        } catch (Exception ex) {


            Log.d("Error ", "SS");
        }


    }

    private class GetObservationsTask extends AsyncTask<ObservationParams, Void, List<Observation>> {


        private final String accessToken;

        public GetObservationsTask(String a) {
            accessToken = a;

        }

        @Override
        protected List<Observation> doInBackground(ObservationParams... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);
                ObservationParams params = clientParams[0];


                return receiver.GetObservations(params.patientCode, params.obsCode, params.from, params.to, params.aggregate);


            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }

        protected void onPostExecute(List<Observation> result) {


            onDataReceived(result);


        }
    }


}
