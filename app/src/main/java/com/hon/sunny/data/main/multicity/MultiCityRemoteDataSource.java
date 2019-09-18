package com.hon.sunny.data.main.multicity;

import android.util.Log;

import com.hon.sunny.component.OrmLite;
import com.hon.sunny.network.RetrofitSingleton;
import com.hon.sunny.network.exception.CityListEmptyException;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.CityORM;
import com.hon.sunny.vo.bean.main.Weather;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityRemoteDataSource implements MultiCityDataSource {

    private static MultiCityRemoteDataSource INSTANCE;

    private MultiCityRemoteDataSource() {

    }

    public static MultiCityRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (MultiCityRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MultiCityRemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Flowable<Weather> fetchMultiCityWeather() {
        return Flowable
                .defer(() -> {
                    List<CityORM> cityList = OrmLite.getInstance().query(CityORM.class);
                    if (cityList == null || cityList.size() == 0) {
                        return Flowable.error(new CityListEmptyException("city list is empty"));
                    } else {
                        return Flowable.fromIterable(cityList);
                    }
                })
                .map(cityORM -> Util.replaceCity(cityORM.getName()))
                .distinct()
                .take(3)
                // control upstream thread
                .subscribeOn(Schedulers.io())
                // for parallel
                .concatMapEager(
                        s -> RetrofitSingleton
                                .getInstance()
                                .fetchWeather(s))
                .observeOn(AndroidSchedulers.mainThread());
//                .filter(weather -> !Constants.UNKNOWN_CITY.equals(weather.status));

    }

    @Override
    public Flowable<Weather> fetchAddedCityWeather(String addedCity) {
        return Flowable.just(addedCity)
                .flatMap(city ->
                        RetrofitSingleton
                                .getInstance()
                                .fetchWeather(city))
                .observeOn(AndroidSchedulers.mainThread());
    }
}
