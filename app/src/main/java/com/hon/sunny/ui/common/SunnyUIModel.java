package com.hon.sunny.ui.common;

/**
 * Created by Frank Hon on 2019-09-18 23:50.
 * E-mail: frank_hon@foxmail.com
 */
public class SunnyUIModel {

    public boolean inProgress;

    public boolean success;

    public String errorMessage;

    public SunnyUIModel(boolean inProgress, boolean success, String errorMessage) {
        this.inProgress = inProgress;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static SunnyUIModel success() {
        return new SunnyUIModel(false, true, "");
    }

    public static SunnyUIModel inProgress() {
        return new SunnyUIModel(true, false, "");
    }

    public static SunnyUIModel failure(String errorMessage) {
        return new SunnyUIModel(false, false, errorMessage);
    }

}
