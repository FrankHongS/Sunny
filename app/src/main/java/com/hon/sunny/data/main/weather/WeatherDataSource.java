package com.hon.sunny.data.main.weather;

import com.hon.sunny.data.main.bean.Weather;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public interface WeatherDataSource {
    Observable<Weather> fetchWeather(String city);
}
