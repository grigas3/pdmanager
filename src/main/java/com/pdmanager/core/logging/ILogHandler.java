package com.pdmanager.core.logging;

/**
 * Created by george on 4/9/2015.
 */
public interface ILogHandler {

    void ProcessLog(String logType, String message);
    //void ProcessLog(LogEvent e);

}
