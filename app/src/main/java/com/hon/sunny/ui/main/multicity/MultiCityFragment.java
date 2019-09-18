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
import com.hon.sunny.ui.common.MaterialScrollListener;
import com.hon.sunny.ui.common.SunnyUIModel;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.ui.main.adapter.MultiCityAdapter;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.RxUtils;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.ToastUtil;
import com.hon.sunny.vo.bean.main.CityORM;
import com.hon.sunny.vo.bean.main.Weather;
import com.hon.sunny.vo.event.ChangeCityEvent;
import com.hon.sunny.vo.event.MultiUpdateEvent;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
public class MultiCityFragment extends Fragment implements MultiCityContract.View {

    @BindView(R.id.rv_multi_city)
    RecyclerView recyclerView;
    @BindView(R.id.srl_multi_city)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.tv_city_empty)
    TextView emptyText;
    @BindView(R.id.iv_error)
    ImageView errorImageView;
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
                showDialog(city, position);
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

    }

    @Override
    public void doOnRequest() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void doOnTerminate() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(Throwable t) {
        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
                t.toString().contains("UnknownHostException") || t.toString().contains("Unsatisfiable")) {
            emptyText.setVisibility(View.GONE);
            errorImageView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        ToastUtil.showShort(t.getMessage());
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
        errorImageView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showContentView() {
        recyclerView.setVisibility(View.VISIBLE);
        errorImageView.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
    }

    // todo refactor 数据部分移到Presenter中
    private void showDialog(String city, int position) {

        new AlertDialog.Builder(getActivity()).setMessage("是否删除该城市?")
                .setPositiveButton("删除", (dialog, which) -> {
                    Disposable d=Observable.just(city)
                            .map(
                                    c -> {
                                        List<CityORM> cityList = OrmLite.getInstance().query(new QueryBuilder<>(CityORM.class).where("name=?", city));
                                        if (cityList.isEmpty()) {
                                            return DeleteCityUIModel.failure("删除失败");
                                        }
                                        int id = cityList.get(0).getId();
                                        OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("id=?", id));
                                        return DeleteCityUIModel.success(id);
                                    }
                            )
                            .onErrorReturn(t -> {
                                t.printStackTrace();
                                return DeleteCityUIModel.failure("删除失败");
                            })
                            .compose(RxUtils.rxSchedulerHelper())
                            .startWith(DeleteCityUIModel.inProgress())
                            .subscribe(
                                    model -> {
                                        deleteProgress.setVisibility(model.inProgress ? View.VISIBLE : View.GONE);
                                        if(!model.inProgress){
                                            if(model.success){
                                                Weather weather = mMultiCityAdapter.removeItem(position);
                                                if (mMultiCityAdapter.isEmpty()) {
                                                    showEmptyView();
                                                }
                                                Snackbar.make(getView(), "已经将" + city + "删掉了 Ծ‸ Ծ", Snackbar.LENGTH_LONG).setAction("撤销",
                                                        v -> {
                                                            OrmLite.getInstance().save(new CityORM(model.deleteCityId, city));
                                                            mMultiCityAdapter.insertItem(position, weather);
                                                            recyclerView.scrollToPosition(position);
                                                            showContentView();
                                                        }).show();
                                            }else {
                                                ToastUtil.showShort(model.errorMessage);
                                            }
                                        }
                                    },
                                    t -> {
                                        // do nothing
                                    }
                            );

                })
                .show();
    }
}
