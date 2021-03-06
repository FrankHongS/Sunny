package com.hon.sunny.data.main.weather;

import com.hon.sunny.vo.bean.main.Weather;

import io.reactivex.Flowable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherRepository implements WeatherDataSource {

    private static WeatherRepository INSTANCE;

    private final WeatherDataSource mWeatherRemoteDataSource;

    private WeatherRepository(WeatherDataSource weatherRemoteDataSource) {
        mWeatherRemoteDataSource = weatherRemoteDataSource;
    }

    public static WeatherRepository getInstance(WeatherDataSource weatherRemoteDataSource) {
        if (INSTANCE == null) {
            synchronized (WeatherRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WeatherRepository(weatherRemoteDataSource);
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public Flowable<Weather> fetchWeather(String city) {
        return mWeatherRemoteDataSource.fetchWeather(city);
    }
}
