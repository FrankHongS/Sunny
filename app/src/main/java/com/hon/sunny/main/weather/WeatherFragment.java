package com.hon.sunny.main.weather;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.common.Constants;
import com.hon.sunny.common.util.CheckVersion;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.common.util.ToastUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.component.rxbus.RxBus;
import com.hon.sunny.main.MainActivity;
import com.hon.sunny.main.adapter.WeatherAdapter;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.component.rxbus.event.ChangeCityEvent;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.hon.sunny.common.Constants.CHANGE_UPDATE_TIME;
import static com.hon.sunny.common.Constants.ONE_HOUR;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherFragment extends RxFragment implements WeatherContract.View,AMapLocationListener{

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.iv_erro)
    ImageView mIvError;

    private WeatherContract.Presenter mWeatherPresenter;
    private WeatherAdapter mWeatherAdapter;
    private final Weather mWeather=new Weather();
    private List<Subscription> mSubscriptionList=new ArrayList<>();
    //  locate current cities by AMap
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRxBus();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.content_main,container,false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RxPermissions.getInstance(getActivity()).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        location();
                    } else {
                        loadWeather();
                    }
                });
        CheckVersion.checkVersionByPgy(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient = null;
        mLocationOption = null;

        unRegisterRxBus();
    }

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        mWeatherPresenter=presenter;
    }

    private void initView(){
        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mRefreshLayout.setOnRefreshListener(
                    () -> mRefreshLayout.postDelayed(this::loadWeather, 1000));
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherAdapter = new WeatherAdapter(mWeather);
        mRecyclerView.setAdapter(mWeatherAdapter);
        safeSetTitle(mWeather.city);
    }

    private void loadWeather(){
        mWeatherPresenter.start();
    }

    @Override
    public void doOnRequest() {
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void doOnNext() {
        mIvError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(Throwable e) {
        mIvError.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        SharedPreferenceUtil.getInstance().setCityName("北京");
        safeSetTitle("找不到城市");
        mRefreshLayout.setRefreshing(false);
        RetrofitSingleton.disposeFailureInfo(e);
    }

    @Override
    public void onNext(Weather weather) {
        mWeather.status = weather.status;
//        mWeather.aqi = weather.aqi;TODO
        mWeather.basic = weather.basic;
        mWeather.lifestyle = weather.lifestyle;
        mWeather.now = weather.now;
        mWeather.dailyForecast = weather.dailyForecast;
        mWeather.hourlyForecast = weather.hourlyForecast;

        safeSetTitle(weather.city);
        mRefreshLayout.setRefreshing(false);
        ToastUtil.showShort(getString(R.string.complete));
        mWeatherAdapter.notifyDataSetChanged();
        //发通知
        Util.normalStyleNotification(Constants.CHANNEL_ID_WEATHER,weather,getActivity(),MainActivity.class);
    }

    private void location(){
        mRefreshLayout.setRefreshing(true);
        mLocationClient = new AMapLocationClient(Sunny.getAppContext());
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(false);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        int tempTime = SharedPreferenceUtil.getInstance().getInt(CHANGE_UPDATE_TIME,3);
        if (tempTime == 0) {
            tempTime = 100;
        }
        mLocationOption.setInterval(tempTime * ONE_HOUR);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                ToastUtil.showShort("located successfully");
                SharedPreferenceUtil.getInstance().setCityName(Util.replaceCity(aMapLocation.getCity()));
            } else {
                if (isAdded()) {
                    ToastUtil.showShort(getString(R.string.errorLocation));
                }
            }
        }else {
            if (isAdded()) {
                ToastUtil.showShort(getString(R.string.errorLocation));
            }
        }
        mRefreshLayout.setRefreshing(false);
        loadWeather();
    }

    private void registerRxBus(){
        Subscription changeCitySubscription=RxBus.getInstance().toObservable(ChangeCityEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new SimpleSubscriber<ChangeCityEvent>() {
            @Override
            public void onNext(ChangeCityEvent changeCityEvent) {
                loadWeather();
            }
        });

        mSubscriptionList.add(changeCitySubscription);
    }

    private void unRegisterRxBus(){
        for(Subscription subscription:mSubscriptionList)
            subscription.unsubscribe();
    }

    private void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null&& !TextUtils.isEmpty(title)) {
            appBarLayout.setTitle(title);
        }
    }

}
