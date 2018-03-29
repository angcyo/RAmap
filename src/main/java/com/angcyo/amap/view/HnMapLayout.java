package com.angcyo.amap.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 可以隐藏搞得地图logo的布局, 适用于不需要显示 放大缩小控制按钮的情况.
 * 否则, 放大缩小按钮会显示不全
 * Created by angcyo on 2017-01-15.
 */

public class HnMapLayout extends FrameLayout {
    public HnMapLayout(Context context) {
        super(context);
    }

    public HnMapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnMapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HnMapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        final View view = getChildAt(0);
//        view.measure(MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), MeasureSpec.EXACTLY),
//                MeasureSpec.makeMeasureSpec((int) (view.getMeasuredHeight() + getResources().getDisplayMetrics().density * 20), MeasureSpec.EXACTLY));
    }
}
