package com.pdmanager.core.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by George on 6/5/2016.
 */
public class MedicationIntake extends PDEntity implements Parcelable {


    public static final Creator<MedicationIntake> CREATOR = new Creator<MedicationIntake>() {
        @Override
        public MedicationIntake createFromParcel(Parcel in) {
            return new MedicationIntake(in);
        }

        @Override
        public MedicationIntake[] newArray(int size) {
            return new MedicationIntake[size];
        }
    };
    public String MedOrderId;
    public String Name;
    /// <summary>
    /// Name
    /// </summary>
    public String Route;


    /// <summary>
    /// Route
    /// </summary>
    /// <summary>
    /// Indication
    /// </summary>
    public String Dose;
    /// <summary>
    /// Timestamp of the intake
    /// </summary>
    public long Timestamp;
    public String Note;


    public MedicationIntake() {


    }


    public MedicationIntake(String pid, String id, String name, String route, String dose, String note, long t) {


        this.Id = "newid";
        this.PatientId = pid;
        this.MedOrderId = id;
        this.Name = name;
        this.Route = route;
        this.Dose = dose;
        this.Note = note;
        this.Timestamp = t;


    }

    protected MedicationIntake(Parcel in) {
        PatientId = in.readString();
        MedOrderId = in.readString();
        Name = in.readString();
        Route = in.readString();
        Dose = in.readString();
        Timestamp = in.readLong();
        Note = in.readString();
    }

    public boolean getTaken() {

        if (Note != null) {

            String[] vals = Note.split(";");


            if (vals.length > 1) {
                return vals[1].equals("taken");

            }
            return false;

        }

        return false;


    }

    public String getTimingId() {
        if (Note != null) {

            String[] vals = Note.split(";");


            if (vals.length > 0) {
                return vals[0];

            }
            return "0";

        }

        return "0";


    }


    @Override
    public String getPDType() {
        return "MedicationIntake";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(PatientId);
        dest.writeString(MedOrderId);
        dest.writeString(Name);
        dest.writeString(Route);
        dest.writeString(Dose);
        dest.writeLong(Timestamp);
        dest.writeString(Note);
    }
}
