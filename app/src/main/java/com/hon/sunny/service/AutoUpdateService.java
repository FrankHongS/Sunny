package com.hon.sunny.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.common.Constants;
import com.hon.sunny.common.MyLogger;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.ui.main.MainActivity;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class AutoUpdateService extends Service {

    private static final String TAG=AutoUpdateService.class.getSimpleName();
    
    private SharedPreferenceUtil mSharedPreferenceUtil;
    private CompositeSubscription mCompositeSubscription;
    private int mCount=0;

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

        boolean initService=intent.getBooleanExtra(Constants.INIT_SERVICE,false);
        if(initService){
            fetchDataByNetWork();
        }

        Subscription netSubscription = Observable
                .interval(SharedPreferenceUtil.getInstance().getInt(Constants.CHANGE_UPDATE_TIME, 3),
                        TimeUnit.HOURS)
                .subscribe(
                            aLong -> fetchDataByNetWork()
                        );
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
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        sendNotification(weather);
                    }
                });
    }

    private void sendNotification(Weather weather){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, Constants.CHANNEL_ID_WEATHER)
                        .setLargeIcon(BitmapFactory.decodeResource(Sunny.getAppContext().getResources(),mSharedPreferenceUtil.getInt(weather.now.txt, R.mipmap.none)))
                        .setContentTitle(weather.city)
                        .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.txt, weather.now.tmp))
                        // important!, not showing if not set
                        .setSmallIcon(R.mipmap.ic_launch_logo)
                        .setContentIntent(pendingIntent)
                        .setTicker("ticker")
                        .build();

        startForeground(1,notification);

    }

}

