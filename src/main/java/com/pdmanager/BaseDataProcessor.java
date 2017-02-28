package com.pdmanager;

import com.pdmanager.common.interfaces.IDataProcessor;

/**
 * Created by George on 5/21/2015.
 */
public abstract class BaseDataProcessor implements IDataProcessor {


    private boolean mEnabled = true;

    public boolean isEnabled() {

        return mEnabled;

    }


    public void setEnabled(boolean enabled) {

        mEnabled = enabled;
    }
}
