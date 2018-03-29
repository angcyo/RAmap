package com.angcyo.amap.iview;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.angcyo.amap.AmapBean;
import com.angcyo.amap.AmapControl;
import com.angcyo.amap.PoiItemAdapter;
import com.angcyo.amap.R;
import com.angcyo.amap.RAmap;
import com.angcyo.amap.SearchPOIUIView;
import com.angcyo.amap.view.HnMapRootLayout;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.EmptyView;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/03/29 15:18
 * 修改人员：Robi
 * 修改时间：2018/03/29 15:18
 * 修改备注：
 * Version: 1.0.0
 */
public class RAmapUIView extends BaseMapIView {
    //view
    TextView mMarkerAddress;
    LinearLayout mLocationInfoLayout;
    AmapBean mTargetBean;
    RRecyclerView mRecyclerView;
    EmptyView mEmptyView;
    RelativeLayout mBottomLayout;
    RelativeLayout mSearchView;
    /**
     * 是否是选择位置模式,否则就是查看位置
     */
    boolean isSelectorLocation = true;
    /**
     * 查看位置显示的marker资源
     */
    int lookMarkerDrawableId = -1;
    private int mCurrentPage = 1;
    private boolean lockCameraChangedListener = false;
    private PoiItemAdapter mPoiItemAdapter;
    /**
     * 最后选择的目标位置
     */
    private LatLng mLastTarget;
    /**
     * 是否强制需要选中兴趣点, 如果为true, 那么会默认选中poi搜索结果中的第一个位置, 否则不会返回
     */
    private boolean needSelectorPoi = false;
    private Action1<AmapBean> onLocationSelector;
    /**
     * 查看位置信息
     */
    private LatLng mLookTarget;

    public RAmapUIView(LatLng lookTarget, int lookMarkerDrawableId) {
        this.lookMarkerDrawableId = lookMarkerDrawableId;
        mLookTarget = lookTarget;
        isSelectorLocation = false;
    }

    public RAmapUIView(LatLng lookTarget) {
        this(lookTarget, R.drawable.dingwei_icon);
    }

    public RAmapUIView() {
    }

    public RAmapUIView(Action1<AmapBean> onLocationSelector) {
        this.onLocationSelector = onLocationSelector;
        isSelectorLocation = true;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarItem> items = new ArrayList<>();
        if (isSelectorLocation) {
            items.add(TitleBarItem.build("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTargetBean == null) {
                        T_.error("请选择位置");
                    } else {
                        finishIView(new Runnable() {
                            @Override
                            public void run() {
                                if (onLocationSelector != null) {
                                    onLocationSelector.call(mTargetBean);
                                }
                            }
                        });
                    }
                }
            }));
        }
        return super.getTitleBar().setTitleString("选择位置").setRightItems(items);
    }

    public RAmapUIView setOnLocationSelector(Action1<AmapBean> onLocationSelector) {
        this.onLocationSelector = onLocationSelector;
        return this;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_amap_layout);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        mMarkerAddress = v(R.id.marker_address);
        mLocationInfoLayout = v(R.id.location_info_layout);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyView = v(R.id.empty_view);
        mBottomLayout = v(R.id.bottom_layout);
        mSearchView = v(R.id.layout_search_view);

        //不显示我的位置, 使用高德自带的
        mViewHolder.gone(R.id.my_location);

        click(R.id.my_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyLocationClick();
            }
        });
        mBottomLayout.setVisibility(isSelectorLocation ? View.VISIBLE : View.GONE);
        if (isSelectorLocation) {
            mSearchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new SearchPOIUIView(new Action1<AmapBean>() {
                        @Override
                        public void call(AmapBean bean) {
                            mTargetBean = bean;
                            mLastTarget = new LatLng(bean.getLatitude(), bean.getLongitude());
                            mMarkerAddress.setText(bean.address);
                            moveToLocation(bean.getLatitude(), bean.getLongitude());
                            searchLastPosition();
                        }
                    }, mLastTarget).setCloseBoundSearch(true));
                }
            });
            initBottomLayout();
        } else {
            //查看位置
            setTitleString("位置信息");
            ((HnMapRootLayout) v(R.id.root_layout)).setEnableScale(false);
            v(R.id.layout_search_view).setVisibility(View.GONE);
            v(R.id.bottom_layout).setVisibility(View.GONE);
            v(R.id.location_pin).setVisibility(View.GONE);
            v(R.id.location_info_layout).setVisibility(View.GONE);
        }
    }

    private void initBottomLayout() {
        mPoiItemAdapter = new PoiItemAdapter(mActivity, new PoiItemAdapter.OnPoiItemListener() {
            @Override
            public void onPoiItemSelector(PoiItem item) {
                lockCameraChangedListener = true;
                mTargetBean = AmapBean.get(item);

                mMarkerAddress.setText(mTargetBean.address);
                moveToLocation(mTargetBean.getLatitude(), mTargetBean.getLongitude());
            }

            @Override
            public void onPoiLoadMore() {
                mCurrentPage++;
                searchLastPosition();
            }
        });

        mRecyclerView.setItemAnim(false);
        mRecyclerView.setAdapter(mPoiItemAdapter);
        UI.setVerticalThumbDrawable(mRecyclerView, new ColorDrawable(SkinHelper.getSkin().getThemeSubColor()));
    }

    private void searchLastPosition() {
        if (mLastTarget == null || !isSelectorLocation) {
            return;
        }
        if (mCurrentPage < 1) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        AmapControl.doSearchQuery(mLastTarget, mCurrentPage, new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int code) {
                if (code != 1000) {
                    return;
                }

                if (mCurrentPage == 1) {
                    mPoiItemAdapter.unSelectorAll(false);
                    mPoiItemAdapter.resetData(poiResult.getPois());

                    if (needSelectorPoi) {
                        mPoiItemAdapter.setSelectorPosition(0);
                    }
                } else {
                    mPoiItemAdapter.appendData(poiResult.getPois());
                }

                mPoiItemAdapter.setLoadMoreEnd();

                mEmptyView.setVisibility(View.GONE);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int code) {
                L.e("" + code);
            }
        });
    }

    public void onMyLocationClick() {
        moveToLocation(myLocationAmapBean);
    }

    /**
     * 是否是查看位置信息
     */
    private boolean isLookLocation() {
        return !isSelectorLocation;
    }

    @Override
    public void onMapLoaded() {
        super.onMapLoaded();
        if (mLookTarget != null) {
            addMarker(mLookTarget, lookMarkerDrawableId);
            moveToLocation(mLookTarget);
        }
    }

    @Override
    protected void onMyLocationChange(AmapBean amapBean) {
        super.onMyLocationChange(amapBean);
        if (!isLookLocation() && onMyLocationChangeCount <= 5 && isMapLoaded) {
            mTargetBean = amapBean;
            //第一次定位成功, 移动到目标点, 之后不处理
            moveToLocation(amapBean);
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        super.onCameraChange(cameraPosition);
        if (isLookLocation()) {
            return;
        }
        mMarkerAddress.setText("正在获取...");
        if (needSelectorPoi) {
        } else {
            //mTargetBean = null;
        }
        mCurrentPage = 1;
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (isLookLocation()) {
            return;
        }
        mLastTarget = cameraPosition.target;
        RAmap.instance().queryLatLngAddress(mLastTarget, new Action1<AmapBean>() {
            @Override
            public void call(AmapBean bean) {
                if (needSelectorPoi) {
                } else {
                    mTargetBean = bean;
                }
                mMarkerAddress.setText(bean.address);
            }
        });
        if (!lockCameraChangedListener) {
            searchLastPosition();
        }
        lockCameraChangedListener = false;
    }
}
