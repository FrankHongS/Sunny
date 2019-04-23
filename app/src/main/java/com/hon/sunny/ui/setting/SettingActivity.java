package com.hon.sunny.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hon.sunny.R;
import com.hon.sunny.base.ToolbarActivity;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class SettingActivity extends ToolbarActivity {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mToolbar.setTitle("设置"); don't work and i find this
        //http://stackoverflow.com/questions/26486730/in-android-app-toolbar-settitle-method-has-no-effect-application-name-is
        // -shown
        getToolbar().setTitle("设置");
        getFragmentManager().beginTransaction().replace(R.id.framelayout, new SettingFragment()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void beforeSetContent() {
        super.beforeSetContent();
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}
