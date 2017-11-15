package com.hon.sunny.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.hon.sunny.R;
import com.hon.sunny.base.Constants;
import com.hon.sunny.city.SearchCityActivity;
import com.hon.sunny.common.util.CircularAnimUtil;
import com.hon.sunny.common.util.RxDrawer;
import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.common.util.ToastUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.rxbus.RxBus;
import com.hon.sunny.data.main.multicity.MultiCityRemoteDataSource;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.data.main.weather.WeatherRemoteDataSource;
import com.hon.sunny.data.main.weather.WeatherRepository;
import com.hon.sunny.main.multicity.MultiCityPresenter;
import com.hon.sunny.main.weather.WeatherFragment;
import com.hon.sunny.main.weather.WeatherPresent;
import com.hon.sunny.main.adapter.HomePagerAdapter;
import com.hon.sunny.component.rxbus.event.ChangeCityEvent;
import com.hon.sunny.component.rxbus.event.MultiUpdate;
import com.hon.sunny.main.multicity.MultiCityFragment;
import com.hon.sunny.service.AutoUpdateService;
import com.hon.sunny.setting.SettingActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */

public class MainActivity extends DaggerAppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Inject
    Lazy<WeatherFragment> weatherFragmentProvider;
    @Inject
    Lazy<MultiCityFragment> multiCityFragmentProvider;
//    @Inject
//    WeatherFragment mWeatherFragment;
//    @Inject
//    MultiCityFragment mMultiCityFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerRxBus();
        ButterKnife.bind(this);
        initView();
        Util.initIcons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, AutoUpdateService.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Util.initIcons();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(mFab,"exit the app",Snackbar.LENGTH_SHORT)
                    .setAction("yeah",(v)->finish())
                    .show();
        }
    }

    private void registerRxBus(){
        RxBus.getDefault().toObservable(ChangeCityEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
                changeCityEvent->
                {
                    mViewPager.setCurrentItem(0,true);
                }
        );
        RxBus.getDefault().toObservable(MultiUpdate.class).subscribe(
                multiUpdate -> {
                    mViewPager.setCurrentItem(1,true);
                }
        );
    }

    private void initView(){
        setSupportActionBar(mToolbar);

        WeatherFragment weatherFragment=weatherFragmentProvider.get();
        MultiCityFragment multiCityFragment=multiCityFragmentProvider.get();

        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.addTab(weatherFragment, "天气详情");
        mHomePagerAdapter.addTab(multiCityFragment, "多城市");
        mViewPager.setAdapter(mHomePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (!mFab.isShown()) {//  this is useful
                    mFab.show();
                }
                mFab.post(new Runnable() {
                    @Override
                    public void run() {
                        mFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
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
                                if(!mFab.isShown()){
                                    fab.show();
                                }
                            }
                        });
                    }
                });

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFab.setOnClickListener(v ->onFabClick(0));

        initDrawer();
    }

    private void onFabClick(int position){
        switch (position){
            case 0:
                ToastUtil.showShort("clicked a Like");
                break;
            case 1:
                if(Util.checkMultiCitiesCount()){
                    Snackbar.make(mFab,R.string.city_count,Snackbar.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(MainActivity.this, SearchCityActivity.class);
                    intent.putExtra(Constants.MULTI_CHECK, true);
                    CircularAnimUtil.startActivity(MainActivity.this, intent, mFab,
                            R.color.colorPrimary);
                }
                break;
            default:
                break;
        }
    }

    private void initDrawer(){
        if (mNavView != null) {
            mNavView.setNavigationItemSelectedListener(this);
            mNavView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                            R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        RxDrawer.close(mDrawerLayout).compose(RxUtils.rxSchedulerHelper(AndroidSchedulers.mainThread())).subscribe(
                new SimpleSubscriber<Void>() {
                    @Override
                    public void onNext(Void aVoid) {
                        switch (item.getItemId()) {
                            case R.id.nav_set:
                                launch(SettingActivity.class);
                                break;
//                            case R.id.nav_about:
//                                launch(AboutActivity.class);
//                                break;
                            case R.id.nav_city:
                                launch(SearchCityActivity.class);
                                break;
                            case R.id.nav_multi_cities:
                                mViewPager.setCurrentItem(1);
                                break;
                        }
                    }
                });
        return false;
    }

    private void launch(Class<? extends Activity> target){
        startActivity(new Intent(this,target));
    }
}
