package com.hon.sunny.modules.main.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;

import com.hon.sunny.R;
import com.hon.sunny.base.BaseActivity;
import com.hon.sunny.base.Constants;
import com.hon.sunny.common.PLog;
import com.hon.sunny.common.util.CircularAnimUtil;
import com.hon.sunny.common.util.RxDrawer;
import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.common.util.ToastUtil;
import com.hon.sunny.modules.city.ui.ChoiceCityActivity;
import com.hon.sunny.modules.city.ui.SearchCityActivity;
import com.hon.sunny.modules.main.adapter.HomePagerAdapter;
import com.hon.sunny.modules.service.AutoUpdateService;
import com.hon.sunny.modules.setting.ui.SettingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

import static com.hon.sunny.R.id.fab;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(fab)
    FloatingActionButton mFab;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        PLog.i("onCreate");
        initView();
        initDrawer();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        PLog.i("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PLog.i("onRestart");
        //为了实现 Intent 重启使图标生效
        initIcon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PLog.i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        PLog.i("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PLog.i("onStop");
    }

    /**
     * 初始化基础View
     */
    private void initView() {
        setSupportActionBar(mToolbar);
        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.addTab(new MainFragment(), "主页面");
        mHomePagerAdapter.addTab(new MultiCityFragment(), "多城市");
        mViewPager.setAdapter(mHomePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                mFab.post(() -> mFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        if (position == 1) {
                            mFab.setImageResource(R.drawable.ic_add_24dp);
                            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                            mFab.setOnClickListener(v -> onFabClick(1));
                        } else if(position == 0){
                            mFab.setImageResource(R.drawable.ic_favorite);
                            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                            mFab.setOnClickListener(v -> onFabClick(0));
                        }
                        fab.show();
                    }
                }));
                if (!mFab.isShown()) {
                    mFab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFab.setOnClickListener(v ->onFabClick(0));
    }

    private void onFabClick(int position){
        switch (position){
            case 0:
                ToastUtil.showShort("clicked a Like");
                break;
            case 1:
                Intent intent = new Intent(MainActivity.this, SearchCityActivity.class);
                intent.putExtra(Constants.MULTI_CHECK, true);
                CircularAnimUtil.startActivity(MainActivity.this, intent, mFab,
                        R.color.colorPrimary);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        //https://segmentfault.com/a/1190000004151222
        if (mNavView != null) {
            mNavView.setNavigationItemSelectedListener(this);
            //navigationView.setItemIconTintList(null);
            mNavView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                            R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 初始化Icon
     */
    private void initIcon() {
        if (SharedPreferenceUtil.getInstance().getIconType() == 0) {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_one_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_one_heavy_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_one_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_one_fog);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_one_fog);
        } else {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_two_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_two_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_two_haze);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_two_fog);
            SharedPreferenceUtil.getInstance().putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    private void showFabDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle("点赞")
                .setMessage("去项目地址给作者个Star，鼓励下作者୧(๑•̀⌄•́๑)૭✧")
                .setPositiveButton("好嘞", (dialog, which) -> {
                    Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    MainActivity.this.startActivity(intent);        //启动Activity
                })
                .show();
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        RxDrawer.close(mDrawerLayout).compose(RxUtils.rxSchedulerHelper(AndroidSchedulers.mainThread())).subscribe(
                new SimpleSubscriber<Void>() {
                    @Override
                    public void onNext(Void aVoid) {
                        switch (item.getItemId()) {
                            case R.id.nav_set:
                                SettingActivity.launch(MainActivity.this);
                                break;
//                            case R.id.nav_about:
//                                AboutActivity.launch(MainActivity.this);
//                                break;
                            case R.id.nav_city:
                                SearchCityActivity.launch(MainActivity.this);
                                break;
                            case R.id.nav_multi_cities:
                                mViewPager.setCurrentItem(1);
                                break;
                        }
                    }
                });
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
//            if (!DoubleClickExit.check()) {
//                ToastUtil.showShort(getString(R.string.double_exit));
//            } else {
//                finish();
//            }
            Snackbar.make(mDrawerLayout,"exit the app",Snackbar.LENGTH_SHORT)
                    .setAction("yeah",(v)->finish())
                    .show();
        }
    }

    protected boolean mIsHidden = false;

    public void hideOrShowToolbar() {
        mTabLayout.animate()
                .translationY(mIsHidden ? 0 : -mTabLayout.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mIsHidden = !mIsHidden;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //OrmLite.getInstance().close();
    }
}
