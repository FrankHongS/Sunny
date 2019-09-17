package com.hon.sunny.component;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Frank_Hon on 9/17/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class WeatherItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        return super.recordPostLayoutInformation(state, viewHolder);
    }

    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        Log.d("shuai", "animateAppearance: " + viewHolder.getLayoutPosition());
        ObjectAnimator slideInFromLeft = ObjectAnimator.ofFloat(viewHolder.itemView, "translationX", -viewHolder.itemView.getWidth(), 0);
        slideInFromLeft.setInterpolator(new DecelerateInterpolator());
        slideInFromLeft.setDuration(300 * (viewHolder.getLayoutPosition() + 1));
//        slideInFromLeft.setEvaluator(new ArgbEvaluator());
        slideInFromLeft.start();
        return true;
    }

    @Override
    public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        Log.d("shuai", "animatePersistence: ");
        return super.animatePersistence(viewHolder, preInfo, postInfo);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        Log.d("shuai", "animateAdd: ");
        return super.animateAdd(holder);
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        Log.d("shuai", "animateChange: ");
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }
}
