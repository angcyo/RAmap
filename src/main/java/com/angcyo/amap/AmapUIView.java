package com.angcyo.amap;

import android.Manifest;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.angcyo.amap.overlay.AMapUtil;
import com.angcyo.amap.overlay.WalkRouteOverlay;
import com.angcyo.amap.view.HnMapRootLayout;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.SwipeBackLayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarItem;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.EmptyView;

import java.util.ArrayList;

import rx.functions.Action1;

import static com.angcyo.amap.AmapControl.DEFAULT_ZOOM_LEVEL;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/27 10:06
 * 修改人员：Robi
 * 修改时间：2016/12/27 10:06
 * 修改备注：
 * Version: 1.0.0
 */
public class AmapUIView extends UIContentView implements AMap.OnCameraChangeListener/*, LocationSource, AMapLocationListener*/ {

    //view
    TextureMapView mMapView;
    TextView mMarkerAddress;
    LinearLayout mLocationInfoLayout;
    AmapBean mLastBean, mTargetBean;
    RRecyclerView mRecyclerView;
    EmptyView mEmptyView;
    RelativeLayout mBottomLayout;
    RelativeLayout mSearchView;
    int marksDrawableId = -1;

    private WalkRouteResult mWalkRouteResult;
    RouteSearch.OnRouteSearchListener mOnRouteSearchListener = new RouteSearch.OnRouteSearchListener() {
        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
            //公交换乘路径规划结果的回调方法
        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
            //驾车路径规划结果的回调方法
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
            //步行路径规划结果的回调方法
            if (errorCode == com.amap.api.services.core.AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getPaths() != null) {
                    if (result.getPaths().size() > 0) {
                        mWalkRouteResult = result;
                        final WalkPath walkPath = mWalkRouteResult.getPaths()
                                .get(0);
                        WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                                mActivity, mMapView.getMap(), walkPath,
                                mWalkRouteResult.getStartPos(),
                                mWalkRouteResult.getTargetPos());
                        walkRouteOverlay.removeFromMap();
                        walkRouteOverlay.addToMap();
                        walkRouteOverlay.zoomToSpan();
                        //mBottomLayout.setVisibility(View.VISIBLE);
                        int dis = (int) walkPath.getDistance();
                        int dur = (int) walkPath.getDuration();
                        String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                        //mRotueTimeDes.setText(des);
//                        //mRouteDetailDes.setVisibility(View.GONE);
//                        mBottomLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext,
//                                        WalkRouteDetailActivity.class);
//                                intent.putExtra("walk_path", walkPath);
//                                intent.putExtra("walk_result",
//                                        mWalkRouteResult);
//                                startActivity(intent);
//                            }
//                        });
                    } else if (result != null && result.getPaths() == null) {
//                        ToastUtil.show(mContext, R.string.no_result);
                    }
                } else {
//                    ToastUtil.show(mContext, R.string.no_result);
                }
            }
        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {
            //骑行路径规划结果的回调方法
        }
    };
    //amap
    private AMap mMap;
    //    private OnLocationChangedListener mListener;
//    private AMapLocationClient mLocationClient;
    private Action1<AmapBean> mOnSelector;
    private int mState = ViewDragHelper.STATE_IDLE;
    private boolean mSend = false;//是否需要发送位置
    private AmapBean mOtherUserAmapBean;
    private int mCurrentPage = 1;
    private PoiItemAdapter mPoiItemAdapter;
    private boolean lockCameraChangedListener = false;
    /**
     * 最后选择的目标位置
     */
    private LatLng mLastTarget;
    /**
     * 查看其它用户的头像
     */
    private String userUrl;
    private AmapControl mAmapControl;
    private boolean isCallAction = false;
    //
    private String titleTextString, sendButtonText;
    /**
     * 是否强制需要选中兴趣点, 如果为true, 那么会默认选中poi搜索结果中的第一个位置, 否则不会返回
     */
    private boolean needSelectorPoi = false;
    /**
     * 是否需要显示步行路线
     */
    private boolean showRoute = false;

    /**
     * 选择位置
     */
    public AmapUIView(Action1<AmapBean> onSelector) {
        mOnSelector = onSelector;
        mSend = true;
    }

    /**
     * 查看位置
     */
    public AmapUIView(AmapBean otherUserAmapBean) {
        mOtherUserAmapBean = otherUserAmapBean;
        mSend = false;
    }

    public AmapUIView(Action1<AmapBean> onSelector, AmapBean otherUserAmapBean, String url, boolean send) {
        mOtherUserAmapBean = otherUserAmapBean;
        userUrl = url;
        mSend = send;
        mOnSelector = onSelector;
    }

    /**
     * 刚进来显示最后一次的位置
     */
    public void setLastBean(AmapBean lastBean) {
        mLastBean = lastBean;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_amap_layout);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        mMapView = v(R.id.map_view);
        mMarkerAddress = v(R.id.marker_address);
        mLocationInfoLayout = v(R.id.location_info_layout);
        mRecyclerView = v(R.id.recycler_view);
        mEmptyView = v(R.id.empty_view);
        mBottomLayout = v(R.id.bottom_layout);
        mSearchView = v(R.id.layout_search_view);

        mSearchView.setGravity(Gravity.START);

        click(R.id.my_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyLocationClick();
            }
        });
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

        mBottomLayout.setVisibility(mSend ? View.VISIBLE : View.GONE);
        mMapView.onCreate(null);
        initAmap();

        if (mSend) {
            initBottomLayout();
        } else {
            setTitleString("位置信息");
            ((HnMapRootLayout) v(R.id.root_layout)).setEnableScale(false);
            v(R.id.layout_search_view).setVisibility(View.GONE);
            v(R.id.bottom_layout).setVisibility(View.GONE);
            v(R.id.location_pin).setVisibility(View.GONE);
            v(R.id.location_info_layout).setVisibility(View.GONE);
//            v(R.id.bottom_layout).setLayoutParams(new LinearLayout.LayoutParams(0, 0, 0));
//            v(R.id.map_root_layout).setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1));
        }
    }

    private void initBottomLayout() {
        mPoiItemAdapter = new PoiItemAdapter(mActivity, new PoiItemAdapter.OnPoiItemListener() {
            @Override
            public void onPoiItemSelector(PoiItem item) {
                AmapBean AmapBeanRealm = new AmapBean();
                LatLonPoint latLonPoint = item.getLatLonPoint();
                lockCameraChangedListener = true;

                AmapBeanRealm.title = item.getTitle();
                AmapBeanRealm.latitude = latLonPoint.getLatitude();
                AmapBeanRealm.longitude = latLonPoint.getLongitude();
                AmapBeanRealm.address = PoiItemAdapter.getAddress(item);
                AmapBeanRealm.city = item.getCityName();
                AmapBeanRealm.province = item.getProvinceName();
                AmapBeanRealm.district = item.getAdName();
                mTargetBean = AmapBeanRealm;

                mMarkerAddress.setText(AmapBeanRealm.address);
                moveToLocation(latLonPoint.getLatitude(), latLonPoint.getLongitude());
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

    public String getTitleTextString() {
        if (TextUtils.isEmpty(titleTextString)) {
            return "我的位置";
        }
        return titleTextString;
    }

    public void setTitleTextString(String titleTextString) {
        this.titleTextString = titleTextString;
    }

    public String getSendButtonText() {
        if (TextUtils.isEmpty(sendButtonText)) {
            return "确定";
        }
        return sendButtonText;
    }

    public void setSendButtonText(String sendButtonText) {
        this.sendButtonText = sendButtonText;
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarItem> items = new ArrayList<>();
        if (mSend) {
            items.add(TitleBarItem.build()
                    .setText(getSendButtonText()).setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mTargetBean == null) {
                                T_.show("还未获取到有效位置信息.");
                            } else {
                                if (needSelectorPoi && TextUtils.isEmpty(mTargetBean.title)) {
                                    T_.show("请选择位置信息.");
                                    return;
                                }

//                    finishIView(AmapUIView.this, new UIParam(true, false));
                                isCallAction = true;

                                finishIView(AmapUIView.this, new UIParam(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnSelector != null) {
                                            mOnSelector.call(mTargetBean);
                                        }
                                    }
                                }));
                                //T_.show(mTargetBean.address);
                            }
                        }
                    }));
        }
        return super.getTitleBar().setTitleString(getTitleTextString()).setShowBackImageView(true).setRightItems(items);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        mActivity.checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    //RAmap.startLocation(true);
                } else {
                    T_.error("必要的权限被禁用了.");
                }
            }
        });
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        if (mILayout instanceof SwipeBackLayout) {
            ((SwipeBackLayout) mILayout).setOnPanelSlideListener(new SwipeBackLayout.OnPanelSlideListener() {
                @Override
                public void onStateChanged(int state) {
                    mState = state;
                    setCameraChangeListener();
                }

                @Override
                public void onRequestClose() {

                }

                @Override
                public void onRequestOpened() {

                }

                @Override
                public void onSlideChange(float percent) {

                }
            });
        }
        mMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                initLocation();
                searchLastPosition();
                mAmapControl.setMapLoadEnd(true);
            }
        });
        mMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    setCameraChangeListener();
                }
            }
        });
    }

    @Override
    public void onViewUnload() {
        super.onViewUnload();
        mAmapControl.deactivate();
        if (mILayout instanceof SwipeBackLayout) {
            ((SwipeBackLayout) mILayout).setOnPanelSlideListener(null);
        }
    }

    @Override
    public void onViewUnloadDelay() {
        super.onViewUnloadDelay();
        mMapView.onDestroy();
        if (!isCallAction && mOnSelector != null) {
            mOnSelector.call(null);
        }
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mMapView.onPause();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        mMapView.onResume();
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        //首次进入, 移动到最后一次坐标位置
        postDelayed(100, new Runnable() {
            @Override
            public void run() {
                onMyLocationClick();
            }
        });
    }

    public void onMyLocationClick() {
        //mLastBean = RAmap.getLastLocation();
        moveToLocation(RAmap.getLastLocation());
    }

    private void initAmap() {
        mMap = mMapView.getMap();
        mAmapControl = new AmapControl(mActivity, mMap);
        mAmapControl.setShowMyMarker(false);
//        mAmapControl.initAmap(mMap, true, mAmapControl);
        MyLocationStyle myLocationStyle = AmapControl.createMyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        mAmapControl.initAmap(mMap, true, myLocationStyle, mAmapControl);
        //mAmapControl.setNeedMoveToLocation(mOtherUserAmapBean);

//        try {
//            mMap = mMapView.getMap();
//
//            UiSettings uiSettings = mMap.getUiSettings();
//            uiSettings.setZoomControlsEnabled(true);
//            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
//
//            mMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//            mMap.setLocationSource(this);// 设置定位监听
//            mMap.setMyLocationEnabled(true);
//            //自己的位置 圆点-------------
//            // 自定义系统定位蓝点
//            MyLocationStyle myLocationStyle = new MyLocationStyle();
//            // 自定义定位蓝点图标
//            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
//                    fromResource(R.drawable.gps_point));
//            // 自定义精度范围的圆形边框颜色
//            myLocationStyle.strokeColor(mActivity.getResources().getColor(R.color.colorAccent));
//            //自定义精度范围的圆形边框宽度
//            myLocationStyle.strokeWidth(5);
//            // 设置圆形的填充颜色
//            myLocationStyle.radiusFillColor(mActivity.getResources().getColor(R.color.theme_color_primary_dark_tran2));
//            // 将自定义的 myLocationStyle 对象添加到地图上
//            mMap.setMyLocationStyle(myLocationStyle);
//            //end--------------------
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void setCameraChangeListener() {
        if (mState == ViewDragHelper.STATE_IDLE) {
            mMap.setOnCameraChangeListener(this);
        } else {
            mMap.setOnCameraChangeListener(null);
        }
    }

    /**
     * 定位到最后一次的位置
     */
    private void initLocation() {
//        if (mOtherUserAmapBean == null) {
//            if (mLastBean != null) {
//                mOtherUserAmapBean = mLastBean;
//                moveToLocation(mLastBean);
//            } else {
//                mOtherUserAmapBean = new AmapBeanRealm();
//                mOtherUserAmapBean.latitude = 39.90923;
//                mOtherUserAmapBean.longitude = 116.397428;
//                mOtherUserAmapBean.address = "天安门";
//                moveToLocation(mOtherUserAmapBean);
//            }
//        } else {
//            //mAmapControl.addMarks(new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude), userUrl);
//            if (marksDrawableId != -1) {
//                //mAmapControl.addMarks(new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude), marksDrawableId);//显示坐标提示marks
//            } else {
//                mAmapControl.addMarks(new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude));//显示坐标提示marks
//            }
//            mAmapControl.setNeedMoveToLocation(mOtherUserAmapBean);
//            postDelayed(300, new Runnable() {
//                @Override
//                public void run() {
//                    mAmapControl.moveToLocation(mOtherUserAmapBean, false);
//                    if (showRoute) {
//                        //规划步行路线
//                        showRoute(RAmap.getLatLngFromBean(mOtherUserAmapBean));
//                    }
//                }
//            });
//        }
//        mMarkerAddress.setText(mOtherUserAmapBean.address);
//        mLastTarget = new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude);
//        if (!mSend) {
//            moveToLocation(mOtherUserAmapBean);
//        }
    }

    private void showRoute(LatLng targetLatLng /*目标地*/) {
        LatLng startPoint = RAmap.getLatLngFromBean(RAmap.getLastLocationNotNull());
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(startPoint.latitude, startPoint.longitude),
                new LatLonPoint(targetLatLng.latitude, targetLatLng.longitude));

        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        RouteSearch mRouteSearch = new RouteSearch(mActivity);
        mRouteSearch.setRouteSearchListener(mOnRouteSearchListener);
        mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }

    private void moveToLocation(double latitude, double longitude) {
        LatLng latlng = new LatLng(latitude, longitude);
        moveToLocation(latlng);
    }

    private void moveToLocation(LatLng latlng) {
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, DEFAULT_ZOOM_LEVEL, 0, 0));
        mMap.animateCamera(camera);
    }

    private void moveToLocation(AmapBean bean) {
        if (bean == null) {
            return;
        }
        mTargetBean = mLastBean;
        moveToLocation(new LatLng(bean.latitude, bean.longitude));
    }

    /**
     * 导航到指定位置
     */
    private void naviTo(LatLng toLatlng) {

        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(toLatlng);
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
        try {
            // 调起高德地图导航
            AMapUtils.openAMapNavi(naviPara, mActivity);
        } catch (AMapException e) {
            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(mActivity);
        }
        mMap.clear();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mMarkerAddress.setText("正在获取...");
        if (needSelectorPoi) {
        } else {
            mTargetBean = null;
        }
        mCurrentPage = 1;
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
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

//
//    /**
//     * 定位成功后回调函数
//     */
//    @Override
//    public void onLocationChanged(AMapLocation amapLocation) {
//        if (mListener != null && amapLocation != null) {
//            if (amapLocation != null
//                    && amapLocation.getErrorCode() == 0) {
//
//                mLastBean = RAmap.saveAmapLocation2(amapLocation);
//
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//            } else {
//                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
//                L.e("AmapErr:" + errText);
//            }
//        }
//    }
//
//    /**
//     * 激活定位
//     */
//    @Override
//    public void activate(OnLocationChangedListener onDataLoadListener) {
//        mListener = onDataLoadListener;
//        if (mLocationClient == null) {
//            mLocationClient = new AMapLocationClient(mActivity);
//            AMapLocationClientOption mLocationOption = RAmap.getDefaultOption();
//            //设置定位监听
//            mLocationClient.setLocationListener(this);
//            //设置为高精度定位模式
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //设置定位参数
//            mLocationClient.setLocationOption(mLocationOption);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            mLocationClient.startLocation();
//        }
//    }
//
//    /**
//     * 停止定位
//     */
//    @Override
//    public void deactivate() {
//        mListener = null;
//        if (mLocationClient != null) {
//            mLocationClient.stopLocation();
//            mLocationClient.onDestroy();
//        }
//        mLocationClient = null;
//    }

    private void searchLastPosition() {
        if (mLastTarget == null || !mSend) {
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

    public AmapUIView setNeedSelectorPoi(boolean needSelectorPoi) {
        this.needSelectorPoi = needSelectorPoi;
        return this;
    }

    public AmapUIView setMarksDrawableId(int marksDrawableId) {
        this.marksDrawableId = marksDrawableId;
        return this;
    }

    public AmapUIView setShowRoute(boolean showRoute) {
        this.showRoute = showRoute;
        return this;
    }
}
