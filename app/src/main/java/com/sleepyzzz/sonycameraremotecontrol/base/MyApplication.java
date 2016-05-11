package com.sleepyzzz.sonycameraremotecontrol.base;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Environment;
import android.os.Vibrator;

import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.CameraEventObserver;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.RemoteApi;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.ServerDevice;
import com.sleepyzzz.sonycameraremotecontrol.location.service.LocationService;

import java.util.Set;

/**
 * @ClassName：
 * @Description：TODO-Application class for the sample application.
 * @author：SleepyzzZ on 2016/3/24 19:06
 * @
 * @
 * @update：Administrator on 2016/3/24 19:06
 * @modify：
 */
public class MyApplication extends Application {

    private ServerDevice mTargetDevice;

    private RemoteApi mRemoteApi;

    private CameraEventObserver mEventObserver;

    private Set<String> mSupportedApiSet;

    public static String sdCardPath;

    public LocationService locationService;
    public Vibrator mVibrator;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        //获取sdcard路径
        sdCardPath = getSDCardPath();
    }

    public static Context getContext()
    {
        return mContext;
    }

    /**
     * 获取sd卡路径
     * @return
     */
    private String getSDCardPath() {

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(hasSDCard)
            return Environment.getExternalStorageDirectory().toString();
        else
            return Environment.getDownloadCacheDirectory().toString();
    }

    /**
     * Sets a target ServerDevice object.
     *
     * @param device
     */
    public void setTargetServerDevice(ServerDevice device) {
        mTargetDevice = device;
    }

    /**
     * Returns a target ServerDevice object.
     *
     * @return return ServiceDevice
     */
    public ServerDevice getTargetServerDevice() {
        return mTargetDevice;
    }

    /**
     * Sets a SimpleRemoteApi object to transmit to Activity.
     *
     * @param remoteApi
     */
    public void setRemoteApi(RemoteApi remoteApi) {
        mRemoteApi = remoteApi;
    }

    /**
     * Returns a SimpleRemoteApi object.
     *
     * @return return SimpleRemoteApi
     */
    public RemoteApi getRemoteApi() {
        return mRemoteApi;
    }

    /**
     * Sets a List of supported APIs.
     *
     * @param apiList
     */
    public void setSupportedApiList(Set<String> apiList) {
        mSupportedApiSet = apiList;
    }

    /**
     * Returns a list of supported APIs.
     *
     * @return Returns a list of supported APIs.
     */
    public Set<String> getSupportedApiList() {
        return mSupportedApiSet;
    }

    /**
     * Sets a SimpleCameraEventObserver object to transmit to Activity.
     *
     * @param observer
     */
    public void setCameraEventObserver(CameraEventObserver observer) {
        mEventObserver = observer;
    }

    /**
     * Returns a SimpleCameraEventObserver object.
     *
     * @return return SimpleCameraEventObserver
     */
    public CameraEventObserver getCameraEventObserver() {
        return mEventObserver;
    }
}
