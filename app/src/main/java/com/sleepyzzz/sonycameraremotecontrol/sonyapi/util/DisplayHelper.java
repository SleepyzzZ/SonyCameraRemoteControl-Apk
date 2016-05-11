package com.sleepyzzz.sonycameraremotecontrol.sonyapi.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * @ClassName：
 * @Description：TODO-Copyright 2014 Sony Corporation
 * @author：SleepyzzZ on 2016/3/23 16:47
 * @
 * @
 * @update：Administrator on 2016/3/23 16:47
 * @modify：
 */
public final class DisplayHelper {

    private DisplayHelper() {
        // Don't make instance of this class.
    }

    /**
     * show toast
     *
     * @param context
     * @param msgId
     */
    public static void toast(final Context context, final int msgId) {

        Handler uiHandler = new Handler(context.getMainLooper());

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show or hide progress indicator on title bar
     *
     * @param activity
     * @param visible
     */
    public static void setProgressIndicator(final Activity activity, final boolean visible) {
        Handler uiHandler = new Handler(activity.getApplicationContext().getMainLooper());

        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                activity.setProgressBarIndeterminateVisibility(visible);
            }
        });
    }

}

