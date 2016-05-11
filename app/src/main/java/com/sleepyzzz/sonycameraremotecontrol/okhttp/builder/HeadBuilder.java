package com.sleepyzzz.sonycameraremotecontrol.okhttp.builder;

import com.sleepyzzz.sonycameraremotecontrol.okhttp.OkHttpUtils;
import com.sleepyzzz.sonycameraremotecontrol.okhttp.request.OtherRequest;
import com.sleepyzzz.sonycameraremotecontrol.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers).build();
    }
}
