package com.hon.sunny.ui.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public abstract class AnimatorAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    private static final int DELAY = 338;
    private int mLastPosition = -1;
    // to control items' animation duration
    private List<ObjectAnimator> mAnimatorList = new ArrayList<>();

    protected void addItemAnimation(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (position > mLastPosition) {
            ObjectAnimator slideInFromLeft = ObjectAnimator.ofFloat(holder.itemView, "translationX", -800, 0);
            slideInFromLeft.setInterpolator(new DecelerateInterpolator());
            slideInFromLeft.setDuration(DELAY * (mAnimatorList.size() + 1));
            slideInFromLeft.addListener(new SimpleAnimationListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mAnimatorList.add(slideInFromLeft);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimatorList.remove(slideInFromLeft);
                }
            });
            slideInFromLeft.start();
            mLastPosition = position;
        }
    }

    protected void changeData() {
        mLastPosition = -1;
        for (ObjectAnimator animator : mAnimatorList) {
            animator.cancel();
        }
        mAnimatorList.clear();
        notifyDataSetChanged();
    }
}
