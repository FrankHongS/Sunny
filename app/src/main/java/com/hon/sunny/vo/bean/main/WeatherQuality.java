package com.hon.sunny.vo.bean.main;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Frank_Hon on 6/25/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class WeatherQuality {

    @SerializedName("HeWeather6")
    public List<Quality> qualityList;

    public static class Quality {
        @SerializedName("air_now_city")
        public AirNowCity airNowCity;
    }

    public static class AirNowCity {
        @SerializedName("qlty")
        public String quality;

        @SerializedName("pm25")
        public String pm25;
    }
}
