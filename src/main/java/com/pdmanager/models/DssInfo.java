package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IP on 11/18/2017.
 */
public class DssInfo implements Parcelable {

    public static final Creator<DssInfo> CREATOR = new Creator<DssInfo>() {
        @Override
        public DssInfo createFromParcel(Parcel in) {
            return new DssInfo(in);
        }

        @Override
        public DssInfo[] newArray(int size) {
            return new DssInfo[size];
        }
    };
    public String Code;
    public String Value;
    public String Category;
    public String Priority;
    public String CreatedBy;
    public long Timestamp;

    protected DssInfo(Parcel in) {
        Code = in.readString();
        Value = in.readString();
        Category = in.readString();
        Priority = in.readString();
        CreatedBy = in.readString();
        Timestamp = in.readLong();
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Timestamp));
        return calendar.getTime();

    }

    public String getCategory() {

        return Category;
    }

    public String getPriority() {

        return Priority;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Code);
        dest.writeString(Value);
        dest.writeString(Category);
        dest.writeString(Priority);
        dest.writeString(CreatedBy);
        dest.writeLong(Timestamp);
    }
}
