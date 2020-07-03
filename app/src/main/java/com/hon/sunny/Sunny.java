package com.hon.sunny;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.hon.mylogger.MyCrashHandler;
import com.hon.mylogger.MyLogger;

import java.io.IOException;
import java.net.SocketException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */
public class Sunny extends Application {
    private static String sCacheDir;
    private static Context sAppContext;

    // TODO: 16/8/1 这里的夜间模式 UI 有些没有适配好 暂时放弃夜间模式
    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static String getAppCacheDir() {
        return sCacheDir;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();

        if (getApplicationContext().getExternalCacheDir() != null && existSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }

        MyLogger.setLoggable(BuildConfig.DEBUG);
        // init MyLogger file path if you want to write into disk
        MyLogger.initLogFilePath(getFilesDir().getPath());
        // init crash handler
        MyCrashHandler.init();

        //https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
        // 统一处理RxJava的uncaught exception
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if ((e instanceof SocketException) || (e instanceof IOException)) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a dispose call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            MyLogger.w("Undeliverable exception received, not sure what to do : %s", e.getMessage());
        });
    }

    private boolean existSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
