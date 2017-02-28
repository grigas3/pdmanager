package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by george on 21/6/2016.
 */
public class PendingMedication implements Parcelable {


    public static final Creator<PendingMedication> CREATOR = new Creator<PendingMedication>() {
        @Override
        public PendingMedication createFromParcel(Parcel in) {
            return new PendingMedication(in);
        }

        @Override
        public PendingMedication[] newArray(int size) {
            return new PendingMedication[size];
        }
    };
    public String Id;
    public String MedOrderId;
    public String Medication;
    public long Time;
    public String Dose;
    public String Instructions;
    public String Status;

    protected PendingMedication(Parcel in) {

        Id = in.readString();
        MedOrderId = in.readString();
        Medication = in.readString();
        Time = in.readLong();
        Dose = in.readString();
        Instructions = in.readString();
        Status = in.readString();
    }


    /**
     *
     * @param id Id
     * @param mid Medication Order id
     * @param m Medication
     * @param t Time
     * @param d Dose
     * @param i Instructions
     * @param s Status
     */
    public PendingMedication(String id, String mid, String m, long t, String d, String i, String s) {


        this.Id = id;
        this.MedOrderId = mid;
        this.Medication = m;
        this.Time = t;
        this.Dose = d;
        this.Instructions = i;
        this.Status = s;

    }

    public PendingMedication() {


    }

    public String getTime() {


        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(this.Time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String shortTimeStr = sdf.format(cal1.getTime());

        return shortTimeStr;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(Id);
        dest.writeString(MedOrderId);
        dest.writeString(Medication);
        dest.writeLong(Time);
        dest.writeString(Dose);
        dest.writeString(Instructions);
        dest.writeString(Status);

    }

    public String getStatus() {

        return Status;
    }
}
