package com.hon.sunny.data.main.weather;


import com.hon.sunny.di.ActivityScoped;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Frank_Hon on 2017/11/15.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class WeatherRepositoryModule {
    @ActivityScoped
    @Binds
    abstract WeatherDataSource provideWeatherRemoteDataSource(WeatherRemoteDataSource dataSource);
}
