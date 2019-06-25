package com.hon.sunny.ui.main.weather;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.network.RetrofitSingleton;
import com.hon.sunny.ui.common.MaterialScrollListener;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.ui.main.adapter.WeatherAdapter;
import com.hon.sunny.utils.CheckVersion;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.ToastUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.Weather;
import com.hon.sunny.vo.event.ChangeCityEvent;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

import static com.hon.sunny.utils.Constants.CHANGE_UPDATE_TIME;
import static com.hon.sunny.utils.Constants.ONE_HOUR;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */
@SuppressWarnings("all")
public class WeatherFragment extends Fragment implements WeatherContract.View, AMapLocationListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.swiprefresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.iv_erro)
    ImageView errorImageView;

    private WeatherContract.Presenter mWeatherPresenter;
    private WeatherAdapter mWeatherAdapter;

    private Disposable mPermissionsDisposable;

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        mWeatherPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RxPermissions rxPermissions = new RxPermissions(this);

        mPermissionsDisposable = rxPermissions
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPermissionsDisposable.dispose();
    }

    private void initView() {
        if (refreshLayout != null) {
            refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            refreshLayout.setOnRefreshListener(
                    () -> refreshLayout.postDelayed(this::loadWeather, 1000));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherAdapter = new WeatherAdapter();
        recyclerView.setAdapter(mWeatherAdapter);
        recyclerView.addOnScrollListener(new MaterialScrollListener((MainActivity) getActivity()));
    }

    private void loadWeather() {
        mWeatherPresenter.start();
    }

    @Override
    public void doOnRequest() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void doOnNext() {
        errorImageView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(Throwable e) {
        errorImageView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        SharedPreferenceUtil.getInstance().setCityName("北京");
        safeSetTitle("找不到城市");
        refreshLayout.setRefreshing(false);
        RetrofitSingleton.disposeFailureInfo(e);
    }

    @Override
    public void onNext(Weather weather) {
//        mWeather.aqi = weather.aqi;TODO
        mWeatherAdapter.setWeather(weather);
        safeSetTitle(weather.city);
        refreshLayout.setRefreshing(false);
        ToastUtil.showShort(getString(R.string.complete));
        //发通知
//        Util.normalStyleNotification(Constants.CHANNEL_ID_WEATHER,weather,getActivity(),MainActivity.class);
    }

    //  locate current cities by AMap
    private void location() {
        refreshLayout.setRefreshing(true);
        AMapLocationClient locationClient = new AMapLocationClient(Sunny.getAppContext());
        locationClient.setLocationListener(this);
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        locationClientOption.setNeedAddress(true);
        locationClientOption.setOnceLocation(false);
        locationClientOption.setWifiActiveScan(true);
        locationClientOption.setMockEnable(false);
        int tempTime = SharedPreferenceUtil.getInstance().getInt(CHANGE_UPDATE_TIME, 3);
        if (tempTime == 0) {
            tempTime = 100;
        }
        locationClientOption.setInterval(tempTime * ONE_HOUR);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
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
        } else {
            if (isAdded()) {
                ToastUtil.showShort(getString(R.string.errorLocation));
            }
        }
        refreshLayout.setRefreshing(false);
        loadWeather();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeCity(ChangeCityEvent event) {
        loadWeather();
    }

    private void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null && !TextUtils.isEmpty(title)) {
            appBarLayout.setTitle(title);
        }
    }

}
