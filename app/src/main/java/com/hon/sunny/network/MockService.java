package com.hon.sunny.network;

import com.hon.sunny.ui.about.domain.VersionAPI;
import com.hon.sunny.vo.bean.main.WeatherContainer;
import com.hon.sunny.vo.bean.main.WeatherQuality;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by Frank Hon on 2020/7/11 9:33 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class MockService implements ApiInterface {

    private final BehaviorDelegate<ApiInterface> delegate;

    public MockService(BehaviorDelegate<ApiInterface> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Flowable<WeatherContainer> weather(String location, String key) {
        return null;
    }

    @Override
    public Flowable<WeatherQuality> weatherQuality(String location, String key) {
        return null;
    }

    @Override
    public Observable<VersionAPI> mVersionAPI(String api_token) {
        return null;
    }
}
