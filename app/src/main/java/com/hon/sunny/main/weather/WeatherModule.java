package com.hon.sunny.main.weather;

import com.hon.sunny.di.ActivityScoped;
import com.hon.sunny.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Frank_Hon on 2017/11/15.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class WeatherModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract WeatherFragment weatherFragment();

    @ActivityScoped
    @Binds
    abstract WeatherContract.Presenter weatherPresenter(WeatherPresent presenter);
}
