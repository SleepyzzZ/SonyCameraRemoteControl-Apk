package com.sleepyzzz.sonycameraremotecontrol.sonyapi;

import android.util.Log;

import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.ServerDevice;
import com.sleepyzzz.sonycameraremotecontrol.sonyapi.util.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName：
 * @Description：TODO-Simple Camera Remote API wrapper class. (JSON based API <--> Java API)
 * @author：SleepyzzZ on 2016/3/24 17:06
 * @
 * @
 * @update：Administrator on 2016/3/24 17:06
 * @modify：
 */
public class RemoteApi {

    private static final String TAG = RemoteApi.class.getSimpleName();

    // If you'd like to suppress detailed log output, change this value into
    // false.
    private static final boolean FULL_LOG = true;

    // API server device you want to send requests.
    private ServerDevice mTargetServer;

    // Request ID of API calling. This will be counted up by each API calling.
    private int mRequestId;

    /**
     * Constructor.
     *
     * @param target server device of Remote API
     */
    public RemoteApi(ServerDevice target) {
        mTargetServer = target;
        mRequestId = 1;
    }

    /**
     * Retrieves Action List URL from Server information.
     *
     * @param service
     * @return
     * @throws IOException
     */
    private String findActionListUrl(String service) throws IOException {
        List<ServerDevice.ApiService> services = mTargetServer.getApiServices();
        for (ServerDevice.ApiService apiService : services) {
            if (apiService.getName().equals(service)) {
                return apiService.getActionListUrl();
            }
        }
        throw new IOException("actionUrl not found. service : " + service);
    }

    /**
     * Request ID. Counted up after calling.
     *
     * @return
     */
    private int id() {
        return mRequestId++;
    }

    // Output a log line.
    private void log(String msg) {
        if (FULL_LOG) {
            Log.d(TAG, msg);
        }
    }

    // Camera Service APIs

    /**
     * Calls getAvailableApiList API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "getAvailableApiList",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getAvailableApiList() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableApiList")
                            .put("params", new JSONArray()).put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getApplicationInfo API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "getApplicationInfo",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getApplicationInfo() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getApplicationInfo") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getShootMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getShootMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setShootMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "setShootMode",
     *   "params": ["still"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param shootMode shoot mode (ex. "still")
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject setShootMode(String shootMode) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setShootMode") //
                            .put("params", new JSONArray().put(shootMode)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getAvailableShootMode API to the target server. Request JSON data
     * is such like as below.
     *
     * <pre>
     * {
     *   "method": "getAvailableShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws all errors and exception are wrapped by this Exception.
     */
    public JSONObject getAvailableShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableShootMode") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSupportedShootMode API to the target server. Request JSON data
     * is such like as below.
     *
     * <pre>
     * {
     *   "method": "getSupportedShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getSupportedShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSupportedShootMode") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startLiveview API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopLiveview API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject stopLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startRecMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startRecMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startRecMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startRecMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actTakePicture API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "actTakePicture",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException
     */
    public JSONObject actTakePicture() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actTakePicture").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startMovieRec API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startMovieRec").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopMovieRec API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject stopMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopMovieRec").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actZoom API to the target server. Request JSON data is such like as
     * below.
     *
     * <pre>
     * {
     *   "method": "actZoom",
     *   "params": ["in","stop"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param direction direction of zoom ("in" or "out")
     * @param movement zoom movement ("start", "stop", or "1shot")
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject actZoom(String direction, String movement) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actZoom") //
                            .put("params", new JSONArray().put(direction).put(movement)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getEvent API to the target server. Request JSON data is such like
     * as below.
     *
     * <pre>
     * {
     *   "method": "getEvent",
     *   "params": [true],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param longPollingFlag true means long polling request.
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getEvent(boolean longPollingFlag) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getEvent") //
                            .put("params", new JSONArray().put(longPollingFlag)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;
            int longPollingTimeout = (longPollingFlag) ? 20000 : 8000; // msec

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString(),
                    longPollingTimeout);
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setCameraFunction API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "setCameraFunction",
     *   "params": ["Remote Shooting"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param cameraFunction camera function to set
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject setCameraFunction(String cameraFunction) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setCameraFunction") //
                            .put("params", new JSONArray().put(cameraFunction)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getMethodTypes API of Camera service to the target server. Request
     * JSON data is such like as below.
     *
     * <pre>
     * {
     *   "method": "getMethodTypes",
     *   "params": ["1.0"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getCameraMethodTypes() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    // Avcontent APIs

    /**
     * Calls getMethodTypes API of AvContent service to the target server.
     * Request JSON data is such like as below.
     *
     * <pre>
     * {
     *   "method": "getMethodTypes",
     *   "params": ["1.0"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getAvcontentMethodTypes() throws IOException {
        String service = "avContent";
        try {
            String url = findActionListUrl(service) + "/" + service;
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", id()).put("version", "1.0"); //

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSchemeList API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getSchemeList",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getSchemeList() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSchemeList").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSourceList API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getSourceList",
     *   "params": [{
     *      "scheme": "storage"
     *      }],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param scheme target scheme to get source
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getSourceList(String scheme) throws IOException {
        String service = "avContent";
        try {

            JSONObject params = new JSONObject().put("scheme", scheme);
            JSONObject requestJson =
                    new JSONObject().put("method", "getSourceList") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getContentList API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getContentList",
     *   "params": [{
     *      "sort" : "ascending"
     *      "view": "date"
     *      "uri": "storage:memoryCard1"
     *      }],
     *   "id": 2,
     *   "version": "1.3"
     * }
     * </pre>
     *
     * @param params request JSON parameter of "params" object.
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getContentList(JSONArray params) throws IOException {
        String service = "avContent";
        try {

            JSONObject requestJson =
                    new JSONObject().put("method", "getContentList").put("params", params) //
                            .put("version", "1.3").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setStreamingContent API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "setStreamingContent",
     *   "params": [
     *      "remotePlayType" : "simpleStreaming"
     *      "uri": "image:content?contentId=01006"
     *      ],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param uri streaming contents uri
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject setStreamingContent(String uri) throws IOException {
        String service = "avContent";
        try {

            JSONObject params = new JSONObject().put("remotePlayType", "simpleStreaming").put(
                    "uri", uri);
            JSONObject requestJson =
                    new JSONObject().put("method", "setStreamingContent") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startStreaming API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startStreaming",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startStreaming() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0").put("id", id());
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopStreaming API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopStreaming",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject stopStreaming() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * <pre>
     * {
     *   "method": "actHalfPressShutter", "cancelHalfPressShutter"
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * 半按快门
     * @param method
     * @return
     * @throws IOException
     */
    public JSONObject setHalPressShuter(String method) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", method).put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = HttpClient.httpPost(url, requestJson.toString());
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * <pre>
     * {
     *   "method": "setTouchAFPosition"
     *   "params": [23.4, 45.6],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * <pre>
     * {
     *   "method": "getTouchAFPosition"
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * <pre>
     * {
     *   "method": "cancelTouchAFPosition"
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * 触屏对焦
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    public JSONObject touchAFPosition(String method, double x, double y) throws IOException {
        String service = "camera";

        if(method.equals("setTouchAFPosition")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(0, x).put(1, y))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getTouchAFPosition")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("cancelTouchAFPosition")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 定时自拍
     * @param method
     * @param second
     * @return
     * @throws IOException
     */
    public JSONObject self_Timer(String method, int second) throws IOException {
        String service = "camera";

        if(method.equals("setSelfTimer")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(second))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSelfTimer")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedSelfTimer")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableSelfTimer")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 曝光模式
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject exposureMode(String method, String params) throws IOException {
        String service = "camera";

        if(method.equals("setExposureMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(params))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getExposureMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedExposureMode")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableExposureMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 对焦模式
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject focusMode(String method, String params) throws IOException {
        String service = "camera";

        if(method.equals("setFocusMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(params))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getFocusMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedFocusMode")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableFocusMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 曝光补偿
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject exposureCompensation(String method, int params) throws IOException {
        String service = "camera";

        if(method.equals("setExposureCompensation")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(params))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getExposureCompensation")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedExposureCompensation")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableExposureCompensation")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 光圈大小
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject f_number(String method, String params) throws IOException {
        String service = "camera";

        if(method.equals("setFNumber")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(params))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getFNumber")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedFNumber")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableFNumber")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 快门速度
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject shutterSpeed(String method, String params) throws IOException {
        String service = "camera";

        if(method.equals("setShutterSpeed")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(params))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getShutterSpeed")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedShutterSpeed")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableShutterSpeed")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * ISO感光度
     * @param method
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject ISO_speedrate(String method, String params) throws IOException {
        String service = "camera";

        if(method.equals("setIsoSpeedRate")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(params))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getIsoSpeedRate")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedIsoSpeedRate")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableIsoSpeedRate")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 白平衡
     * @param method
     * @param mode
     * @param flag
     * @param value
     * @return
     * @throws IOException
     */
    public JSONObject whiteBalance(String method, String mode, boolean flag, int value) throws IOException {
        String service = "camera";

        if(method.equals("setWhiteBalance")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method)
                                .put("params", new JSONArray().put(0, mode).put(1, flag).put(2, value))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getWhiteBalance")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedWhiteBalance")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableWhiteBalance")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 拍摄分辨率
     * @param method
     * @param aspect
     * @param size
     * @return
     * @throws IOException
     */
    public JSONObject stillSize(String method, String aspect, String size) throws IOException {
        String service = "camera";

        if(method.equals("setStillSize")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method)
                                .put("params", new JSONArray().put(0, aspect).put(1, size))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getStillSize")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedStillSize")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableStillSize")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    /**
     * 哔哔声
     * @param method
     * @param mode
     * @return
     * @throws IOException
     */
    public JSONObject beepMode(String method, String mode) throws IOException {
        String service = "camera";

        if(method.equals("setBeepMode")) {
            Log.e(TAG, "setBeepMode in");
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray().put(mode))
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getBeepMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getSupportedBeepMode")){
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        } else if(method.equals("getAvailableBeepMode")) {
            try {
                JSONObject requestJson =
                        new JSONObject().put("method", method).put("params", new JSONArray())
                                .put("id", id()).put("version", "1.0");
                String url = findActionListUrl(service) + "/" + service;

                log("Request:  " + requestJson.toString());
                String responseJson = HttpClient.httpPost(url, requestJson.toString());
                return new JSONObject(responseJson);
            } catch (JSONException e) {
                throw new IOException(e);
            }
        }

        return null;
    }

    // static method

    /**
     * Parse JSON and return whether it has error or not.
     *
     * @param replyJson JSON object to check
     * @return return true if JSON has error. otherwise return false.
     */
    public static boolean isErrorReply(JSONObject replyJson) {
        boolean hasError = (replyJson != null && replyJson.has("error"));
        return hasError;
    }
}
