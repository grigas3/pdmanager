package com.pdmanager.views;

/**
 * Created by George on 1/31/2016.
 */

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.core.R;
import com.pdmanager.core.communication.DataReceiver;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.ObservationCode;
import com.pdmanager.views.charts.AreaFragment;
import com.pdmanager.views.drawers.IBasePatientChartFragment;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.databinding.FieldNameDataPointBinding;
import com.telerik.widget.chart.visualization.cartesianChart.axes.CategoricalAxis;
import com.telerik.widget.chart.visualization.cartesianChart.axes.LinearAxis;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.BarSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DistributionChartFragment extends AreaFragment implements View.OnClickListener, IBasePatientChartFragment {
    private static final float VERTICAL_AXIS_SINGLE_STEP = 5;
    private static final float VERTICAL_AXIS_SINGLE_MIN = 60;
    private static final float VERTICAL_AXIS_SINGLE_MAX = 90;
    Spinner mCodeSpinner;
    boolean enableSpinnerListener = false;
    List<ObservationCode> codes = null;
    DatePickerDialog fromDatePickerDialog;
    DatePickerDialog toDatePickerDialog;
    Date from = null;
    Date to = null;
    EditText fromDateEtxt;
    EditText toDateEtxt;
    private BarSeries series;
    private SimpleDateFormat dateFormatter;
    private String selectedCode = "001";

    public DistributionChartFragment() {


    }

    protected int getLayoutID() {
        return R.layout.fragment_distribution;
    }

    @Override
    protected void populateChart() {


        if (patient != null)
            FetchData(patient.Id, selectedCode, from, to);

    }

    @Override
    public void onClick(View v) {
        if (v == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if (v == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }

    protected void FetchData(String patient, String code, Date from, Date to) {

        new GetObservationsTask(getAccessToken()).execute(new ObservationParams(patient, code, from, to));

    }

    @Override
    protected void prepareAreaChart() {


        if (patient != null) {

            PatientBaseChartFragmentManager manager = new PatientBaseChartFragmentManager(this);
            manager.setHeader(rootView);

        }


        series = new BarSeries();
        CategoricalAxis horizontal = new CategoricalAxis();
        horizontal.setLabelFitMode(AxisLabelFitMode.MULTI_LINE);

        LinearAxis vertical = new LinearAxis();

        cartesianChart.setHorizontalAxis(horizontal);
        cartesianChart.setVerticalAxis(vertical);
        cartesianChart.getSeries().add(series);

        series.setCategoryBinding(new FieldNameDataPointBinding("Value"));
        series.setValueBinding(new FieldNameDataPointBinding("Timestamp"));


        PreparePickers();
    }

    private void PreparePickers() {


        mCodeSpinner = (Spinner) rootView.findViewById(R.id.obsCodeSpinner);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        fromDateEtxt = (EditText) rootView.findViewById(R.id.fromPicker);
        toDateEtxt = (EditText) rootView.findViewById(R.id.toPicker);

        setDateTimeField();

        addListenerOnSpinnerItemSelection();
        initCodeSpinner();
        //new GetCodesTask().execute();
        enableSpinnerListener = true;


    }

    private void initCodeSpinner() {
        ArrayList<ObservationCode> codes = new ArrayList<ObservationCode>();

        codes.add(
                new ObservationCode("LID", "LID", "UPDRS"));
        codes.add(
                new ObservationCode("TREMOR", "TREMOR", "UPDRS"));

        codes.add(
                new ObservationCode("HR", "HR", "UPDRS"));


        addItemsOnSpinners(codes);

    }

    // add items into spinner dynamically
    public void addItemsOnSpinners(List<ObservationCode> pcodes) {


        this.codes = pcodes;


        ArrayList<String> list3 = new ArrayList<String>();
        for (int i = 0; i < codes.size(); i++) {

            list3.add(codes.get(i).Title);
        }

        ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list3);
        mCodeSpinner.setAdapter(dataAdapter3);


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

    }

    @Override
    protected void onDataReceived(List<Observation> data) {

        setData(data);
        super.onDataReceived(data);
    }

    public void setData(List<Observation> observations) {

        try {

            if (series != null)
                series.setData(observations);
            //cartesianChart.invalidate();
            // cartesianChart.invalidateOutline();


        } catch (Exception ex) {


            Log.d("Error ", "SS");
        }


    }

    private class ObservationParams {

        public String patientCode;
        public String obsCode;

        public Date from;
        public Date to;


        public ObservationParams(String ppatientCode, String pcode) {

            this.patientCode = ppatientCode;
            this.obsCode = pcode;
            this.from = null;
            this.to = null;
        }

        public ObservationParams(String ppatientCode, String pcode, Date pfrom, Date pto) {

            this.patientCode = ppatientCode;
            this.obsCode = pcode;
            this.from = pfrom;
            this.to = pto;
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


                return receiver.GetObservationDistribution(params.patientCode, params.obsCode, params.from, params.to);


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


}
