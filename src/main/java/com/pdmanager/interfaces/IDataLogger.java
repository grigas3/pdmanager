package com.pdmanager.interfaces;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

import com.pdmanager.models.LoggerStat;

import java.util.Collection;

/**
 * The purpose of this interface is to abstract Band, Device and GPS data logging
 * Each data source should just implement a IDataLogger
 * Each data logger should maintain
 */
public interface IDataLogger {

    void start();

    void pause();

    void stop();

    void resume();

    Collection<LoggerStat> getUsageStats();

    void resetStats();
}
