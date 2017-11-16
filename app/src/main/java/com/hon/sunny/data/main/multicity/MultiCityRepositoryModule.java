package com.hon.sunny.data.main.multicity;

import com.hon.sunny.di.ActivityScoped;


import dagger.Binds;
import dagger.Module;

/**
 * Created by Frank_Hon on 2017/11/15.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class MultiCityRepositoryModule {

    @ActivityScoped
    @Binds
    abstract MultiCityDataSource provideMultiCityRemoteDataSource(MultiCityRemoteDataSource dataSource);
}
