package com.hon.sunny.city.view.expandrecycleview;

import java.util.List;

import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public class ParentBean implements ItemType {
    public String text;

    public List<ChildBean> mChild;

    public String zone="";

    /**
     * 是否展开
     */
    boolean isExpand=false;

    @Override
    public int itemType() {
        return RecyclerItemType.PARENT.getValue();
    }
}

