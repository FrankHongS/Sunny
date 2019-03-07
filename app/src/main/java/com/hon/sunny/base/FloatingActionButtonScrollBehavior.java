package com.hon.sunny.base;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class FloatingActionButtonScrollBehavior extends FloatingActionButton.Behavior {

//    private static final String TAG=FloatingActionButtonScrollBehavior.class.getSimpleName();

    public FloatingActionButtonScrollBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final
    FloatingActionButton child, final View directTargetChild, final View target, final int
                                               nestedScrollAxes) {
        // 确保是竖直判断的滚动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll
                (coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final
    FloatingActionButton child, final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed);
//        Log.d(TAG, "dyConsumed: "+dyConsumed+" child.isShown: "+child.isShown());
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            // fab can't be GONE, otherwise onNestedScroll won't be invoked...
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    fab.setVisibility(View.INVISIBLE);
                }
            });
//            CircularAnimUtil.hide(child,10,CircularAnimUtil.PERFECT_MILLS);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
//            CircularAnimUtil.show(child,10,CircularAnimUtil.PERFECT_MILLS);
            child.show();
        }
    }
}

