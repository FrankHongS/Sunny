package com.hon.sunny.about.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseActivity;
import com.hon.sunny.common.util.CheckVersion;
import com.hon.sunny.common.util.StatusBarUtil;
import com.hon.sunny.common.util.Util;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class AboutActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @Bind(R.id.tv_version)
    TextView mTvVersion;

    @Bind(R.id.bt_code)
    Button mBtCode;
    @Bind(R.id.bt_blog)
    Button mBtBlog;
    @Bind(R.id.bt_pay)
    Button mBtPay;
    @Bind(R.id.bt_share)
    Button mBtShare;
    @Bind(R.id.bt_update)
    Button mBtUpdate;
    @Bind(R.id.bt_bug)
    Button mBtBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        StatusBarUtil.setImmersiveStatusBar(this);
        StatusBarUtil.setImmersiveStatusBarToolbar(mToolbar,this);
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

    @OnClick({ R.id.bt_code, R.id.bt_blog, R.id.bt_pay, R.id.bt_share, R.id.bt_bug, R.id.bt_update })
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

    public static void launch(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }
}
