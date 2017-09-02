package com.pdmanager.interfaces;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

/**
 * Fatal error Handler
 * This is an interface that allows to pass fatal exceptions to a central handler
 */
public interface IFataErrorHandler {


    ///Log Fatal
    void LogFatal(String message, int code);

}
