package com.hon.sunny.data.city;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Frank_Hon on 2017/11/16.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class CityRepositoryModule {
    @Singleton
    @Binds
    abstract CityDataSource provideCityLocalDataSource(CityLocalDataSource dataSource);
}
