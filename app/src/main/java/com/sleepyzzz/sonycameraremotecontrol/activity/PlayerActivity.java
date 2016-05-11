package com.sleepyzzz.sonycameraremotecontrol.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.sleepyzzz.photo_selector.activity.PhotoPickerActivity;
import com.sleepyzzz.sonycameraremotecontrol.R;
import com.sleepyzzz.sonycameraremotecontrol.base.MyApplication;
import com.sleepyzzz.sonycameraremotecontrol.location.bean.GpsInfo;
import com.sleepyzzz.sonycameraremotecontrol.location.service.LocationService;
import com.sleepyzzz.sonycameraremotecontrol.okhttp.OkHttpUtils;
import com.sleepyzzz.sonycameraremotecontrol.okhttp.callback.FileCallBack;
import com.sleepyzzz.sonycameraremotecontrol.okhttp.utils.ImageUtils;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.RemoteApi;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.CameraEventObserver;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.DisplayHelper;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.ServerDevice;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.view.DrawImageView;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.view.PlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Semaphore;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * @ClassName：
 * @Description：TODO
 * @author：SleepyzzZ on 2016/3/24 19:39
 * @
 * @
 * @update：Administrator on 2016/3/24 19:39
 * @modify：
 */
public class PlayerActivity extends Activity {

    private static final String TAG = PlayerActivity.class.getSimpleName();

    private static final String CAPTURE_MODE = "still";

    private static final String VIDEO_MODE = "movie";
    @Bind(R.id.pv_liveview)
    PlayerView pvLiveview;
    @Bind(R.id.btn_capture)
    ImageButton btnCapture;
    @Bind(R.id.btn_zoom_tele)
    ImageButton btnZoomTele;
    @Bind(R.id.btn_zoom_wide)
    ImageButton btnZoomWide;
    @Bind(R.id.toolright_ly)
    LinearLayout toolrightLy;
    @Bind(R.id.tv_camera_status)
    TextView tvCameraStatus;
    @Bind(R.id.iv_image_wipe)
    ImageView ivImageWipe;
    @Bind(R.id.btn_setup)
    ImageButton btnSetup;
    @Bind(R.id.btn_change_mode)
    ImageButton btnChangeMode;
    @Bind(R.id.btn_start_record)
    ImageButton btnStartRecord;
    @Bind(R.id.btn_stop_record)
    ImageButton btnStopRecord;
    @Bind(R.id.tv_lng)
    TextView tvLng;
    @Bind(R.id.tv_lat)
    TextView tvLat;
    @Bind(R.id.div_paint)
    DrawImageView divPaint;

    private ServerDevice mTargetServer;

    private RemoteApi mRemoteApi;

    private CameraEventObserver mEventObserver;

    private CameraEventObserver.ChangeListener mEventListener;

    private final Set<String> mAvailableCameraApiSet = new HashSet<String>();

    private final Set<String> mSupportedApiSet = new HashSet<String>();

    private SurfaceHolder mSurfaceHolder = null;

    private View view;
    private PopupWindow popMenuMode;
    private ImageButton btnCaptureMode;
    private ImageButton btnRecordMode;

    public static int mScreenHeightPixels;
    public static int mScreenWidthPixels;

    private static ProgressDialog progressDialog;

    private volatile Semaphore mSemaphore = new Semaphore(0);

    private ArrayList<String> mSelectPath;

    /**
     * GPS
     */
    private LocationService locService;
    private LocationClientOption mOption;
    private static final int LOCATION_FREQUENCE = 60 * 1000;
    private static final int LOCATION_DISTANCE = 100;
    private boolean isLocation = false;

    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;

    /**
     * 是否传送照片到手机标识符
     */
    private boolean isDownload = true;
    private static final int SHOW_PROGRESS = 0;
    private static final int ON_LOADING = 1;
    private static final int CANCEL_PROGRESS = 2;

    private static class MyHandler extends Handler {

        private final WeakReference<PlayerActivity> mActivity;

        private MyHandler(PlayerActivity activity) {
            mActivity = new WeakReference<PlayerActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerActivity activity = mActivity.get();
            if (activity != null) {

                switch (msg.what) {
                    case SHOW_PROGRESS:
                        progressDialog.show();
                        break;

                    case ON_LOADING:
                        int value = msg.getData().getInt("downloading");
                        progressDialog.setProgress(value);
                        break;

                    case CANCEL_PROGRESS:
                        progressDialog.cancel();
                        break;
                }
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        setContentView(R.layout.activity_player);
        powerManager = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        ButterKnife.bind(this);

        initEvents();
        initListerners();

        Log.d(TAG, "onCreate() completed.");
    }

    /**
     * 初始化
     */
    private void initEvents() {

        MyApplication app = (MyApplication) getApplication();
        mTargetServer = app.getTargetServerDevice();
        mRemoteApi = new RemoteApi(mTargetServer);
        app.setRemoteApi(mRemoteApi);
        mEventObserver = new CameraEventObserver(getApplicationContext(), mRemoteApi);
        app.setCameraEventObserver(mEventObserver);

        /**
         * 启动GPS服务
         * 配置模式
         */
        /*locService = ((MyApplication) getApplication()).locationService;
        locService.stop();
        locService.setLocationOption(locService.getDefaultLocationClientOption());*/
        /**
         * 启动GPS服务
         * 回调模式
         */
        locService = ((MyApplication) getApplication()).locationService;
        locService.stop();
        mOption = new LocationClientOption();
        mOption = locService.getDefaultLocationClientOption();
        mOption.setOpenAutoNotifyMode(LOCATION_FREQUENCE, LOCATION_DISTANCE, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        locService.setLocationOption(mOption);

        /**
         * 设置预览界面到底层，覆盖上矩形框界面
         */
        pvLiveview.setZOrderOnTop(false);
        mSurfaceHolder = pvLiveview.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        /**
         * 初始化popupwindow
         */
        view = LayoutInflater.from(this).inflate(R.layout.popmenu_mode, null);
        view.setFocusableInTouchMode(true);
        btnCaptureMode = (ImageButton) view.findViewById(R.id.btn_capture_mode);
        btnRecordMode = (ImageButton) view.findViewById(R.id.btn_record_mode);
        popMenuMode = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popMenuMode.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 监听相机状态，并更新UI空间状态
     * 抽象方法
     */
    private void initListerners() {

        mEventListener = new CameraEventObserver.ChangeListenerTmpl() {

            @Override
            public void onShootModeChanged(String shootMode) {

                Log.d(TAG, "onShootModeChanged() called: " + shootMode);
                refreshUi();
            }

            @Override
            public void onCameraStatusChanged(String status) {

                Log.d(TAG, "onCameraStatusChanged() called: " + status);
                refreshUi();
            }

            @Override
            public void onApiListModified(List<String> apis) {

                Log.d(TAG, "onApiListModified() called");
                synchronized (mAvailableCameraApiSet) {

                    mAvailableCameraApiSet.clear();
                    for (String api : apis) {

                        mAvailableCameraApiSet.add(api);
                    }
                    if (!mEventObserver.getLiveviewStatus()
                            && isCameraApiAvailable("startLiveview")) {

                        if (pvLiveview != null && !pvLiveview.isStarted()) {

                            startLiveview();
                        }
                    }
                    if (isCameraApiAvailable("actZoom")) {

                        Log.d(TAG, "onApiListModified(): prepareActZoomButtons()");
                        prepareActZoomButtons(true);
                    } else {

                        prepareActZoomButtons(false);
                    }
                }
            }

            @Override
            public void onZoomPositionChanged(int zoomPosition) {

                Log.d(TAG, "onZoomPositionChanged() called = " + zoomPosition);
                if (zoomPosition == 0) {

                    btnZoomWide.setEnabled(true);
                    btnZoomTele.setEnabled(false);
                } else if (zoomPosition == 100) {

                    btnZoomWide.setEnabled(false);
                    btnZoomTele.setEnabled(true);
                } else {

                    btnZoomWide.setEnabled(true);
                    btnZoomTele.setEnabled(true);
                }
            }

            @Override
            public void onLiveviewStatusChanged(boolean status) {

                Log.d(TAG, "onLiveviewStatusChanged() called = " + status);
            }

            @Override
            public void onStorageIdChanged(String storageId) {

                Log.d(TAG, "onStorageIdChanged() called: " + storageId);
                refreshUi();
            }
        };
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
        //获取locationservice实例，建议应用中只初始化1个location实例
        locService.registerListener(mListener);
        //注册监听
        locService.start();
    }

    /**
     * gps监听接口
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                Log.e("Location", location.getLatitude() + " " + location.getLongitude());
                isLocation = true;
                GpsInfo.getInstance().setLongitude(location.getLongitude());
                GpsInfo.getInstance().setLatitude(location.getLatitude());
                tvLng.setText(GpsInfo.getInstance().getLongitude() + "");
                tvLat.setText(GpsInfo.getInstance().getLatitude() + "");
                if (location.getLocType() == BDLocation.TypeGpsLocation) { //GPS定位结果
                    Log.e("Location", location.getAltitude() + "");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) { //网络定位结果
                    Log.e("Location", "采用wifi直连模式,此场景不适用");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) { //离线定位结果
                    Log.e("Location", "在无法收到卫星的情况下进入离线定位场景");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    Log.e("Location", "服务端网路定位失败");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    Log.e("Location", "网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    Log.e("Location", "无法获取有效定位依据导致定位失败，一般是由于手机的原因，" +
                            "处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
            }
        }
    };

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        wakeLock.acquire();

        getScreenSize();
        mEventObserver.activate();
        initZoomListerner();

        prepareOpenConnection();

        /**
         * 初始化下载图片进度条
         */
        progressDialog = new ProgressDialog(PlayerActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Tips");
        progressDialog.setMessage("transfer picture...");

        popMenuMode.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                //设置背景颜色变亮
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        btnCaptureMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentMode = mEventObserver.getShootMode();
                if ("IDLE".equals(mEventObserver.getCameraStatus())
                        && !CAPTURE_MODE.equals(currentMode)) {

                    setShootMode(CAPTURE_MODE);
                    btnRecordMode.setImageResource(R.drawable.icon_shooting_mode_movie);
                }
                popMenuMode.dismiss();
            }
        });

        btnRecordMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentMode = mEventObserver.getShootMode();
                if ("IDLE".equals(mEventObserver.getCameraStatus())
                        && !VIDEO_MODE.equals(currentMode)) {

                    setShootMode(VIDEO_MODE);
                    btnCaptureMode.setImageResource(R.drawable.icon_shooting_mode_still);
                }
                popMenuMode.dismiss();
            }
        });

        Log.d(TAG, "onResume() completed.");
        Log.e(TAG, GpsInfo.getInstance().getLatitude() + "");
    }

    @Override
    protected void onStop() {
        //closeConnection();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locService.unregisterListener(mListener);//注销掉监听
        locService.stop();
        closeConnection();
        mHandler.removeCallbacksAndMessages(null);      //清除消息
    }

    private void initZoomListerner() {
        btnZoomWide.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                actZoom("in", "start");
                return true;
            }
        });
        btnZoomTele.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                actZoom("out", "start");
                return true;
            }
        });
        btnZoomWide.setOnTouchListener(new View.OnTouchListener() {

            private long downTime = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - downTime > 500) {
                        actZoom("in", "stop");
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                }
                return false;
            }
        });
        btnZoomTele.setOnTouchListener(new View.OnTouchListener() {

            private long downTime = -1;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - downTime > 500) {
                        actZoom("out", "stop");
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downTime = System.currentTimeMillis();
                }
                return false;
            }
        });

        divPaint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final FocusPos pos = calculateFocusXY(event, pvLiveview);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                JSONObject replyJson = null;
                                replyJson = mRemoteApi.touchAFPosition("setTouchAFPosition", pos.xScale, pos.yScale);
                                Log.e(TAG, replyJson.toString());
                            } catch (IOException e) {
                            }
                        }
                    }.start();
                }
                return true;
            }
        });
    }

    private FocusPos calculateFocusXY(MotionEvent event, PlayerView playerView) {
        FocusPos focusPos = new FocusPos();
        int oX = (int) (event.getX() - (playerView.getWidth() - playerView.dst.width())/2.0);
        int oY = (int) (event.getY() - (playerView.getHeight() - playerView.dst.height())/2.0);
        Log.e(TAG,oX+" "+oY);
        double xScale = (double)oX/playerView.dst.width()*100.0;
        double yScale = (double)oY/playerView.dst.height()*100.0;
        Log.e(TAG, xScale+" "+yScale);
        focusPos.xScale = xScale;
        focusPos.yScale = yScale;
        return focusPos;
    }

    private class FocusPos {
        double xScale;
        double yScale;
    }

    /**
     * 获取屏幕分辨率
     */
    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.mScreenHeightPixels = dm.heightPixels;
        this.mScreenWidthPixels = dm.widthPixels;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
        //closeConnection();

        Log.d(TAG, "onPause() completed.");
    }

    private void prepareOpenConnection() {

        Log.d(TAG, "prepareToOpenConection() exec");
        //setProgressBarIndeterminateVisibility(true);
        new Thread() {

            @Override
            public void run() {
                try {
                    // Get supported API list (Camera API)
                    JSONObject replyJsonCamera = mRemoteApi.getCameraMethodTypes();
                    loadSupportedApiList(replyJsonCamera);
                    try {
                        // Get supported API list (AvContent API)
                        JSONObject replyJsonAvcontent = mRemoteApi.getAvcontentMethodTypes();
                        loadSupportedApiList(replyJsonAvcontent);
                    } catch (IOException e) {

                        Log.d(TAG, "AvContent is not support.");
                    }

                    MyApplication app = (MyApplication) getApplication();
                    app.setSupportedApiList(mSupportedApiSet);

                    if (!isApiSupported("setCameraFunction")) {

                        // this device does not support setCameraFunction.
                        // No need to check camera status.
                        openConnection();
                    } else {

                        // this device supports setCameraFunction.
                        // after confirmation of camera state, open connection.
                        Log.d(TAG, "this device support set camera function");

                        if (!isApiSupported("getEvent")) {

                            Log.e(TAG, "this device is not support getEvent");
                            openConnection();
                            return;
                        }

                        // confirm current camera status
                        String cameraStatus = null;
                        JSONObject replyJson = mRemoteApi.getEvent(false);
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        JSONObject cameraStatusObj = resultsObj.getJSONObject(1);
                        String type = cameraStatusObj.getString("type");
                        if ("cameraStatus".equals(type)) {

                            cameraStatus = cameraStatusObj.getString("cameraStatus");
                        } else {

                            throw new IOException();
                        }

                        if (isShootingStatus(cameraStatus)) {

                            Log.d(TAG, "camera function is Remote Shooting.");
                            openConnection();
                        } else {
                            // set Listener
                            startOpenConnectionAfterChangeCameraState();
                            // set Camera function to Remote Shooting
                            replyJson = mRemoteApi.setCameraFunction("Remote Shooting");
                        }
                    }
                } catch (IOException e) {

                    Log.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_api_calling);
                    DisplayHelper.setProgressIndicator(PlayerActivity.this, false);
                } catch (JSONException e) {

                    Log.w(TAG, "prepareToStartContentsListMode: JSONException: " + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_api_calling);
                    DisplayHelper.setProgressIndicator(PlayerActivity.this, false);
                }
            }
        }.start();
    }

    private static boolean isShootingStatus(String currentStatus) {
        Set<String> shootingStatus = new HashSet<String>();
        shootingStatus.add("IDLE");
        shootingStatus.add("NotReady");
        shootingStatus.add("StillCapturing");
        shootingStatus.add("StillSaving");
        shootingStatus.add("MovieWaitRecStart");
        shootingStatus.add("MovieRecording");
        shootingStatus.add("MovieWaitRecStop");
        shootingStatus.add("MovieSaving");
        shootingStatus.add("IntervalWaitRecStart");
        shootingStatus.add("IntervalRecording");
        shootingStatus.add("IntervalWaitRecStop");
        shootingStatus.add("AudioWaitRecStart");
        shootingStatus.add("AudioRecording");
        shootingStatus.add("AudioWaitRecStop");
        shootingStatus.add("AudioSaving");

        return shootingStatus.contains(currentStatus);
    }

    private void startOpenConnectionAfterChangeCameraState() {

        Log.d(TAG, "startOpenConectiontAfterChangeCameraState() exec");
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mEventObserver
                        .setEventChangeListener(new CameraEventObserver.ChangeListenerTmpl() {

                            @Override
                            public void onCameraStatusChanged(String status) {

                                Log.d(TAG, "onCameraStatusChanged:" + status);
                                if ("IDLE".equals(status) || "NotReady".equals(status)) {

                                    openConnection();
                                }
                                refreshUi();
                            }

                            @Override
                            public void onShootModeChanged(String shootMode) {

                                refreshUi();
                            }

                            @Override
                            public void onStorageIdChanged(String storageId) {

                                refreshUi();
                            }
                        });

                mEventObserver.start();
            }
        });
    }

    /**
     * Open connection to the camera device to start monitoring Camera events
     * and showing liveview.
     */
    private void openConnection() {

        mEventObserver.setEventChangeListener(mEventListener);
        new Thread() {

            @Override
            public void run() {
                Log.d(TAG, "openConnection(): exec.");

                try {
                    JSONObject replyJson = null;
                    // getAvailableApiList
                    replyJson = mRemoteApi.getAvailableApiList();
                    loadAvailableCameraApiList(replyJson);
                    // check version of the server device
                    if (isCameraApiAvailable("getApplicationInfo")) {

                        Log.d(TAG, "openConnection(): getApplicationInfo()");
                        replyJson = mRemoteApi.getApplicationInfo();
                        if (!isSupportedServerVersion(replyJson)) {

                            DisplayHelper.toast(getApplicationContext(), //
                                    R.string.msg_error_non_supported_device);
                            PlayerActivity.this.finish();
                            return;
                        }
                    } else {
                        // never happens;
                        return;
                    }

                    // startRecMode if necessary.
                    if (isCameraApiAvailable("startRecMode")) {

                        Log.d(TAG, "openConnection(): startRecMode()");
                        replyJson = mRemoteApi.startRecMode();

                        // Call again.
                        replyJson = mRemoteApi.getAvailableApiList();
                        loadAvailableCameraApiList(replyJson);
                    }

                    // getEvent start
                    if (isCameraApiAvailable("getEvent")) {

                        Log.d(TAG, "openConnection(): EventObserver.start()");
                        mEventObserver.start();
                    }

                    // Liveview start
                    if (isCameraApiAvailable("startLiveview")) {

                        Log.d(TAG, "openConnection(): LiveviewSurface.start()");
                        startLiveview();
                    }

                    // prepare UIs
                    if (isCameraApiAvailable("getAvailableShootMode")) {

                        Log.d(TAG, "openConnection(): prepareShootModeSpinner()");
                        //prepareShootModeSpinner();
                        // Note: hide progress bar on title after this calling.
                    }

                    // prepare UIs
                    if (isCameraApiAvailable("actZoom")) {

                        Log.d(TAG, "openConnection(): prepareActZoomButtons()");
                        prepareActZoomButtons(true);
                    } else {

                        prepareActZoomButtons(false);
                    }

                    Log.d(TAG, "openConnection(): completed.");
                } catch (IOException e) {

                    Log.w(TAG, "openConnection : IOException: " + e.getMessage());
                    DisplayHelper.setProgressIndicator(PlayerActivity.this, false);
                    DisplayHelper.toast(getApplicationContext(), R.string.msg_error_connection);
                }
            }
        }.start();

    }

    /**
     * Stop monitoring Camera events and close liveview connection.
     */
    private void closeConnection() {

        Log.d(TAG, "closeConnection(): exec.");
        // Liveview stop
        Log.d(TAG, "closeConnection(): LiveviewSurface.stop()");
        if (pvLiveview != null) {

            pvLiveview.stop();
            pvLiveview = null;
            stopLiveview();
        }
        // getEvent stop
        Log.d(TAG, "closeConnection(): EventObserver.release()");
        mEventObserver.release();

        Log.d(TAG, "closeConnection(): completed.");
    }

    /**
     * 根据相机当前状态更新UI
     */
    private void refreshUi() {

        String cameraStatus = mEventObserver.getCameraStatus();
        String shootMode = mEventObserver.getShootMode();
        List<String> availableShootModes = mEventObserver.getAvailableShootModes();
        // CameraStatus TextView
        tvCameraStatus.setText(cameraStatus);

        // Recording Start/Stop Button
        if ("MovieRecording".equals(cameraStatus)) {

            btnCapture.setVisibility(View.INVISIBLE);
            btnCapture.setEnabled(false);
            btnStartRecord.setVisibility(View.INVISIBLE);
            btnStartRecord.setEnabled(false);
            btnStopRecord.setVisibility(View.VISIBLE);
            btnStopRecord.setEnabled(true);
            btnChangeMode.setEnabled(false);
            btnSetup.setEnabled(false);
        } else if ("IDLE".equals(cameraStatus) && "movie".equals(shootMode)) {

            btnCapture.setVisibility(View.INVISIBLE);
            btnCapture.setEnabled(false);
            btnStartRecord.setVisibility(View.VISIBLE);
            btnStartRecord.setEnabled(true);
            btnStopRecord.setVisibility(View.INVISIBLE);
            btnStopRecord.setEnabled(false);
            btnRecordMode.setImageResource(R.drawable.icon_shooting_mode_movie_selected);
            btnChangeMode.setImageResource(R.drawable.btn_shooting_mode_movie);
            btnChangeMode.setEnabled(true);
            btnSetup.setEnabled(true);
        } else {
            btnStartRecord.setEnabled(false);
        }

        // Take picture Button
        if ("still".equals(shootMode) && "IDLE".equals(cameraStatus)) {

            Log.e(TAG, "still,IDLE");
            btnCapture.setVisibility(View.VISIBLE);
            btnCapture.setEnabled(true);
            btnStartRecord.setVisibility(View.INVISIBLE);
            btnStartRecord.setEnabled(false);
            btnStopRecord.setVisibility(View.INVISIBLE);
            btnStopRecord.setEnabled(false);
            btnCaptureMode.setImageResource(R.drawable.icon_shooting_mode_still_selected);
            btnChangeMode.setImageResource(R.drawable.btn_shooting_mode_still);
        } else {
            btnCapture.setEnabled(false);
        }

        // Picture wipe Image
        if (!"still".equals(shootMode)) {

            ivImageWipe.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Retrieve a list of APIs that are available at present.
     *
     * @param replyJson
     */
    private void loadAvailableCameraApiList(JSONObject replyJson) {

        synchronized (mAvailableCameraApiSet) {
            mAvailableCameraApiSet.clear();
            try {

                JSONArray resultArrayJson = replyJson.getJSONArray("result");
                JSONArray apiListJson = resultArrayJson.getJSONArray(0);
                for (int i = 0; i < apiListJson.length(); i++) {
                    mAvailableCameraApiSet.add(apiListJson.getString(i));
                }
            } catch (JSONException e) {

                Log.w(TAG, "loadAvailableCameraApiList: JSON format error.");
            }
        }
    }

    /**
     * Retrieve a list of APIs that are supported by the target device.
     *
     * @param replyJson
     */
    private void loadSupportedApiList(JSONObject replyJson) {

        synchronized (mSupportedApiSet) {
            try {

                JSONArray resultArrayJson = replyJson.getJSONArray("results");
                for (int i = 0; i < resultArrayJson.length(); i++) {
                    mSupportedApiSet.add(resultArrayJson.getJSONArray(i).getString(0));
                }
            } catch (JSONException e) {

                Log.w(TAG, "loadSupportedApiList: JSON format error.");
            }
        }
    }

    /**
     * Check if the specified API is available at present. This works correctly
     * only for Camera API.
     *
     * @param apiName
     * @return
     */
    private boolean isCameraApiAvailable(String apiName) {

        boolean isAvailable = false;
        synchronized (mAvailableCameraApiSet) {

            isAvailable = mAvailableCameraApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Check if the specified API is supported. This is for camera and avContent
     * service API. The result of this method does not change dynamically.
     *
     * @param apiName
     * @return
     */
    private boolean isApiSupported(String apiName) {
        boolean isAvailable = false;
        synchronized (mSupportedApiSet) {
            isAvailable = mSupportedApiSet.contains(apiName);
        }
        return isAvailable;
    }

    /**
     * Check if the version of the server is supported in this application.
     *
     * @param replyJson
     * @return
     */
    private boolean isSupportedServerVersion(JSONObject replyJson) {
        try {
            JSONArray resultArrayJson = replyJson.getJSONArray("result");
            String version = resultArrayJson.getString(1);
            String[] separated = version.split("\\.");
            int major = Integer.valueOf(separated[0]);
            if (2 <= major) {
                return true;
            }
        } catch (JSONException e) {
            Log.w(TAG, "isSupportedServerVersion: JSON format error.");
        } catch (NumberFormatException e) {
            Log.w(TAG, "isSupportedServerVersion: Number format error.");
        }
        return false;
    }

    /**
     * Check if the shoot mode is supported in this application.
     *
     * @param mode
     * @return
     */
    private boolean isSupportedShootMode(String mode) {

        if ("still".equals(mode) || "movie".equals(mode)) {

            return true;
        }
        return false;
    }

    /**
     * Prepare for Spinner to select "shootMode" by user.
     */
    /*private void prepareShootModeSpinner() {
        new Thread() {

            @Override
            public void run() {
                Log.d(TAG, "prepareShootModeSpinner(): exec.");
                JSONObject replyJson = null;
                try {
                    replyJson = mRemoteApi.getAvailableShootMode();

                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    final String currentMode = resultsObj.getString(0);
                    JSONArray availableModesJson = resultsObj.getJSONArray(1);
                    final List<String> availableModes = new ArrayList<String>();

                    for (int i = 0; i < availableModesJson.length(); i++) {
                        String mode = availableModesJson.getString(i);
                        if (!isSupportedShootMode(mode)) {
                            mode = "";
                        }
                        availableModes.add(mode);
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            prepareShootModeSpinnerUi(//
                                    availableModes.toArray(new String[0]), currentMode);
                            // Hide progress indeterminately on title bar.
                            setProgressBarIndeterminateVisibility(false);
                        }
                    });
                } catch (IOException e) {
                    Log.w(TAG, "prepareShootModeRadioButtons: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "prepareShootModeRadioButtons: JSON format error.");
                }
            };
        }.start();
    }*/

    /**
     * Selection for Spinner UI of Shoot Mode.
     *
     * @param spinner
     * @param mode
     */
    /*private void selectionShootModeSpinner(Spinner spinner, String mode) {
        if (!isSupportedShootMode(mode)) {
            mode = "";
        }
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            mSpinnerShootMode.setSelection(adapter.getPosition(mode));
        }
    }*/

    /**
     * Prepare for Spinner UI of Shoot Mode.
     *
     * @param availableShootModes
     * @param currentMode
     */
    /*private void prepareShootModeSpinnerUi(String[] availableShootModes, String currentMode) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, //
                android.R.layout.simple_spinner_item);
        for (String mode : availableShootModes) {
            adapter.add(mode);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerShootMode.setAdapter(adapter);
        mSpinnerShootMode.setPrompt(getString(R.string.prompt_shoot_mode));
        selectionShootModeSpinner(mSpinnerShootMode, currentMode);
        mSpinnerShootMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // selected Spinner dropdown item
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                if (!spinner.isFocusable()) {
                    // ignored the first call, because shoot mode has not
                    // changed
                    spinner.setFocusable(true);
                } else {
                    String mode = spinner.getSelectedItem().toString();
                    String currentMode = mEventObserver.getShootMode();
                    Log.e(TAG, "mode="+mode+" "+"currentMode="+currentMode);
                    if (mode.isEmpty()) {
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_no_supported_shootmode);
                        // now state that can not be changed
                        selectionShootModeSpinner(spinner, currentMode);
                    } else {
                        if ("IDLE".equals(mEventObserver.getCameraStatus()) //
                                && !mode.equals(currentMode)) {
                            setShootMode(mode);
                        } else {
                            // now state that can not be changed
                            selectionShootModeSpinner(spinner, currentMode);
                        }
                    }
                }
            }

            // not selected Spinner dropdown item
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }*/

    /**
     * Prepare for Button to select "actZoom" by user.
     *
     * @param flag
     */
    private void prepareActZoomButtons(final boolean flag) {

        Log.d(TAG, "prepareActZoomButtons(): exec.");
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                prepareActZoomButtonsUi(flag);
            }
        });

    }

    /**
     * Prepare for ActZoom Button UI.
     *
     * @param flag
     */
    private void prepareActZoomButtonsUi(boolean flag) {
        if (flag) {

            btnZoomWide.setVisibility(View.VISIBLE);
            btnZoomTele.setVisibility(View.VISIBLE);
        } else {

            btnZoomWide.setVisibility(View.GONE);
            btnZoomTele.setVisibility(View.GONE);
        }
    }

    /**
     * Call setShootMode
     *
     * @param mode
     */
    private void setShootMode(final String mode) {

        new Thread() {

            @Override
            public void run() {
                try {

                    JSONObject replyJson = mRemoteApi.setShootMode(mode);
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {

                        // Success, but no refresh UI at the point.
                        Log.v(TAG, "setShootMode: success.");
                    } else {

                        Log.w(TAG, "setShootMode: error: " + resultCode);
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_api_calling);
                    }
                } catch (IOException e) {

                    Log.w(TAG, "setShootMode: IOException: " + e.getMessage());
                } catch (JSONException e) {

                    Log.w(TAG, "setShootMode: JSON format error.");
                }
            }
        }.start();
    }

    /**
     * Take a picture and retrieve the image data.
     */
    private void takeAndFetchPicture() {

        if (pvLiveview == null || !pvLiveview.isStarted()) {

            DisplayHelper.toast(getApplicationContext(), R.string.msg_error_take_picture);
            return;
        }

        new Thread() {

            @Override
            public void run() {

                try {

                    JSONObject replyJson = mRemoteApi.actTakePicture();
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    final JSONArray imageUrlsObj = resultsObj.getJSONArray(0);
                    String postImageUrl = null;
                    if (1 <= imageUrlsObj.length()) {
                        postImageUrl = imageUrlsObj.getString(0);
                    }
                    if (postImageUrl == null) {
                        Log.w(TAG, "takeAndFetchPicture: post image URL is null.");
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_take_picture);
                        return;
                    }
                    // Show progress indicator
                    DisplayHelper.setProgressIndicator(PlayerActivity.this, true);

                    ImageFile imageFile = getImageFileDirAndName();
                    mHandler.sendEmptyMessage(SHOW_PROGRESS);
                    OkHttpUtils
                            .get()
                            .url(postImageUrl)
                            .build()
                            .execute(new FileCallBack(imageFile.fileDir, imageFile.fileName) {

                                @Override
                                public void inProgress(float progress, long total) {
                                    /**
                                     * 注：Message msg = Handler.obtainMessage()代替Message msg = new Message()
                                     * 否则msg already in use
                                     */
                                    Message msg = mHandler.obtainMessage();
                                    Bundle bundle = new Bundle();
                                    msg.what = ON_LOADING;
                                    bundle.putInt("downloading", (int) (100 * progress));
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                }

                                @Override
                                public void onError(Call call, Exception e) {
                                    //取消上传监听进度条
                                    mHandler.sendEmptyMessage(CANCEL_PROGRESS);
                                    DisplayHelper.toast(getApplicationContext(),
                                            R.string.msg_error_trans_picture);
                                }

                                @Override
                                public void onResponse(File file) {
                                    ////取消上传监听进度条
                                    mHandler.sendEmptyMessage(CANCEL_PROGRESS);
                                    Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                                    mSemaphore.release();
                                }
                            });

                    /**
                     * 根据ImageView大小压缩后再显示
                     * 注意:必须等图片传输完后再压缩显示，用信号量同步
                     * 此方法比解析InpusStream再压缩显示效率高
                     */
                    try {
                        mSemaphore.acquire();
                    } catch (InterruptedException e) {
                        return;
                    }
                    String imagePath = imageFile.fileDir + "/" + imageFile.fileName;
                    final Bitmap bm = decodeSampledBitmapFromResource(imagePath, ivImageWipe);
                    writeGpsTojpeg(imagePath, GpsInfo.getInstance().getLatitude(), GpsInfo.getInstance().getLongitude());

                   /* ImageSize imageSize = getImageViewWidth(ivImageWipe);
                    int reqWidth = imageSize.width;
                    int reqHeight = imageSize.height;
                    String destDir = imageFile.fileDir + "/" +imageFile.fileName;
                    final Bitmap bm = decodeSampledBitmapFromResource(destDir, reqWidth,
                            reqHeight);*/
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ivImageWipe.setVisibility(View.VISIBLE);
                            ivImageWipe.setImageBitmap(bm);
                        }
                    });

                } catch (IOException e) {

                    Log.w(TAG, "IOException while closing slicer: " + e.getMessage());
                    DisplayHelper.toast(getApplicationContext(), //
                            R.string.msg_error_take_picture);
                } catch (JSONException e) {

                    Log.w(TAG, "JSONException while closing slicer");
                    DisplayHelper.toast(getApplicationContext(), //
                            R.string.msg_error_take_picture);
                } finally {

                    DisplayHelper.setProgressIndicator(PlayerActivity.this, false);
                }
            }
        }.start();
    }

    /**
     * 将经纬度信息写入jpeg的exif头中
     *
     * @param path
     * @param lat
     * @param lng
     */
    private void writeGpsTojpeg(final String path, final double lat, final double lng) {
        new Thread() {
            @Override
            public void run() {
                File file = new File(path);
                if (file.exists()) {
                    try {
                        ExifInterface exif = new ExifInterface(path);
                        String tagLat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                        String tagLng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        if (tagLat == null && tagLng == null) {
                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
                                    decimalToDMS(lat));
                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
                                    lat > 0 ? "N" : "S");
                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
                                    decimalToDMS(lng));
                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
                                    lng > 0 ? "E" : "W");
                            //保存
                            exif.saveAttributes();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }.start();
    }

    /**
     * 格式转化使gps正确写入exif
     *
     * @param value
     * @return
     */
    private String decimalToDMS(double value) {
        String output, degrees, minutes, seconds;

        // gets the modulus the coordinate divided by one (MOD1).
        // in other words gets all the numbers after the decimal point.
        // e.g. mod := -79.982195 % 1 == 0.982195
        //
        // next get the integer part of the coord. On other words the whole
        // number part.
        // e.g. intPart := -79

        double mod = value % 1;
        int intPart = (int) value;

        // set degrees to the value of intPart
        // e.g. degrees := "-79"

        degrees = String.valueOf(intPart);

        // next times the MOD1 of degrees by 60 so we can find the integer part
        // for minutes.
        // get the MOD1 of the new coord to find the numbers after the decimal
        // point.
        // e.g. coord := 0.982195 * 60 == 58.9317
        // mod := 58.9317 % 1 == 0.9317
        //
        // next get the value of the integer part of the coord.
        // e.g. intPart := 58

        value = mod * 60;
        mod = value % 1;
        intPart = (int) value;
        if (intPart < 0) {
            // Convert number to positive if it's negative.
            intPart *= -1;
        }

        // set minutes to the value of intPart.
        // e.g. minutes = "58"
        minutes = String.valueOf(intPart);

        // do the same again for minutes
        // e.g. coord := 0.9317 * 60 == 55.902
        // e.g. intPart := 55
        value = mod * 60;
        intPart = (int) value;
        if (intPart < 0) {
            // Convert number to positive if it's negative.
            intPart *= -1;
        }

        // set seconds to the value of intPart.
        // e.g. seconds = "55"
        seconds = String.valueOf(intPart);

        // I used this format for android but you can change it
        // to return in whatever format you like
        // e.g. output = "-79/1,58/1,56/1"
        output = degrees + "/1," + minutes + "/1," + seconds + "/1";

        // Standard output of D°M′S″
        // output = degrees + "°" + minutes + "'" + seconds + "\"";

        return output;
    }

    /**
     * 根据ImageView大小与传过来的本地图片大小进行压缩
     *
     * @param path
     * @param imageView
     * @return
     */
    private Bitmap decodeSampledBitmapFromResource(String path, ImageView imageView) {

        ImageUtils.ImageSize targetSize = ImageUtils.getImageViewSize(imageView);
        ImageUtils.ImageSize srcSize = ImageUtils.getImageSize(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = ImageUtils.calculateInSampleSize(srcSize, targetSize);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    /**
     * 根据当前时间生成保存的图片路径和名称
     *
     * @return
     */
    private ImageFile getImageFileDirAndName() {

        ImageFile imageFile = new ImageFile();
        Date now = new Date();
        DateFormat dateFormat = DateFormat.getDateInstance();
        String date = dateFormat.format(now);
        imageFile.fileDir = MyApplication.sdCardPath + "/SonyCameraRemoteControl/" + date;
        Log.e(TAG, imageFile.fileDir + "");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss",
                Locale.getDefault());
        imageFile.fileName = simpleDateFormat.format(now);
        imageFile.fileName = imageFile.fileName + ".jpg";
        Log.e(TAG, imageFile.fileName + "");

        return imageFile;
    }

    /**
     * 根据ImageView大小压缩网络图片后显示
     * 注：BitmapFactory.decodeStream只能使用一次，连续调用需重新初始化流
     *
     * @param imageUrl
     * @param imageView
     * @return
     */
    private Bitmap decodeSampledBitmapFromNetResource(String imageUrl, ImageView imageView) {

        Bitmap bm = null;
        try {

            URL url = new URL(imageUrl);
            InputStream iStream = new BufferedInputStream(url.openStream());
            ImageUtils.ImageSize srcSize = ImageUtils.getImageSize(iStream);
            iStream.close();
            iStream = new BufferedInputStream(url.openStream());        //重新初始化
            ImageUtils.ImageSize targetSize = ImageUtils.getImageViewSize(imageView);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = ImageUtils.calculateInSampleSize(srcSize, targetSize);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeStream(iStream, null, options);
            iStream.close();
        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }

        return bm;
    }

    private class ImageFile {
        String fileDir;
        String fileName;
    }

    /**
     * Call startMovieRec
     */
    private void startMovieRec() {
        new Thread() {

            @Override
            public void run() {
                try {
                    Log.d(TAG, "startMovieRec: exec.");
                    JSONObject replyJson = mRemoteApi.startMovieRec();
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {
                        DisplayHelper.toast(getApplicationContext(), R.string.msg_rec_start);
                    } else {
                        Log.w(TAG, "startMovieRec: error: " + resultCode);
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_api_calling);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "startMovieRec: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "startMovieRec: JSON format error.");
                }
            }
        }.start();
    }

    /**
     * Call stopMovieRec
     */
    private void stopMovieRec() {
        new Thread() {

            @Override
            public void run() {
                try {
                    Log.d(TAG, "stopMovieRec: exec.");
                    JSONObject replyJson = mRemoteApi.stopMovieRec();
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    String thumbnailUrl = resultsObj.getString(0);
                    if (thumbnailUrl != null) {
                        DisplayHelper.toast(getApplicationContext(), R.string.msg_rec_stop);
                    } else {
                        Log.w(TAG, "stopMovieRec: error");
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_api_calling);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "stopMovieRec: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "stopMovieRec: JSON format error.");
                }
            }
        }.start();
    }

    /**
     * Call actZoom
     *
     * @param direction
     * @param movement
     */
    private void actZoom(final String direction, final String movement) {
        new Thread() {

            @Override
            public void run() {
                try {
                    JSONObject replyJson = mRemoteApi.actZoom(direction, movement);
                    JSONArray resultsObj = replyJson.getJSONArray("result");
                    int resultCode = resultsObj.getInt(0);
                    if (resultCode == 0) {
                        // Success, but no refresh UI at the point.
                        Log.v(TAG, "actZoom: success");
                    } else {
                        Log.w(TAG, "actZoom: error: " + resultCode);
                        DisplayHelper.toast(getApplicationContext(), //
                                R.string.msg_error_api_calling);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "actZoom: IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "actZoom: JSON format error.");
                }
            }
        }.start();
    }

    private void prepareToStartContentsListMode() {
        Log.d(TAG, "prepareToStartContentsListMode() exec");
        new Thread() {

            @Override
            public void run() {
                try {
                    // set Listener
                    moveToDateListAfterChangeCameraState();

                    // set camera function to Contents Transfer
                    Log.d(TAG, "call setCameraFunction");
                    JSONObject replyJson = mRemoteApi.setCameraFunction("Contents Transfer");
                    Log.e(TAG, replyJson.toString());
                    if (RemoteApi.isErrorReply(replyJson)) {
                        Log.w(TAG, "prepareToStartContentsListMode: set CameraFunction error: ");
                        DisplayHelper.toast(getApplicationContext(), R.string.msg_error_content);
                        mEventObserver.setEventChangeListener(mEventListener);
                    }

                } catch (IOException e) {
                    Log.w(TAG, "prepareToStartContentsListMode: IOException: " + e.getMessage());
                }
            }
        }.start();

    }

    private void moveToDateListAfterChangeCameraState() {
        Log.d(TAG, "moveToDateListAfterChangeCameraState() exec");

        // set Listener
        mEventObserver.setEventChangeListener(new CameraEventObserver.ChangeListenerTmpl() {

            @Override
            public void onCameraStatusChanged(String status) {
                Log.d(TAG, "onCameraStatusChanged:" + status);
                if ("ContentsTransfer".equals(status)) {
                    // start ContentsList mode
                    //Intent intent = new Intent(getApplicationContext(), DateListActivity.class);
                    //startActivity(intent);
                }

                refreshUi();
            }

            @Override
            public void onShootModeChanged(String shootMode) {
                refreshUi();
            }
        });
    }

    private void startLiveview() {
        if (pvLiveview == null) {
            Log.w(TAG, "startLiveview mLiveviewSurface is null.");
            return;
        }
        new Thread() {
            @Override
            public void run() {

                try {
                    JSONObject replyJson = null;
                    replyJson = mRemoteApi.startLiveview();

                    if (!RemoteApi.isErrorReply(replyJson)) {
                        JSONArray resultsObj = replyJson.getJSONArray("result");
                        if (1 <= resultsObj.length()) {
                            // Obtain liveview URL from the result.
                            final String liveviewUrl = resultsObj.getString(0);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    pvLiveview.start(liveviewUrl, //
                                            new PlayerView.StreamErrorListener() {

                                                @Override
                                                public void onError(StreamErrorReason reason) {
                                                    stopLiveview();
                                                }
                                            });
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    Log.w(TAG, "startLiveview IOException: " + e.getMessage());
                } catch (JSONException e) {
                    Log.w(TAG, "startLiveview JSONException: " + e.getMessage());
                }
            }
        }.start();
    }

    private void stopLiveview() {
        new Thread() {
            @Override
            public void run() {
                try {

                    mRemoteApi.stopLiveview();
                } catch (IOException e) {

                    Log.w(TAG, "stopLiveview IOException: " + e.getMessage());
                }
            }
        }.start();
    }

    public static void actionStart(Context context) {

        Intent intent = new Intent(context, PlayerActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.btn_capture, R.id.btn_zoom_tele, R.id.btn_zoom_wide,
            R.id.iv_image_wipe, R.id.btn_setup, R.id.btn_change_mode,
            R.id.btn_start_record, R.id.btn_stop_record})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_capture:
                takeAndFetchPicture();
                break;

            case R.id.btn_zoom_tele:
                actZoom("out", "1shot");
                break;

            case R.id.btn_zoom_wide:
                actZoom("in", "1shot");
                break;

            case R.id.iv_image_wipe:
                PhotoPickerActivity.actionStart(PlayerActivity.this, 9,
                        Environment.getExternalStorageDirectory() + "/SonyCameraRemoteControl");
                ivImageWipe.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_setup:
                break;

            case R.id.btn_change_mode:
                popMenuMode.showAtLocation(view, Gravity.CENTER, 0, 0);
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = .3f;
                getWindow().setAttributes(lp);
                break;

            case R.id.btn_start_record:
                if ("IDLE".equals(mEventObserver.getCameraStatus())) {

                    startMovieRec();
                } else {

                    Toast.makeText(getApplicationContext(), "faile to start movie record!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_stop_record:
                if ("MovieRecording".equals(mEventObserver.getCameraStatus())) {

                    stopMovieRec();
                } else {

                    Toast.makeText(getApplicationContext(), "faile to stop movie record!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
