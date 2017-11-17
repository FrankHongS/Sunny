package com.hon.sunny.setting;

import com.hon.sunny.di.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by Frank_Hon on 2017/11/17.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class SettingModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract SettingFragment settingFragment();
}
