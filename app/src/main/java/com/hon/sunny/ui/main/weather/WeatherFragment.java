package com.hon.sunny.ui.main.weather;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hon.mylogger.MyLogger;
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

    @BindView(R.id.rv_weather)
    RecyclerView recyclerView;
    @BindView(R.id.srl_weather)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.iv_error)
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
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RxPermissions rxPermissions = new RxPermissions(this);

        mPermissionsDisposable = rxPermissions
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted && savedInstanceState == null) {
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
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(this::loadWeather);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWeatherAdapter = new WeatherAdapter();
        recyclerView.setAdapter(mWeatherAdapter);
        recyclerView.addOnScrollListener(new MaterialScrollListener((MainActivity) getActivity()));
        recyclerView.setItemAnimator(new RecyclerView.ItemAnimator() {
            @Override
            public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
                Animation animation = AnimationUtils.loadAnimation(getContext(),
                        R.anim.slide_in_left);
                viewHolder.itemView.startAnimation(animation);
                return false;
            }

            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
//                viewHolder.itemView.postDelayed(() -> {
                    Animation animation = AnimationUtils.loadAnimation(getContext(),
                            R.anim.slide_in_left);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                        view.setAlpha(1);
//                    }
//
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                    }
//
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
                    viewHolder.itemView.startAnimation(animation);
//                }, 138 * viewHolder.getLayoutPosition());
//                mLastPosition = position;
                return false;
            }

            @Override
            public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                Animation animation = AnimationUtils.loadAnimation(getContext(),
                        R.anim.slide_in_left);
                viewHolder.itemView.startAnimation(animation);
                return false;
            }

            @Override
            public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {

                return false;
            }

            @Override
            public void runPendingAnimations() {

            }

            @Override
            public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

            }

            @Override
            public void endAnimations() {

            }

            @Override
            public boolean isRunning() {
                return false;
            }
        });
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
        safeSetTitle("找不到城市");
        refreshLayout.setRefreshing(false);
        RetrofitSingleton.disposeFailureInfo(e);
    }

    @Override
    public void onNext(Weather weather) {
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

    // 设置sticky的原因是防止系统回收Fragment，导致收不到消息
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
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
