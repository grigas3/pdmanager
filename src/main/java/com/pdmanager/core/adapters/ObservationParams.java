package com.pdmanager.core.adapters;

import com.pdmanager.helpers.DateConverter;

import java.util.Date;
import java.util.List;

/**
 * Created by George on 6/5/2016.
 */
public class ObservationParams {

    public String patientCode;
    public String obsCode;
    public List<String> obsCodes;

    public Date from;
    public Date to;
    public int aggregate = -1;

    public boolean hasManyCodes = false;

    public ObservationParams(String ppatientCode, String pcode) {

        this.patientCode = ppatientCode;
        this.obsCode = pcode;
        this.from = null;
        this.to = null;
        hasManyCodes = false;
    }


    public ObservationParams(String ppatientCode, String pcode, Date pfrom, Date pto, int paggregate) {

        this.patientCode = ppatientCode;
        this.obsCode = pcode;
        this.from = pfrom;
        this.to = pto;
        this.aggregate = paggregate;
    }

    public ObservationParams(String ppatientCode, List<String> pcode, Date pfrom, Date pto, int paggregate) {
        hasManyCodes = true;
        this.patientCode = ppatientCode;
        this.obsCodes = pcode;
        this.from = pfrom;
        this.to = pto;
        this.aggregate = paggregate;
    }

    public ObservationParams(String ppatientCode, String pcode, long pfrom, long pto, int paggregate) {
        hasManyCodes = false;
        this.patientCode = ppatientCode;
        this.obsCode = pcode;
        this.from = DateConverter.unixToCalendar(pfrom).getTime();
        this.to = DateConverter.unixToCalendar(pto).getTime();
        this.aggregate = paggregate;
    }

    public ObservationParams(String ppatientCode, List<String> pcode, long pfrom, long pto, int paggregate) {
        hasManyCodes = true;
        this.patientCode = ppatientCode;
        this.obsCodes = pcode;
        this.from = DateConverter.unixToCalendar(pfrom).getTime();
        this.to = DateConverter.unixToCalendar(pto).getTime();
        this.aggregate = paggregate;
    }
}