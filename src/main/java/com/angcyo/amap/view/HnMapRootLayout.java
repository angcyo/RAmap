package com.angcyo.amap.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：用来对高德地图进行缩放
 * 创建人员：Robi
 * 创建时间：2017/02/20 10:51
 * 修改人员：Robi
 * 修改时间：2017/02/20 10:51
 * 修改备注：
 * Version: 1.0.0
 */
public class HnMapRootLayout extends LinearLayout {

    public static final int DURATION = 300;
    /**
     * 是否激活缩放效果
     */
    boolean mEnableScale = true;
    /**
     * 是否处于缩放状态
     */
    boolean isScaleStatus;

    /**
     * 地图最小时, 和最大时的大小 dp
     */
    int minMapHeight = 200, maxMapHeight = 400, currentMapHeight;

    long lastTime = 0;
    private ValueAnimator mAnimator;
    private View mTargetView;


    public HnMapRootLayout(Context context) {
        super(context);
        init();
    }

    public HnMapRootLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        minMapHeight *= density;
        maxMapHeight *= density;
        currentMapHeight = maxMapHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mEnableScale) {
            View mapLayout = getChildAt(0);
            View listLayout = getChildAt(1);
            int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
            //int mapHeight = currentMapHeight;//MeasureSpec.makeMeasureSpec(minMapHeight, MeasureSpec.EXACTLY);
            mapLayout.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(currentMapHeight, MeasureSpec.EXACTLY));
            listLayout.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(viewHeight - currentMapHeight, MeasureSpec.EXACTLY));
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        mTargetView = target;
        return mEnableScale && target instanceof RecyclerView;
    }

    public void setEnableScale(boolean enableScale) {
        mEnableScale = enableScale;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
//        scrollBy(dx, dy);
//        consumed[1] = dy;

        if (mAnimator != null && mAnimator.isRunning()) {
            return;
        }

        long timeMillis = System.currentTimeMillis();
        if (timeMillis - lastTime < DURATION * 2) {
            return;
        }

        lastTime = timeMillis;

        if (dy > 0) {
            //手指向上滚动, 想要缩小地图
            if (isScaleStatus) {
                return;
            } else {
                isScaleStatus = true;
                consumed[1] = dy;
                anim(maxMapHeight, minMapHeight);
            }
        } else if (dy < 0) {
            //手指向下滚动, 想要放大地图
            if (isScaleStatus) {
                if (!ViewCompat.canScrollVertically(target, -1)) {
                    //并且列表一已经滚动到顶部了
                    isScaleStatus = false;
                    consumed[1] = dy;
                    anim(minMapHeight, maxMapHeight);
                }
            } else {
                return;
            }
        }
    }

    private void anim(int startHeight, int endHeight) {
        if (mAnimator != null && mAnimator.isRunning()) {
            return;
        }
        mAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        mAnimator.setDuration(DURATION);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                currentMapHeight = height;
                requestLayout();
            }
        });
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTargetView.scrollTo(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }
}
