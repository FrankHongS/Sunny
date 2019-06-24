package com.hon.sunny.component.retrofit;

import com.hon.sunny.ui.about.domain.VersionAPI;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public interface ApiInterface {
    String HOST = "https://free-api.heweather.com/s6/";

    @GET("weather")
    Flowable<WeatherAPI> mWeatherAPI(@Query("location") String location, @Query("key") String key);

    //而且在Retrofit 2.0中我们还可以在@Url里面定义完整的URL：这种情况下Base URL会被忽略。
    @GET("http://api.fir.im/apps/latest/5630e5f1f2fc425c52000006")
    Observable<VersionAPI> mVersionAPI(
            @Query("api_token") String api_token);
}

