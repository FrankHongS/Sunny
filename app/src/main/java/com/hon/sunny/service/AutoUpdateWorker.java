package com.hon.sunny.service;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hon.sunny.common.Constants;
import com.hon.sunny.common.MyLogger;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.main.MainActivity;

import java.util.concurrent.TimeUnit;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Frank_Hon on 2/15/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class AutoUpdateWorker extends Worker {

    private Context mContext;

    public AutoUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Observable
                .interval(SharedPreferenceUtil.getInstance().getInt(Constants.CHANGE_UPDATE_TIME, 3),
                        TimeUnit.HOURS)
                .flatMap(new Func1<Long, Observable<Weather>>() {
                    @Override
                    public Observable<Weather> call(Long aLong) {
                        MyLogger.d("fetchDataByNetWork: ");

                        String cityName = SharedPreferenceUtil.getInstance().getCityName();
                        if (cityName != null) {
                            cityName = Util.replaceCity(cityName);
                        }

                        return RetrofitSingleton.getInstance().fetchWeather(cityName);
                    }
                })
                .subscribe(
                        new Subscriber<Weather>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
//                        RetrofitSingleton.disposeFailureInfo(e);
                            }

                            @Override
                            public void onNext(Weather weather) {
                                Util.normalStyleNotification(Constants.CHANNEL_ID_WEATHER, weather, mContext, MainActivity.class);
                            }
                        }
                );

        return Result.success();
    }

}
