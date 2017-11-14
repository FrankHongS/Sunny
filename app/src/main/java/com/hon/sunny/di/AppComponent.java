package com.hon.sunny.di;

import android.app.Application;

import com.hon.sunny.Sunny;
import com.hon.sunny.data.city.CityRepositoryModule;

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
        CityRepositoryModule.class,
        AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<DaggerApplication>{
    void inject(Sunny application);

    @Component.Builder
    interface Builder{
        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
