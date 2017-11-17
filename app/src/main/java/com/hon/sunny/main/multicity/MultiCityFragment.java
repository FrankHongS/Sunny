package com.hon.sunny.main.multicity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hon.sunny.R;
import com.hon.sunny.base.Constants;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.common.util.ToastUtil;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.component.rxbus.RxBus;
import com.hon.sunny.di.ActivityScoped;
import com.hon.sunny.main.adapter.MultiCityAdapter;
import com.hon.sunny.component.rxbus.event.ChangeCityEvent;
import com.hon.sunny.data.main.bean.CityORM;
import com.hon.sunny.component.rxbus.event.MultiUpdate;
import com.hon.sunny.data.main.bean.Weather;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@ActivityScoped
public class MultiCityFragment extends DaggerFragment implements MultiCityContract.View{

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

    @Inject
    MultiCityContract.Presenter mMultiCityPresenter;
    @Inject
    RxBus mRxBus;

    // current loading city when multi load
    private String mCurrentLoadingCity="unknown city";

    @Inject
    public MultiCityFragment(){}

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        multiLoad();
    }


    private void initView(){
        mWeathers = new ArrayList<>();
        mMultiCityAdapter = new MultiCityAdapter(mWeathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mMultiCityAdapter);
        mMultiCityAdapter.setOnMultiCityLongClick(new MultiCityAdapter.OnMultiCityClickListener() {
            @Override
            public void onLongClick(String city) {
                showDialog(city);
            }

            @Override
            public void onClick(String city) {
                SharedPreferenceUtil.getInstance().setCityName(city);
                mRxBus.post(new ChangeCityEvent());
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
    public void currentLoadingCity(String currentCityName) {
        mCurrentLoadingCity=currentCityName;
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
            weather.basic=new Weather.BasicEntity();
            weather.basic.city=mCurrentLoadingCity;
            ToastUtil.showLong("there's an unknown city...");
        }
        mWeathers.add(weather);
    }

    private void multiLoad() {
        mWeathers.clear();
        mMultiCityPresenter.takeView(this);
    }

    private void registerRxBus(){
        mRxBus.toObservable(MultiUpdate.class).observeOn(AndroidSchedulers.mainThread()).subscribe(new SimpleSubscriber<MultiUpdate>() {
            @Override
            public void onNext(MultiUpdate multiUpdate) {
                multiLoad();
            }
        });
    }

    private void showDialog(String city){
        new AlertDialog.Builder(getActivity()).setMessage("是否删除该城市?")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                        OrmLite.OrmTest(CityORM.class);
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
