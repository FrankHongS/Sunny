package com.hon.sunny.ui.main.weather;

import androidx.annotation.NonNull;

import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.data.main.weather.WeatherRepository;
import com.hon.sunny.data.main.bean.Weather;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Subscriber;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherPresent implements WeatherContract.Presenter{

    private WeatherRepository mWeatherRepository;

    private WeatherContract.View mWeatherView;

    public WeatherPresent(@NonNull WeatherRepository weatherRepository, @NonNull WeatherContract.View weatherView){
        mWeatherRepository=weatherRepository;
        mWeatherView=weatherView;

        mWeatherView.setPresenter(this);
    }

    @Override
    public void start() {
        loadWeather();
    }

    @Override
    public void loadWeather() {
        String cityName = SharedPreferenceUtil.getInstance().getCityName();
        mWeatherRepository.fetchWeather(cityName)
                .compose(((RxFragment)mWeatherView).bindToLifecycle())
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
