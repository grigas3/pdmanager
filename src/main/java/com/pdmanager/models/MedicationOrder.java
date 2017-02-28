package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by George on 6/14/2016.
 */
public class MedicationOrder extends PDEntity implements Parcelable {
    public static final Creator<MedicationOrder> CREATOR = new Creator<MedicationOrder>() {
        @Override
        public MedicationOrder createFromParcel(Parcel in) {
            return new MedicationOrder(in);
        }

        @Override
        public MedicationOrder[] newArray(int size) {
            return new MedicationOrder[size];
        }
    };
    /// <summary>
    /// Prescribed By Name
    /// </summary>
    public String PrescribedBy;
    /// <summary>
    /// Date Written
    /// </summary>
    public String DateWriten;
    /// <summary>
    /// Date Ended
    /// </summary>
    public String DateEnded;
    public String Reasonended;
    /// <summary>
    /// Reason for medication
    /// </summary>
    public String Reason;
    /// <summary>
    /// 	Information about the prescription
    /// </summary>
    public String Note;


    /// <summary>
    /// Why prescription was stopped
    /// </summary>
    //  public CodeableConcept reasonEnded { get; set; }


    /// <summary>
    /// Why prescription was stopped
    /// </summary>
    //    public CodeableConcept reason { get; set; }
    public String Status;
    /// <summary>
    /// Instruction to patient
    /// </summary>
    public String Instructions;
    public String Timing;
    public String MedicationId;
    /// <summary>
    /// Medication ID
    /// </summary>
    public long Timestamp;


    /// <summary>
    /// active | on-hold | completed | entered-in-error | stopped | draft
    /// </summary>
    //  public Coding status { get; set; }


    protected MedicationOrder(Parcel in) {

        Id = in.readString();
        PatientId = in.readString();
        PrescribedBy = in.readString();
        DateWriten = in.readString();
        DateEnded = in.readString();
        Reasonended = in.readString();
        Reason = in.readString();
        Note = in.readString();
        Status = in.readString();
        Instructions = in.readString();
        MedicationId = in.readString();
        Timing = in.readString();
        Timestamp = in.readLong();
    }



    public MedicationOrder() {
    }

    @Override
    public String getPDType() {
        return "MedicationOrder";
    }

    public List<MedTiming> getTimings() {


        if (this.Timing != null) {
            Gson json = new Gson();
            List<MedTiming> timings = json.fromJson(this.Timing, new TypeToken<List<MedTiming>>() {
            }.getType());

            return timings;
        } else {

            return new ArrayList<MedTiming>();

        }


    }

    public void setTimings(List<MedTiming> timings)
    {

        Gson json = new Gson();
        Timing= json.toJson(timings, new TypeToken<List<MedTiming>>(){
        }.getType());
    }

    public String getTimingsAsString() {

        List<MedTiming> timings = getTimings();
        StringBuilder str = new StringBuilder();
        for (MedTiming t : timings) {

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t.Time);
            SimpleDateFormat f = new SimpleDateFormat("HH:mm");


            str.append(f.format(c.getTime()) + "/" + t.Dose + " ");


        }


        return str.toString();


    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String s) {

        this.Status = s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(PatientId);
        dest.writeString(PrescribedBy);
        dest.writeString(DateWriten);
        dest.writeString(DateEnded);
        dest.writeString(Reasonended);
        dest.writeString(Reason);
        dest.writeString(Note);
        dest.writeString(Status);
        dest.writeString(Instructions);
        dest.writeString(MedicationId);
        dest.writeString(Timing);
        dest.writeLong(Timestamp);
    }
    /// <summary>
    /// Medication
    /// </summary>

    //[Required]
    //public PrescribedMedication medication { get; set; }


    /// <summary>
    /// Patient
    /// </summary>


    /// <summary>
    /// Prescriber
    /// </summary>
    //public virtual  Prescriber { get; set; }


}
