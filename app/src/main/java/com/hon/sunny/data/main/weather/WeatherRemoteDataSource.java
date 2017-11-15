package com.hon.sunny.data.main.weather;

import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.data.main.bean.Weather;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@Singleton
public class WeatherRemoteDataSource implements WeatherDataSource{

    @Inject
    public WeatherRemoteDataSource(){}

    @Override
    public Observable<Weather> fetchWeather(String city) {
        return RetrofitSingleton.getInstance().fetchWeather(city);
    }
}
