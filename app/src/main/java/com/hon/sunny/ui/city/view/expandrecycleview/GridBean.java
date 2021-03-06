package com.hon.sunny.ui.city.view.expandrecycleview;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Frank on 2017/11/19.
 * E-mail:frank_hon@foxmail.com
 */

public class GridBean implements ItemType {

    public String cityName;

    public GridBean(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public int itemType() {
        return RecyclerItemType.Grid.getValue();
    }
}
