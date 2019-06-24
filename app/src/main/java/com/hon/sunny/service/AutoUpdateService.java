package com.hon.sunny.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.PLog;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.Util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class AutoUpdateService extends Service {

    private static final String TAG=AutoUpdateService.class.getSimpleName();
    
    private SharedPreferenceUtil mSharedPreferenceUtil;
    private CompositeDisposable mAutoUpdateCompositeDisposable;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        mAutoUpdateCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        boolean initService=intent.getBooleanExtra(Constants.INIT_SERVICE,false);
//        if(initService){ todo 如何实现启动之后立即更新，下面的interval应该不可以
//            fetchDataByNetWork();
//        }

        Disposable netSubscription = Flowable
                .interval(SharedPreferenceUtil.getInstance().getInt(Constants.CHANGE_UPDATE_TIME, 3),
                        TimeUnit.HOURS)
                .flatMap(l->{
                    String cityName = mSharedPreferenceUtil.getCityName();
                    if (cityName != null) {
                        cityName = Util.replaceCity(cityName);
                        return RetrofitSingleton.getInstance()
                                .fetchWeather(cityName);
                    }else {
                        return Flowable.error(new IllegalArgumentException("city name is null"));
                    }
                })
                .subscribe(
                        this::sendNotification,
                        throwable -> PLog.e(throwable.getMessage())
                );
        mAutoUpdateCompositeDisposable.add(netSubscription);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAutoUpdateCompositeDisposable.dispose();
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

