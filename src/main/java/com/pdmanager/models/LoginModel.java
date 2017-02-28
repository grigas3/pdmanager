package com.pdmanager.models;

/**
 * Created by George on 6/15/2016.
 */
public class LoginModel {

    String userName;
    String password;

    public LoginModel(String u, String p) {

        this.userName = u;
        this.password = p;

    }

    public String toJsonString() {

        return "username=" + userName + "&password=" + password + "&grant_type=password";

    }
}
