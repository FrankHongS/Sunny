package com.hon.sunny.component.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hon.sunny.data.main.bean.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherAPI {

    //    @SerializedName("HeWeather data service 3.0") @Expose
    @SerializedName("HeWeather5") @Expose
    public List<Weather> mHeWeatherDataService30s
            = new ArrayList<>();
}