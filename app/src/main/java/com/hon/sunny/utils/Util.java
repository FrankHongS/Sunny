package com.hon.sunny.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hon.mylogger.MyLogger;
import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.ui.about.domain.VersionBean;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.vo.bean.main.CityORM;
import com.hon.sunny.vo.bean.main.Weather;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.hon.sunny.utils.Constants.CHANGE_ICONS;
import static com.hon.sunny.utils.Constants.CITY_COUNT;
import static com.hon.sunny.utils.Constants.INIT_ICONS;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public class Util {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */

    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.can_not_find_version_name);
        }
    }

    /**
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 只关注是否联网
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 修要判断的时间
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */
    public static String dayForWeek(String pTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(pTime));
        int dayForWeek;
        String week = "";
        dayForWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayForWeek) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

    /**
     * 安全的 String 返回
     *
     * @param prefix 默认字段
     * @param obj    拼接字段 (需检查)
     */
    public static String safeText(String prefix, String obj) {
        if (TextUtils.isEmpty(obj)) return "";
        return TextUtils.concat(prefix, obj).toString();
    }

    public static String safeText(String msg) {
        if (null == msg) {
            return "~";
        }
        return safeText("", msg);
    }

    /**
     * 天气代码 100 为晴 101-213 500-901 为阴 300-406为雨
     *
     * @param code 天气代码
     * @return 天气情况
     */
    public static String getWeatherType(int code) {
        if (code == 100) {
            return "晴";
        }
        if ((code >= 101 && code <= 213) || (code >= 500 && code <= 901)) {
            return "阴";
        }
        if (code >= 300 && code <= 406) {
            return "雨";
        }
        return "错误";
    }

    /**
     * 匹配掉错误信息
     */
    public static String replaceCity(String city) {
        city = safeText(city).replaceAll("(?:省|市|自治区|自治县|特别行政区|地区|盟|区|县)", "");
        return city;
    }


    /**
     * 匹配掉无关信息
     */

    public static String replaceInfo(String city) {
        city = safeText(city).replace("API没有", "");
        return city;
    }

    /**
     * Java 中有一个 Closeable 接口,标识了一个可关闭的对象,它只有一个 close 方法.
     */
    public static void closeQuietly(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取顶部status bar 高度
     */
    public static int getStatusBarHeight(Activity mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        MyLogger.i("Status height:" + height);
        return height;
    }

    /**
     * 获取底部 navigation bar 高度
     */
    public static int getNavigationBarHeight(Activity mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        MyLogger.i("Navi height:" + height);
        return height;
    }

    /**
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }

    public static void copyToClipboard(String info, Context context) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("msg", info);
        manager.setPrimaryClip(clipData);
        ToastUtil.showShort(String.format("[%s] 已经复制到剪切板啦( •̀ .̫ •́ )✧", info));
    }

    //parse json by Gson
    public static VersionBean parseJsonByGson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json,
                new TypeToken<VersionBean>() {
                }.getType());
    }

    // check if cities count is more than 3
    public static boolean checkMultiCitiesCount() {
        int count = (int) OrmLite.getInstance().queryCount(CityORM.class);

        return count >= CITY_COUNT;
    }

    // check if selected city exists
    public static boolean checkIfCityExists(String city) {
        List<CityORM> cities = OrmLite.getInstance().query(CityORM.class);
        for (CityORM cityOrm : cities) {
            if (city != null && city.equals(cityOrm.getName())) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void createNotificationChannel(String channelId,
                                                 String channelName,
                                                 int importance) {
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, importance);
        NotificationManager notificationManager =
                (NotificationManager) Sunny.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

    }

    public static void normalStyleNotification(String channelId, Weather weather, Context context, Class<? extends Activity> target) {
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();
        Intent intent = new Intent(context, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        Notification notification = builder
                .setContentIntent(pendingIntent)
                // 这里部分 ROM 无法成功
                .setSmallIcon(R.mipmap.ic_launch_logo)
                .setLargeIcon(BitmapFactory.decodeResource(Sunny.getAppContext().getResources(), sharedPreferenceUtil.getInt(weather.now.txt, R.mipmap.none)))
                .setContentTitle(weather.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.txt, weather.now.tmp))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .build();
        notification.flags = sharedPreferenceUtil.getNotificationModel();
        notification.defaults |= Notification.DEFAULT_ALL;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }

    public static void initIcons(boolean reset) {

        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance();

        if (!reset) {
            if (sharedPreferenceUtil.getBoolean(INIT_ICONS)) {
                return;
            } else {
                sharedPreferenceUtil.putBoolean(INIT_ICONS, true);
            }
        }

        if (sharedPreferenceUtil.getInt(CHANGE_ICONS, 0) == 0) {
            sharedPreferenceUtil.putInt("未知", R.mipmap.none);
            sharedPreferenceUtil.putInt("晴", R.mipmap.type_one_sunny);
            sharedPreferenceUtil.putInt("阴", R.mipmap.type_one_cloudy);
            sharedPreferenceUtil.putInt("多云", R.mipmap.type_one_cloudy);
            sharedPreferenceUtil.putInt("少云", R.mipmap.type_one_cloudy);
            sharedPreferenceUtil.putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            sharedPreferenceUtil.putInt("小雨", R.mipmap.type_one_light_rain);
            sharedPreferenceUtil.putInt("中雨", R.mipmap.type_one_light_rain);
            sharedPreferenceUtil.putInt("大雨", R.mipmap.type_one_heavy_rain);
            sharedPreferenceUtil.putInt("阵雨", R.mipmap.type_one_thunderstorm);
            sharedPreferenceUtil.putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            sharedPreferenceUtil.putInt("霾", R.mipmap.type_one_fog);
            sharedPreferenceUtil.putInt("雾", R.mipmap.type_one_fog);
        } else {
            sharedPreferenceUtil.putInt("未知", R.mipmap.none);
            sharedPreferenceUtil.putInt("晴", R.mipmap.type_two_sunny);
            sharedPreferenceUtil.putInt("阴", R.mipmap.type_two_cloudy);
            sharedPreferenceUtil.putInt("多云", R.mipmap.type_two_cloudy);
            sharedPreferenceUtil.putInt("少云", R.mipmap.type_two_cloudy);
            sharedPreferenceUtil.putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            sharedPreferenceUtil.putInt("小雨", R.mipmap.type_two_light_rain);
            sharedPreferenceUtil.putInt("中雨", R.mipmap.type_two_rain);
            sharedPreferenceUtil.putInt("大雨", R.mipmap.type_two_rain);
            sharedPreferenceUtil.putInt("阵雨", R.mipmap.type_two_rain);
            sharedPreferenceUtil.putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            sharedPreferenceUtil.putInt("霾", R.mipmap.type_two_haze);
            sharedPreferenceUtil.putInt("雾", R.mipmap.type_two_fog);
            sharedPreferenceUtil.putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningService = myManager
                .getRunningServices(200);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
}
