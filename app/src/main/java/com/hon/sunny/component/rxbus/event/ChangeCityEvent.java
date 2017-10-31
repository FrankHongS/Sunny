package com.hon.sunny.component.rxbus.event;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class ChangeCityEvent {

    String city;
    boolean isSetting;

    public ChangeCityEvent() {
    }

    public ChangeCityEvent(boolean isSetting) {
        this.isSetting = isSetting;
    }

    public ChangeCityEvent(String city) {
        this.city = city;
    }
}
