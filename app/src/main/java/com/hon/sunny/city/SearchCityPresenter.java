package com.hon.sunny.city;

import android.database.sqlite.SQLiteDatabase;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.common.util.SimpleSubscriber;
import com.hon.sunny.data.city.CityRepository;
import com.hon.sunny.data.city.db.DBManager;
import com.hon.sunny.city.view.expandrecycleview.ParentBean;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class SearchCityPresenter implements SearchCityContract.Presenter{

    private CityRepository mCityRepository;
    private SearchCityContract.View mSearchCityView;

    private SQLiteDatabase mCityDataBase;

    public SearchCityPresenter(CityRepository cityRepository,SearchCityContract.View searchCityView){
        mCityRepository=cityRepository;
        mSearchCityView=searchCityView;

        mSearchCityView.setPresenter(this);
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
            mCityDataBase=dbManager.getDatabase();
        }).start();
    }

    @Override
    public void addItemToHistoryTable(SearchItem item) {
        mCityRepository.addItemToHistoryTable(item);
    }

    @Override
    public void fillResultToRecyclerView(String query) {
        mCityRepository.searchCity(mCityDataBase,query)
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
