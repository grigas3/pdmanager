package com.telerik.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.pdmanager.core.R;


/**
 * Created by ginev on 3/18/2015.
 */
public class TelerikActivityHelper {

    @TargetApi(21)
    public static void updateActivityTaskDescription(Activity target) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap icon = BitmapFactory.decodeResource(target.getResources(), R.drawable.telerik_logo);
            target.setTaskDescription(new ActivityManager.TaskDescription(target.getTitle().toString(), icon, target.getResources().getColor(R.color.telerikGreen)));
        }
    }
}
