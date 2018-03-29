package com.angcyo.amap;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.services.core.PoiItem;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/12 14:32
 * 修改人员：Robi
 * 修改时间：2017/01/12 14:32
 * 修改备注：
 * Version: 1.0.0
 */
public class PoiItemAdapter extends RModelAdapter<PoiItem> {

    public String searchWord = "";
    OnPoiItemListener mPoiItemListener;

    public PoiItemAdapter(Context context) {
        this(context, null);
    }

    public PoiItemAdapter(Context context, OnPoiItemListener poiItemListener) {
        super(context);
        mPoiItemListener = poiItemListener;
        setEnableLoadMore(true);
        setModel(MODEL_SINGLE);
    }

    public static String getAddress(PoiItem bean) {
        StringBuilder stringBuilder = new StringBuilder(bean.getProvinceName());
        if (!TextUtils.equals(bean.getProvinceName(), bean.getCityName())) {
            stringBuilder.append(bean.getCityName());
        }
        stringBuilder.append(bean.getAdName());
        stringBuilder.append(bean.getSnippet());
        return stringBuilder.toString();
    }

    public void setPoiItemListener(OnPoiItemListener poiItemListener) {
        mPoiItemListener = poiItemListener;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_amap_poi;
    }

    @Override
    protected void onBindCommonView(RBaseViewHolder holder, int position, PoiItem bean) {
        holder.tv(R.id.title_view).setText(bean.getTitle());
        holder.tv(R.id.address_view).setText(getAddress(bean));
    }

    @Override
    protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, PoiItem bean) {
        ResUtil.setBgDrawable(holder.v(R.id.root_layout), SkinHelper.getSkin().getThemeTranMaskBackgroundSelector());
        holder.v(R.id.root_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectorPosition(position);
            }
        });

        if (isSelector) {
            onSelectorPosition(holder, position, isSelector);
        } else {
            onUnSelectorPosition(holder, position, isSelector);
        }
    }

    @Override
    protected boolean onSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        super.onSelectorPosition(viewHolder, position, isSelector);
        viewHolder.v(R.id.select_image_view).setVisibility(View.VISIBLE);
        viewHolder.tv(R.id.title_view).setTextColor(SkinHelper.getSkin().getThemeSubColor());
        viewHolder.tv(R.id.address_view).setTextColor(SkinHelper.getSkin().getThemeSubColor());

        if (mPoiItemListener != null) {
            mPoiItemListener.onPoiItemSelector(getAllDatas().get(position));
        }

        return true;
    }

    @Override
    protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
        super.onUnSelectorPosition(viewHolder, position, isSelector);
        viewHolder.v(R.id.select_image_view).setVisibility(View.INVISIBLE);
        int mainColor = R.color.base_text_color;
        int darkColor = R.color.base_text_color_dark;
        viewHolder.tv(R.id.title_view).setTextColor(mContext.getResources().getColor(mainColor));
        viewHolder.tv(R.id.address_view).setTextColor(mContext.getResources().getColor(darkColor));

        viewHolder.rtv(R.id.title_view).setHighlightWord(searchWord);
        viewHolder.rtv(R.id.address_view).setHighlightWord(searchWord);

        return true;
    }

    @Override
    protected void onBindNormalView(RBaseViewHolder holder, int position, PoiItem bean) {

    }

    @Override
    protected void onLoadMore() {
        super.onLoadMore();
        if (mPoiItemListener != null) {
            mPoiItemListener.onPoiLoadMore();
        }
    }

    public interface OnPoiItemListener {
        void onPoiItemSelector(PoiItem item);

        void onPoiLoadMore();
    }
}
