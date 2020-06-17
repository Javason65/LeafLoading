package com.javason.myleafloading;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

public class AnimationUtils {
    public static RotateAnimation initRotateAnimation(long duration, int fromAngle, int toAngle,
                                                      boolean isFillAfter, int repeatCount) {
        RotateAnimation mLoadingRotateAnimation = new RotateAnimation(fromAngle, toAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        mLoadingRotateAnimation.setInterpolator(linearInterpolator);
        mLoadingRotateAnimation.setDuration(duration);
        mLoadingRotateAnimation.setFillAfter(isFillAfter);//动画结束时停留在开始的画面还是结束时的画面
        mLoadingRotateAnimation.setRepeatCount(repeatCount);
        mLoadingRotateAnimation.setRepeatMode(Animation.RESTART);
        return mLoadingRotateAnimation;
    }

    public static RotateAnimation initRorateAnimation(boolean isClockWise, long duration, boolean
            isFillAfter, int repeatCount) {
        int endAngle;
        if (isClockWise) {
            endAngle = 360;
        } else {
            endAngle = -360;
        }
        RotateAnimation mLoadingRotateAnimation = new RotateAnimation(0, endAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        LinearInterpolator lirInterpolator = new LinearInterpolator();
        mLoadingRotateAnimation.setInterpolator(lirInterpolator);
        mLoadingRotateAnimation.setDuration(duration);
        mLoadingRotateAnimation.setFillAfter(isFillAfter);
        mLoadingRotateAnimation.setRepeatCount(repeatCount);
        mLoadingRotateAnimation.setRepeatMode(Animation.RESTART);
        return mLoadingRotateAnimation;
    }

    public static AnimationDrawable initAnimationDrawable(Context context, int[] drawableIds, int durationTime, boolean isOneShot) {
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();
        for (int i = 0; i < drawableIds.length; i++) {
            int id = drawableIds[i];
            mAnimationDrawable.addFrame(context.getResources().getDrawable(id), durationTime);
        }
        mAnimationDrawable.setOneShot(isOneShot);
        return mAnimationDrawable;
    }

    public static Animation initAlphaAnimation(Context context, float fromAlpha, float toAlpha, long duration) {
        Animation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(duration);
        return alphaAnimation;
    }
}
