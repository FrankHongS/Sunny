package com.hon.sunny.vo.bean.main;

/**
 * Created by Frank_Hon on 4/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SuggestionEntity {

    private int imageId;

    private String title;

    private String desc;

    public SuggestionEntity(int imageId, String title, String desc) {
        this.imageId = imageId;
        this.title = title;
        this.desc = desc;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
}
