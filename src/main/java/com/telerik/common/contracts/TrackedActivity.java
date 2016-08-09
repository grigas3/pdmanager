package com.telerik.common.contracts;

import java.util.HashMap;

/**
 * Created by ginev on 18/06/2014.
 */
public interface TrackedActivity {
    String getScreenName();

    HashMap<String, Object> getAdditionalParameters();
}
