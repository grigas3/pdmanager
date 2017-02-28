package com.pdmanager.alerting;

import com.pdmanager.models.UserAlert;

/**
 * Created by george on 30/11/2015.
 */
public interface IUserAlertManager {


    /**
     * Add Alert with fields
     * @param alert
     * @param message
     * @param type
     * @param source
     * @param expiration
     */
    void add(String alert, String message, String type, String source, long expiration);

    /**
     * Add alert from UserAlert entity
     * @param alert
     */
    void add(UserAlert alert);

    void setNotified(UserAlert alert);
    boolean setNotified(String id);
    boolean setNotActive(String id);
    boolean clearAll();
    boolean anyActive();
    boolean anyUnNotified();
    UserAlert getActive();
    UserAlert getUnNotified();

    UserAlert getAlert(String alertId);


}
