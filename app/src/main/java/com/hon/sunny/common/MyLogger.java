package com.hon.sunny.common;

import android.util.Log;

import com.hon.sunny.BuildConfig;

/**
 * Created by Frank_Hon on 2/15/2019.
 * E-mail: v-shhong@microsoft.com
 */
public final class MyLogger {

    private static final boolean DEBUG= BuildConfig.DEBUG;

    public static void i(String tag,String message){
        Log.i(tag, message);
    }

    public static void i(Class<?> clz,String message){
        Log.i(clz.getSimpleName(), message);
    }

    public static void d(String tag,String message){
        if(DEBUG){
            Log.d(tag, message);
        }
    }

    public static void d(Class<?> clz,String message){
        if(DEBUG){
            Log.d(clz.getSimpleName(), message);
        }
    }

    public static void d(String message){
        if(DEBUG){
            Log.d(MyLogger.class.getSimpleName(), message);
        }
    }

    public static void w(String tag,String message){
        if(DEBUG){
            Log.w(tag, message);
        }
    }

    public static void w(Class<?> clz,String message){
        if(DEBUG){
            Log.w(clz.getSimpleName(), message);
        }
    }

    public static void e(String tag,String message){
        if(DEBUG){
            Log.e(tag, message);
        }
    }

    public static void e(Class<?> clz,String message){
        if(DEBUG){
            Log.e(clz.getSimpleName(), message);
        }
    }
}
