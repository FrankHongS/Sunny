package com.hon.sunny.ui.splash;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.hon.sunny.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2017/9/18.
 * E-mail:frank_hon@foxmail.com
 */

public class SvgView extends View {
    private final Paint mPaint;
    private final SvgHelper mSvgHelper;
    private int mSvgResource;
    private List<SvgHelper.SvgPath> mPaths = new ArrayList<>();

    private float mPhase;
    private int mDuration;

    private ValueAnimator mSvgAnimator;
    private SvgCompletedCallBack mCallback;

    public SvgView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SvgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mSvgHelper = new SvgHelper(mPaint);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SvgView, defStyle, 0);
        if (a != null) {
            mPaint.setStrokeWidth(a.getFloat(R.styleable.SvgView_svgStrokeWidth, 1.0f));
            mPaint.setColor(a.getColor(R.styleable.SvgView_strokeColor, 0xff000000));
            mPhase = a.getFloat(R.styleable.SvgView_phase, 0.0f);
            mDuration = a.getInt(R.styleable.SvgView_duration, 4000);
            a.recycle();
        }
    }

    public void setSvgResource(int svgResource) {
        mSvgResource = svgResource;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mSvgHelper.load(getContext(), mSvgResource, w, h, paths -> {
            mPaths = paths;
            startAnimation();
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mPaths.isEmpty()) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            for (SvgHelper.SvgPath svgPath : mPaths) {
                canvas.drawPath(svgPath.path, svgPath.paint);
            }
            canvas.restore();
        }
    }


    private void startAnimation() {
        mSvgAnimator = ValueAnimator.ofFloat(mPhase, 0.0f);
        mSvgAnimator.setDuration(mDuration);
        mSvgAnimator.addUpdateListener(animation -> {
            for (SvgHelper.SvgPath svgPath : mPaths) {
                PathEffect pathEffect = new DashPathEffect(new float[]{svgPath.length, svgPath.length},
                        Math.max((float) animation.getAnimatedValue() * svgPath.length, 0.0f));
                svgPath.paint.setPathEffect(pathEffect);
            }
            invalidate();
        });
        mSvgAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                if (mCallback != null) {
                    mCallback.onSvgStart();
                }
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                if (mCallback != null) {
                    mCallback.onSvgCompleted();
                }
            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }
        });
        mSvgAnimator.start();
    }

    public void stop() {
        mSvgHelper.cancel();
        mSvgAnimator.end();
        mPaths.clear();
    }

    public void setCallback(SvgCompletedCallBack callback) {
        this.mCallback = callback;
    }

    interface SvgCompletedCallBack {
        void onSvgStart();

        void onSvgCompleted();
    }

}
