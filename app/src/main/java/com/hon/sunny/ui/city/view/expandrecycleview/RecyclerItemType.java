package com.hon.sunny.ui.city.view.expandrecycleview;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

enum RecyclerItemType {
    NORMAL(0), Grid(1), TYPE2(2), TYPE3(3), PARENT(4), CHILD(5);

    // 定义私有变量
    private int mCode;

    RecyclerItemType(int code) {
        this.mCode = code;
    }

    public int getValue() {
        return this.mCode;
    }
}
