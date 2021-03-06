package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by george on 28/12/2016.
 */

public class Device extends PDEntity implements Parcelable {


    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    @SerializedName("Identifier")
    public String Identifier;
    @SerializedName("OrganizationId")
    public String OrganizationId;
    @SerializedName("Type")
    public String Type;
    @SerializedName("Note")
    public String Note;
    @SerializedName("Status")
    public String Status;
    @SerializedName("Manufacturer")
    public String Manufacturer;
    @SerializedName("Model")
    public String Model;
    @SerializedName("Version")
    public String Version;

    public Device(Parcel in) {
        Identifier = in.readString();
        PatientId = in.readString();
        OrganizationId = in.readString();
        Organization = OrganizationId;
        Type = in.readString();
        Note = in.readString();
        Status = in.readString();
        Manufacturer = in.readString();
        Model = in.readString();
        Version = in.readString();
    }

    public Device(String patientId, String id, String android, String deviceName) {
        super();

        PatientId = patientId;
        Identifier = id;
        Type = android;
        Model = deviceName;
        Status = "ACTIVE";

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Identifier);
        dest.writeString(PatientId);
        dest.writeString(OrganizationId);
        dest.writeString(Type);
        dest.writeString(Note);
        dest.writeString(Status);
        dest.writeString(Manufacturer);
        dest.writeString(Model);
        dest.writeString(Version);
    }

    @Override
    public String getPDType() {
        return "Device";
    }


    public void setStatus(String s) {
        Status = s;
    }
}
