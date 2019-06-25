package com.hon.sunny.data.main.weather;

import com.hon.sunny.network.RetrofitSingleton;
import com.hon.sunny.vo.bean.main.Weather;

import io.reactivex.Flowable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherRemoteDataSource implements WeatherDataSource {

    private static WeatherRemoteDataSource INSTANCE;

    private WeatherRemoteDataSource() {
    }

    public static WeatherRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (WeatherRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WeatherRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Flowable<Weather> fetchWeather(String city) {
        return RetrofitSingleton.getInstance().fetchWeather(city);
    }
}
