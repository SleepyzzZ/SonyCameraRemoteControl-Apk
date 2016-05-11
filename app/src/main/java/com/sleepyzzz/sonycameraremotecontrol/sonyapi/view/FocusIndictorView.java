package com.sleepyzzz.sonycameraremotecontrol.sonyapi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sleepyzzz.sonycameraremotecontrol.R;

/**
 * @ClassName：
 * @Description：TODO
 * @author：Administrator on 2016/3/31 15:47
 * @
 * @
 * @update：Administrator on 2016/3/31 15:47
 * @modify：
 */
public class FocusIndictorView extends View implements FocusIndictor {

    public FocusIndictorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setDrawable(int resid) {
        setBackgroundDrawable(getResources().getDrawable(resid));
    }

    @Override
    public void focusStart() {
        setDrawable(R.drawable.image_touch_af_frame_processing);
    }

    @Override
    public void focusSuccess() {
        setDrawable(R.drawable.image_touch_af_frame_success);
    }

    @Override
    public void focusFail() {
        setDrawable(R.drawable.image_touch_af_frame_failure);
    }

    @Override
    public void focusClear() {
        setBackgroundDrawable(null);
    }
}
