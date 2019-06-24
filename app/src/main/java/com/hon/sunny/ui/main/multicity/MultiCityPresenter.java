package com.hon.sunny.ui.main.multicity;

import com.hon.sunny.utils.PLog;
import com.hon.sunny.utils.RxUtils;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.data.main.bean.Weather;
import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityPresenter implements MultiCityContract.Presenter{

    private MultiCityRepository mMultiCityRepository;

    private MultiCityContract.View mMultiCityView;

    public MultiCityPresenter(MultiCityRepository multiCityRepository, MultiCityContract.View multiCityView) {
        mMultiCityRepository = multiCityRepository;
        mMultiCityView = multiCityView;

        mMultiCityView.setPresenter(this);
    }

    @Override
    public void start() {
        loadMultiCityWeather();
    }

    @Override
    public void loadMultiCityWeather() {

        Observable<String> cityObservable=mMultiCityRepository.getCities();

        if(cityObservable==null)
            mMultiCityView.onEmpty();
        else
            mMultiCityRepository.fetchMultiCityWeather(cityObservable)
                .compose(RxUtils.rxSchedulerHelper())
                .compose(((RxFragment)mMultiCityView).bindToLifecycle())
                .doOnRequest(aLong->mMultiCityView.doOnRequest())
                .doOnTerminate(()->mMultiCityView.doOnTerminate())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onError(Throwable e) {
                        mMultiCityView.onError(e);
                    }

                    @Override
                    public void onNext(Weather weather) {
                        mMultiCityView.onNext(weather);
                    }

                    @Override
                    public void onCompleted() {
                        mMultiCityView.onCompleted();
                    }
                });
    }
}
