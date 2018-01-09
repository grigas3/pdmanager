package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by george on 28/12/2016.
 */

public class DeviceResult implements Parcelable {

    public static final Creator<DeviceResult> CREATOR = new Creator<DeviceResult>() {
        @Override
        public DeviceResult createFromParcel(Parcel in) {
            return new DeviceResult(in);
        }

        @Override
        public DeviceResult[] newArray(int size) {
            return new DeviceResult[size];
        }
    };
    @SerializedName("HasError")

    public boolean HasError;
    @SerializedName("DeviceId")
    public String DeviceId;
    @SerializedName("Error")
    public String Error;

    public DeviceResult() {

    }

    protected DeviceResult(Parcel in) {
        HasError = in.readByte() != 0;
        DeviceId = in.readString();
        Error = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (HasError ? 1 : 0));
        dest.writeString(DeviceId);
        dest.writeString(Error);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
