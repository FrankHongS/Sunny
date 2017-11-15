package com.hon.sunny.main.multicity;

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
public abstract class MultiCityModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract MultiCityFragment multiCityFragment();

    @ActivityScoped
    @Binds
    abstract MultiCityContract.Presenter multiCityPresenter(MultiCityPresenter presenter);
}
