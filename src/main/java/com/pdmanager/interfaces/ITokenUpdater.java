package com.pdmanager.interfaces;

import com.pdmanager.models.LoginModel;

/**
 * Created by george on 17/6/2016.
 */
public interface ITokenUpdater {


    String getAccessToken();

    void updateToken(String token, int expiresin);

    LoginModel getLoginToken();

    boolean hasTokenExpired();
}
