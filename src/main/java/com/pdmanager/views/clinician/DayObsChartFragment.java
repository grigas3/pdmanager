package com.pdmanager.views.clinician;

/**
 * Created by George on 1/31/2016.
 */

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
import android.widget.Spinner;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.R;
import com.pdmanager.adapters.FetchObservationTask;
import com.pdmanager.adapters.ObservationParams;
import com.pdmanager.communication.DataReceiver;
import com.pdmanager.interfaces.IObservationDataHandler;
import com.pdmanager.models.MedIntakeListResult;
import com.pdmanager.models.MedicationIntake;
import com.pdmanager.models.MedicationOrder;
import com.pdmanager.models.Observation;
import com.pdmanager.models.ObservationCode;
import com.pdmanager.models.ObservationResult;
import com.pdmanager.views.charts.AreaFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.primitives.CustomTextRenderer;
import com.telerik.primitives.PDAnnotationRenderer;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.databinding.FieldNameDataPointBinding;
import com.telerik.widget.chart.visualization.annotations.cartesian.CartesianCustomAnnotation;
import com.telerik.widget.chart.visualization.annotations.cartesian.CartesianGridLineAnnotation;
import com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartPanZoomMode;
import com.telerik.widget.chart.visualization.cartesianChart.axes.DateTimeContinuousAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.LinearAxis;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.LineSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayObsChartFragment extends AreaFragment implements View.OnClickListener, IBasePatientChartFragment, IObservationDataHandler {
    private static final float VERTICAL_AXIS_SINGLE_STEP = 1;
    long day;
    LinearAxis vAxis;
    DateTimeContinuousAxis hAxis;
    Spinner mAggSpinner;
    Spinner mCodeSpinner;
    boolean enableSpinnerListener = false;
    List<ObservationCode> codes = null;
    private LineSeries series;
    private int selectedAggr = 2;
    private String selectedCode = null;
    private long maxT;
    private long minT;
    private double maxX;
    private double minX;
    private LruCache<String, Bitmap> mMemoryCache;


    //  BollingerBandsIndicator bollingerBands;
    //  MovingAverageIndicator movingAverage;

    public DayObsChartFragment() {


    }

    private void InitAxisMap() {


    }

    public void setDay(long pday) {

        day = pday;
    }

    @Override
    protected void restoreVariables(Bundle savedInstanceState) {


        day = savedInstanceState.getLong("Day");
        super.restoreVariables(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putLong("Day", day);

        super.onSaveInstanceState(outState);
    }

    protected int getLayoutID() {
        return R.layout.fragment_day_observations;
    }

    @Override
    protected void populateChart() {


        initCache();

        if (patient != null) {
            FetchData(patient.Id, selectedCode, day + 6 * 60 * 60 * 1000, day + 24 * 60 * 60 * 1000, 0);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onObservationReceived(ObservationResult result) {


        if (result.observations == null || (result.observations != null && result.observations.size() == 0)) {
            if (result.observations == null)
                result.observations = new ArrayList<Observation>();

            for (long d = day + 8 * 60 * 60 * 1000; d < day + 20 * 60 * 60 * 1000; d += 60 * 60 * 1000) {
                Observation obs = new Observation();
                obs.setValue(0);
                obs.setTimestamp(d);
                result.observations.add(obs);

            }

        }


        maxT = result.getmaxt();
        minT = result.getmint();
        if (maxT > 0)
            maxT = Math.max(result.getmaxt(), day + 21 * 60 * 60 * 1000);
        else
            maxT = day + 21 * 60 * 60 * 1000;
        if (minT > 0)
            minT = Math.min(result.getmint(), day + 6 * 60 * 60 * 1000);
        else
            minT = day + 6 * 60 * 60 * 1000;
/*
        maxX=result.getmax();
        minX=result.getmin();

        maxX=Math.floor(maxX+(maxX-minX)/3)+1;
        minX=Math.floor(minX - (maxX - minX) / 3);


        minX =Math.max(minX,0);
        */
        onDataReceived(result.observations);

    }

    public void updateChart(String code) {


        selectedCode = code;
        int selectedIndex = 0;
        ArrayList<String> list3 = new ArrayList<String>();
        for (int i = 0; i < codes.size(); i++) {


            if (selectedCode != null && selectedCode.equals(codes.get(i).Code)) {
                selectedIndex = i;
            }

        }
        mCodeSpinner.setSelection(selectedIndex);
        FetchData(patient.Id, selectedCode, day + 6 * 60 * 60 * 1000, day + 24 * 60 * 60 * 1000, 0);

    }

    protected void FetchData(String patient, String code, long from, long to, int aggregate) {
        new FetchObservationTask(this, getAccessToken()).execute(new ObservationParams(patient, code, from, to, aggregate));


    }

    @Override
    protected void prepareAreaChart() {


        if (patient != null) {


            try {
                PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
                manager.setHeader(rootView);

            } catch (Exception ex) {

            }
        }


        try {

            Bundle args = getArguments();
            if (args != null)
                selectedCode = args.getString("selectedCode");

        } catch (Exception e) {


        }


        vAxis = new LinearAxis();


        //  vAxis.setShowLabels(false);

        //vAxis.setMajorStep(VERTICAL_AXIS_SINGLE_STEP);

        hAxis = new DateTimeContinuousAxis();
        hAxis.setLabelFitMode(AxisLabelFitMode.ROTATE);
        hAxis.setDateTimeFormat(new SimpleDateFormat("HH:mm"));


        //      hAxis.setDateTimeComponent(DateTimeComponent.TIME_OF_DAY);
//

        series = new LineSeries();
        FieldNameDataPointBinding p1 = new FieldNameDataPointBinding("Date");
        FieldNameDataPointBinding p2 = new FieldNameDataPointBinding("Value");
        //series.setStrokeThickness(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2));
        series.setCategoryBinding(p1);
        series.setValueBinding(p2);

        /*series.setCategoryBinding(new Function<Observation, Calendar>() {
            @Override
            public Calendar apply(Observation argument) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(argument.getTimestamp()));

                return calendar;

            }
        });
        */






        /*bollingerBands = new BollingerBandsIndicator();
        bollingerBands.setCategoryBinding(p1);
        bollingerBands.setValueBinding(p2);
        bollingerBands.setPeriod(5);
        bollingerBands.setStandardDeviations(2);


        movingAverage = new MovingAverageIndicator();
        movingAverage.setCategoryBinding(p1);
        movingAverage.setValueBinding(p2);
        movingAverage.setPeriod(5);
*/
        //  series.setDataPointIndicatorRenderer(new SphericalDataPointIndicatorRenderer(series));
        cartesianChart.setVerticalAxis(vAxis);
        cartesianChart.setHorizontalAxis(hAxis);
        cartesianChart.getSeries().add(series);
        //    cartesianChart.getSeries().add(movingAverage);
        //      cartesianChart.getSeries().add(bollingerBands);


        ChartPanAndZoomBehavior panZoom = new ChartPanAndZoomBehavior();
        panZoom.setPanMode(ChartPanZoomMode.BOTH);
        panZoom.setZoomMode(ChartPanZoomMode.BOTH);
        this.chart.getBehaviors().add(panZoom);
        PreparePickers();


        if (selectedCode != null)
            FetchData(patient.Id, selectedCode, day + 6 * 60 * 60 * 1000, day + 24 * 60 * 60 * 1000, 0);


    }

    private void PreparePickers() {


        mCodeSpinner = (Spinner) rootView.findViewById(R.id.obsCodeSpinner);


        addListenerOnSpinnerItemSelection();
        initCodeSpinner();
        // new GetCodesTask().execute();
        enableSpinnerListener = true;


    }

    private void initCodeSpinner() {


        addItemsOnSpinners(getObservationsCodes());

    }

    // add items into spinner dynamically
    public void addItemsOnSpinners(List<ObservationCode> pcodes) {


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

    /*
        private class GetCodesTask extends AsyncTask<Void, Void, List<ObservationCode>> {



            @Override
            protected List<ObservationCode> doInBackground(Void... clientParams) {

                BandPendingResult<ConnectionState> pendingResult = null;
                try {
                    DataReceiver receiver=new DataReceiver();


                    return receiver.GetCodes();



                }
                catch (Exception ex) {
    setM
                    // Util.handleException("Getting data", ex);
                    return null;
                    // handle BandException
                }
            }

            protected void onPostExecute(List<ObservationCode> result) {


                    addItemsOnSpinners(result);
            }
        }
        */
    public void addListenerOnSpinnerItemSelection() {

        mCodeSpinner.setOnItemSelectedListener(new CodeSelectedListener());


    }

    @Override
    protected void onDataReceived(List<Observation> data) {


        setData(data);

        new GetMedicationsTask(patient.Id, getAccessToken()).execute();

        super.onDataReceived(data);


    }

    protected void onMedReceived(MedIntakeListResult data) {


        if (data != null) {

            cartesianChart.getAnnotations().clear();


/*
if(data.intakes!=null&&data.intakes.size()>0)
{
    Calendar tmin = Calendar.getInstance();
    tmin.setTime(new Date(minT));

    Calendar tmax = Calendar.getInstance();
    tmax.setTime(new Date(maxT));
    hAxis.setMinimum(tmin);
    hAxis.setMaximum(tmax);
}
*/

            for (MedicationIntake d : data.intakes) {


                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(d.Timestamp));


                if (hAxis != null) {


                    CartesianGridLineAnnotation annotation = new CartesianGridLineAnnotation(hAxis, calendar);
                    cartesianChart.getAnnotations().add(annotation);

                    if (d.getTaken())
                        annotation.setStrokeColor(Color.GREEN);
                    else
                        annotation.setStrokeColor(Color.RED);

                    annotation.setStrokeWidth(2);


                    //        double maxX=vAxis.getMaximum();
                    //      double minX=vAxis.getMinimum();

                    CartesianCustomAnnotation pill = new CartesianCustomAnnotation(vAxis, hAxis, (maxX - minX) / 2 + minX, calendar, d.Dose);
                    int n = Math.min(cartesianChart.getWidth(), cartesianChart.getHeight()) / 15;
                    cartesianChart.getAnnotations().add(pill);

                    // pill.setContentRenderer(new MedAnnotationRenderer(mMemoryCache,n));
                    pill.setContentRenderer(new CustomTextRenderer());

                    //    int n=Math.min(cartesianChart.getWidth(), cartesianChart.getHeight())/15;
                    //  cartesianChart.getAnnotations().add(pill);
                    //pill.setContentRenderer(new MedAnnotationRenderer(mMemoryCache,n));


                }


            }
        }


    }

    public void setData(List<Observation> res) {

        try {

            if (series != null) {


                maxX = -1000;
                minX = 1000;
                for (Observation o : res) {

                    if (o.getValue() > maxX)
                        maxX = o.getValue();


                    if (o.getValue() < minX)
                        minX = o.getValue();

                    //   if(selectedAggr==2)
                    //     o.setToday();
                    o.calcDate();
                }
             /*   vAxis.setMinimum(minX);
                vAxis.setMaximum(maxX);


                Calendar tmin = Calendar.getInstance();
                tmin.setTime(new Date(minT));

                Calendar tmax = Calendar.getInstance();
                tmax.setTime(new Date(maxT));

                hAxis.setMinimum(tmin);
                hAxis.setMaximum(tmax);

*/


                series.setData(res);

                //movingAverage.setData(res);
                //bollingerBands.setData(res);
            }


            //cartesianChart.invalidate();
            // cartesianChart.invalidateOutline();


        } catch (Exception ex) {


            Log.d("Error ", "SS");
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
                /*Date date1 =new java.util.Date();
                date1.setHours(0);
                date1.setMinutes(0);
                //Date date2= new java.util.Date(t2);
                Calendar cal1 = Calendar.getInstance();
                //Calendar cal2 = Calendar.getInstance();
                cal1.setTime(date1);
                long from=date1.getTime();*/
                res.intakes = receiver.GetMedicationIntakes(patientCode, day, day + 24 * 60 * 60 * 1000);


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
