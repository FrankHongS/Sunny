package com.hon.sunny.di;

import com.hon.sunny.main.MainActivity;
import com.hon.sunny.main.multicity.MultiCityModule;
import com.hon.sunny.main.weather.WeatherModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Frank_Hon on 2017/11/14.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(modules = {WeatherModule.class, MultiCityModule.class})
    abstract MainActivity mainActivity();
}
