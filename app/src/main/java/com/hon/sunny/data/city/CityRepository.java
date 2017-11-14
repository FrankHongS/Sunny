package com.hon.sunny.data.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.city.view.expandrecycleview.ParentBean;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */
@Singleton
public class CityRepository implements CityDataSource{

    private final CityDataSource mCityDataSource;

    @Inject
    CityRepository(CityDataSource cityLocalDataSource){
        mCityDataSource=cityLocalDataSource;
    }


    @Override
    public void addItemToHistoryTable(SearchItem item) {
        mCityDataSource.addItemToHistoryTable(item);
    }

    @Override
    public Observable<List<ParentBean>> searchCity(SQLiteDatabase database, String query) {
        return mCityDataSource.searchCity(database, query);
    }
}
