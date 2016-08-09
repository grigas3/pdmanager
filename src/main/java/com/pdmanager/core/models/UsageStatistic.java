package com.pdmanager.core.models;


/**
 * Created by george on 6/1/2016.
 */
public class UsageStatistic extends PDEntity {


    private long Timestamp;
    private long Value;
    private String Code;
    private String PatientIdentifier = "PAT01";

    public UsageStatistic(long value, String code, long t, String patient) {
        this.Value = value;
        this.Code = code;
        this.Timestamp = t;

        this.PatientIdentifier = patient;
        //SetPatientIdentifier();
    }


    /*
    private void SetPatientIdentifier()
    {

        RecordingSettings settings= RecordingSettingsHandler.getInstance().getSettings();
        if(settings!=null)
        {

            PatientId=settings.getOrganization()+"_"+settings.getPatientID();

        }

    }
    */
    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.Timestamp = mTimestamp;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String mCode) {
        this.Code = mCode;
    }

    public double getValue() {
        return Value;
    }

    public void setValue(long mValue) {
        this.Value = mValue;
    }

    @Override
    public String getPDType() {
        return "UsageStatistic";
    }
}
