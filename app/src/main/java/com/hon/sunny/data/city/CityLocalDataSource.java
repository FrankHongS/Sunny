package com.hon.sunny.data.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchHistoryTable;
import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.Sunny;
import com.hon.sunny.data.city.db.CityDBUtil;
import com.hon.sunny.ui.city.view.expandrecycleview.ParentBean;
import com.hon.sunny.utils.RxUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class CityLocalDataSource implements CityDataSource {

    private SearchHistoryTable mSearchHistoryTable = SearchHistoryTable.getInstance(Sunny.getAppContext());

    private static CityLocalDataSource INSTANCE;

    private CityLocalDataSource() {
    }

    public static CityLocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (CityLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CityLocalDataSource();
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
    public Observable<List<ParentBean>> searchCity(SQLiteDatabase database, String query) {
        return Observable
                .create((ObservableOnSubscribe<List<ParentBean>>) emitter -> {
                    List<ParentBean> cityList = CityDBUtil.queryCities(database, query);
                    List<ParentBean> zoneList = CityDBUtil.queryZones(database, query);
                    emitter.onNext(cityList);
                    emitter.onNext(zoneList);
                    emitter.onComplete();
                })
                .compose(RxUtils.rxSchedulerHelper());
    }
}
