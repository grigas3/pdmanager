package com.pdmanager.views.clinician;

/**
 * Created by George on 1/31/2016.
 */

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.adapters.ObservationParams;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.models.MedIntakeListResult;
import com.pdmanager.models.MedTiming;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.Observation;
import com.pdmanager.models.ObservationCode;
import com.pdmanager.views.charts.AreaFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.primitives.CustomTextRenderer;
import com.telerik.primitives.PDAnnotationRenderer;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.axes.common.TimeInterval;
import com.telerik.widget.chart.engine.databinding.FieldNameDataPointBinding;
import com.telerik.widget.chart.visualization.annotations.cartesian.CartesianCustomAnnotation;
import com.telerik.widget.chart.visualization.annotations.cartesian.CartesianGridLineAnnotation;
import com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartPanZoomMode;
import com.telerik.widget.chart.visualization.cartesianChart.axes.DateTimeContinuousAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.LinearAxis;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.BollingerBandsIndicator;
import com.telerik.widget.chart.visualization.cartesianChart.indicators.MovingAverageIndicator;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.LineSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ObservationChartFragment extends AreaFragment implements View.OnClickListener, IBasePatientChartFragment {
    private static final double VERTICAL_AXIS_SINGLE_STEP = 1;
    private static final float VERTICAL_AXIS_SINGLE_MIN = 60;
    private static final float VERTICAL_AXIS_HALF_SINGLE_STEP = 30;
    private static final float VERTICAL_AXIS_SINGLE_MAX = 90;
    LinearAxis vAxis;
    DateTimeContinuousAxis hAxis;
    BollingerBandsIndicator bollingerBands;
    MovingAverageIndicator movingAverage;
    Spinner mAggSpinner;
    Spinner mCodeSpinner;
    boolean enableSpinnerListener = false;
    List<ObservationCode> codes = null;
    DatePickerDialog fromDatePickerDialog;
    DatePickerDialog toDatePickerDialog;
    Date from = null;
    Date to = null;
    EditText fromDateEtxt;
    EditText toDateEtxt;
    TextView xAxisTextVIew;
    TextView yAxisTextVIew;
    private LineSeries series;
    private SimpleDateFormat dateFormatter;
    private int selectedAggr = 2;
    private String selectedCode = null;
    private LruCache<String, Bitmap> mMemoryCache;
    private double maxX = 1;
    private double minX = 0;
    private long maxT;
    private long minT;

    public ObservationChartFragment() {


    }

    protected int getLayoutID() {
        return R.layout.fragment_line;
    }

    @Override
    protected void populateChart() {

        initCache();
        if (patient != null)
            FetchData(patient.Id, selectedCode, from, to, selectedAggr);

    }

    @Override
    public void onClick(View v) {
        if (v == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if (v == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }

    protected void FetchData(String patient, String code, Date from, Date to, int aggregate) {

        new GetObservationsTask(getAccessToken()).execute(new ObservationParams(patient, code, from, to, aggregate));

    }

    @Override
    protected void prepareAreaChart() {

        xAxisTextVIew = (TextView) rootView.findViewById(R.id.xAxisLegend);
        yAxisTextVIew = (TextView) rootView.findViewById(R.id.yAxisLegend);


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
//         vAxis.setMinimum(VERTICAL_AXIS_SINGLE_MIN);
//         vAxis.setMaximum(VERTICAL_AXIS_SINGLE_MAX);
//         vAxis.setMajorStep(VERTICAL_AXIS_HALF_SINGLE_STEP);

        hAxis = new DateTimeContinuousAxis();
        hAxis.setMajorStepUnit(TimeInterval.MINUTE);
        hAxis.setMajorStep(30);

        hAxis.setShowLabels(true);
        // hAxis.setLabelRenderer(CustomLabelRenderer);

        hAxis.setLabelFitMode(AxisLabelFitMode.ROTATE);

        hAxis.setLabelTextColor(R.color.axesVolumeColor);
        //hAxis.setLabelFormat("TIME");

//        vAxis.setShowLabels(true);
//        vAxis.setLabelFormat("UPDRS");


        FieldNameDataPointBinding p1 = new FieldNameDataPointBinding("Date");
        FieldNameDataPointBinding p2 = new FieldNameDataPointBinding("Value");
        series = new LineSeries();
        //series.setStrokeThickness(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2));
        series.setCategoryBinding(p1);
        series.setValueBinding(p2);


/*

        */


        bollingerBands = new BollingerBandsIndicator();
        bollingerBands.setCategoryBinding(p1);
        bollingerBands.setValueBinding(p2);
        bollingerBands.setPeriod(3);
        bollingerBands.setStandardDeviations(2);


        movingAverage = new MovingAverageIndicator();
        movingAverage.setCategoryBinding(p1);
        movingAverage.setValueBinding(p2);
        movingAverage.setPeriod(3);


        //  series.setDataPointIndicatorRenderer(new SphericalDataPointIndicatorRenderer(series));
        cartesianChart.setVerticalAxis(vAxis);
        cartesianChart.setHorizontalAxis(hAxis);
        cartesianChart.getSeries().add(series);
        // cartesianChart.getSeries().add(bollingerBands);
        cartesianChart.getSeries().add(movingAverage);


        ChartPanAndZoomBehavior panZoom = new ChartPanAndZoomBehavior();
        panZoom.setPanMode(ChartPanZoomMode.BOTH);
        panZoom.setZoomMode(ChartPanZoomMode.BOTH);
        this.chart.getBehaviors().add(panZoom);
        PreparePickers();


        if (selectedCode != null)
            FetchData(patient.Id, selectedCode, from, to, selectedAggr);
    }

    private void PreparePickers() {


        mCodeSpinner = (Spinner) rootView.findViewById(R.id.obsCodeSpinner);

        mAggSpinner = (Spinner) rootView.findViewById(R.id.obsAggSpinner);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        fromDateEtxt = (EditText) rootView.findViewById(R.id.fromPicker);
        toDateEtxt = (EditText) rootView.findViewById(R.id.toPicker);

        setDateTimeField();

        addListenerOnSpinnerItemSelection();
        initCodeSpinner();
        enableSpinnerListener = true;

        mAggSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String aggSpinnerItemValue = mAggSpinner.getSelectedItem().toString();

                xAxisTextVIew.setVisibility(View.VISIBLE);
                yAxisTextVIew.setVisibility(View.VISIBLE);

                if (aggSpinnerItemValue.equals("Day")) {
                    xAxisTextVIew.setText("TIME OF DAY");
                } else {
                    xAxisTextVIew.setText("TIME");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCodeSpinner() {


        addItemsOnSpinners(getObservationsCodes());

    }

    // add items into spinner dynamically
    public void addItemsOnSpinners(List<ObservationCode> pcodes) {


        if (pcodes != null) {
            this.codes = pcodes;


            int selectedIndex = 0;
            ArrayList<String> list3 = new ArrayList<String>();
            for (int i = 0; i < codes.size(); i++) {


                if (selectedCode != null && selectedCode.equals(codes.get(i).Code)) {
                    selectedIndex = i;
                }
                list3.add(codes.get(i).Title);
            }

            ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, list3);


            mCodeSpinner.setAdapter(dataAdapter3);
            mCodeSpinner.setSelection(selectedIndex);
        }

        ArrayList<String> list4 = new ArrayList<String>();
        //   list4.add("No");
        list4.add("Day");
        list4.add("Time");


        ArrayAdapter<String> dataAdapter4 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list4);
        mAggSpinner.setAdapter(dataAdapter4);
        mAggSpinner.setSelection(1);

    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                from = newDate.getTime();
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                to = newDate.getTime();
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void addListenerOnSpinnerItemSelection() {

        mCodeSpinner.setOnItemSelectedListener(new CodeSelectedListener());

        mAggSpinner.setOnItemSelectedListener(new AggrSelectedListener());

    }

    @Override
    protected void onDataReceived(List<Observation> data) {

        setData(data);
        super.onDataReceived(data);
    }

    public void setData(List<Observation> observations) {

        try {

            if (series != null) {


                cartesianChart.getAnnotations().clear();


                if (observations == null || (observations.size() == 0)) {


                    if (observations == null)
                        observations = new ArrayList<Observation>();
                    for (long d = 8 * 60 * 60 * 1000; d < 23 * 60 * 60 * 1000; d += 60 * 60 * 1000) {
                        Observation obs = new Observation();
                        obs.setValue(0);
                        obs.setTimestamp(d);
                        observations.add(obs);

                    }

                }


                maxX = -1000;
                minX = 1000;
                for (Observation o : observations) {

                    if (o.getValue() > maxX)
                        maxX = o.getValue();


                    if (o.getValue() < minX)
                        minX = o.getValue();

                    //   if(selectedAggr==2)
                    //     o.setToday();
                    o.calcDate();
                }

                if (selectedAggr == 1) {
                    hAxis.setDateTimeFormat(new SimpleDateFormat("MM/dd/yy"));
                    //  hAxis.setDateTimeComponent(DateTimeComponent.DATE);


                } else

                {


                    hAxis.setDateTimeFormat(new SimpleDateFormat("HH:mm"));
                    //    hAxis.setDateTimeComponent(DateTimeComponent.TIME_OF_DAY);


                }


                if (selectedAggr == 1) {


                    chart.getSeries().clear();
                    cartesianChart.getSeries().add(series);
                    cartesianChart.getSeries().add(bollingerBands);
                    cartesianChart.getSeries().add(movingAverage);
                    series.setData(observations);
                    movingAverage.setData(observations);
                    bollingerBands.setData(observations);
                } else {
                    chart.getSeries().clear();
                    cartesianChart.getSeries().add(series);
                    series.setData(observations);

                }

            }

            //cartesianChart.invalidate();
            // cartesianChart.invalidateOutline();


        } catch (Exception ex) {


            Log.d("Error ", "SS");
        }

        if (selectedAggr == 2) {


            new GetMedicationsTask(patient.Id, getAccessToken()).execute();


        }


    }

    private void initCache() {

        if (mMemoryCache == null) {

            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            PatientChartFragment.RetainFragment retainFragment =
                    PatientChartFragment.RetainFragment.findOrCreateRetainFragment(getFragmentManager());
            mMemoryCache = retainFragment.mRetainedCache;
            if (mMemoryCache == null) {
                mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                    @Override
                    protected int sizeOf(String key, Bitmap bitmap) {
                        // The cache size will be measured in kilobytes rather than
                        // number of items.
                        return bitmap.getByteCount() / 1024;
                    }
                };
                retainFragment.mRetainedCache = mMemoryCache;
            }
        }
    }

    protected void onMedReceived(MedIntakeListResult data) {


        if (data != null) {




            /*if(data.o!=null&&data.intakes.size()>0)
            {
                Calendar tmin = Calendar.getInstance();
                tmin.setTime(new Date(minT));

                Calendar tmax = Calendar.getInstance();
                tmax.setTime(new Date(maxT));
                hAxis.setMinimum(tmin);
                hAxis.setMaximum(tmax);
            }

        */


            for (MedicationOrder d : data.orders) {


                if (d.getStatus().toLowerCase().equals("active")) {
                    for (MedTiming t : d.getTimings()) {


                        Calendar calendar = Calendar.getInstance();
                        Date d0 = new Date(t.Time);
                        Date d1 = new Date(0);

                        d0.setYear(d1.getYear());
                        d0.setMonth(d1.getMonth());
                        d0.setDate(d1.getDate());
                        calendar.setTime(d0);


                        if (hAxis != null) {

                            if (hAxis.getMinimum().getTimeInMillis() > calendar.getTimeInMillis()) {
                                hAxis.setMinimum(calendar);

                            }


                            if (hAxis.getMaximum().getTimeInMillis() < calendar.getTimeInMillis()) {
                                hAxis.setMaximum(calendar);

                            }
                            CartesianGridLineAnnotation annotation = new CartesianGridLineAnnotation(hAxis, calendar);
                            cartesianChart.getAnnotations().add(annotation);

                            annotation.setStrokeColor(Color.BLACK);

                            annotation.setStrokeWidth(2);

                            //double maxX = vAxis.getMaximum();
                            //double minX = vAxis.getMinimum();

                            CartesianCustomAnnotation pill = new CartesianCustomAnnotation(vAxis, hAxis, (maxX - minX) / 2 + minX, calendar, t.Dose);


                            int n = Math.min(cartesianChart.getWidth(), cartesianChart.getHeight()) / 15;
                            cartesianChart.getAnnotations().add(pill);

                            // pill.setContentRenderer(new MedAnnotationRenderer(mMemoryCache,n));
                            pill.setContentRenderer(new CustomTextRenderer());

                        }

                    }

                }
            }
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

    private class CodeSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            if (codes != null) {

                selectedCode = codes.get(pos).Code;


            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private class AggrSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            selectedAggr = pos + 1;


        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            selectedAggr = 1;
        }

    }

    private class GetMedicationsTask extends AsyncTask<Void, Void, MedIntakeListResult> {


        private final String accessToken;
        private final String patientCode;

        public GetMedicationsTask(String p, String a) {
            accessToken = a;
            patientCode = p;

        }

        @Override
        protected MedIntakeListResult doInBackground(Void... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);

                MedIntakeListResult res = new MedIntakeListResult();
                List<MedicationOrder> orders = receiver.GetMedicationOrders(patientCode);
                res.orders = receiver.GetMedicationOrders(patientCode);


                return res;


            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }

        protected void onPostExecute(MedIntakeListResult result) {


            onMedReceived(result);


        }
    }

    private class MedAnnotationRenderer extends PDAnnotationRenderer {
        public MedAnnotationRenderer(LruCache<String, Bitmap> mm, int size) {
            super(mm, size);

        }

        @Override
        protected Resources getFragmentResources() {
            return getResources();
        }
    }


}
