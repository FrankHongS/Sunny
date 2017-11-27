package com.hon.sunny.city;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hon.persistentsearchview.PersistentSearchView;
import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.R;
import com.hon.sunny.base.Constants;
import com.hon.sunny.city.view.expandrecycleview.GridAdapter;
import com.hon.sunny.common.util.SharedPreferenceUtil;
import com.hon.sunny.common.util.ToastUtil;
import com.hon.sunny.common.util.Util;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.component.rxbus.RxBus;
import com.hon.sunny.data.city.CityLocalDataSource;
import com.hon.sunny.data.city.CityRepository;
import com.hon.sunny.data.main.bean.CityORM;
import com.hon.sunny.city.view.expandrecycleview.ExpandAdapter;
import com.hon.sunny.city.view.expandrecycleview.ParentBean;
import com.hon.sunny.city.view.popup.CityCardPopup;
import com.hon.sunny.city.view.searchview.CitySuggestionBuilder;
import com.hon.sunny.city.view.searchview.SimpleAnimationListener;
import com.hon.sunny.component.rxbus.event.ChangeCityEvent;
import com.hon.sunny.component.rxbus.event.MultiUpdate;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

import static android.R.id.list;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class SearchCityActivity extends RxAppCompatActivity implements SearchCityContract.View{

    @Bind(R.id.searchview)
    PersistentSearchView mSearchView;
    @Bind(R.id.searchview_tint)
    View mSearchTintView;
    @Bind(R.id.practical_recyclerview_search_result)
    PracticalRecyclerView mRecyclerView;

    private ExpandAdapter mResultAdapter;
    private GridAdapter mHintAdapter;
    private CityCardPopup mPopup;

    private List<ParentBean> mSearchResultList=new ArrayList<>();
    // if CheckBox is checked
    private boolean mIsChecked = false;
    private SearchCityContract.Presenter mSearchCityPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void setPresenter(SearchCityContract.Presenter presenter) {
        mSearchCityPresenter=presenter;
    }

    private void initView(){
        mPopup=new CityCardPopup(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mResultAdapter = new ExpandAdapter();
        mResultAdapter.setOnItemClickListener(this::onCityItemClick);

        mHintAdapter=new GridAdapter();
        mHintAdapter.setOnCityHintItemClickListener(this::onCityItemClick);

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
            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String query) {
                mSearchCityPresenter.addItemToHistoryTable(new SearchItem(query,query));
                mSearchCityPresenter.fillResultToRecyclerView(query);
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
                return false;
            }

            @Override
            public void onSearchExit() {
                showCityHint();
            }
        });
        mSearchTintView.setOnClickListener(v -> mSearchView.cancelEditing());

        CheckBox checkBox=(CheckBox) mPopup.getContentView().findViewById(R.id.popup_cb);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsChecked=isChecked;
                if(isChecked&&!Util.checkMultiCitiesCount()){
                    if(SharedPreferenceUtil.getInstance().getBoolean("Tips",true)){
                        showTips();
                    }
                }
            }
        });

        Intent intent = getIntent();
        mIsChecked = intent.getBooleanExtra(Constants.MULTI_CHECK, false);

        checkBox.setChecked(intent.getBooleanExtra(Constants.MULTI_CHECK, false));
        // init Presenter
        new SearchCityPresenter(CityRepository.getInstance(CityLocalDataSource.getInstance()),this);
        // show city hint
        showCityHint();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchCityPresenter.start();
    }

    @Override
    public void doOnSubscribe() {
        mSearchResultList.clear();
        mResultAdapter.clear();
    }

    @Override
    public void onNext(List<ParentBean> list) {
        mSearchResultList.addAll(list);
    }

    @Override
    public void onCompleted() {
        if(mSearchResultList!=null&&mSearchResultList.size()>0)
            showCitySearchResult(mSearchResultList);
        else
            showCityHint();
    }

    private void quit() {
        SearchCityActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void showTips() {
        new AlertDialog.Builder(this).setTitle("多城市管理模式").setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
                + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)").setPositiveButton("明白", (dialog, which) -> dialog.dismiss()).setNegativeButton("不再提示", (dialog, which) -> SharedPreferenceUtil.getInstance().putBoolean("Tips", false)).show();
    }

    private void showCityHint(){
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setAdapter(mHintAdapter);
    }

    private void showCitySearchResult(List<ParentBean> list){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mResultAdapter);
        mResultAdapter.addAll(list);
    }

    private void onCityItemClick(String text){
        String city= Util.replaceCity(text);
        if (mIsChecked) {
            if(Util.checkIfCityExists(city)){
                Snackbar.make(mRecyclerView,R.string.city_exists,Snackbar.LENGTH_LONG).show();
                return;
            }
            if(Util.checkMultiCitiesCount()){
                Snackbar.make(mRecyclerView,R.string.city_count,Snackbar.LENGTH_LONG).show();
                return;
            }else{
                OrmLite.getInstance().save(new CityORM(city));
                RxBus.getDefault().post(new MultiUpdate());
            }
        } else {
            SharedPreferenceUtil.getInstance().setCityName(city);
            RxBus.getDefault().post(new ChangeCityEvent());
        }
        quit();
    }
}
