package com.hon.sunny.vo.event;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiUpdateEvent {

    private String addedCity;

    public MultiUpdateEvent(String addedCity) {
        this.addedCity = addedCity;
    }

    public String getAddedCity() {
        return addedCity;
    }
}
