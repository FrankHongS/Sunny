package com.hon.sunny.utils;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public class RxUtils {
    public static <T> Observable.Transformer<T, T> rxSchedulerHelper() {
        return tObservable -> tObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 可自定义线程
     */
    public static <T> Observable.Transformer<T, T> rxSchedulerHelper(Scheduler scheduler) {
        return tObservable -> tObservable.subscribeOn(scheduler)
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

