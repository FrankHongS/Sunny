package com.hon.sunny.data.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchHistoryTable;
import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.Sunny;
import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.data.city.db.CityDBUtil;
import com.hon.sunny.city.view.expandrecycleview.ParentBean;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class CityLocalDataSource implements CityDataSource{

    private SearchHistoryTable mSearchHistoryTable=SearchHistoryTable.getInstance(Sunny.getAppContext());

    private static CityLocalDataSource INSTANCE;

    private CityLocalDataSource(){}

    public static CityLocalDataSource getInstance(){
        if(INSTANCE==null){
            synchronized (CityLocalDataSource.class){
                if(INSTANCE==null){
                    INSTANCE=new CityLocalDataSource();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void addItemToHistoryTable(SearchItem item) {
        mSearchHistoryTable.addItem(item);
    }

    @Override
    public Observable<List<ParentBean>> searchCity(SQLiteDatabase database,String query) {
        return Observable.create(new Observable.OnSubscribe<List<ParentBean>>() {
            @Override
            public void call(Subscriber<? super List<ParentBean>> subscriber) {
                List<ParentBean> cityList=CityDBUtil.queryCities(database,query);
                List<ParentBean> zoneList=CityDBUtil.queryZones(database,query);
                subscriber.onNext(cityList);
                subscriber.onNext(zoneList);
                subscriber.onCompleted();
            }
        })
                .compose(RxUtils.rxSchedulerHelper());
    }
}
