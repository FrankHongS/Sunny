package com.hon.sunny.utils;

import android.os.Build;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class SdkUtil {

    public static boolean sdkVersionGe(int version) {
        return Build.VERSION.SDK_INT >= version;
    }

    public static boolean sdkVersionEq(int version) {
        return Build.VERSION.SDK_INT == version;
    }

    public static boolean sdkVersionLt(int version) {
        return Build.VERSION.SDK_INT < version;
    }

    public static boolean sdkVersionGe19() {
        return sdkVersionGe(19);
    }

    public static boolean sdkVersionGe21() {
        return sdkVersionGe(21);
    }
}
