package com.sleepyzzz.sonycameraremotecontrol.location.bean;

import java.io.Serializable;

/**
 * @ClassName：GpsInfo
 * @Description：TODO-存储GPS信息
 * @author：SleepyzzZ on 2016/3/30 16:39
 * @
 * @
 * @update：Administrator on 2016/3/30 16:39
 * @modify：
 */
public class GpsInfo implements Serializable {

    private double longitude;
    private double latitude;

    private static GpsInfo mInstance;

    public GpsInfo(double lng, double lat) {
        longitude = lng;
        latitude = lat;
    }

    //双重同步锁，保证线程安全
    public static GpsInfo getInstance() {
        if(mInstance == null) {
            synchronized (GpsInfo.class) {
                if(mInstance == null) {
                    mInstance = new GpsInfo(0.0, 0.0);
                }
            }
        }
        return mInstance;
    }

    public synchronized void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public synchronized void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public synchronized double getLatitude() {
        return latitude;
    }

    public synchronized double getLongitude() {
        return longitude;
    }
}
