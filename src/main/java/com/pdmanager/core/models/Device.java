package com.pdmanager.core.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by george on 17/8/2016.
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
    /**
     * Device GCM Identifier
     */
    public String Identifier;
    /***
     * Device Name
     */
    public String Name;
    /***
     * Device Model
     */
    public String Model;


    public Device(String pid, String id, String name, String model)

    {
        this.PatientId = pid;
        this.Identifier = id;
        this.Name = name;
        this.Model = model;

    }

    protected Device(Parcel in) {
        PatientId = in.readString();
        Identifier = in.readString();
        Name = in.readString();
        Model = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PatientId);
        dest.writeString(Identifier);
        dest.writeString(Name);
        dest.writeString(Model);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getPDType() {
        return "Device";
    }
}
