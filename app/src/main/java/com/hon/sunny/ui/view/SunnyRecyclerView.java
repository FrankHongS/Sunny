package com.hon.sunny.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Frank_Hon on 4/12/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class SunnyRecyclerView extends RecyclerView {
    public SunnyRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public SunnyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunnyRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return false;
    }
}
