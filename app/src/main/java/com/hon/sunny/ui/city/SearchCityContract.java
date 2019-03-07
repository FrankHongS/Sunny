package com.hon.sunny.ui.city;

import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.BasePresenter;
import com.hon.sunny.BaseView;
import com.hon.sunny.ui.city.view.expandrecycleview.ParentBean;

import java.util.List;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public interface SearchCityContract {

    interface View extends BaseView<Presenter>{
        void doOnSubscribe();
        void onNext(List<ParentBean> list);
        void onCompleted();
    }

    interface Presenter extends BasePresenter{
        void initCityDataBase();
        void addItemToHistoryTable(SearchItem item);
        void fillResultToRecyclerView(String query);
    }
}
