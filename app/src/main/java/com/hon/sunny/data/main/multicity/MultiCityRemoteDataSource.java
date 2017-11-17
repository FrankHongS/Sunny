package com.hon.sunny.data.main.multicity;

import android.os.Looper;
import android.util.Log;

import com.hon.sunny.base.Constants;
import com.hon.sunny.common.util.ToastUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.data.main.bean.CityORM;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.di.ActivityScoped;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */
@ActivityScoped
public class MultiCityRemoteDataSource implements MultiCityDataSource{

    private String mCurrentLoadingCity;

    @Inject
    public MultiCityRemoteDataSource(){}

    @Override
    public Observable<Weather> fetchMultiCityWeather(Observable<String> citiesObservable) {
        return citiesObservable.flatMap(
                s -> RetrofitSingleton.getInstance()
                .getApiService()
                .mWeatherAPI(s, Constants.KEY)
                .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
        );
//                .filter(weather -> !Constants.UNKNOWN_CITY.equals(weather.status));

    }

    @Override
    public Observable<String> getCities() {
        return Observable
                .defer(() -> Observable.from(OrmLite.getInstance().query(CityORM.class)))
                .map(cityORM -> {
                    mCurrentLoadingCity=Util.replaceCity(cityORM.getName());
                    return mCurrentLoadingCity;
                })
                .distinct()
                .take(3);
    }

    @Override
    public String currentLoadingCity() {
        return mCurrentLoadingCity;
    }
}
