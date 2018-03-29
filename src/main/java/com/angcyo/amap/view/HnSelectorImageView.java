package com.angcyo.amap.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.angcyo.uiview.skin.SkinHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/02 18:45
 * 修改人员：Robi
 * 修改时间：2017/05/02 18:45
 * 修改备注：
 * Version: 1.0.0
 */
public class HnSelectorImageView extends AppCompatImageView {
    public HnSelectorImageView(Context context) {
        super(context);
    }

    public HnSelectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HnSelectorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getDrawable() != null) {
            getDrawable().mutate().setColorFilter(SkinHelper.getSkin().getThemeSubColor(), PorterDuff.Mode.MULTIPLY);
        }
    }
}
