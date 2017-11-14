package com.hon.sunny.di;

import android.app.Application;
import android.content.Context;

import dagger.Binds;
import dagger.Module;

/**
 * Created by Frank_Hon on 2017/11/14.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Module
public abstract class ApplicationModule {

    @Binds
    abstract Context bindContext(Application application);
}
