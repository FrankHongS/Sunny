package com.hon.sunny.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hon.sunny.R;
import com.hon.sunny.base.Constants;
import com.hon.sunny.common.PLog;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.main.MainActivity;
import com.hon.sunny.data.main.bean.Weather;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class AutoUpdateService extends Service {

    private SharedPreferenceUtil mSharedPreferenceUtil;
    // http://blog.csdn.net/lzyzsd/article/details/45033611
    // 在生命周期的某个时刻取消订阅。一个很常见的模式就是使用CompositeSubscription来持有所有的Subscriptions，然后在onDestroy()或者onDestroyView()里取消所有的订阅
    private CompositeSubscription mCompositeSubscription;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCompositeSubscription.clear();
        if (mSharedPreferenceUtil.getAutoUpdate() != 0) {
            Subscription netSubscription = Observable.interval(mSharedPreferenceUtil.getAutoUpdate(), TimeUnit.HOURS)
                    .subscribe(aLong -> {
                        fetchDataByNetWork();
                    });
            mCompositeSubscription.add(netSubscription);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = mSharedPreferenceUtil.getCityName();
        if (cityName != null) {
            cityName = Util.replaceCity(cityName);
        }
        RetrofitSingleton.getInstance().fetchWeather(cityName)
//                .doOnError(throwable -> RetrofitSingleton.disposeFailureInfo(throwable))
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        RetrofitSingleton.disposeFailureInfo(e);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        Util.normalStyleNotification(Constants.CHANNEL_ID_WEATHER,weather, AutoUpdateService.this, MainActivity.class);
                    }
                });
    }

}

