package com.hon.sunny.component;

import android.animation.Animator;
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Frank_Hon on 9/17/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class MyItemAnimator extends DefaultItemAnimator {

    private ArrayMap<RecyclerView.ViewHolder, AnimatorInfo> mAnimatorMap = new ArrayMap<>();

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

//    @Override
//    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
//        if (oldHolder != newHolder) {
//            // use default behavior if not re-using view holders
//            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
//        }
//
//        final MyViewHolder viewHolder = (MyViewHolder) newHolder;
//
//        // Get the pre/post change values; these are what we are animating between
//        ColorTextInfo oldInfo = (ColorTextInfo) preInfo;
//        ColorTextInfo newInfo = (ColorTextInfo) postInfo;
//        int oldColor = oldInfo.color;
//        int newColor = newInfo.color;
//        final String oldText = oldInfo.text;
//        final String newText = newInfo.text;
//
//        // These are the objects whose values will be animated
//        LinearLayout newContainer = viewHolder.container;
//        final TextView newTextView = viewHolder.textView;
//
//        // Check to see if there's a change animation already running on this item
//        AnimatorInfo runningInfo = mAnimatorMap.get(newHolder);
//        long prevAnimPlayTime = 0;
//        boolean firstHalf = false;
//        if (runningInfo != null) {
//            // The information we need to construct the new animators is whether we
//            // are in the 'first half' (fading to black and rotating the old text out)
//            // and how far we are in whichever half is running
//            firstHalf = runningInfo.oldTextRotator != null &&
//                    runningInfo.oldTextRotator.isRunning();
//            prevAnimPlayTime = firstHalf ?
//                    runningInfo.oldTextRotator.getCurrentPlayTime() :
//                    runningInfo.newTextRotator.getCurrentPlayTime();
//            // done with previous animation - cancel it
//            runningInfo.overallAnim.cancel();
//        }
//
//        // Construct the fade to/from black animation
//        ObjectAnimator fadeToBlack = null, fadeFromBlack;
//        if (runningInfo == null || firstHalf) {
//            // The first part of the animation fades to black. Skip this phase
//            // if we're interrupting an animation that was already in the second phase.
//            int startColor = oldColor;
//            if (runningInfo != null) {
//                startColor = (Integer) runningInfo.fadeToBlackAnim.getAnimatedValue();
//            }
//            fadeToBlack = ObjectAnimator.ofInt(newContainer, "backgroundColor",
//                    startColor, Color.BLACK);
//            fadeToBlack.setEvaluator(mColorEvaluator);
//            if (runningInfo != null) {
//                // Seek to appropriate time in new animator if we were already
//                // running a previous animation
//                fadeToBlack.setCurrentPlayTime(prevAnimPlayTime);
//            }
//        }
//
//        // Second phase of animation fades from black to the new bg color
//        fadeFromBlack = ObjectAnimator.ofInt(newContainer, "backgroundColor",
//                Color.BLACK, newColor);
//        fadeFromBlack.setEvaluator(mColorEvaluator);
//        if (runningInfo != null && !firstHalf) {
//            // Seek to appropriate time in new animator if we were already
//            // running a previous animation
//            fadeFromBlack.setCurrentPlayTime(prevAnimPlayTime);
//        }
//
//        // Set up an animation to play both the first (if non-null) and second phases
//        AnimatorSet bgAnim = new AnimatorSet();
//        if (fadeToBlack != null) {
//            bgAnim.playSequentially(fadeToBlack, fadeFromBlack);
//        } else {
//            bgAnim.play(fadeFromBlack);
//        }
//
//        // The other part of the animation rotates the text, switching it to the
//        // new value half-way through (when it is perpendicular to the user)
//        ObjectAnimator oldTextRotate = null, newTextRotate;
//        if (runningInfo == null || firstHalf) {
//            // The first part of the animation rotates text to be perpendicular to user.
//            // Skip this phase if we're interrupting an animation that was already
//            // in the second phase.
//            oldTextRotate = ObjectAnimator.ofFloat(newTextView, View.ROTATION_X, 0, 90);
//            oldTextRotate.setInterpolator(mAccelerateInterpolator);
//            if (runningInfo != null) {
//                oldTextRotate.setCurrentPlayTime(prevAnimPlayTime);
//            }
//            oldTextRotate.addListener(new AnimatorListenerAdapter() {
//                boolean mCanceled = false;
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    // text was changed as part of the item change notification. Change
//                    // it back for the first phase of the animation
//                    newTextView.setText(oldText);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                    mCanceled = true;
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    if (!mCanceled) {
//                        // Set it to the new text when the old rotator ends - this is when
//                        // it is perpendicular to the user (thus making the switch
//                        // invisible)
//                        newTextView.setText(newText);
//                    }
//                }
//            });
//        }
//
//        // Second half of text rotation rotates from perpendicular to 0
//        newTextRotate = ObjectAnimator.ofFloat(newTextView, View.ROTATION_X, -90, 0);
//        newTextRotate.setInterpolator(mDecelerateInterpolator);
//        if (runningInfo != null && !firstHalf) {
//            // If we're interrupting a previous second-phase animation, seek to that time
//            newTextRotate.setCurrentPlayTime(prevAnimPlayTime);
//        }
//
//        // Choreograph first and second half. First half may be null if we interrupted
//        // a second-phase animation
//        AnimatorSet textAnim = new AnimatorSet();
//        if (oldTextRotate != null) {
//            textAnim.playSequentially(oldTextRotate, newTextRotate);
//        } else {
//            textAnim.play(newTextRotate);
//        }
//
//        // Choreograph both animations: color fading and text rotating
//        AnimatorSet changeAnim = new AnimatorSet();
//        changeAnim.playTogether(bgAnim, textAnim);
//        changeAnim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                dispatchAnimationFinished(newHolder);
//                mAnimatorMap.remove(newHolder);
//            }
//        });
//        changeAnim.start();
//
//        // Store info about this animation to be re-used if a succeeding change event
//        // occurs while it's still running
//        AnimatorInfo runningAnimInfo = new AnimatorInfo(changeAnim, fadeToBlack, fadeFromBlack,
//                oldTextRotate, newTextRotate);
//        mAnimatorMap.put(newHolder, runningAnimInfo);
//
//        return true;
//    }

    private class AnimatorInfo {
        Animator overallAnim;
        ObjectAnimator fadeToBlackAnim, fadeFromBlackAnim, oldTextRotator, newTextRotator;

        public AnimatorInfo(Animator overallAnim, ObjectAnimator fadeToBlackAnim, ObjectAnimator fadeFromBlackAnim,
                            ObjectAnimator oldTextRotator, ObjectAnimator newTextRotator) {
            this.overallAnim = overallAnim;
            this.fadeToBlackAnim = fadeToBlackAnim;
            this.fadeFromBlackAnim = fadeFromBlackAnim;
            this.oldTextRotator = oldTextRotator;
            this.newTextRotator = newTextRotator;
        }
    }
}
