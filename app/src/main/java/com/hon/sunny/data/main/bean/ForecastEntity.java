package com.hon.sunny.data.main.bean;

/**
 * Created by Frank Hon on 2019/4/12 11:40 PM.
 * E-mail: frank_hon@foxmail.com
 */
public class ForecastEntity {

    private String date;
    private String max;
    private String min;
    private String txtD;
    private String sc;
    private String dir;
    private String spd;
    private String pop;

    public ForecastEntity(String date, String max, String min,
                          String txtD, String sc, String dir, String spd, String pop) {
        this.date = date;
        this.max = max;
        this.min = min;
        this.txtD = txtD;
        this.sc = sc;
        this.dir = dir;
        this.spd = spd;
        this.pop = pop;
    }

    public String getDate() {
        return date;
    }

    public String getMax() {
        return max;
    }

    public String getMin() {
        return min;
    }

    public String getTxtD() {
        return txtD;
    }

    public String getSc() {
        return sc;
    }

    public String getDir() {
        return dir;
    }

    public String getSpd() {
        return spd;
    }

    public String getPop() {
        return pop;
    }
}
