package com.pdmanager.core.interfaces;

import com.pdmanager.core.models.LoginModel;

/**
 * Created by george on 17/6/2016.
 */
public interface ITokenUpdater {


    String getAccessToken();

    void updateToken(String token, int expiresin);

    LoginModel getLoginToken();

    boolean hasTokenExpired();
}
