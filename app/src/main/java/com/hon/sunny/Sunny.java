package com.hon.sunny;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 *
 * project architecture:https://github.com/googlesamples/android-architecture/tree/todo-mvp/
 */

public class Sunny extends Application{
    private static String sCacheDir;
    private static Context sAppContext;

    // TODO: 16/8/1 这里的夜间模式 UI 有些没有适配好 暂时放弃夜间模式
    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext=getApplicationContext();

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppCacheDir() {
        return sCacheDir;
    }
}
