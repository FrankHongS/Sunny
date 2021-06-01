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
    private List<Animator> mAnimatorList = new ArrayList<>();

    protected void addItemAnimation(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        if (position > mLastPosition) {
            ObjectAnimator slideInFromLeft = ObjectAnimator.ofFloat(holder.itemView, "translationX", -800, 0);
            slideInFromLeft.setInterpolator(new DecelerateInterpolator());
            slideInFromLeft.setDuration(DELAY * (mAnimatorList.size() + 1));
            slideInFromLeft.addListener(new SimpleAnimationListener() {
                // if canceled, don't invoke onAnimationEnd()
                private boolean cancel = false;

                @Override
                public void onAnimationStart(Animator animation) {
                    mAnimatorList.add(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    cancel = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!cancel) {
                        mAnimatorList.remove(animation);
                    }
                }
            });
            slideInFromLeft.start();
            mLastPosition = position;
        }
    }

    protected void changeData() {
        mLastPosition = -1;
        for (Animator animator : mAnimatorList) {
            // cancel方法中会调用onAnimationEnd回调，导致遍历mAnimatorList过程中
            // 操作mAnimatorList，报错java.util.ConcurrentModificationException
            animator.cancel();
        }
        mAnimatorList.clear();
        notifyDataSetChanged();
    }
}
