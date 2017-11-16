package com.hon.sunny.data.main.multicity;

import com.hon.sunny.data.main.bean.Weather;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public interface MultiCityDataSource {

    Observable<Weather> fetchMultiCityWeather(Observable<String> citiesObservable);

    Observable<String> getCities();

}
