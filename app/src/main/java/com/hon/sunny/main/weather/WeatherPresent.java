package com.hon.sunny.main.weather;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.data.main.weather.WeatherRepository;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.di.ActivityScoped;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Subscriber;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */
@ActivityScoped
public class WeatherPresent implements WeatherContract.Presenter{

    private WeatherRepository mWeatherRepository;

    @Nullable
    private WeatherContract.View mWeatherView;


    @Inject
    WeatherPresent(@NonNull WeatherRepository weatherRepository){
        mWeatherRepository=weatherRepository;
    }

    @Override
    public void takeView(WeatherContract.View view) {
        mWeatherView=view;
        loadWeather();
    }

    @Override
    public void dropView() {

    }

    @Override
    public void loadWeather() {
        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        mWeatherRepository.fetchWeather(cityName)
//                .compose(((RxFragment)mWeatherView).bindToLifecycle())
                .doOnRequest(aLong -> mWeatherView.doOnRequest())
                .doOnNext(weather -> mWeatherView.doOnNext())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mWeatherView.onError(e);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        mWeatherView.onNext(weather);
                    }
                });
    }
}
