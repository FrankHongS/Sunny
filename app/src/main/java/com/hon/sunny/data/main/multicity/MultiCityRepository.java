package com.hon.sunny.data.main.multicity;

import com.hon.sunny.data.main.bean.Weather;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@Singleton
public class MultiCityRepository implements MultiCityDataSource{

    private final MultiCityDataSource mMultiCityRemoteDataSource;

    @Inject
    MultiCityRepository(MultiCityDataSource multiCityRemoteDataSource){
        mMultiCityRemoteDataSource=multiCityRemoteDataSource;
    }

    @Override
    public Observable<Weather> fetchMultiCityWeather(Observable<String> citiesObservable) {
        return mMultiCityRemoteDataSource.fetchMultiCityWeather(citiesObservable);
    }

    @Override
    public Observable<String> getCities() {
        return mMultiCityRemoteDataSource.getCities();
    }

    @Override
    public String currentLoadingCity() {
        return mMultiCityRemoteDataSource.currentLoadingCity();
    }
}
