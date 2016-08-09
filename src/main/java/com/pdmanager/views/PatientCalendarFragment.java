package com.pdmanager.views;

/**
 * Created by George on 6/4/2016.
 */

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.pdmanager.core.R;
import com.pdmanager.core.communication.DataReceiver;
import com.pdmanager.core.models.Observation;
import com.pdmanager.core.models.Patient;
import com.pdmanager.core.models.PatientCalendarResult;
import com.pdmanager.helpers.DateConverter;
import com.pdmanager.views.calendar.BaseCalendarFragment;
import com.telerik.android.common.Function;
import com.telerik.android.common.Procedure;
import com.telerik.android.common.Util;
import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.CalendarCellType;
import com.telerik.widget.calendar.CalendarDayCell;
import com.telerik.widget.calendar.CalendarDisplayMode;
import com.telerik.widget.calendar.CalendarSelectionMode;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.ScrollMode;
import com.telerik.widget.calendar.WeekNumbersDisplayMode;
import com.telerik.widget.calendar.events.Event;
import com.telerik.widget.calendar.events.EventRenderMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PatientCalendarFragment extends BaseCalendarFragment {

    public static final int SELECTION_WEEK = 0;
    public static final int SELECTION_MONTH = 1;
    public static final int SELECTION_YEAR = 2;
    private List<Long> hasData = new ArrayList<Long>();
    private RadCalendarView calendarView;
    private Spinner calendarModesSpinner;
    private ListView listEvents;
    private Button btnShowToday;
    private Button btnShowData;
    private TextView txtDayTitle;
    private ImageButton btnUpCaret;
    private Patient patient;

    public PatientCalendarFragment() {
    }

    public void setPatient(Patient p) {

        patient = p;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {


        // Save the user's current game state
        outState.putParcelable("Patient", patient);


        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_calendar_main, null);

        if (savedInstanceState != null) {

            patient = savedInstanceState.getParcelable("Patient");

        }


        this.calendarView = (RadCalendarView) root.findViewById(R.id.calendarView);


        this.btnShowData = (Button) root.findViewById(R.id.btnGotoDay);

        this.btnShowToday = (Button) root.findViewById(R.id.btnShowToday);
        this.btnUpCaret = (ImageButton) root.findViewById(R.id.btnUpCaret);
        this.btnUpCaret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarView.getDisplayMode() == CalendarDisplayMode.Week) {
                    calendarModesSpinner.setSelection(SELECTION_MONTH);
                } else if (calendarView.getDisplayMode() == CalendarDisplayMode.Month) {
                    calendarModesSpinner.setSelection(SELECTION_YEAR);
                }
            }
        });
        this.txtDayTitle = (TextView) root.findViewById(R.id.txtDayTitle);
        this.btnShowToday.setText(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
        this.btnShowToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar today = Calendar.getInstance();
                calendarView.setDisplayDate(today.getTimeInMillis());

                if (calendarView.getDisplayMode() == CalendarDisplayMode.Week) {
                    ArrayList<Long> dates = new ArrayList<Long>();
                    dates.add(today.getTimeInMillis());
                    calendarView.setSelectedDates(dates);
                }

                listEvents.setAdapter(new EventsListAdapter(getActivity(), 0, today.getTimeInMillis()));
                updateWeekModeDayString(true);
            }
        });
        this.calendarView.setScrollMode(ScrollMode.Combo);
        final Calendar calendar = Calendar.getInstance();
        this.calendarView.setDateToColor(new Function<Long, Integer>() {
            @Override
            public Integer apply(Long argument) {
                calendar.setTimeInMillis(argument);
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                    return Color.RED;

                return null;
            }
        });
        final int colorEnabled = calendarView.getAdapter().getDateCellBackgroundColorEnabled();
        final int colorDisabled = calendarView.getAdapter().getDateCellBackgroundColorDisabled();
        final int borderColor = Color.parseColor("#f1891b");
        final float borderWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 3);
        final Bitmap sun = BitmapFactory.decodeResource(getResources(), R.drawable.ic_calendar_sun);


        this.btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                List<Long> selectedDates = calendarView.getSelectedDates();
                if (selectedDates.size() > 0) {


                    DayOverViewFragment newFragment = new DayOverViewFragment();
                    newFragment.setPatient(patient);
                    newFragment.setDay(selectedDates.get(0));
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    //                  ((OnDestinationSelectedListener)newFragment).onDestinationSelected((Destination)adapter.getItem(slideLayoutManager.getCurrentPosition()));
                    transaction.replace(R.id.container, newFragment);
//
                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    transaction.commit();


                }
                /*Calendar today = Calendar.getInstance();
                calendarView.setDisplayDate(today.getTimeInMillis());

                if (calendarView.getDisplayMode() == CalendarDisplayMode.Week) {
                    ArrayList<Long> dates = new ArrayList<Long>();
                    dates.add(today.getTimeInMillis());
                    calendarView.setSelectedDates(dates);
                }

                listEvents.setAdapter(new EventsListAdapter(getActivity(), 0, today.getTimeInMillis()));
                updateWeekModeDayString(true);
                */
            }
        });

        this.calendarView.setSelectionMode(CalendarSelectionMode.Single);
        this.calendarView.setOnDisplayDateChangedListener(new RadCalendarView.OnDisplayDateChangedListener() {
            @Override
            public void onDisplayDateChanged(long oldValue, long newValue) {
                ((CalendarModesSpinnerAdapter) calendarModesSpinner.getAdapter()).notifyDataSetChanged();
            }
        });
        this.calendarView.setOnCellClickListener(new RadCalendarView.OnCellClickListener() {
            @Override
            public void onCellClick(CalendarCell clickedCell) {
                if (clickedCell instanceof CalendarDayCell) {
                    List<Long> selectedDates = calendarView.getSelectedDates();

                    if (selectedDates.size() > 0) {
                        if (calendarView.getDisplayMode() != CalendarDisplayMode.Week) {
                            calendarView.setDisplayDate(selectedDates.get(0));
                            calendarModesSpinner.setSelection(SELECTION_WEEK);
                        } else {
                            listEvents.setAdapter(new EventsListAdapter(getActivity(), 0, selectedDates.get(0)));
                            updateWeekModeDayString(false);
                        }
                    }
                }
            }
        });
        this.calendarView.setOnDisplayModeChangedListener(new RadCalendarView.OnDisplayModeChangedListener() {
            @Override
            public void onDisplayModeChanged(CalendarDisplayMode oldValue, CalendarDisplayMode newValue) {
                switch (newValue) {
                    case Week:
                        calendarModesSpinner.setSelection(SELECTION_WEEK);
                        break;
                    case Month:
                        calendarModesSpinner.setSelection(SELECTION_MONTH);
                        break;
                    case Year:
                        calendarModesSpinner.setSelection(SELECTION_YEAR);
                }
            }
        });
        this.listEvents = (ListView) root.findViewById(R.id.listEvents);
        this.calendarModesSpinner = (Spinner) root.findViewById(R.id.calendarModesSpinner);
        this.calendarModesSpinner.setAdapter(new CalendarModesSpinnerAdapter());
        this.calendarModesSpinner.setSelection(SELECTION_MONTH);
        this.calendarModesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SELECTION_WEEK:
                        calendarView.changeDisplayMode(CalendarDisplayMode.Week, true);
                        long date;
                        if (calendarView.getSelectedDates() != null && calendarView.getSelectedDates().size() > 0) {
                            date = calendarView.getSelectedDates().get(0);
                        } else {
                            date = calendarView.getDisplayDate();
                        }
                        listEvents.setAdapter(new EventsListAdapter(getActivity(), 0, date));
                        updateWeekModeDayString(false);
                        btnShowToday.setVisibility(View.VISIBLE);
                        btnUpCaret.setVisibility(View.VISIBLE);
                        break;
                    case SELECTION_MONTH:
                        calendarView.changeDisplayMode(CalendarDisplayMode.Month, true);
                        listEvents.setAdapter(null);
                        btnShowToday.setVisibility(View.VISIBLE);
                        btnUpCaret.setVisibility(View.VISIBLE);
                        break;
                    case SELECTION_YEAR:
                        calendarView.changeDisplayMode(CalendarDisplayMode.Year, true);
                        listEvents.setAdapter(null);
                        btnShowToday.setVisibility(View.INVISIBLE);
                        btnUpCaret.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        this.calendarView.setShowTitle(false);
        updateHandledGestures(this.calendarView);
        boolean isLandscape = false;
        boolean isXLarge = false;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            isXLarge = true;
        }
        if (isXLarge || isLandscape) {
            this.calendarView.getEventAdapter().getRenderer().setEventRenderMode(EventRenderMode.Text);
            this.calendarView.setWeekNumbersDisplayMode(WeekNumbersDisplayMode.None);
        } else {
            this.calendarView.getEventAdapter().getRenderer().setEventRenderMode(EventRenderMode.Shape);
            this.calendarView.setWeekNumbersDisplayMode(WeekNumbersDisplayMode.Inline);
        }
        //   CalendarEventsHelper calendarEventsHelper = new CalendarEventsHelper();
        //   this.calendarView.getEventAdapter().setEvents(calendarEventsHelper.generateEvents());
        //   this.calendarView.notifyDataChanged();

        if (patient != null)
            new GetPatientsTask(this.calendarView, patient.Id, getAccessToken()).execute();

        return root;
    }

    private void increaseDisplayDate() {
        Calendar c = Calendar.getInstance();
        Long date;
        if (calendarView.getSelectedDates() != null && calendarView.getSelectedDates().size() > 0) {
            date = calendarView.getSelectedDates().get(0);
        } else {
            date = calendarView.getDisplayDate();
        }
        c.setTimeInMillis(date);
        c.add(Calendar.DATE, 1);
        ArrayList<Long> dates = new ArrayList<Long>();
        dates.add(c.getTimeInMillis());
        calendarView.setDisplayDate(c.getTimeInMillis());
        calendarView.setSelectedDates(dates);
    }

    private void decreaseDisplayDate() {
        Calendar c = Calendar.getInstance();
        Long date;
        if (calendarView.getSelectedDates() != null && calendarView.getSelectedDates().size() > 0) {
            date = calendarView.getSelectedDates().get(0);
        } else {
            date = calendarView.getDisplayDate();
        }
        c.setTimeInMillis(date);
        c.add(Calendar.DATE, -1);
        ArrayList<Long> dates = new ArrayList<Long>();
        dates.add(c.getTimeInMillis());
        calendarView.setDisplayDate(c.getTimeInMillis());
        calendarView.setSelectedDates(dates);
    }

    private void updateHandledGestures(RadCalendarView calendarView) {
        calendarView.setHorizontalScroll(false);
        calendarView.getGestureManager().setSwipeUpToChangeDisplayMode(false);
        //calendarView.getGestureManager().setTapToChangeDisplayMode(true);
        calendarView.getGestureManager().setSwipeDownToChangeDisplayMode(true);

    }

    private void updateWeekModeDayString(boolean showToday) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(calendarView.getDisplayDate());
        if (!showToday && calendarView.getSelectedDates() != null && calendarView.getSelectedDates().size() > 0) {
            currentDate.setTimeInMillis(calendarView.getSelectedDates().get(0));
        }
        txtDayTitle.setText(String.format("%s, %s %d",
                currentDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).toUpperCase(),
                currentDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).toUpperCase(),
                currentDate.get(Calendar.DAY_OF_MONTH)));
    }

    private class GetPatientsTask extends AsyncTask<Void, Void, PatientCalendarResult> {


        private RadCalendarView calendarView;
        private String code;
        private String accessToken;

        public GetPatientsTask(RadCalendarView pcalendarView, String pcode, String a) {

            this.calendarView = pcalendarView;
            this.code = pcode;
            this.accessToken = a;

        }

        @Override
        protected PatientCalendarResult doInBackground(Void... clientParams) {

            BandPendingResult<ConnectionState> pendingResult = null;
            try {
                DataReceiver receiver = new DataReceiver(accessToken);


                List<Observation> total = receiver.GetObservations(code, "LID;OFF;MED_ADH;DATA", 0, 0, 1);
                /*List<Observation> lid=receiver.GetObservations(code,"LID",0,0,1);
                List<Observation> tremor=receiver.GetObservations(code,"TREMOR_C",0,0,1);
                List<Observation> data=receiver.GetObservations(code,"DATA",0,0,1);
*/
                List<Observation> off = new ArrayList<Observation>();
                List<Observation> lid = new ArrayList<Observation>();
                List<Observation> tremor = new ArrayList<Observation>();
                List<Observation> med_adh = new ArrayList<Observation>();
                List<Observation> data = new ArrayList<Observation>();

                for (Observation o : total) {

                    if (o.getCode() != null && o.getCode().equals("LID"))
                        lid.add(o);
                        ///  else if(o.getCode()!=null&&o.getCode().equals("TREMOR_C"))
                        //      tremor.add(o);
                    else if (o.getCode() != null && o.getCode().equals("OFF"))
                        off.add(o);
                    else if (o.getCode() != null && o.getCode().equals("MED_ADH"))
                        med_adh.add(o);
                    else
                        data.add(o);

                }
                PatientCalendarResult res = new PatientCalendarResult();

                res.lid = lid;
                res.tremor = tremor;
                res.off = off;
                res.med_adh = med_adh;
                res.data = data;


                return res;
            } catch (Exception ex) {

                //Util.handleException("Getting data", ex);
                return null;
                // handle BandException
            }
        }


        private List<Event> eventConterter(PatientCalendarResult day) {
            ArrayList<Event> newEventList = new ArrayList<Event>();
            for (Observation d : day.data) {
                long key = d.getTimestamp();
                hasData.add(key);
            }
            if (day != null && day.tremor != null) {
                for (Observation d : day.tremor) {

                    Calendar eventDate = DateConverter.unixToCalendar(d.getTimestamp());

                    int constMult = 1;
                    long key = d.getTimestamp();


                    if (d.getValue() > 0.5) {
                        Event event = new Event("Time spent with tremor more than 50%", d.getTimestamp(), d.getTimestamp() + 60 * 60 * 1000);
                        event.setEventColor(Color.parseColor("#F35755"));
                        event.setAllDay(true);


                        newEventList.add(event);
                    }


                }

            }

            if (day != null && day.lid != null) {
                for (Observation d : day.lid) {

                    Calendar eventDate = DateConverter.unixToCalendar(d.getTimestamp());

                    int constMult = 1;
                    long key = d.getTimestamp();


                    if (d.getValue() > 0.5) {
                        Event event = new Event("Time spent with dyskinesia more than 50%", d.getTimestamp(), d.getTimestamp() + 60 * 60 * 1000);
                        event.setEventColor(Color.parseColor("#F35755"));
                        event.setAllDay(true);


                        newEventList.add(event);
                    }


                }

            }
            if (day != null && day.off != null) {
                for (Observation d : day.off) {

                    Calendar eventDate = DateConverter.unixToCalendar(d.getTimestamp());

                    int constMult = 1;
                    long key = d.getTimestamp();


                    if (d.getValue() > 0.5) {
                        Event event = new Event("Time spent with OFF more than 50%", d.getTimestamp(), d.getTimestamp() + 60 * 60 * 1000);
                        event.setEventColor(Color.parseColor("#F35755"));
                        event.setAllDay(true);


                        newEventList.add(event);
                    }


                }

            }

            if (day != null && day.med_adh != null) {
                for (Observation d : day.med_adh) {

                    Calendar eventDate = DateConverter.unixToCalendar(d.getTimestamp());

                    int constMult = 1;
                    long key = d.getTimestamp();


                    if (d.getValue() < 0.8) {
                        Event event = new Event("Medication adherence was less than 80%", d.getTimestamp(), d.getTimestamp() + 60 * 60 * 1000);
                        event.setEventColor(Color.parseColor("#F35755"));
                        event.setAllDay(true);


                        newEventList.add(event);
                    }


                }

            }
            return newEventList;

        }


        private boolean sameDay(long t1, long t2)

        {

            Date date1 = new java.util.Date(t1);
            Date date2 = new java.util.Date(t2);
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            return sameDay;

        }

        protected boolean dayHasData(long t) {

            for (long d : hasData) {


                if (sameDay(t, d))
                    return true;

            }

            return false;
        }

        protected void onPostExecute(PatientCalendarResult result) {

            this.calendarView.getEventAdapter().setEvents(eventConterter(result));

            this.calendarView.notifyDataChanged();
            final int colorEnabled = calendarView.getAdapter().getDateCellBackgroundColorEnabled();
            final int colorDisabled = calendarView.getAdapter().getDateCellBackgroundColorDisabled();

            final int borderColor = Color.parseColor("#f1891b");
            final float borderWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 3);
//            final Bitmap sun = BitmapFactory.decodeResource(getResources(), R.drawable.ic_calendar_sun);
            this.calendarView.setCustomizationRule(new Procedure<CalendarCell>() {
                @Override
                public void apply(CalendarCell argument) {


                    if (argument.getCellType() == CalendarCellType.Date) {


                        if (dayHasData(argument.getDate())) {
                            argument.setBackgroundColor(Color.parseColor("#f9cc9d"), Color.parseColor("#f9cc9d"));
                            argument.setBorderColor(borderColor);
                            argument.setBorderWidth(borderWidth);
                            //  argument.setBitmap(sun);
                        } else {
                            argument.setBackgroundColor(colorEnabled, colorDisabled);
                            argument.setBorderColor(Color.TRANSPARENT);
                            //    argument.setBitmap(null);
                        }
                    }

                }

            });


        }
    }

    class CalendarModesSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        public CalendarModesSpinnerAdapter() {
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return getHeaderText(position, true);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View rootView;
            if (convertView == null) {
                rootView = View.inflate(getActivity(), R.layout.calendar_main_spinner_item, null);
            } else {
                rootView = convertView;
            }

            TextView modeName = (TextView) rootView.findViewById(R.id.txtCalendarViewMode);
            TextView modeValue = (TextView) rootView.findViewById(R.id.txtCurrentModeValue);
            switch (position) {
                case 0:
                    modeName.setText("Week");
                    break;
                case 1:
                    modeName.setText("Month");
                    break;
                case 2:
                    modeName.setText("Year");
                    break;
            }
            modeValue.setText(getHeaderText(position, false));
            return rootView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View spinView;
            if (convertView == null) {
                spinView = View.inflate(getActivity(), R.layout.calendar_main_spinner_header, null);
            } else {
                spinView = convertView;
            }
            if (spinView != null) {
                TextView textView = (TextView) spinView.findViewById(R.id.txtCalendarViewMode);
                if (textView != null) {
                    textView.setText(getHeaderText(position, true));
                }
            }
            return spinView;
        }

        private String getHeaderText(int position, boolean includeYear) {
            Calendar currentValue = Calendar.getInstance();
            currentValue.setTimeInMillis(calendarView.getDisplayDate());
            switch (position) {
                case SELECTION_WEEK:
                    Calendar weekStart = (Calendar) currentValue.clone();
                    weekStart.set(Calendar.DAY_OF_WEEK, currentValue.getFirstDayOfWeek());
                    Calendar weekEnd = (Calendar) weekStart.clone();
                    weekEnd.add(Calendar.DATE, 6);
                    if (weekStart.get(Calendar.MONTH) == weekEnd.get(Calendar.MONTH)) {
                        return String.format("%s %d-%d",
                                weekStart.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                                weekStart.get(Calendar.DAY_OF_MONTH),
                                weekEnd.get(Calendar.DAY_OF_MONTH));
                    } else {
                        return String.format("%s %d - %s %d",
                                weekStart.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                                weekStart.get(Calendar.DAY_OF_MONTH),
                                weekEnd.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()),
                                weekEnd.get(Calendar.DAY_OF_MONTH));
                    }
                case SELECTION_MONTH:
                    if (includeYear) {
                        return String.format("%s %d",
                                currentValue.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
                                currentValue.get(Calendar.YEAR));
                    } else {
                        return String.format("%s",
                                currentValue.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
                    }
                case SELECTION_YEAR:
                    return String.format("%d", currentValue.get(Calendar.YEAR));
            }
            return null;
        }


    }

    class EventsListAdapter extends ArrayAdapter<Event> {

        private ArrayList<Event> events;

        public EventsListAdapter(Context context, int resource, long date) {
            super(context, resource);
            this.events = new ArrayList<Event>();
            List<Event> eventsForDate = calendarView.getEventAdapter().getEventsForDate(date);
            if (eventsForDate != null) {
                this.events.addAll(calendarView.getEventAdapter().getEventsForDate(date));
            }
        }


        @Override
        public int getCount() {
            return this.events.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView;
            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
            Calendar value = Calendar.getInstance();
            String formattedValue;
            Event e = events.get(position);
            if (e.isAllDay()) {
                rootView = View.inflate(getActivity(), R.layout.calendar_main_event_list_allday_item, null);
                rootView.setBackgroundColor(events.get(position).getEventColor());
            } else {

                rootView = View.inflate(getActivity(), R.layout.calendar_main_event_list_item, null);
                View eventColor = rootView.findViewById(R.id.eventColor);
                eventColor.setBackgroundColor(events.get(position).getEventColor());
                TextView txtStartDate = (TextView) rootView.findViewById(R.id.txtStart);
                TextView txtEndDate = (TextView) rootView.findViewById(R.id.txtEnd);

                value.setTimeInMillis(e.getStartDate());
                formattedValue = dateFormat.format(value.getTime());
                txtStartDate.setText(formattedValue);

                value.setTimeInMillis(e.getEndDate());
                formattedValue = dateFormat.format(value.getTime());
                txtEndDate.setText(formattedValue);
            }

            TextView txtEventName = (TextView) rootView.findViewById(R.id.txtEventTitle);
            txtEventName.setText(events.get(position).getTitle());

            return rootView;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }


    }
}
