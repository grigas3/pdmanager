package com.pdmanager.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by George on 6/15/2016.
 */
public class LoginResult implements Parcelable {

    @SerializedName("access_token")
    public String access_token;
    @SerializedName("token_type")
    public String token_type;
    @SerializedName("expires_in")
    public int expires_in;
    @SerializedName("success")
    public boolean success;
    @SerializedName("id")
    public String id;
    @SerializedName("role")
    public String role;
    @SerializedName("rolemapid")
    public String rolemapid;

    public LoginResult()
    {

    }
    protected LoginResult(Parcel in) {
        access_token = in.readString();
        token_type = in.readString();
        expires_in = in.readInt();
        success = in.readByte() != 0;
        id = in.readString();
        role = in.readString();
        rolemapid = in.readString();
    }

    public static final Creator<LoginResult> CREATOR = new Creator<LoginResult>() {
        @Override
        public LoginResult createFromParcel(Parcel in) {
            return new LoginResult(in);
        }

        @Override
        public LoginResult[] newArray(int size) {
            return new LoginResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(access_token);
        dest.writeString(token_type);
        dest.writeInt(expires_in);
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(id);
        dest.writeString(role);
        dest.writeString(rolemapid);
    }
}
