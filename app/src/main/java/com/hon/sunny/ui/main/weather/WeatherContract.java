package com.hon.sunny.ui.main.weather;

import com.hon.sunny.BasePresenter;
import com.hon.sunny.BaseView;
import com.hon.sunny.data.main.bean.Weather;

/**
 * Created by Frank on 2017/10/27.
 * E-mail:frank_hon@foxmail.com
 */

public interface WeatherContract {

    interface View extends BaseView<Presenter>{
        void doOnRequest();
        void doOnNext();
        void onError(Throwable e);
        void onNext(Weather weather);
    }

    interface Presenter extends BasePresenter{
         void loadWeather();
    }
}
