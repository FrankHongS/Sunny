package com.hon.sunny.ui.main.multicity;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.hon.sunny.component.OrmLite;
import com.hon.sunny.data.main.multicity.MultiCityRepository;
import com.hon.sunny.network.exception.CityListEmptyException;
import com.hon.sunny.utils.RxUtils;
import com.hon.sunny.vo.bean.main.CityORM;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Frank on 2017/10/28.
 * E-mail:frank_hon@foxmail.com
 */

public class MultiCityPresenter implements MultiCityContract.Presenter, LifecycleObserver {

    private MultiCityRepository mMultiCityRepository;
    private MultiCityContract.View mMultiCityView;

    private CompositeDisposable mMultiCityCompositeDisposable;

    public MultiCityPresenter(Lifecycle lifecycle, MultiCityRepository multiCityRepository, MultiCityContract.View multiCityView) {
        mMultiCityRepository = multiCityRepository;
        mMultiCityView = multiCityView;

        mMultiCityView.setPresenter(this);

        lifecycle.addObserver(this);
        mMultiCityCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void start() {
        loadMultiCityWeather();
    }

    @Override
    public void loadMultiCityWeather() {

        Disposable multiCityDisposable = mMultiCityRepository
                .fetchMultiCityWeather()
                .doOnRequest(aLong -> mMultiCityView.doOnRequest())
                .doOnTerminate(() -> mMultiCityView.doOnTerminate())
                .subscribe(
                        weather -> mMultiCityView.onNext(weather),
                        throwable -> {
                            if (throwable instanceof CityListEmptyException) {
                                mMultiCityView.onEmpty();
                            } else {
                                mMultiCityView.onError(throwable);
                            }
                        },
                        () -> mMultiCityView.onCompleted()
                );
        mMultiCityCompositeDisposable.add(multiCityDisposable);
    }

    @Override
    public void loadAddedCityWeather(String addedCity) {
        Disposable addedCityDisposable = mMultiCityRepository
                .fetchAddedCityWeather(addedCity)
                .subscribe(
                        weather -> mMultiCityView.onAdded(weather),
                        throwable -> mMultiCityView.onError(throwable)
                );
        mMultiCityCompositeDisposable.add(addedCityDisposable);
    }

    @Override
    public void deleteCity(String city, int position) {
        Disposable deleteCityDisposable = Observable.just(city)
                .map(
                        c -> {
                            List<CityORM> cityList = OrmLite.getInstance().query(new QueryBuilder<>(CityORM.class).where("name=?", city));
                            if (cityList.isEmpty()) {
                                return DeleteCityUIModel.failure("删除失败");
                            }
                            int id = cityList.get(0).getId();
                            OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("id=?", id));
                            return DeleteCityUIModel.success(id);
                        }
                )
                .onErrorReturn(t -> {
                    t.printStackTrace();
                    return DeleteCityUIModel.failure("删除失败");
                })
                .compose(RxUtils.rxSchedulerHelper())
                .startWith(DeleteCityUIModel.inProgress())
                .subscribe(
                        model -> {
                            mMultiCityView.onDeleteInProgress(model.inProgress);
                            if (!model.inProgress) {
                                if (model.success) {
                                    mMultiCityView.onDeleteSuccess(city, position, model.deleteCityId);
                                } else {
                                    mMultiCityView.onDeleteError(model.errorMessage);
                                }
                            }
                        },
                        t -> {
                            // do nothing
                        }
                );

        mMultiCityCompositeDisposable.add(deleteCityDisposable);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void dispose() {
        mMultiCityCompositeDisposable.dispose();
    }
}
