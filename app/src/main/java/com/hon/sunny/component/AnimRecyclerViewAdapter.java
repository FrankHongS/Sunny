package com.hon.sunny.component;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public abstract class AnimRecyclerViewAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T> {

    private static final int DELAY = 338;
    private int mLastPosition = -1;

    protected void showItemAnim(RecyclerView.ViewHolder holder) {
        Log.d("shuai", "showItemAnim: "+holder.getLayoutPosition()+" "+holder.getAdapterPosition());
        int position=holder.getLayoutPosition();
        if ( position> mLastPosition) {
            ObjectAnimator slideInFromLeft = ObjectAnimator.ofFloat(holder.itemView, "translationX", -800, 0);
            slideInFromLeft.setInterpolator(new DecelerateInterpolator());
            slideInFromLeft.setDuration(DELAY * (position + 1));
            slideInFromLeft.start();
            mLastPosition = position;
        }
    }

    protected void changeData() {
        mLastPosition = -1;
        notifyDataSetChanged();
    }
}
