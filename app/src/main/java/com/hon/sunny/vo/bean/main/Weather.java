package com.hon.sunny.vo.bean.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */
public class Weather implements Serializable {

    public String city;

    @SerializedName("basic")
    public BasicEntity basic;

    @SerializedName("update")
    public UpdateEntity update;

    @SerializedName("status")
    public String status;

    @SerializedName("now")
    public NowEntity now;

    @SerializedName("daily_forecast")
    public List<DailyForecastEntity> dailyForecast;

    @SerializedName("hourly")
    public List<HourlyForecastEntity> hourlyForecast;

    @SerializedName("lifestyle")
    public List<LifestyleEntity> lifestyle;

    @Expose(serialize = false, deserialize = false)
    public String quality;

    @Expose(serialize = false, deserialize = false)
    public String pm25;

    public static class BasicEntity implements Serializable {
        @SerializedName("cnty")
        public String cnty;
        @SerializedName("id")
        public String id;
        @SerializedName("lat")
        public String lat;
        @SerializedName("lon")
        public String lon;

    }

    public static class UpdateEntity implements Serializable {
        @SerializedName("loc")
        public String loc;
        @SerializedName("utc")
        public String utc;
    }

    public static class NowEntity implements Serializable {

        @SerializedName("cond_txt")
        public String txt;
        @SerializedName("cond_code")
        public String code;

        @SerializedName("fl")
        public String fl;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pcpn")
        public String pcpn;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public String tmp;
        @SerializedName("vis")
        public String vis;

        @SerializedName("wind_deg")
        public String deg;
        @SerializedName("wind_dir")
        public String dir;
        @SerializedName("wind_sc")
        public String sc;
        @SerializedName("wind_spd")
        public String spd;

    }

    /*
     *
     * */
    public static class LifestyleEntity implements Serializable {
        @SerializedName("type")
        public String type;
        @SerializedName("brf")
        public String brf;
        @SerializedName("txt")
        public String txt;
    }

    public static class DailyForecastEntity implements Serializable {
        @SerializedName("sr")
        public String sr;
        @SerializedName("ss")
        public String ss;

        @SerializedName("cond_code_d")
        public String codeD;
        @SerializedName("cond_code_n")
        public String codeN;
        @SerializedName("cond_txt_d")
        public String txtD;
        @SerializedName("cond_txt_n")
        public String txtN;
        @SerializedName("date")
        public String date;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pcpn")
        public String pcpn;
        @SerializedName("pop")
        public String pop;
        @SerializedName("pres")
        public String pres;

        @SerializedName("tmp_max")
        public String max;
        @SerializedName("tmp_min")
        public String min;

        @SerializedName("vis")
        public String vis;

        @SerializedName("wind_deg")
        public String deg;
        @SerializedName("wind_dir")
        public String dir;
        @SerializedName("wind_sc")
        public String sc;
        @SerializedName("wind_spd")
        public String spd;

    }

    public static class HourlyForecastEntity implements Serializable {
        @SerializedName("time")
        public String date;
        @SerializedName("hum")
        public String hum;
        @SerializedName("pop")
        public String pop;
        @SerializedName("pres")
        public String pres;
        @SerializedName("tmp")
        public String tmp;

        @SerializedName("wind_deg")
        public String deg;
        @SerializedName("wind_dir")
        public String dir;
        @SerializedName("wind_sc")
        public String sc;
        @SerializedName("wind_spd")
        public String spd;
    }
}

