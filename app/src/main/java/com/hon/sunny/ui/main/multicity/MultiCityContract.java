package com.hon.sunny.ui.main.multicity;

import com.hon.sunny.BasePresenter;
import com.hon.sunny.BaseView;
import com.hon.sunny.vo.bean.main.Weather;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public interface MultiCityContract {

    interface View extends BaseView<Presenter> {
        void doOnRequest();

        void doOnTerminate();

        void onError(Throwable e);

        void onEmpty();

        void onNext(Weather weather);

        void onCompleted();

        void onAdded(Weather weather);

        void onDeleteInProgress(boolean inProgress);

        void onDeleteSuccess(String city, int position,int deleteCityId);

        void onDeleteError(String errorMessage);
    }

    interface Presenter extends BasePresenter {
        void loadMultiCityWeather();

        void loadAddedCityWeather(String addedCity);

        void deleteCity(String city, int position);
    }
}
