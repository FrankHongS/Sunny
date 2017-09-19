package com.hon.sunny.modules.city.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.hon.persistentsearchview.PersistentSearchView;
import com.hon.persistentsearchview.SearchHistoryTable;
import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.R;
import com.hon.sunny.base.Constants;
import com.hon.sunny.common.PLog;
import com.hon.sunny.common.util.RxUtils;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.RxBus;
import com.hon.sunny.modules.city.db.CityDBUtil;
import com.hon.sunny.modules.city.db.DBManager;
import com.hon.sunny.modules.city.expand.ExpandAdapter;
import com.hon.sunny.modules.city.expand.ParentBean;
import com.hon.sunny.modules.city.popup.CityCardPopup;
import com.hon.sunny.modules.city.searchview.CitySuggestionBuilder;
import com.hon.sunny.modules.city.searchview.SimpleAnimationListener;
import com.hon.sunny.modules.main.domain.ChangeCityEvent;
import com.hon.sunny.modules.main.domain.CityORM;
import com.hon.sunny.modules.main.domain.MultiUpdate;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static com.hon.sunny.modules.city.db.CityDBUtil.queryZones;

/**
 * Created by Frank on 2017/9/4.
 * E-mail:frank_hon@foxmail.com
 */

public class SearchCityActivity extends AppCompatActivity{
    private PersistentSearchView mSearchView;
    private View mSearchTintView;
    private PracticalRecyclerView mRecyclerView;
    private ExpandAdapter mResultAdapter;
    private SearchHistoryTable mSearchHistoryTable=new SearchHistoryTable(this);
    private CityCardPopup mPopup;
    private CheckBox mCheckBox;

    private DBManager mDBManager=DBManager.getInstance();
    private SQLiteDatabase mDatabase;

    private boolean isChecked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        mPopup=new CityCardPopup(this);
        mSearchView = (PersistentSearchView) findViewById(R.id.searchview_success);
        mSearchTintView = findViewById(R.id.view_success_search_tint);
        mRecyclerView=(PracticalRecyclerView) findViewById(R.id.practical_recyclerview_success_search_result);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResultAdapter = new ExpandAdapter();
        mRecyclerView.setAdapter(mResultAdapter);
        mResultAdapter.setOnItemClickListener(new ExpandAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String text) {
                String city= Util.replaceCity(text);
                if (isChecked) {
                    OrmLite.getInstance().save(new CityORM(city));
                    RxBus.getDefault().post(new MultiUpdate());
                } else {
                    SharedPreferenceUtil.getInstance().setCityName(city);
                    RxBus.getDefault().post(new ChangeCityEvent());
                }
                quit();
            }
        });

        mSearchView.setHomeButtonListener(new PersistentSearchView.HomeButtonListener() {
            @Override
            public void onHomeButtonClick() {
                finish();
            }
        });
        mSearchView.setOverflowButtonListener(new PersistentSearchView.OverflowButtonListener() {
            @Override
            public void onOverflowButtonClick() {
                mPopup.showOnAnchor(mSearchView.findViewById(R.id.button_mic), RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT);
            }
        });

        mSearchView.setSuggestionBuilder(new CitySuggestionBuilder(this));
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public boolean onSuggestion(SearchItem searchItem) {
                return true;
            }

            @Override
            public void onSearchCleared() {
                Toast.makeText(SearchCityActivity.this,"onSearchCleared",Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String query) {
                mSearchHistoryTable.addItem(new SearchItem(query,query));
                mRecyclerView.setVisibility(View.VISIBLE);
                fillResultToRecyclerView(query);
            }

            @Override
            public void onSearchEditOpened() {
                //Use this to tint the screen
                mSearchTintView.setVisibility(View.VISIBLE);
                mSearchTintView
                        .animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new SimpleAnimationListener())
                        .start();
            }

            @Override
            public void onSearchEditClosed() {
                //Use this to un-tint the screen
                mSearchTintView
                        .animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new SimpleAnimationListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSearchTintView.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }

            @Override
            public boolean onSearchEditBackPressed() {
                Toast.makeText(SearchCityActivity.this,"onSearchEditBackPressed",Toast.LENGTH_SHORT)
                        .show();
                return false;
            }

            @Override
            public void onSearchExit() {
                Toast.makeText(SearchCityActivity.this,"onSearchExit",Toast.LENGTH_SHORT)
                        .show();
                mResultAdapter.clear();
//                mResultAdapter.notifyDataSetChanged();
//                下面三行代码使recyclerView的项只是隐藏，而非清除。？？？
//                if (mRecyclerView.getVisibility() == View.VISIBLE) {
//                    mRecyclerView.setVisibility(View.GONE);
//                }
            }
        });
        mSearchTintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.cancelEditing();
            }
        });

        mCheckBox=(CheckBox) mPopup.getContentView().findViewById(R.id.popup_cb);

        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra(Constants.MULTI_CHECK, false);
        if (isChecked && SharedPreferenceUtil.getInstance().getBoolean("Tips", true)) {
            showTips();
        }

        mCheckBox.setChecked(isChecked);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked=!isChecked;
            }
        });

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                mDBManager.openDatabase();
                mDatabase=mDBManager.getDatabase();
            }
        })
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe();
    }

    private void fillResultToRecyclerView(String query) {

        Observable.create(new Observable.OnSubscribe<List<ParentBean>>() {
            @Override
            public void call(Subscriber<? super List<ParentBean>> subscriber) {
                mResultAdapter.clear();
                List<ParentBean> cityList=CityDBUtil.queryCities(mDatabase,query);
                List<ParentBean> zoneList=CityDBUtil.queryZones(mDatabase,query);
                subscriber.onNext(cityList);
                subscriber.onNext(zoneList);
                subscriber.onCompleted();
            }
        })
                .compose(RxUtils.rxSchedulerHelper())
                .doOnNext(new Action1<List<ParentBean>>() {
                    @Override
                    public void call(List<ParentBean> list) {
                        mResultAdapter.addAll(list);
                    }
                })
                .subscribe();


    }

    public void onBackPressed() {
        if(mSearchView.isEditing()) {
            mSearchView.cancelEditing();
        } else {
            super.onBackPressed();
        }
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, SearchCityActivity.class));
    }

    private void showTips() {
        new AlertDialog.Builder(this).setTitle("多城市管理模式").setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
                + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)").setPositiveButton("明白", (dialog, which) -> dialog.dismiss()).setNegativeButton("不再提示", (dialog, which) -> SharedPreferenceUtil.getInstance().putBoolean("Tips", false)).show();
    }

    private void quit() {
        SearchCityActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
