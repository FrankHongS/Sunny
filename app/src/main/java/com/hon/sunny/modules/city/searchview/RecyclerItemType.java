package com.hon.sunny.modules.city.searchview;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public enum  RecyclerItemType {
    NORMAL(0), TYPE1(1), TYPE2(2), TYPE3(3), PARENT(4), CHILD(5);

    // 定义私有变量
    private int nCode;

    RecyclerItemType(int _nCode) {
        this.nCode = _nCode;
    }

    public int getValue() {
        return this.nCode;
    }
}
