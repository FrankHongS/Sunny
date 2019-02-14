package com.hon.sunny.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hon.sunny.common.Constants;
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

import static com.hon.sunny.common.Constants.AUTO_UPDATE;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class AutoUpdateService extends Service {

    private static final String TAG=AutoUpdateService.class.getSimpleName();
    
    private SharedPreferenceUtil mSharedPreferenceUtil;
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

        Log.d(TAG, "onStartCommand: ");
        Subscription netSubscription = Observable
                .interval(mSharedPreferenceUtil.getInt(Constants.CHANGE_UPDATE_TIME, 3),
                        TimeUnit.HOURS)
                .subscribe(aLong -> {
                    fetchDataByNetWork();
                });
        mCompositeSubscription.add(netSubscription);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.clear();
        Log.d(TAG, "onDestroy: ");
    }

    private void fetchDataByNetWork() {

        Log.d(TAG, "fetchDataByNetWork: ");
        
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
                        Util.normalStyleNotification(Constants.CHANNEL_ID_WEATHER, weather, AutoUpdateService.this, MainActivity.class);
                    }
                });
    }

}

