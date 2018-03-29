package com.angcyo.amap;

import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.angcyo.realm.RRealm;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.SwipeBackLayout;
import com.angcyo.uiview.utils.T_;

import io.realm.RealmResults;
import rx.functions.Action1;
import rx.functions.Action2;

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
public class AMapSelectLocationUIView extends UIContentView implements AMap.OnCameraChangeListener/*, LocationSource, AMapLocationListener*/ {

    //view
    TextureMapView mMapView;
    TextView mMarkerAddress;
    LinearLayout mLocationInfoLayout;
    AmapBean mLastBean, mTargetBean;
    int marksDrawableId = R.drawable.icon_hongbaoguangchang_gerendingwei;
    /**
     * 0-附近2公里 1-全国 2-全市
     */
    private LocationInfo mlocation = new LocationInfo();
    //amap
    private AMap mMap;
    private int mState = ViewDragHelper.STATE_IDLE;
    private AmapBean mOtherUserAmapBean;
    private boolean lockCameraChangedListener = false;
    /**
     * 最后选择的目标位置
     */
    private LatLng mLastTarget;
    private boolean mSend = true;//是否需要发送位置
    /**
     * 查看其它用户的头像
     */
    private String userUrl;
    private AmapControl mAmapControl;
    //
    private Action2<AmapBean, LocationInfo> mBeanAction1;

    public AMapSelectLocationUIView(Action2<AmapBean, LocationInfo> action) {
        this.mBeanAction1 = action;
    }

    public AMapSelectLocationUIView(boolean send, AmapBean otherUserAmapBean) {
        this.mSend = send;
        this.mOtherUserAmapBean = otherUserAmapBean;
    }


    public AMapSelectLocationUIView(Action2<AmapBean, LocationInfo> action, AmapBean otherUserAmapBean, LocationInfo locationInfo) {
        this(action, otherUserAmapBean, locationInfo, "");
    }

    public AMapSelectLocationUIView(Action2<AmapBean, LocationInfo> action, AmapBean otherUserAmapBean, LocationInfo locationInfo, String range_type) {
        this.mBeanAction1 = action;
        this.mOtherUserAmapBean = otherUserAmapBean;
        this.mlocation = locationInfo;
        if (TextUtils.isEmpty(mlocation.getArea_code())) {
            mlocation.setArea_code("0755");
        }
        if (TextUtils.isEmpty(mlocation.getCity())) {
            AmapBean amapBean = RAmap.getLastLocation();
            if (amapBean != null) {
                mlocation.setCity(amapBean.getCity());
            }
        }
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_amap_layout2);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        mMapView = v(R.id.map_view);
        mMarkerAddress = v(R.id.marker_address);
        mLocationInfoLayout = v(R.id.location_info_layout);
//        mViewHolder.tv(R.id.cb_near).setText(range_type.getDistance() / 1000 + "公里");

//        if (!range_type.getRange_type().contains("0")) {
//            mViewHolder.gone(R.id.cb_near);
//        }
//        if (!range_type.getRange_type().contains("1")) {
//            mViewHolder.gone(R.id.cb_country);
//        }
//        if (!range_type.getRange_type().contains("2")) {
//            mViewHolder.gone(R.id.cb_city);
//        }

//        mViewHolder.<RadioGroup>v(R.id.rg_group).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.cb_near) {
//                    mlocation.setState(1);
//                    mlocation.setRange_type("0");
//                } else if (checkedId == R.id.cb_city) {
//                    mlocation.setState(2);
//                    mlocation.setRange_type("2");
//                } else if (checkedId == R.id.cb_country) {
//                    mlocation.setState(3);
//                    mlocation.setRange_type("1");
//                }
//            }
//        });

        click(R.id.my_location, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyLocationClick();
            }
        });

        if (!mSend) {
            setTitleString("位置信息");
            mViewHolder.v(R.id.ll_action).setVisibility(View.GONE);
            mViewHolder.v(R.id.tv_select).setVisibility(View.GONE);
            mViewHolder.v(R.id.location_info_layout).setVisibility(View.GONE);
        } else {
//            if (mlocation != null) {
//                post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (!TextUtils.isEmpty(mlocation.getCity())) {
//                            mViewHolder.tv(R.id.tv_selectcity).setText(mlocation.getCity());
//                        }
//                        if (mlocation.getState() == 1) {
//                            mViewHolder.<RadioGroup>v(R.id.rg_group).check(R.id.cb_near);
//                        } else if (mlocation.getState() == 2) {
//                            mViewHolder.<RadioGroup>v(R.id.rg_group).check(R.id.cb_city);
//                        } else if (mlocation.getState() == 3) {
//                            mViewHolder.<RadioGroup>v(R.id.rg_group).check(R.id.cb_country);
//                        }
//                    }
//                });
//            }

            mViewHolder.click(R.id.tv_select, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlocation.getState() == 2 && TextUtils.isEmpty(mlocation.getArea_code())) {
                        T_.show("请选择城市");
                        return;
                    }
                    finishIView(new Runnable() {
                        @Override
                        public void run() {
                            if (mBeanAction1 != null) {
//                            if (mTargetBean == null) {
//                                mTargetBean = mOtherUserAmapBean;
//                            }
                                mBeanAction1.call(mTargetBean, mlocation);
                            }
                        }
                    });
                }
            });

//            AmapBean amapBean = RAmap.getLastLocation();
//            if (amapBean != null) {
//                mViewHolder.<TextView>v(R.id.tv_selectcity).setText(amapBean.getCity());
//            }

            mViewHolder.click(R.id.tv_selectcity, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CitySelectUIView.start(mILayout, new Action1<CitySelectControl.CityBean>() {
//                        @Override
//                        public void call(CitySelectControl.CityBean bean) {
//                            if (bean != null) {
////                            moveToLocation(new LatLng(bean.getH(), bean.longitude));
//                                mViewHolder.tv(R.id.tv_selectcity).setText(bean.getN());
//                                String cityName = bean.getN();
//                                mlocation.setCity(cityName);
//                                if ("全国".equals(cityName)) {
//                                    cityName = "";
//                                }
//                                mlocation.setArea_code(bean.getC());
//                                RAmap.instance().getLatlogByCity(cityName, new Action1<LatLng>() {
//                                    @Override
//                                    public void call(LatLng latLng) {
//                                        moveToLocation(latLng);
//                                        onCameraChangeTo(latLng);
//                                    }
//                                });
//                            }
//                        }
//                    });
                }
            });
        }

        mMapView.onCreate(null);
        initAmap();
    }

    @Override
    protected String getTitleString() {
        return "选择位置";
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
        if (mBeanAction1 != null) {
            mBeanAction1.call(null, null);
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
        mLastBean = RAmap.getLastLocation();
        moveToLocation(mLastBean);
    }

    private void initAmap() {
        mMap = mMapView.getMap();
        mAmapControl = new AmapControl(mActivity, mMap);
        mAmapControl.setShowMyMarker(false);
        mAmapControl.initAmap(mMap, true, mAmapControl);
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
        if (mOtherUserAmapBean == null) {
            RealmResults<AmapBean> all = RRealm.realm().where(AmapBean.class).findAll();
            if (all.size() > 0) {
                mLastBean = all.last();
                mOtherUserAmapBean = mLastBean;
                moveToLocation(mLastBean);
            } else {
                mOtherUserAmapBean = new AmapBean();
                mOtherUserAmapBean.latitude = 39.90923;
                mOtherUserAmapBean.longitude = 116.397428;
                mOtherUserAmapBean.address = "--";
                moveToLocation(mOtherUserAmapBean);
            }
        } else {
            //mAmapControl.addMarks(new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude), userUrl);
            if (marksDrawableId != -1) {
                //mAmapControl.addMarks(new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude), marksDrawableId);//显示坐标提示marks
            } else {
                mAmapControl.addMarks(new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude));//显示坐标提示marks
            }
            mAmapControl.setNeedMoveToLocation(mOtherUserAmapBean);
            postDelayed(300, new Runnable() {
                @Override
                public void run() {
                    mAmapControl.moveToLocation(mOtherUserAmapBean, false);
                    mTargetBean = mOtherUserAmapBean;
                }
            });
        }
        mMarkerAddress.setText(mOtherUserAmapBean.address);
        mLastTarget = new LatLng(mOtherUserAmapBean.latitude, mOtherUserAmapBean.longitude);
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
        mTargetBean = mLastBean;
        moveToLocation(new LatLng(bean.latitude, bean.longitude));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        onCameraChangeStart();
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        onCameraChangeTo(cameraPosition.target);
    }

    private void onCameraChangeStart() {
        mMarkerAddress.setText("正在拉取...");
        mTargetBean = null;
    }

    private void onCameraChangeTo(LatLng target) {
        mLastTarget = target;
        RAmap.instance().queryLatLngAddress(mLastTarget, new Action1<AmapBean>() {
            @Override
            public void call(AmapBean bean) {
                mTargetBean = bean;
                mMarkerAddress.setText(bean.address);
            }
        });
        if (!lockCameraChangedListener) {

        }
        lockCameraChangedListener = false;
    }

    public AMapSelectLocationUIView setMarksDrawableId(int marksDrawableId) {
        this.marksDrawableId = marksDrawableId;
        return this;
    }

    /**
     * "area_code":"0755",            // 地区码【显示范围为全市时必填】
     * "range_type":"0",              // 显示范围【0-默认 1-全国 2-全市】 默认0
     */
    public static class LocationInfo {
        private String area_code;
        private String range_type;
        private String city;
        private int state = 3;// 1 附近2公里  2 全市 3 全国

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea_code() {
            return area_code;
        }

        public void setArea_code(String area_code) {
            this.area_code = area_code;
        }

        public String getRange_type() {
            return range_type;
        }

        public void setRange_type(String range_type) {
            this.range_type = range_type;
        }
    }
}
