package com.sleepyzzz.sonycameraremotecontrol.okhttp.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 *
 *
 */

/**
 * @ClassName：BitmapCallback
 * @Description：TODO
 * @author：Created by zhy on 15/12/14.
 * @
 * @
 * @update：SleepyzzZ on 2016/3/24 19:39
 * @modify：加入压缩后在显示图片
 */
public abstract class BitmapCallback extends Callback<Bitmap>
{
    private BitmapFactory.Options options;

    public BitmapCallback(BitmapFactory.Options options) {
        this.options = options;
    }

    @Override
    public Bitmap parseNetworkResponse(Response response) throws Exception
    {
        return BitmapFactory.decodeStream(response.body().byteStream(), null, options);
    }

}
