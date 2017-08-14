package com.hon.sunny.common.util;

import com.hon.sunny.common.PLog;

import rx.Subscriber;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public abstract class SimpleSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        PLog.e(e.toString());
    }
}

