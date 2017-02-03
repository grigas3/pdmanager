package com.pdmanager.core.alerting;

import com.pdmanager.core.models.Alert;
import com.pdmanager.core.models.UserAlert;

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

    void notify(UserAlert alert);
    boolean setNotified(String id);
    boolean setNotActive(String id);
    boolean clearAll();
    boolean anyActive();
    boolean anyUnNotified();
    UserAlert getActive();
    UserAlert getUnNotified();

    UserAlert getAlert(String alertId);


}
