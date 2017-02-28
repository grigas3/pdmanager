package com.pdmanager.models;

/**
 * user Alert are alerts that are for user notification
 * and fragment manager
 * User Alerts are created based on medications and test schedule
 * Created by george on 14/1/2017.
 */

public class UserAlert extends Alert {

    /**
     * Alert Constructor

     * @param pTitle Alert Title
     * @param pMessage Alert Message
     * @param pCode Alert Code
     * @param pTimestamp
     * @param pExpiration
     * @param pSource
     */
    public UserAlert(String pTitle, String pMessage, String pCode, long pTimestamp, long pExpiration,String pSource) {

        super(pTitle,pMessage,pCode,pTimestamp,pExpiration,pSource);


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
    public UserAlert(String pid,String pTitle, String pMessage, String pCode,long pTimestamp, long pExpiration,  String pSource) {


        super(pid,pTitle,pMessage,pCode,pTimestamp,pExpiration,pSource);

    }
}
