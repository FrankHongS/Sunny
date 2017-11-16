package com.hon.sunny.di;

import com.hon.sunny.city.SearchCityActivity;
import com.hon.sunny.city.SearchCityModule;
import com.hon.sunny.data.city.CityRepositoryModule;
import com.hon.sunny.data.main.multicity.MultiCityRepositoryModule;
import com.hon.sunny.data.main.weather.WeatherRepositoryModule;
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
    @ContributesAndroidInjector(modules = {WeatherModule.class, WeatherRepositoryModule.class,
                                            MultiCityModule.class, MultiCityRepositoryModule.class})
    abstract MainActivity mainActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {SearchCityModule.class, CityRepositoryModule.class})
    abstract SearchCityActivity searchCityActivity();
}
