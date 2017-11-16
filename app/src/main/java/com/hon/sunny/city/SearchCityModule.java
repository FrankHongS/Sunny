package com.hon.sunny.city;

import com.hon.sunny.di.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Frank_Hon on 2017/11/16.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class SearchCityModule {
    @ActivityScoped
    @Binds
    abstract SearchCityContract.Presenter searchCityPresenter(SearchCityPresenter presenter);
}
