package com.hon.sunny.data.main.multicity;

import com.hon.sunny.vo.bean.main.Weather;

import io.reactivex.Flowable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public interface MultiCityDataSource {

    Flowable<Weather> fetchMultiCityWeather();

}
