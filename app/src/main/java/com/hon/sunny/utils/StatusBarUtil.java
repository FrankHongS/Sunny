package com.hon.sunny.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class StatusBarUtil {

    public static void setImmersiveStatusBar(@NonNull Activity activity) {
        if (SdkUtil.sdkVersionGe21()) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (SdkUtil.sdkVersionEq(19)) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setImmersiveStatusBarToolbar(@NonNull Toolbar toolbar, Context context) {
        ViewGroup.MarginLayoutParams toolLayoutParams = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
        toolLayoutParams.height = EnvUtil.getStatusBarHeight() + EnvUtil.getActionBarSize(context);
        toolbar.setLayoutParams(toolLayoutParams);
        toolbar.setPadding(0, EnvUtil.getStatusBarHeight(), 0, 0);
        toolbar.requestLayout();
    }
}