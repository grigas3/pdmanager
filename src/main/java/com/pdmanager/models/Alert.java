package com.pdmanager.models;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by george on 30/11/2015.
 */
public class Alert extends PDEntity implements Parcelable {


    public String Title;
    public String Message;
    public String Type;
    public long Expiration;
    public long Timestamp;
    public String Source = "PAT01";
    public boolean UserNotified;

    /**
     * Alert Constructor

     * @param pTitle Alert Title
     * @param pMessage Alert Message
     * @param pCode Alert Code
     * @param pTimestamp
     * @param pExpiration
     * @param pSource
     */
    public Alert(String pTitle, String pMessage, String pCode, long pTimestamp, long pExpiration,String pSource) {

        Id = "newid";
        this.Title = pTitle;
        this.Message = pMessage;
        this.Timestamp = pTimestamp;
        this.Type = pCode;
        this.Expiration = pExpiration;
        this.Source = pSource;
        UserNotified=false;

    }
    /**
     * Alert Constructor

     * @param pTitle Alert Title
     * @param pMessage Alert Message
     * @param pCode Alert Code
     * @param pTimestamp

     * @param pSource
     */
    public Alert(String pTitle, String pMessage, String pCode, long pTimestamp, String pSource) {


        Id = "newid";
        this.Title = pTitle;
        this.Message = pMessage;
        this.Timestamp = pTimestamp;
        this.Type = pCode;
        this.Expiration = 0;
        this.Source = pSource;
        UserNotified=false;

    }
    /**
     * Alert Constructor
     * @param pid Id
     * @param pTitle Alert Title
     * @param pMessage Alert Message
     * @param pCode Alert Code
     * @param pTimestamp
     * @param pExpiration
     * @param pSource
     */
    public Alert(String pid,String pTitle, String pMessage, String pCode,long pTimestamp, long pExpiration,  String pSource) {


        Id =pid;
        this.Title = pTitle;
        this.Message = pMessage;
        this.Timestamp = pTimestamp;
        this.Type = pCode;
        this.Expiration = pExpiration;
        this.Source = pSource;
        UserNotified=false;

    }
    protected Alert(Parcel in) {

        Id = "newid";
        Title = in.readString();
        Message = in.readString();
        Type = in.readString();
        Expiration = in.readLong();
        Timestamp = in.readLong();
        Source = in.readString();

    }



    public static final Creator<Alert> CREATOR = new Creator<Alert>() {
        @Override
        public Alert createFromParcel(Parcel in) {
            return new Alert(in);
        }

        @Override
        public Alert[] newArray(int size) {
            return new Alert[size];
        }
    };

    public String getId() {
        return Id;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long mTimestamp) {
        this.Timestamp = mTimestamp;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String mSource) {
        this.Title = mSource;
    }

    public String getAlertType() {

        return Type.toLowerCase();
    }

    public void setAlertType(String mType) {
        this.Type = mType;
    }


    public String getMessage() {
        return Message;
    }

    public void setMessage(String mMessage) {
        this.Message = mMessage;
    }



    public long getExpiration() {
        return Expiration;
    }

    public void setExpiration(long pExpiration) {
        this.Expiration = pExpiration;
    }


    public String getSource() {
        return Source;
    }

    public void setSource(String mSource) {
        this.Source = mSource;
    }

    @Override
    public String getPDType() {
        return "Alert";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeString(Message);
        dest.writeString(Type);
        dest.writeLong(Expiration);
        dest.writeLong(Timestamp);
        dest.writeString(Source);
    }
}
