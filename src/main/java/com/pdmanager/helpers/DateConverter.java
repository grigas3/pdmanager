package com.pdmanager.helpers;

import java.util.Calendar;

/**
 * Created by George on 6/5/2016.
 */
public class DateConverter {

    public static Calendar unixToCalendar(long unixTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixTime);
        return calendar;
    }




}
