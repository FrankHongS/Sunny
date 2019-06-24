package com.hon.sunny.data.main.multicity;

import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.data.main.bean.CityORM;
import com.hon.sunny.data.main.bean.Weather;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.Util;

import java.util.List;

import io.reactivex.Flowable;

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
        List<CityORM> cityList = OrmLite.getInstance().query(CityORM.class);

        if (cityList == null || cityList.size() == 0)
            return null;
        else
            return Flowable
                    .defer(() -> Flowable.fromIterable(cityList))
                    .map(cityORM -> Util.replaceCity(cityORM.getName()))
                    .distinct()
                    .take(3)
                    .flatMap(
                            s -> RetrofitSingleton.getInstance()
                                    .getApiService()
                                    .mWeatherAPI(s, Constants.KEY)
                                    .map(weatherAPI -> {
                                        Weather weather = weatherAPI.mHeWeatherDataService30s.get(0);
                                        weather.city = s;
                                        return weather;
                                    })
                    );
//                .filter(weather -> !Constants.UNKNOWN_CITY.equals(weather.status));

    }

}
