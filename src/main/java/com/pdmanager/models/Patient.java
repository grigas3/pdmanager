package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by george on 25/1/2016.
 */
public class Patient extends PDEntity implements Parcelable {
    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };
    /// PD Manager Identifier
    /// </summary>

    public String Code;
    /// <summary>
    /// Given Name
    /// </summary>
    public String Given;
    /// <summary>
    /// Family Name
    /// </summary>
    public String Family;
    /// <summary>
    /// Prefix
    /// </summary>
    public String Prefix;
    /// <summary>
    /// Phone
    /// </summary>
    public List<com.pdmanager.models.Telecom> Telecom;
    /// <summary>
    /// Phone
    /// </summary>
    public ArrayList<String> Allergies;
    /// <summary>
    /// Birth Date
    /// </summary>
    public String BirthDate;
    /// <summary>
    /// Last Visit
    /// </summary>
    public String LastVisitDate;
    /// <summary>
    /// Years with parkinson
    /// </summary>
    public String YWP;
    /// <summary>
    /// Status
    /// </summary>
    public String Status;
    /// <summary>
    /// MRN
    /// </summary>
    public String MRN;
    /***
     * Gender
     */
    public String Gender;
    /***
     * Clinical Information
     */
    public String ClinicalInfo;
    /***
     * Dss Information
     */
    public String DssInfo;


    protected Patient(Parcel in) {
        Code = in.readString();
        Id = in.readString();
        Given = in.readString();
        Family = in.readString();
        Prefix = in.readString();
        Gender = in.readString();
        MRN = in.readString();
        Status = in.readString();
//        Telecom = in.readString();
        BirthDate = in.readString();
        LastVisitDate = in.readString();
        YWP = in.readString();

        Allergies = new ArrayList<>();
        in.readStringList(Allergies);
        ClinicalInfo = in.readString();

/*
        parcel.writeString(MRN);
        parcel.writeString(Status);
        parcel.writeString(Phone);
        parcel.writeString(BirthDate);
        parcel.writeString(LastVisitDate);
        parcel.writeString(YWP);
        */
    }

    /***
     * Allergies
     */
    //public String Allergies;
    @Override
    public String getPDType() {
        return "Patient";
    }

    public String getPhone() {

        if (Telecom != null && Telecom.size() > 0) {

            //return Telecom.get(0).value;
            return "no phone";
        }

        return "-";
    }

    public void fillFields() {

        //  MRN="423434";
        Status = "Worsen";
        // Phone="+323342343";
        //BirthDate="07/02/1961";
        YWP = "5";
        // LastVisitDate="06/05/2016";


    }

    public List<String> getAllergies() {
        if (Allergies != null)
            return Allergies;

        return new ArrayList<String>();
    }

    public List<com.pdmanager.models.ClinicalInfo> getClinicalInfo() {

        try {


            if (ClinicalInfo != null) {


                Gson gson = new Gson();
                List<com.pdmanager.models.ClinicalInfo> observations = gson.fromJson(ClinicalInfo, new TypeToken<List<com.pdmanager.models.ClinicalInfo>>() {
                }.getType());

                return observations;
            }

        } catch (Exception ex) {

            Log.d("ERROR", "Converting Clinical info");
        }

        return new ArrayList<com.pdmanager.models.ClinicalInfo>();

    }


    public List<DssInfo> getDssInfo() {

        try {

            //DssInfo=ClinicalInfo;//TMP TMP TMP
            DssInfo = "[{\"Code\":\"Decision\",\"Value\":\"CHANGE\",\"Category\":\"PD\",\"Priority\":\"Normal\",\"CreatedBy\":\"Dss System\",\"Timestamp\":1466866597},{\"Code\":\"Rule\",\"Value\":\"The patient seems to have ……\",\"Category\":\"PD\",\"Priority\":\"Normal\",\"CreatedBy\":\"System\",\"Timestamp\":1466866597},{\"Code\":\"Medication_change\",\"Value\":\"Suggested modification of the treatment plan is Levodopa twice a day\",\"Category\":\"PD\",\"Priority\":\"Normal\",\"CreatedBy\":\"System\",\"Timestamp\":1466866597}]";
            if (DssInfo != null) {

                Gson gson = new Gson();
                List<DssInfo> dssInformation = gson.fromJson(DssInfo, new TypeToken<List<DssInfo>>() {
                }.getType());

                return dssInformation;
            }

        } catch (Exception ex) {

            Log.d("ERROR", "Converting Clinical info");
        }

        return new ArrayList<DssInfo>();

    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Code);
        parcel.writeString(Id);
        parcel.writeString(Given);
        parcel.writeString(Family);
        parcel.writeString(Prefix);
        parcel.writeString(Gender);
        parcel.writeString(MRN);
        parcel.writeString(Status);
        //parcel.writeString(Telecom);
        parcel.writeString(BirthDate);
        parcel.writeString(LastVisitDate);
        parcel.writeString(YWP);
        parcel.writeStringList(Allergies);
        parcel.writeString(ClinicalInfo);
    }
}
