package com.pdmanager.models;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by george on 6/1/2016.
 */
public class Observation extends PDEntity {


    private long Timestamp;
    private double Value;
    private String CodeId;
    private Calendar Date;

    public Observation() {

    }

    public Observation(double value, String code, long t, String patient) {

        Id = "newid";
        this.Value = value;
        this.CodeId = code;
        this.Timestamp = t;
        this.PatientId = patient;

        //SetPatientIdentifier();
    }

    public Observation(double value, String pid, String code, long t) {
        Id = "newid";
        this.Value = value;
        this.CodeId = code;
        this.Timestamp = t;
        this.PatientId = pid;


    }

    public void calcDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Timestamp));

        Date = calendar;
    }

    public void setToday() {
        Date d0 = new Date(Timestamp);
        Date d1 = new Date();


        d0.setYear(d1.getYear());
        d0.setMonth(d1.getMonth());
        d0.setDate(d1.getDate());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d0);

        Timestamp = calendar.getTimeInMillis();


    }

    public String getCategory() {

        if (CodeId != null) {


            if (CodeId.toLowerCase().startsWith("tremor") || CodeId.toLowerCase().startsWith("lid")
                    || CodeId.toLowerCase().startsWith("gait")
                    || CodeId.toLowerCase().startsWith("off")
                    || CodeId.toLowerCase().startsWith("fog")
                    || CodeId.toLowerCase().startsWith("brad")
                    )
                return "Motor Symptoms";


            else if (CodeId.toLowerCase().startsWith("med"))
                return "Medication";
            else if (CodeId.toLowerCase().startsWith("act"))
                return "Activity";
            else if (CodeId.toLowerCase().startsWith("test"))
                return "Tests";
            else if (CodeId.toLowerCase().startsWith("nutr"))
                return "Nutrition";

        }
        return "?";

    }

    public String getCategoryCode() {

        if (CodeId != null) {


            if (CodeId.toLowerCase().startsWith("tremor") || CodeId.toLowerCase().startsWith("lid")
                    || CodeId.toLowerCase().startsWith("gait")
                    || CodeId.toLowerCase().startsWith("off")
                    || CodeId.toLowerCase().startsWith("fog")
                    || CodeId.toLowerCase().startsWith("brad")
                    )
                return "MOTOR";


            else if (CodeId.toLowerCase().startsWith("med"))
                return "MED";

            else if (CodeId.toLowerCase().startsWith("act"))
                return "ACT";
            else if (CodeId.toLowerCase().startsWith("test"))
                return "TEST";
            else if (CodeId.toLowerCase().startsWith("nutr"))
                return "NUTR";
        }
        return "?";

    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Timestamp));
        return calendar.getTime();

    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.Timestamp = mTimestamp;
    }

    public String getCode() {
        return CodeId;
    }

    public void setCode(String mCode) {
        this.CodeId = mCode;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String pid) {
        this.PatientId = pid;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(double mValue) {
        this.Value = mValue;
    }

    @Override
    public String getPDType() {
        return "Observation";
    }
}
