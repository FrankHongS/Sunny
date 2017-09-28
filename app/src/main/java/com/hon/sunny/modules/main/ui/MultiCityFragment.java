package com.hon.sunny.modules.main.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
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
import android.widget.LinearLayout;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.hon.sunny.R;
import com.hon.sunny.base.BaseFragment;
import com.hon.sunny.base.Constants;
import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.RetrofitSingleton;
import com.hon.sunny.component.RxBus;
import com.hon.sunny.modules.main.adapter.MultiCityAdapter;
import com.hon.sunny.modules.main.domain.ChangeCityEvent;
import com.hon.sunny.modules.main.domain.CityORM;
import com.hon.sunny.modules.main.domain.MultiUpdate;
import com.hon.sunny.modules.main.domain.Weather;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

import static android.content.ContentValues.TAG;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static java.lang.Thread.currentThread;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityFragment extends BaseFragment {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.swiprefresh)
    SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.empty)
    LinearLayout mLayout;

    private MultiCityAdapter mAdapter;
    private List<Weather> mWeathers;

    private View view;

    /**
     * 加载数据操作,在视图创建之前初始化
     */
    @Override
    protected void lazyLoad() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_multicity, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault().toObservable(MultiUpdate.class).subscribe(new SimpleSubscriber<MultiUpdate>() {
            @Override
            public void onNext(MultiUpdate multiUpdate) {
               mRefreshLayout.setRefreshing(true);
                multiLoad();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mRefreshLayout.setRefreshing(true);
        multiLoad();
    }

    private void initView() {
        mWeathers = new ArrayList<>();
        mAdapter = new MultiCityAdapter(mWeathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnMultiCityLongClick(new MultiCityAdapter.OnMultiCityClickListener() {
            @Override
            public void onLongClick(String city) {
                new AlertDialog.Builder(getActivity()).setMessage("是否删除该城市?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                                OrmLite.OrmTest(CityORM.class);
                                mRefreshLayout.setRefreshing(true);
                                multiLoad();
                                Snackbar.make(getView(), "已经将" + city + "删掉了 Ծ‸ Ծ", Snackbar.LENGTH_LONG).setAction("撤销",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                OrmLite.getInstance().save(new CityORM(city));
                                                mRefreshLayout.setRefreshing(true);
                                                multiLoad();
                                            }
                                        }).show();
                            }
                        })
                        .show();
            }

            @Override
            public void onClick(String city) {
                SharedPreferenceUtil.getInstance().setCityName(city);
                RxBus.getDefault().post(new ChangeCityEvent());
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
                    mRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            multiLoad();
                        }
                    }, 1000);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void multiLoad() {
        mWeathers.clear();
        Observable
                .defer(() -> Observable.from(OrmLite.getInstance().query(CityORM.class)))
//                .doOnRequest(aLong -> mRefreshLayout.setRefreshing(true))
                .map(cityORM -> Util.replaceCity(cityORM.getName()))
                .distinct()
                .flatMap(s -> RetrofitSingleton.getInstance()
                        .getApiService()
                        .mWeatherAPI(s, Constants.KEY)
                        .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
                        )
                .compose(RxUtils.rxSchedulerHelper())
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(weather -> !Constants.UNKNOWN_CITY.equals(weather.status))
                .take(3)
//                .doOnTerminate(() -> {
//                    mRefreshLayout.setRefreshing(false);
//                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                        if (mAdapter.isEmpty()) {
                            mLayout.setVisibility(View.VISIBLE);
                        } else {
                            mLayout.setVisibility(View.GONE);
                        }
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAdapter.isEmpty() && mLayout != null) {
                            mLayout.setVisibility(View.VISIBLE);
                        }
                        RetrofitSingleton.disposeFailureInfo(e);
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        mWeathers.add(weather);
                    }
                });
    }

    private void test(String city){
        NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(getActivity());
        dialogBuilder
                .withTitle("Delete City")                                  //.withTitle(null)  no title
                .withTitleColor("#000000")                                  //def
                .withDividerColor("#FFFFFF")                              //def
                .withMessage("Are you sure ? \n")                     //.withMessage(null)  no Msg
                .withMessageColor("#000000")                              //def  | withMessageColor(int resid)
                .withDialogColor("#FFFFFF")
                .withDuration(700)
                .withEffect(Effectstype.SlideBottom)
                .withButton1Text("Cancel")
                .withButton2Text("Yeah")
                .isCancelableOnTouchOutside(true)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.cancel();
                    }
                })
                .setButton2Click(v->{
                    OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                    OrmLite.OrmTest(CityORM.class);
                    mRefreshLayout.setRefreshing(true);
                    multiLoad();
                    Snackbar.make(getView(), "已经将" + city + "删掉了 Ծ‸ Ծ", Snackbar.LENGTH_LONG).setAction("撤销",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    OrmLite.getInstance().save(new CityORM(city));
                                    mRefreshLayout.setRefreshing(true);
                                    multiLoad();
                                }
                            }).show();
                })
                .show();
    }
}

