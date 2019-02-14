package com.hon.sunny.common.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import com.hon.sunny.Sunny;

import static com.hon.sunny.common.Constants.ANIM_START;
import static com.hon.sunny.common.Constants.AUTO_UPDATE;
import static com.hon.sunny.common.Constants.CHANGE_ICONS;
import static com.hon.sunny.common.Constants.CITY_NAME;
import static com.hon.sunny.common.Constants.HOUR;
import static com.hon.sunny.common.Constants.NOTIFICATION_MODEL;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public class SharedPreferenceUtil {

    private SharedPreferences mPrefs;

    public static SharedPreferenceUtil getInstance() {
        return SPHolder.sInstance;
    }

    private static class SPHolder {
        private static final SharedPreferenceUtil sInstance = new SharedPreferenceUtil();
    }

    private SharedPreferenceUtil() {
        mPrefs = Sunny.getAppContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
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
        return mPrefs.getBoolean(key,false);
    }

    public boolean getBoolean(String key,boolean defValue) {
        return mPrefs.getBoolean(key,defValue);
    }

    //当前城市
    public void setCityName(String name) {
        mPrefs.edit().putString(CITY_NAME, name).apply();
    }

    public String getCityName() {
        return mPrefs.getString(CITY_NAME, "北京");
    }

    //  通知栏模式 默认为常驻
    public void setNotificationModel(int t) {
        mPrefs.edit().putInt(NOTIFICATION_MODEL, t).apply();
        if(Build.VERSION.SDK_INT>=23){
            NotificationManager manager= (NotificationManager) Sunny.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications=manager.getActiveNotifications();
            for(StatusBarNotification notification:notifications){
                notification.getNotification().flags=t;
                manager.notify(1,notification.getNotification());
            }
        }

    }

    public int getNotificationModel() {
        return mPrefs.getInt(NOTIFICATION_MODEL, Notification.FLAG_ONGOING_EVENT);
    }

    // 首页 Item 动画效果 默认关闭

    public void setMainAnim(boolean b) {
        mPrefs.edit().putBoolean(ANIM_START, b).apply();
    }

    public boolean getMainAnim() {
        return mPrefs.getBoolean(ANIM_START, false);
    }
}

