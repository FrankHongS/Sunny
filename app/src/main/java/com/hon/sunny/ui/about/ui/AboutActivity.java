package com.hon.sunny.ui.about.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseActivity;
import com.hon.sunny.utils.CheckVersion;
import com.hon.sunny.utils.StatusBarUtil;
import com.hon.sunny.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class AboutActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.tv_version)
    TextView mTvVersion;

    @BindView(R.id.bt_code)
    Button mBtCode;
    @BindView(R.id.bt_blog)
    Button mBtBlog;
    @BindView(R.id.bt_pay)
    Button mBtPay;
    @BindView(R.id.bt_share)
    Button mBtShare;
    @BindView(R.id.bt_update)
    Button mBtUpdate;
    @BindView(R.id.bt_bug)
    Button mBtBug;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        StatusBarUtil.setImmersiveStatusBar(this);
        StatusBarUtil.setImmersiveStatusBarToolbar(mToolbar, this);
        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        mTvVersion.setText(String.format("当前版本: %s (Build %s)", Util.getVersion(this), Util.getVersionCode(this)));
        mToolbarLayout.setTitleEnabled(false);
        // TODO: 2016/12/4 这里有个 bug
        //mToolbarLayout.setTitle(getString(R.string.app_name));
        mToolbar.setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @OnClick({R.id.bt_code, R.id.bt_blog, R.id.bt_pay, R.id.bt_share, R.id.bt_bug, R.id.bt_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_code:
                goToHtml(getString(R.string.app_html));
                break;
            case R.id.bt_blog:
                goToHtml("http://imxie.cc");
                break;
            case R.id.bt_pay:
                Util.copyToClipboard(getString(R.string.alipay), this);
                break;
            case R.id.bt_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)));
                break;
            case R.id.bt_bug:
                goToHtml(getString(R.string.bugTableUrl));
                break;
            case R.id.bt_update:
                CheckVersion.checkVersion(this, true);
                break;
        }
    }

    private void goToHtml(String url) {
        Uri uri = Uri.parse(url);   //指定网址
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);           //指定Action
        intent.setData(uri);                            //设置Uri
        startActivity(intent);        //启动Activity
    }
}
