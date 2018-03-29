package com.angcyo.amap;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.angcyo.github.utilcode.utils.SingleTextWatcher;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.AnimParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;

import rx.functions.Action1;

import static com.angcyo.amap.AmapControl.POI_TYPE2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：搜索添加好友
 * 创建人员：Robi
 * 创建时间：2016/12/26 9:04
 * 修改人员：Robi
 * 修改时间：2016/12/26 9:04
 * 修改备注：
 * Version: 1.0.0
 */
public class SearchPOIUIView extends UIContentView {

    //view
    ExEditText mSearchInputView;
    TextView mSearchTipView;
    LinearLayout mSearchControlLayout;
    RRecyclerView mRecyclerView;
    TextView mEmptyTipView;
    AmapBean mLastBean, mTargetBean;
    //adapter
    private PoiItemAdapter mPoiAdapter;
    //latlng
    private LatLng latLng;
    //onDataLoadListener
    private PoiSearch.OnPoiSearchListener onPoiSearchListener;

    //action
    private Action1<AmapBean> action;

    //str
    private String query;
    private String type = POI_TYPE2;

    private int mCurrentPage = 1;

    /**
     * 关闭周边搜索
     */
    private boolean closeBoundSearch = false;

    public SearchPOIUIView(Action1<AmapBean> action, LatLng latLng) {
        this.latLng = latLng;
        this.action = action;
    }

    public SearchPOIUIView setCloseBoundSearch(boolean closeBoundSearch) {
        this.closeBoundSearch = closeBoundSearch;
        return this;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_search_layout);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return null;
    }

    @Override
    public Animation loadLayoutAnimation(AnimParam animParam) {
        return null;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mSearchInputView = v(R.id.edit_text_view);
        mSearchTipView = v(R.id.search_tip_view);
        mSearchControlLayout = v(R.id.search_control_layout);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyTipView = v(R.id.empty_tip_view);

        click(R.id.search_tip_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchTipClick();
            }
        });

        mPoiAdapter = new PoiItemAdapter(mActivity, new PoiItemAdapter.OnPoiItemListener() {
            @Override
            public void onPoiItemSelector(com.amap.api.services.core.PoiItem item) {
                // 选中
                AmapBean amapBean = new AmapBean();
                LatLonPoint latLonPoint = item.getLatLonPoint();

                amapBean.title = item.getTitle();
                amapBean.latitude = latLonPoint.getLatitude();
                amapBean.longitude = latLonPoint.getLongitude();
                amapBean.address = PoiItemAdapter.getAddress(item);
                amapBean.city = item.getCityName();
                amapBean.province = item.getProvinceName();
                amapBean.district = item.getAdName();
                mTargetBean = amapBean;

//                mILayout.finishIView(AmapUIView.class);
                // 销毁ampauiview

                //销毁uiview
                finishIView();
                if (action != null) {
                    action.call(mTargetBean);
                }
            }

            @Override
            public void onPoiLoadMore() {
                mCurrentPage++;
                if (closeBoundSearch) {
                    onSearch(null, mCurrentPage, onPoiSearchListener, query, type);
                } else {
                    onSearch(latLng, mCurrentPage, onPoiSearchListener, query, type);
                }
            }
        });

        mPoiAdapter.setEnableLoadMore(false);
        onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int code) {
                if (code != 1000) {
                    return;
                }
                mPoiAdapter.searchWord = query;
                if (mCurrentPage == 1) {
                    mPoiAdapter.unSelectorAll(false);
                    mPoiAdapter.resetData(poiResult.getPois());
                } else {
                    mPoiAdapter.appendData(poiResult.getPois());
                }

                mPoiAdapter.setLoadMoreEnd();

                mEmptyTipView.setVisibility(View.GONE);
                if (poiResult.getPois().size() == 0) {
                    mPoiAdapter.setEnableLoadMore(false);
                    if (mPoiAdapter.isItemEmpty()) {
                        mEmptyTipView.setVisibility(View.VISIBLE);
                        mEmptyTipView.setText("该地点不存在");
                    }
                }
            }

            @Override
            public void onPoiItemSearched(com.amap.api.services.core.PoiItem poiItem, int i) {
                L.e("call: onPoiItemSearched([poiItem, i])-> ");
            }
        };

        mRecyclerView.setAdapter(mPoiAdapter);

        mViewHolder.v(R.id.title_bar_layout).setBackgroundColor(SkinHelper.getSkin().getThemeColor());
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishIView();
            }
        });

        mSearchInputView.setHint("搜索");

        RSoftInputLayout.showSoftInput(mSearchInputView);

        mSearchInputView.addTextChangedListener(new SingleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                super.onTextChanged(charSequence, start, before, count);
                if (TextUtils.isEmpty(charSequence)) {
                    mEmptyTipView.setText("");
                } else {
                    query = charSequence.toString();
                    mCurrentPage = 1;
                    if (closeBoundSearch) {
                        onSearch(null, mCurrentPage, onPoiSearchListener, charSequence.toString(), type);
                    } else {
                        onSearch(latLng, mCurrentPage, onPoiSearchListener, charSequence.toString(), type);
                    }
                }
            }
        });
    }

    private void onSearch(LatLng latLng, int currentPage, PoiSearch.OnPoiSearchListener onPoiSearchListener, String query, String type) {
        AmapControl.doSearchQuery(latLng, currentPage, onPoiSearchListener, query, type);
    }

    @Override
    public int getDefaultBackgroundColor() {
        return mActivity.getResources().getColor(R.color.default_base_bg_dark2);
    }

    @NonNull
    @Override
    protected UIBaseView.LayoutState getDefaultLayoutState() {
        return UIBaseView.LayoutState.CONTENT;
    }


    /**
     * 开始搜索地点
     */
    public void onSearchTipClick() {
        if (closeBoundSearch) {
            onSearch(null, 1, onPoiSearchListener, query, type);
        } else {
            onSearch(latLng, 1, onPoiSearchListener, query, type);
        }
    }
}
