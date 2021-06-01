package com.hon.sunny.ui.main.weather;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.hon.sunny.data.main.weather.WeatherRepository;
import com.hon.sunny.utils.SharedPreferenceUtil;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherPresent implements WeatherContract.Presenter, LifecycleObserver {

    private WeatherRepository mWeatherRepository;
    private WeatherContract.View mWeatherView;

    private CompositeDisposable mWeatherCompositeDisposable;

    public WeatherPresent(Lifecycle lifecycle, @NonNull WeatherRepository weatherRepository, @NonNull WeatherContract.View weatherView) {
        mWeatherRepository = weatherRepository;
        mWeatherView = weatherView;

        mWeatherView.setPresenter(this);

        lifecycle.addObserver(this);

        mWeatherCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void start() {
        loadWeather();
    }

    @Override
    public void loadWeather() {
        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        Disposable weatherDisposable = mWeatherRepository.fetchWeather(cityName)
                .doOnRequest(l -> mWeatherView.doOnRequest())
                .doOnNext(weather -> mWeatherView.doOnNext())
                .subscribe(
                        weather -> mWeatherView.onNext(weather),
                        throwable -> mWeatherView.onError(throwable)
                );

        mWeatherCompositeDisposable.add(weatherDisposable);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void dispose() {
        mWeatherCompositeDisposable.dispose();
    }
}
