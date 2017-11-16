package com.hon.sunny.di;

import android.app.Application;

import com.hon.sunny.Sunny;
import com.hon.sunny.data.city.CityRepository;
import com.hon.sunny.data.city.CityRepositoryModule;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.data.main.multicity.MultiCityRepositoryModule;
import com.hon.sunny.data.main.weather.WeatherRepository;
import com.hon.sunny.data.main.weather.WeatherRepositoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

/**
 * Created by Frank_Hon on 2017/11/14.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Singleton
@Component(modules = {
        ApplicationModule.class,
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<DaggerApplication>{
    void inject(Sunny application);

//    MultiCityRepository getMultiCityRepository();
//    WeatherRepository getWeatherRepository();

    @Component.Builder
    interface Builder{
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
