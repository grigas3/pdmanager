package com.pdmanager.alerting;

import com.pdmanager.models.UserAlert;

import java.util.List;

/**
 * Created by george on 30/11/2015.
 */
public interface IUserAlertManager {


    /**
     * Get All Active Alerts
     * @return
     */
    List<UserAlert> getAlerts();

    /**
     * Get All Active Alerts with expiration date greater than current time
     * @return
     */
    List<UserAlert> getActive();
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

    void setNotified(String id);

    void setNotified(UserAlert alert);
    //boolean setNotified(String id);
   // boolean setNotActive(String id);
    boolean clearAll();
    boolean anyActive();
    boolean anyUnNotified();
    UserAlert getFirstActive();
    UserAlert getUnNotified();

    UserAlert getAlert(String alertId);
    boolean updateAlert(UserAlert alert);
    boolean updateAlerts(String code);

    /**
     * Update all expired alerts
     *
     * @return
     */
    boolean updateExpired();

}
