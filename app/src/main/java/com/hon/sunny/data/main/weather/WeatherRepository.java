package com.hon.sunny.data.main.weather;

import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.di.ActivityScoped;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@ActivityScoped
public class WeatherRepository implements WeatherDataSource{

    private final WeatherDataSource mWeatherRemoteDataSource;

    @Inject
    WeatherRepository(WeatherDataSource weatherRemoteDataSource){
        mWeatherRemoteDataSource=weatherRemoteDataSource;
    }

    @Override
    public Observable<Weather> fetchWeather(String city) {
       return mWeatherRemoteDataSource.fetchWeather(city);
    }
}
