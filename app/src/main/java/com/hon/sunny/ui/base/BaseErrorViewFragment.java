package com.hon.sunny.ui.base;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hon.sunny.R;

import butterknife.BindView;

/**
 * Created by Frank Hon on 2020/7/12 12:39 AM.
 * E-mail: frank_hon@foxmail.com
 */
public class BaseErrorViewFragment extends Fragment {
    @BindView(R.id.fl_error)
    protected FrameLayout errorLayout;

    protected void bindErrorView(View.OnClickListener listener){
        errorLayout.setOnClickListener(listener);
    }

    protected void safeSetTitle(String title) {
        ActionBar appBarLayout = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (appBarLayout != null && !TextUtils.isEmpty(title)) {
            appBarLayout.setTitle(title);
        }
    }
}
