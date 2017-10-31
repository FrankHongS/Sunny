package com.hon.sunny.data.main.multicity;

import com.hon.sunny.data.main.bean.Weather;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityRepository implements MultiCityDataSource{
    private static MultiCityRepository INSTANCE;

    private final MultiCityDataSource mMultiCityRemoteDataSource;

    private MultiCityRepository(MultiCityDataSource multiCityRemoteDataSource){
        mMultiCityRemoteDataSource=multiCityRemoteDataSource;
    }

    public static MultiCityRepository getInstance(MultiCityDataSource multiCityRemoteDataSource){
        if(INSTANCE==null){
            synchronized (MultiCityRepository.class){
                if(INSTANCE==null){
                    INSTANCE=new MultiCityRepository(multiCityRemoteDataSource);
                }
            }
        }
        return INSTANCE;
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
