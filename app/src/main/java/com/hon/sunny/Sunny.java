package com.hon.sunny;

import android.app.Application;
import android.content.Context;

import com.hon.sunny.utils.CrashHandler;
import com.hon.sunny.utils.PLog;

import java.io.IOException;
import java.net.SocketException;

import androidx.appcompat.app.AppCompatDelegate;
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

    public static String getAppFileDir() {
        return sAppContext.getFilesDir().toString();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();

        CrashHandler.init(new CrashHandler(this));

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }

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
            PLog.w("Undeliverable exception received, not sure what to do", e.getMessage());
        });
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
}
