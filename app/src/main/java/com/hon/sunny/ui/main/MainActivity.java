package com.hon.sunny.ui.main;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hon.sunny.R;
import com.hon.sunny.data.main.multicity.MultiCityRemoteDataSource;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.data.main.weather.WeatherRemoteDataSource;
import com.hon.sunny.data.main.weather.WeatherRepository;
import com.hon.sunny.ui.city.SearchCityActivity;
import com.hon.sunny.ui.main.adapter.HomePagerAdapter;
import com.hon.sunny.ui.main.multicity.MultiCityContract;
import com.hon.sunny.ui.main.multicity.MultiCityFragment;
import com.hon.sunny.ui.main.multicity.MultiCityPresenter;
import com.hon.sunny.ui.main.weather.WeatherContract;
import com.hon.sunny.ui.main.weather.WeatherFragment;
import com.hon.sunny.ui.main.weather.WeatherPresent;
import com.hon.sunny.ui.setting.SettingActivity;
import com.hon.sunny.utils.CircularAnimUtil;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.ToastUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.event.ChangeCityEvent;
import com.hon.sunny.vo.event.MultiUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String KEY_MULTI_CITY = "MULTI_CITY";
    private static final String KEY_WEATHER = "WEATHER";
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private Fragment mMultiCityFragment;
    private Fragment mWeatherFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView(savedInstanceState);
        Util.initIcons(false);
        createNotificationChannel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, KEY_MULTI_CITY, mMultiCityFragment);
        getSupportFragmentManager().putFragment(outState, KEY_WEATHER, mWeatherFragment);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(fab, "exit the app", Snackbar.LENGTH_SHORT)
                    .setAction("yeah", v -> finish())
                    .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeCity(ChangeCityEvent event) {
        viewPager.setCurrentItem(0, true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void multiUpdate(MultiUpdateEvent event) {
        viewPager.setCurrentItem(1, true);
    }

    private void initView(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            mWeatherFragment = new WeatherFragment();
            mMultiCityFragment = new MultiCityFragment();
        } else {
            mWeatherFragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_WEATHER);
            mMultiCityFragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_MULTI_CITY);
        }

        new WeatherPresent(getLifecycle(), WeatherRepository.getInstance(WeatherRemoteDataSource.getInstance()), (WeatherContract.View) mWeatherFragment);
        new MultiCityPresenter(getLifecycle(), MultiCityRepository.getInstance(MultiCityRemoteDataSource.getInstance()), (MultiCityContract.View) mMultiCityFragment);

        HomePagerAdapter homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        homePagerAdapter.addTab(mWeatherFragment, getResources().getString(R.string.weather_fragment));
        homePagerAdapter.addTab(mMultiCityFragment, getResources().getString(R.string.multi_city_fragment));
        viewPager.setAdapter(homePagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (!fab.isShown()) {//  this is useful
                    fab.show();
                }
                fab.post(new Runnable() {
                    @Override
                    public void run() {
                        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                            @Override
                            public void onHidden(FloatingActionButton fab) {
                                if (position == 1) {
                                    fab.setImageResource(R.drawable.ic_add_24dp);
                                    fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                                    fab.setOnClickListener(v -> onFabClick(1));
                                } else if (position == 0) {
                                    fab.setImageResource(R.drawable.ic_favorite);
                                    fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                                    fab.setOnClickListener(v -> onFabClick(0));
                                }
                                if (!fab.isShown()) {
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

        fab.setOnClickListener(v -> onFabClick(0));

        initDrawer();
    }

    private void onFabClick(int position) {
        switch (position) {
            case 0:
                ToastUtil.showShort("clicked a Like");
                break;
            case 1:
                if (Util.checkMultiCitiesCount()) {
                    Snackbar.make(fab, R.string.city_count, Snackbar.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, SearchCityActivity.class);
                    intent.putExtra(Constants.MULTI_CHECK, true);
                    CircularAnimUtil.startActivity(MainActivity.this, intent, fab,
                            R.color.colorPrimary);
                }
                break;
            default:
                break;
        }
    }

    private void initDrawer() {
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle =
                    new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                            R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.nav_set:
                launch(SettingActivity.class);
                break;
//            case R.id.nav_about:
//                launch(AboutActivity.class);
//                break;
            case R.id.nav_city:
                launch(SearchCityActivity.class);
                break;
            case R.id.nav_multi_cities:
                viewPager.setCurrentItem(1);
                break;
        }

        return false;
    }

    private void launch(Class<? extends Activity> target) {
        startActivity(new Intent(this, target));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Util.createNotificationChannel(Constants.CHANNEL_ID_WEATHER, Constants.CHANNEL_NAME_WEATHER,
                    NotificationManager.IMPORTANCE_LOW);
        }
    }

    public void displayFabMaterial(boolean isScrolledDown) {
        if (isScrolledDown && !fab.isShown()) {
            fab.show();
        } else if (!isScrolledDown && fab.isShown()) {
            fab.hide();
        }
    }
}
