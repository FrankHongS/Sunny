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

    private final String TAG = AutoUpdateService.class.getSimpleName();
    private SharedPreferenceUtil mSharedPreferenceUtil;
    // http://blog.csdn.net/lzyzsd/article/details/45033611
    // 在生命周期的某个时刻取消订阅。一个很常见的模式就是使用CompositeSubscription来持有所有的Subscriptions，然后在onDestroy()或者onDestroyView()里取消所有的订阅
    private CompositeSubscription mCompositeSubscription;
    private Subscription mNetSubscription;

    private boolean mIsUnSubscribed = true;

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
        fetchDataByNetWork();
        Log.d(TAG, "onStartCommand: ");
        synchronized (this) {
            unSubscribed();
            if (mIsUnSubscribed) {
                unSubscribed();
                if (mSharedPreferenceUtil.getAutoUpdate() != 0) {
                    mNetSubscription = Observable.interval(mSharedPreferenceUtil.getAutoUpdate(), TimeUnit.HOURS)
                            .subscribe(aLong -> {
                                Log.d(TAG, "fetchDataByNetWork: ");
                                mIsUnSubscribed = false;
                                //PLog.i(TAG, SystemClock.elapsedRealtime() + " 当前设置" + mSharedPreferenceUtil.getAutoUpdate());
                                fetchDataByNetWork();
                            });
                    mCompositeSubscription.add(mNetSubscription);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void unSubscribed() {
        mIsUnSubscribed = true;
        mCompositeSubscription.remove(mNetSubscription);
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
                        Util.normalStyleNotification(weather,AutoUpdateService.this,MainActivity.class);
                    }
                });
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(AutoUpdateService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(AutoUpdateService.this);
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
//                .setWhen(System.currentTimeMillis())
//                .setShowWhen(true)
                // 这里部分 ROM 无法成功
                .setSmallIcon(mSharedPreferenceUtil.getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = mSharedPreferenceUtil.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}

