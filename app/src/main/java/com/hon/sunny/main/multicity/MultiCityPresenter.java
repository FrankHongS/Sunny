package com.hon.sunny.main.multicity;

import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.di.ActivityScoped;
import com.trello.rxlifecycle.components.support.RxFragment;

import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Subscriber;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@ActivityScoped
public class MultiCityPresenter implements MultiCityContract.Presenter{

    private MultiCityRepository mMultiCityRepository;

    @Nullable
    private MultiCityContract.View mMultiCityView;

    @Inject
    public MultiCityPresenter(MultiCityRepository multiCityRepository) {
        mMultiCityRepository = multiCityRepository;
    }

    @Override
    public void takeView(MultiCityContract.View view) {
        mMultiCityView=view;
        loadMultiCityWeather();
    }

    @Override
    public void dropView() {

    }

    @Override
    public void loadMultiCityWeather() {
        mMultiCityRepository.fetchMultiCityWeather(mMultiCityRepository.getCities())
                .compose(RxUtils.rxSchedulerHelper())
//                .compose(((RxFragment)mMultiCityView).bindToLifecycle())
                .doOnRequest(aLong->mMultiCityView.doOnRequest())
                .doOnTerminate(()->mMultiCityView.doOnTerminate())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        mMultiCityView.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMultiCityView.onError(e);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        mMultiCityView.currentLoadingCity(mMultiCityRepository.currentLoadingCity());
                        mMultiCityView.onNext(weather);
                    }
                });
    }
}
