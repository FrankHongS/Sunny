package com.hon.sunny.data.city;

import com.hon.sunny.di.ActivityScoped;
import com.hon.sunny.di.FragmentScoped;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Frank_Hon on 2017/11/16.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class CityRepositoryModule {
    @ActivityScoped
    @Binds
    abstract CityDataSource provideCityLocalDataSource(CityLocalDataSource dataSource);
}
