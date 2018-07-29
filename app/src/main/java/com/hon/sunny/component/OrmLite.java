package com.hon.sunny.component;

import com.hon.sunny.BuildConfig;
import com.hon.sunny.Sunny;
import com.hon.sunny.base.Constants;
import com.hon.sunny.common.PLog;
import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.data.main.bean.CityORM;
import com.litesuits.orm.LiteOrm;

import rx.Observable;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public class OrmLite {
    private static LiteOrm sLiteOrm;

    public static LiteOrm getInstance() {
        if(sLiteOrm==null){
            sLiteOrm = LiteOrm.newSingleInstance(Sunny.getAppContext(), Constants.ORM_NAME);
            sLiteOrm.setDebugged(BuildConfig.DEBUG);
        }
        return sLiteOrm;
    }

}

