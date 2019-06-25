package com.hon.sunny.ui.city;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.hon.persistentsearchview.PersistentSearchView;
import com.hon.persistentsearchview.SearchItem;
import com.hon.sunny.R;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.data.city.CityLocalDataSource;
import com.hon.sunny.data.city.CityRepository;
import com.hon.sunny.ui.city.view.expandrecycleview.ExpandAdapter;
import com.hon.sunny.ui.city.view.expandrecycleview.GridAdapter;
import com.hon.sunny.ui.city.view.expandrecycleview.ParentBean;
import com.hon.sunny.ui.city.view.popup.CityCardPopup;
import com.hon.sunny.ui.city.view.searchview.CitySuggestionBuilder;
import com.hon.sunny.ui.city.view.searchview.SimpleAnimationListener;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.CityORM;
import com.hon.sunny.vo.event.ChangeCityEvent;
import com.hon.sunny.vo.event.MultiUpdateEvent;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import zlc.season.practicalrecyclerview.PracticalRecyclerView;

/**
 * Created by Frank on 2017/10/29.
 * E-mail:frank_hon@foxmail.com
 */

public class SearchCityActivity extends AppCompatActivity implements SearchCityContract.View {

    @BindView(R.id.searchview)
    PersistentSearchView mSearchView;
    @BindView(R.id.searchview_tint)
    View mSearchTintView;
    @BindView(R.id.practical_recyclerview_search_result)
    PracticalRecyclerView mRecyclerView;

    private ExpandAdapter mResultAdapter;
    private GridAdapter mHintAdapter;
    private CityCardPopup mPopup;

    private List<ParentBean> mSearchResultList = new ArrayList<>();
    // if CheckBox is checked
    private boolean isChecked = false;
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
        mSearchCityPresenter = presenter;
    }

    private void initView() {
        mPopup = new CityCardPopup(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mResultAdapter = new ExpandAdapter();
        mResultAdapter.setOnItemClickListener(this::onCityItemClick);

        mHintAdapter = new GridAdapter();
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
                mSearchCityPresenter.addItemToHistoryTable(new SearchItem(query, query));
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

        CheckBox checkBox = mPopup.getContentView().findViewById(R.id.popup_cb);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean c) {
                isChecked = c;
                if (isChecked && !Util.checkMultiCitiesCount()) {
                    if (SharedPreferenceUtil.getInstance().getBoolean("Tips", true)) {
                        showTips();
                    }
                }
            }
        });

        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra(Constants.MULTI_CHECK, false);

        checkBox.setChecked(intent.getBooleanExtra(Constants.MULTI_CHECK, false));
        // init Presenter
        new SearchCityPresenter(getLifecycle(), CityRepository.getInstance(CityLocalDataSource.getInstance()), this);
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
        if (mSearchResultList != null && mSearchResultList.size() > 0)
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

    private void showCityHint() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mHintAdapter);
    }

    private void showCitySearchResult(List<ParentBean> list) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mResultAdapter);
        mResultAdapter.addAll(list);
    }

    private void onCityItemClick(String text) {
        String city = Util.replaceCity(text);
        if (isChecked) {
            if (Util.checkIfCityExists(city)) {
                Snackbar.make(mRecyclerView, R.string.city_exists, Snackbar.LENGTH_LONG).show();
                return;
            }
            if (Util.checkMultiCitiesCount()) {
                Snackbar.make(mRecyclerView, R.string.city_count, Snackbar.LENGTH_LONG).show();
                return;
            } else {
                OrmLite.getInstance().save(new CityORM(city));
                EventBus.getDefault().post(new MultiUpdateEvent());
            }
        } else {
            SharedPreferenceUtil.getInstance().setCityName(city);
            EventBus.getDefault().post(new ChangeCityEvent());
        }
        quit();
    }
}
