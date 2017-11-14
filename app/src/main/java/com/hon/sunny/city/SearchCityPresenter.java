package com.hon.sunny.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.data.city.CityRepository;
import com.hon.sunny.data.city.db.DBManager;
import com.hon.sunny.city.view.expandrecycleview.ParentBean;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class SearchCityPresenter implements SearchCityContract.Presenter{

    private CityRepository mCityRepository;
    private SearchCityContract.View mSearchCityView;

    private SQLiteDatabase mCityDatabase;

    @Inject
    public SearchCityPresenter(CityRepository cityRepository,SearchCityContract.View searchCityView){
        mCityRepository=cityRepository;
        mSearchCityView=searchCityView;

    }

    @Override
    public void start() {
        initCityDataBase();
    }

    @Override
    public void initCityDataBase() {
        DBManager dbManager=DBManager.getInstance();
        new Thread(()->{
            dbManager.openDatabase();
            mCityDatabase=dbManager.getDatabase();
        }).start();
    }

    @Override
    public void addItemToHistoryTable(SearchItem item) {
        mCityRepository.addItemToHistoryTable(item);
    }

    @Override
    public void fillResultToRecyclerView(String query) {
        mCityRepository.searchCity(mCityDatabase,query)
                .doOnSubscribe(()->mSearchCityView.doOnSubscribe())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<List<ParentBean>>() {
                    @Override
                    public void onNext(List<ParentBean> list) {
                        mSearchCityView.onNext(list);
                    }
                });
    }
}
