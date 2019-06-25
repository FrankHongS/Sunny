package com.hon.sunny.vo.bean.main;

/**
 * Created by Frank_Hon on 4/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class HourInfoEntity {

    private String city;
    private String time;
    private String tmp;
    private String hum;
    private String spd;

    public HourInfoEntity(String city, String time, String tmp, String hum, String spd) {
        this.city = city;
        this.time = time;
        this.tmp = tmp;
        this.hum = hum;
        this.spd = spd;
    }

    public String getCity() {
        return city;
    }

    public String getTime() {
        return time;
    }

    public String getTmp() {
        return tmp;
    }

    public String getHum() {
        return hum;
    }

    public String getSpd() {
        return spd;
    }
}
