package com.hon.sunny.ui.main.multicity;

import com.hon.sunny.ui.common.SunnyUIModel;

/**
 * Created by Frank Hon on 2019-09-19 00:21.
 * E-mail: frank_hon@foxmail.com
 */
public class DeleteCityUIModel extends SunnyUIModel {

    public int deleteCityId;

    public DeleteCityUIModel(boolean inProgress, boolean success, String errorMessage) {
        super(inProgress, success, errorMessage);
    }

    public DeleteCityUIModel(boolean inProgress, boolean success, String errorMessage, int deleteCityId) {
        super(inProgress, success, errorMessage);
        this.deleteCityId = deleteCityId;
    }

    public static DeleteCityUIModel success(int deleteCityId) {
        return new DeleteCityUIModel(false, true, "", deleteCityId);
    }

    public static DeleteCityUIModel inProgress() {
        return new DeleteCityUIModel(true, false, "");
    }

    public static DeleteCityUIModel failure(String errorMessage) {
        return new DeleteCityUIModel(false, false, errorMessage);
    }


}
