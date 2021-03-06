package com.hon.sunny.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import com.hon.sunny.Sunny;

import static com.hon.sunny.utils.Constants.ANIM_START;
import static com.hon.sunny.utils.Constants.CITY_NAME;
import static com.hon.sunny.utils.Constants.NOTIFICATION_MODEL;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public class SharedPreferenceUtil {

    private SharedPreferences mPrefs;

    private SharedPreferenceUtil() {
        mPrefs = Sunny.getAppContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public static SharedPreferenceUtil getInstance() {
        return SPHolder.sInstance;
    }

    public void putInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    public void putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return mPrefs.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPrefs.getBoolean(key, defValue);
    }

    public String getCityName() {
        return mPrefs.getString(CITY_NAME, "北京");
    }

    //当前城市
    public void setCityName(String name) {
        mPrefs.edit().putString(CITY_NAME, name).apply();
    }

    private static class SPHolder {
        private static final SharedPreferenceUtil sInstance = new SharedPreferenceUtil();
    }
}

