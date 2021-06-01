package com.hon.sunny.ui.main.multicity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.hon.sunny.R;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.network.RetrofitSingleton;
import com.hon.sunny.ui.base.BaseErrorViewFragment;
import com.hon.sunny.ui.common.MaterialScrollListener;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.ui.main.adapter.MultiCityAdapter;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.ToastUtil;
import com.hon.sunny.vo.bean.main.CityORM;
import com.hon.sunny.vo.bean.main.Weather;
import com.hon.sunny.vo.event.ChangeCityEvent;
import com.hon.sunny.vo.event.MultiUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@SuppressWarnings("all")
public class MultiCityFragment extends BaseErrorViewFragment implements MultiCityContract.View {

    @BindView(R.id.rv_multi_city)
    RecyclerView recyclerView;
    @BindView(R.id.srl_multi_city)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_city_empty)
    TextView emptyText;
//    @BindView(R.id.iv_error)
//    ImageView errorImageView;
    @BindView(R.id.pb_delete_city)
    ProgressBar deleteProgress;

    private List<Weather> mWeathers;
    private MultiCityAdapter mMultiCityAdapter;
    private MultiCityContract.Presenter mMultiCityPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multicity, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        multiLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setPresenter(MultiCityContract.Presenter presenter) {
        mMultiCityPresenter = presenter;
    }

    private void initView() {
        mMultiCityAdapter = new MultiCityAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mMultiCityAdapter);
        recyclerView.addOnScrollListener(new MaterialScrollListener((MainActivity) getActivity()));
        mMultiCityAdapter.setOnMultiCityClickListener(new MultiCityAdapter.OnMultiCityClickListener() {
            @Override
            public void onLongClick(String city, int position) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.main_delete_city_hint)
                        .setPositiveButton(R.string.main_delete_city_confirm, (dialog, which) -> mMultiCityPresenter.deleteCity(city, position))
                        .show();
            }

            @Override
            public void onClick(String city) {
                SharedPreferenceUtil.getInstance().setCityName(city);
                EventBus.getDefault().post(new ChangeCityEvent());
            }
        });

        refreshLayout.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright
        );
        refreshLayout.setOnRefreshListener(this::multiLoad);
        bindErrorView(v -> multiLoad());
    }

    @Override
    public void doOnRequest() {
        refreshLayout.setVisibility(View.VISIBLE);
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void doOnTerminate() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(Throwable t) {
        emptyText.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        refreshLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort("Error: " + t.getMessage());
        RetrofitSingleton.disposeFailureInfo(t);
    }

    @Override
    public void onEmpty() {
        mMultiCityAdapter.submitList(mWeathers);
        showEmptyView();
    }

    @Override
    public void onNext(Weather weather) {
        if (Constants.UNKNOWN_CITY.equals(weather.status)) {
            ToastUtil.showLong("there's an unknown city...");
        }
        mWeathers.add(0, weather);
    }

    @Override
    public void onCompleted() {
        mMultiCityAdapter.submitList(mWeathers);
        showContentView();
    }

    @Override
    public void onAdded(Weather weather) {
        mMultiCityAdapter.insertItem(0, weather);
        recyclerView.scrollToPosition(0);
        showContentView();
    }

    @Override
    public void onDeleteInProgress(boolean inProgress) {
        deleteProgress.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDeleteSuccess(String city, int position, int deleteCityId) {
        Weather weather = mMultiCityAdapter.removeItem(position);
        if (mMultiCityAdapter.isEmpty()) {
            showEmptyView();
        }
        Snackbar.make(getView(), getString(R.string.main_delete_city_message, city), Snackbar.LENGTH_LONG)
                .setAction(R.string.main_delete_city_undo, v -> {
                    OrmLite.getInstance().save(new CityORM(deleteCityId, city));
                    mMultiCityAdapter.insertItem(position, weather);
                    recyclerView.scrollToPosition(position);
                    showContentView();
                })
                .show();
    }

    @Override
    public void onDeleteError(String errorMessage) {
        ToastUtil.showShort(errorMessage);
    }

    private void multiLoad() {
        mWeathers = new ArrayList<>();
        mMultiCityPresenter.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void multiUpdate(MultiUpdateEvent event) {
        mMultiCityPresenter.loadAddedCityWeather(event.getAddedCity());
    }

    private void showEmptyView() {
        emptyText.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showContentView() {
        recyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
    }
}
