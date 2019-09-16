package com.hon.sunny.ui.setting;

import android.os.Bundle;

import com.hon.sunny.R;
import com.hon.sunny.base.ToolbarActivity;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class SettingActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initToolbar();
        setToolbarTitle(getResources().getString(R.string.setting));

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout, new SettingFragment()).commit();
    }

}
