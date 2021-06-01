package com.hon.sunny.ui.base;

import androidx.appcompat.widget.Toolbar;

import com.hon.sunny.R;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

@SuppressWarnings("all")
public class ToolbarActivity extends BaseActivity {

    private Toolbar toolbar;

    protected void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    protected void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}

