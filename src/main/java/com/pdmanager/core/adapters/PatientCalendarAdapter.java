package com.pdmanager.core.adapters;

import com.telerik.widget.calendar.CalendarAdapter;
import com.telerik.widget.calendar.CalendarDayCell;
import com.telerik.widget.calendar.RadCalendarView;

/**
 * Created by George on 6/4/2016.
 */
public class PatientCalendarAdapter extends CalendarAdapter {

    public PatientCalendarAdapter(RadCalendarView owner) {
        super(owner);
    }

    @Override
    public CalendarDayCell getDateCell() {
        PatientCalendarCell cell = new PatientCalendarCell(owner);

        this.dateCells.add(cell);

        return cell;
    }
}
