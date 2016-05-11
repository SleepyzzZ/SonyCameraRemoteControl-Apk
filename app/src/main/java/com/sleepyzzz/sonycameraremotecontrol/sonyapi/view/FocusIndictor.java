package com.sleepyzzz.sonycameraremotecontrol.sonyapi.view;

/**
 * @ClassName：FocusIndictor
 * @Description：TODO
 * @author：SleepyzzZ on 2016/3/31 15:45
 * @
 * @
 * @update：Administrator on 2016/3/31 15:45
 * @modify：
 */
public interface FocusIndictor {

    public void focusStart();
    public void focusSuccess();
    public void focusFail();
    public void focusClear();
}
