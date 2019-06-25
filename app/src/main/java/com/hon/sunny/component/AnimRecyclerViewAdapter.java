package com.hon.sunny.component;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.hon.sunny.R;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public abstract class AnimRecyclerViewAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    private static final int DELAY = 138;
    private int mLastPosition = -1;

    protected void showItemAnim(final View view, final int position) {
        Context context = view.getContext();
        if (position > mLastPosition) {
            view.setAlpha(0);
            view.postDelayed(() -> {
                Animation animation = AnimationUtils.loadAnimation(context,
                        R.anim.slide_in_right);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        view.setAlpha(1);
                    }


                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }


                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                view.startAnimation(animation);
            }, DELAY * position);
            mLastPosition = position;
        }
    }
}
