package com.hon.sunny.data.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.ui.city.view.expandrecycleview.ParentBean;

import java.util.List;

import rx.Observable;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public interface CityDataSource {
    void addItemToHistoryTable(SearchItem item);

    Observable<List<ParentBean>> searchCity(SQLiteDatabase database,String query);
}
