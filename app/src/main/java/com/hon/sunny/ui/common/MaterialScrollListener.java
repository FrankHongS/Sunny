package com.hon.sunny.ui.common;

import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hon.sunny.ui.main.MainActivity;

/**
 * Created by Frank Hon on 2019/4/13 10:28 AM.
 * E-mail: frank_hon@foxmail.com
 */
public class MaterialScrollListener extends RecyclerView.OnScrollListener {

    private MainActivity mActivity;

    public MaterialScrollListener(MainActivity mainActivity) {
        this.mActivity = mainActivity;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        int touchSlop = ViewConfiguration.get(mActivity.getApplicationContext()).getScaledTouchSlop();//28

        if (dy < 0 && Math.abs(dy) >= touchSlop) {
            mActivity.displayFabMaterial(true);
        } else if (dy > 0 && Math.abs(dy) >= touchSlop) {
            mActivity.displayFabMaterial(false);
        }
    }
}
