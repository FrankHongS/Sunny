package com.hon.sunny.ui.city;

import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.data.city.CityRepository;
import com.hon.sunny.data.city.db.DBManager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class SearchCityPresenter implements SearchCityContract.Presenter, LifecycleObserver {

    private CityRepository mCityRepository;
    private SearchCityContract.View mSearchCityView;

    private SQLiteDatabase mCityDataBase;

    private CompositeDisposable mCityCompositeDisposable;

    public SearchCityPresenter(Lifecycle lifecycle, CityRepository cityRepository, SearchCityContract.View searchCityView) {
        mCityRepository = cityRepository;
        mSearchCityView = searchCityView;

        mSearchCityView.setPresenter(this);

        lifecycle.addObserver(this);
        mCityCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void start() {
        initCityDataBase();
    }

    @Override
    public void initCityDataBase() {
        DBManager dbManager = DBManager.getInstance();
        new Thread(() -> {
            dbManager.openDatabase();
            mCityDataBase = dbManager.getDatabase();
        }).start();
    }

    @Override
    public void addItemToHistoryTable(SearchItem item) {
        mCityRepository.addItemToHistoryTable(item);
    }

    @Override
    public void fillResultToRecyclerView(String query) {

        Disposable cityDisposable = mCityRepository.searchCity(mCityDataBase, query)
                .doOnSubscribe(disposable -> mSearchCityView.doOnSubscribe())
                .subscribe(
                        list -> mSearchCityView.onNext(list),
                        throwable -> {/*do nothing*/},
                        () -> mSearchCityView.onCompleted()
                );
        mCityCompositeDisposable.add(cityDisposable);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void dispose() {
        mCityCompositeDisposable.dispose();
    }
}
