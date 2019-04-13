package com.hon.sunny.ui.main.multicity;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hon.sunny.R;
import com.hon.sunny.ui.common.MaterialScorllListener;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.SimpleSubscriber;
import com.hon.sunny.utils.ToastUtil;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.component.rxbus.RxBus;
import com.hon.sunny.ui.main.adapter.MultiCityAdapter;
import com.hon.sunny.component.rxbus.event.ChangeCityEvent;
import com.hon.sunny.data.main.bean.CityORM;
import com.hon.sunny.component.rxbus.event.MultiUpdate;
import com.hon.sunny.data.main.bean.Weather;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityFragment extends RxFragment implements MultiCityContract.View{

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.empty)
    LinearLayout mLayout;
    @Bind(R.id.iv_erro)
    ImageView mIvError;

    private List<Weather> mWeathers;
    private MultiCityAdapter mMultiCityAdapter;
    private MultiCityContract.Presenter mMultiCityPresenter;
    private List<Subscription> mSubscriptionList=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRxBus();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_multicity,container,false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    /**
     * Fragment's onActivityCreated invoked after Activity's onCreate, which
     * ensure that mMultiCityPresenter has been instantiated.
     * (
     *  bug should be fixed. 2018/7/24
     *  crash :caused by java.lang.NullPointerException:
     *  Attempt to invoke interface method
     *  'void com.hon.sunny.ui.main.multicity.MultiCityContract$Presenter.start()
     * )
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        multiLoad();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterRxBus();
    }

    @Override
    public void setPresenter(MultiCityContract.Presenter presenter) {
        mMultiCityPresenter=presenter;
    }

    private void initView(){
        mWeathers = new ArrayList<>();
        mMultiCityAdapter = new MultiCityAdapter(mWeathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMultiCityAdapter);
        mRecyclerView.addOnScrollListener(new MaterialScorllListener((MainActivity) getActivity()));
        mMultiCityAdapter.setOnMultiCityClickListener(new MultiCityAdapter.OnMultiCityClickListener() {
            @Override
            public void onLongClick(String city) {
                showDialog(city);
            }

            @Override
            public void onClick(String city) {
                SharedPreferenceUtil.getInstance().setCityName(city);
                RxBus.getInstance().post(new ChangeCityEvent());
            }
        });

        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright
            );
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mRefreshLayout.postDelayed(()->multiLoad(), 1000);
                }
            });
        }
    }

    @Override
    public void doOnRequest() {
        mRefreshLayout.setRefreshing(true);
    }

    @Override
    public void doOnTerminate() {
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCompleted() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mMultiCityAdapter.notifyDataSetChanged();

        if (mMultiCityAdapter.isEmpty()) {
            mIvError.setVisibility(View.GONE);
            mLayout.setVisibility(View.VISIBLE);
        } else {
            mIvError.setVisibility(View.GONE);
            mLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(Throwable t) {
        if(mLayout!=null){
            if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
                    t.toString().contains("UnknownHostException")||t.toString().contains("Unsatisfiable")){
                mLayout.setVisibility(View.GONE);
                mIvError.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }
        RetrofitSingleton.disposeFailureInfo(t);
    }

    @Override
    public void onNext(Weather weather) {
        if(Constants.UNKNOWN_CITY.equals(weather.status)){
            ToastUtil.showLong("there's an unknown city...");
        }
        mWeathers.add(weather);
    }

    private void multiLoad() {
        mWeathers.clear();
        mMultiCityPresenter.start();
    }

    private void registerRxBus(){
        Subscription multiUpdateSubscription=
                RxBus.getInstance()
                        .toObservable(MultiUpdate.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SimpleSubscriber<MultiUpdate>() {
                            @Override
                            public void onNext(MultiUpdate multiUpdate) {
                                multiLoad();
                            }
                        });

        mSubscriptionList.add(multiUpdateSubscription);
    }

    private void unRegisterRxBus(){
        for(Subscription subscription:mSubscriptionList)
            subscription.unsubscribe();
    }

    private void showDialog(String city){
        new AlertDialog.Builder(getActivity()).setMessage("是否删除该城市?")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                        multiLoad();
                        Snackbar.make(getView(), "已经将" + city + "删掉了 Ծ‸ Ծ", Snackbar.LENGTH_LONG).setAction("撤销",
                                v -> {
                                    OrmLite.getInstance().save(new CityORM(city));
                                    multiLoad();
                                }).show();
                    }
                })
                .show();
    }
}
