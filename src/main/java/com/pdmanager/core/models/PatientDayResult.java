package com.pdmanager.core.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by George on 6/4/2016.
 */
public class PatientDayResult implements Parcelable {

    public static final Creator<PatientDayResult> CREATOR = new Creator<PatientDayResult>() {
        @Override
        public PatientDayResult createFromParcel(Parcel in) {
            return new PatientDayResult(in);
        }

        @Override
        public PatientDayResult[] newArray(int size) {
            return new PatientDayResult[size];
        }
    };
    public double TimeOff;
    public double TimeLID;
    public double TimeActive;
    public double TimeTremor;
    public double WalkingDistance;
    public boolean HasData;
    public long Timestamp;

    public PatientDayResult() {

    }

    public PatientDayResult(long t, double off, double lid, double tremor, double active, double wd) {
        TimeOff = off;
        TimeLID = lid;
        TimeActive = active;
        TimeTremor = tremor;
        WalkingDistance = wd;
        Timestamp = t;
        HasData = true;
    }

    protected PatientDayResult(Parcel in) {
        TimeOff = in.readDouble();
        TimeLID = in.readDouble();
        TimeActive = in.readDouble();
        TimeTremor = in.readDouble();
        WalkingDistance = in.readDouble();
        Timestamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(TimeOff);
        dest.writeDouble(TimeLID);
        dest.writeDouble(TimeActive);
        dest.writeDouble(TimeTremor);
        dest.writeDouble(WalkingDistance);
        dest.writeLong(Timestamp);
    }
}


