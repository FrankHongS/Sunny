package com.hon.sunny.utils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public abstract class SimpleSubscriber<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        PLog.e(e.toString());
    }
}

