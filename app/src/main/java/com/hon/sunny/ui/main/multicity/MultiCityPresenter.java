package com.hon.sunny.ui.main.multicity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.network.exception.CityListEmptyException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityPresenter implements MultiCityContract.Presenter, LifecycleObserver {

    private MultiCityRepository mMultiCityRepository;
    private MultiCityContract.View mMultiCityView;

    private CompositeDisposable mMultiCityCompositeDisposable;

    public MultiCityPresenter(Lifecycle lifecycle, MultiCityRepository multiCityRepository, MultiCityContract.View multiCityView) {
        mMultiCityRepository = multiCityRepository;
        mMultiCityView = multiCityView;

        mMultiCityView.setPresenter(this);

        lifecycle.addObserver(this);
        mMultiCityCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void start() {
        loadMultiCityWeather();
    }

    @Override
    public void loadMultiCityWeather() {

        Disposable multiCityDisposable = mMultiCityRepository
                .fetchMultiCityWeather()
                .doOnRequest(aLong -> mMultiCityView.doOnRequest())
                .doOnTerminate(() -> mMultiCityView.doOnTerminate())
                .subscribe(
                        weather -> mMultiCityView.onNext(weather),
                        throwable -> {
                            if (throwable instanceof CityListEmptyException) {
                                mMultiCityView.onEmpty();
                            } else {
                                mMultiCityView.onError(throwable);
                            }
                        },
                        () -> mMultiCityView.onCompleted()
                );
        mMultiCityCompositeDisposable.add(multiCityDisposable);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void dispose() {
        mMultiCityCompositeDisposable.dispose();
    }
}
