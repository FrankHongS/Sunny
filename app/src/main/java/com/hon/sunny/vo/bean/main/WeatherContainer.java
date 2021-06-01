package com.hon.sunny.vo.bean.main;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class WeatherContainer {

    @SerializedName("HeWeather6")
    public List<Weather> weatherList;
}