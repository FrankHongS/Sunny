package com.hon.sunny.city.view.expandrecycleview;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public class ChildBean implements ItemType {

    public String text;

    @Override
    public int itemType() {
        return RecyclerItemType.CHILD.getValue();
    }
}

