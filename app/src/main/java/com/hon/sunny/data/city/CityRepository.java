package com.hon.sunny.data.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.ui.city.view.expandrecycleview.ParentBean;

import java.util.List;

import rx.Observable;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class CityRepository implements CityDataSource{

    private static CityRepository INSTANCE;

    private final CityDataSource mCityDataSource;

    private CityRepository(CityDataSource cityLocalDataSource){
        mCityDataSource=cityLocalDataSource;
    }

    public static CityRepository getInstance(CityDataSource cityLocalDataSource){
        if(INSTANCE==null){
            synchronized (MultiCityRepository.class){
                if(INSTANCE==null){
                    INSTANCE=new CityRepository(cityLocalDataSource);
                }
            }
        }
        return INSTANCE;
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
