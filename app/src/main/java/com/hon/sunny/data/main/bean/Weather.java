package com.hon.sunny.data.main.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class Weather implements Serializable{

    public String city;

    @SerializedName("basic") public BasicEntity basic;

    @SerializedName("update") public UpdateEntity update;

    @SerializedName("status") public String status;

    @SerializedName("now") public NowEntity now;

    @SerializedName("daily_forecast") public List<DailyForecastEntity> dailyForecast;

    @SerializedName("hourly") public List<HourlyForecastEntity> hourlyForecast;

    @SerializedName("lifestyle") public List<LifestyleEntity> lifestyle;


    public static class BasicEntity implements Serializable {
        @SerializedName("cnty") public String cnty;
        @SerializedName("id") public String id;
        @SerializedName("lat") public String lat;
        @SerializedName("lon") public String lon;

    }

    public static class UpdateEntity implements Serializable {
        @SerializedName("loc") public String loc;
        @SerializedName("utc") public String utc;
    }

    public static class NowEntity implements Serializable {

        @SerializedName("cond_txt") public String txt;
        @SerializedName("cond_code") public String code;

        @SerializedName("fl") public String fl;
        @SerializedName("hum") public String hum;
        @SerializedName("pcpn") public String pcpn;
        @SerializedName("pres") public String pres;
        @SerializedName("tmp") public String tmp;
        @SerializedName("vis") public String vis;

        @SerializedName("wind_deg") public String deg;
        @SerializedName("wind_dir") public String dir;
        @SerializedName("wind_sc") public String sc;
        @SerializedName("wind_spd") public String spd;

    }
/*
*
* */
    public static class LifestyleEntity implements Serializable {
        @SerializedName("type") public String type;
        @SerializedName("brf") public String brf;
        @SerializedName("txt") public String txt;
    }

    public static class DailyForecastEntity implements Serializable {
        @SerializedName("sr") public String sr;
        @SerializedName("ss") public String ss;

        @SerializedName("cond_code_d") public String codeD;
        @SerializedName("cond_code_n") public String codeN;
        @SerializedName("cond_txt_d") public String txtD;
        @SerializedName("cond_txt_n") public String txtN;
        @SerializedName("date") public String date;
        @SerializedName("hum") public String hum;
        @SerializedName("pcpn") public String pcpn;
        @SerializedName("pop") public String pop;
        @SerializedName("pres") public String pres;

        @SerializedName("tmp_max") public String max;
        @SerializedName("tmp_min") public String min;

        @SerializedName("vis") public String vis;

        @SerializedName("wind_deg") public String deg;
        @SerializedName("wind_dir") public String dir;
        @SerializedName("wind_sc") public String sc;
        @SerializedName("wind_spd") public String spd;

    }

    /**
     * {
     type: "comf",
     brf: "较舒适",
     txt: "今天夜间有降雨，但会使人们感觉有些热，不过大部分人仍会有比较舒适的感觉。"
     },
     -{
     type: "drsg",
     brf: "热",
     txt: "天气热，建议着短裙、短裤、短薄外套、T恤等夏季服装。"
     },
     -{
     type: "flu",
     brf: "少发",
     txt: "各项气象条件适宜，发生感冒机率较低。但请避免长期处于空调房间中，以防感冒。"
     },
     -{
     type: "sport",
     brf: "较不宜",
     txt: "有降水，推荐您在室内进行低强度运动；若坚持户外运动，须注意选择避雨防滑并携带雨具。"
     },
     -{
     type: "trav",
     brf: "适宜",
     txt: "稍热，但是有较弱降水和微风作伴，会给您的旅行带来意想不到的景象，适宜旅游，可不要错过机会呦！"
     },
     -{
     type: "uv",
     brf: "弱",
     txt: "紫外线强度较弱，建议出门前涂擦SPF在12-15之间、PA+的防晒护肤品。"
     },
     -{
     type: "cw",
     brf: "不宜",
     txt: "不宜洗车，未来24小时内有雨，如果在此期间洗车，雨水和路上的泥水可能会再次弄脏您的爱车。"
     },
     -{
     type: "air",
     brf: "良",
     txt: "气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。"
     }
     */
    public static class HourlyForecastEntity implements Serializable {
        @SerializedName("time") public String date;
        @SerializedName("hum") public String hum;
        @SerializedName("pop") public String pop;
        @SerializedName("pres") public String pres;
        @SerializedName("tmp") public String tmp;

        @SerializedName("wind_deg") public String deg;
        @SerializedName("wind_dir") public String dir;
        @SerializedName("wind_sc") public String sc;
        @SerializedName("wind_spd") public String spd;
    }
}

