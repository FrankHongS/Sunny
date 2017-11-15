package com.hon.sunny.main.multicity;

import com.hon.sunny.BasePresenter;
import com.hon.sunny.BaseView;
import com.hon.sunny.data.main.bean.Weather;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public interface MultiCityContract {

    interface View extends BaseView<Presenter>{
        void currentLoadingCity(String currentCityName);
        void doOnRequest();
        void doOnTerminate();
        void onCompleted();
        void onError(Throwable e);
        void onNext(Weather weather);
    }

    interface Presenter extends BasePresenter<View>{
        void loadMultiCityWeather();
    }
}
