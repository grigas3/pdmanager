package com.pdmanager.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by george on 6/1/2016.
 */
public class UsageStatistic extends PDEntity implements Parcelable {

    public static final Creator<UsageStatistic> CREATOR = new Creator<UsageStatistic>() {
        @Override
        public UsageStatistic createFromParcel(Parcel in) {
            return new UsageStatistic(in);
        }

        @Override
        public UsageStatistic[] newArray(int size) {
            return new UsageStatistic[size];
        }
    };
    @SerializedName("Timestamp")
    private long Timestamp;
    @SerializedName("Value")
    private double Value;
    @SerializedName("CodeId")
    private String CodeId;
    @SerializedName("PatientIdentifier")
    private String PatientIdentifier = "PAT01";
    @SerializedName("DeviceId")
    private String DeviceId = "DEV01";


    public UsageStatistic(double value, String code, long t, String patient, String deviceId) {

        Id = "newid";
        this.Value = value;
        this.CodeId = code;
        this.Timestamp = t;

        this.PatientIdentifier = patient;
        this.DeviceId = deviceId;
        //SetPatientIdentifier();
    }

    protected UsageStatistic(Parcel in) {
        Timestamp = in.readLong();
        Value = in.readDouble();
        CodeId = in.readString();
        PatientIdentifier = in.readString();
        DeviceId = in.readString();
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
        return CodeId;
    }

    public void setCode(String mCode) {
        this.CodeId = mCode;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Timestamp);
        dest.writeDouble(Value);
        dest.writeString(CodeId);
        dest.writeString(PatientIdentifier);
        dest.writeString(DeviceId);
    }
}
