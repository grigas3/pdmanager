package com.pdmanager.dataloggers;

import android.content.Context;
import android.util.Log;

import com.bugfender.sdk.Bugfender;
import com.pdmanager.common.data.SensorData;
import com.pdmanager.common.interfaces.ISensorDataHandler;

/**
 * Created by pdneuro-dev01 on 9/2/2017.
 */

public abstract class BaseDataLogger {

    private final Context mContext;
    private final ISensorDataHandler mHandler;

    public BaseDataLogger(ISensorDataHandler pHandler, Context pContext) {
        this.mContext = pContext;
        this.mHandler = pHandler;
    }

    protected abstract String getTag();



    protected ISensorDataHandler getSensorHandler() {

        return mHandler;
    }

    protected Context getContext() {

        return mContext;
    }

    protected void handleData(SensorData data) {

        if (mHandler != null)
            mHandler.handleData(data);

    }

    //region Logs
    protected void LogFatal(String e, int code) {

        Bugfender.f(getTag(), String.format("ERROR%03d", code));


    }

    protected void LogFatal(String mess, Exception ex, int code) {

        //Log.e(getTag(), mess, ex.getCause());
        Bugfender.f(getTag(), mess);


    }

    protected void LogError(String source) {
        // logger.log(Level.ERROR,source);

        Bugfender.e(getTag(), source);
        //Log.e(getTag(), source);
        // ProcessLog(getTag(), source);

    }

    protected void LogError(String source, Throwable cause) {

        //logger.log(Level.ERROR,source,cause);
        Bugfender.e(getTag(), source);
        // Log.e(getTag(), source, cause);
        // ProcessLog(getTag(), source);

    }

    protected void LogError(String e, Exception ex) {
        //logger.log(Level.ERROR,e,ex.getCause());

        Bugfender.e(getTag(), e);
        // Log.e(getTag(), e, ex.getCause());
        // ProcessLog(getTag(), e);

    }

    protected void LogInfo(String e) {
        //logger.log(Level.INFO,e);
        Log.i(getTag(), e);

    }

    protected void LogDebug(String e) {

        //logger.log(Level.DEBUG,e);
        Log.d(getTag(), e);


    }

    protected void LogWarn(String e) {
        //logger.log(Level.WARN,e);
        Log.w(getTag(), e);
        //Bugfender.w(getTag(), e);
        // ProcessLog("WARNING", e);

    }

    /**
     * Send Warning with code
     * This is also sent as an alert
     *
     * @param e
     * @param code
     */
    protected void LogWarn(String e, int code) {
        //logger.log(Level.WARN,e);
        //Log.w(getTag(), e);
        Bugfender.w(getTag(), e);
        //  ProcessLog("WARNING", e);

    }

    //endregion

}
