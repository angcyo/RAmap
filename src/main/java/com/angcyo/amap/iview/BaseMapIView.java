package com.angcyo.amap.iview;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.angcyo.amap.AmapBean;
import com.angcyo.amap.R;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIContentView;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/03/29 13:46
 * 修改人员：Robi
 * 修改时间：2018/03/29 13:46
 * 修改备注：
 * Version: 1.0.0
 */
public class BaseMapIView extends UIContentView implements AMap.OnMyLocationChangeListener,
        AMap.OnMapLoadedListener, AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnCameraChangeListener {
    public static final String TAG = "BaseMapIView";

    protected TextureMapView mTextureMapView;
    protected AMap mAMap;
    /**
     * 蓝点定位成功的位置
     */
    protected AmapBean myLocationAmapBean;
    protected long onMyLocationChangeCount = 0;
    protected boolean isMapLoaded = false;

    @Override
    protected void inflateContentLayout(@NonNull ContentLayout baseContentLayout, @NonNull LayoutInflater inflater) {
        inflate(R.layout.base_amap_layout);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mTextureMapView = v(R.id.base_map_view);
        mTextureMapView.onCreate(null);
        mAMap = mTextureMapView.getMap();
        initAmap();
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        checkPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        onPermissionsResult(aBoolean);
                    }
                });
    }

    /**
     * 权限状态返回
     */
    protected void onPermissionsResult(Boolean aBoolean) {
        if (aBoolean) {
            //RAmap.startLocation(true);
        } else {
            T_.error("请打开定位权限.");
        }
    }

    @Override
    public void onViewShow(Bundle bundle, Class<?> fromClz) {
        super.onViewShow(bundle, fromClz);
        mTextureMapView.onResume();
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
        mTextureMapView.onPause();
    }

    @Override
    public void onViewUnload(UIParam uiParam) {
        super.onViewUnload(uiParam);
        mTextureMapView.onDestroy();
    }

    public void moveToLocation(double latitude, double longitude) {
        LatLng latlng = new LatLng(latitude, longitude);
        moveToLocation(latlng);
    }

    public void moveToLocation(LatLng latlng) {
        moveToLocation(latlng, mAMap.getCameraPosition().zoom /*DEFAULT_ZOOM_LEVEL*/, true);
    }

    public void moveToLocation(AmapBean amapBean) {
        if (amapBean != null) {
            moveToLocation(amapBean.latitude, amapBean.longitude);
        }
    }

    public void moveToLocation(LatLng latlng, float zoom) {
        moveToLocation(latlng, zoom, true);
    }

    public void moveToLocation(LatLng latlng, float zoom, boolean anim) {
        if (latlng == null) {
            return;
        }
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, zoom /*目标可视区域的缩放级别*/,
                0 /*倾斜度，以角度为单位*/, 0 /*可视区域指向的方向, 从正北向逆时针方向计算，从0 度到360 度。*/));
        if (anim) {
            mAMap.animateCamera(camera);
        } else {
            mAMap.moveCamera(camera);
        }
    }

    protected void addMarker(LatLng latLng, int resId) {
        mAMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(resId)));
    }

    //首次初始化入口
    protected void initAmap() {
        //缩放级别范围为[3, 20]
        mAMap.setMaxZoomLevel(20);
        mAMap.setMinZoomLevel(3);

        initAmapUI();
        initAmapGestures();
        initAmapLocation();
        //有了以上设置, 地图可以正常显示和定位了

        //监听定位坐标的改变
        initListener();
        initAmapSource();
    }

    /**
     * Amap地图上UI控制
     */
    protected void initAmapUI() {
        UiSettings uiSettings = mAMap.getUiSettings();
        /**放大,缩小按钮,是否激活*/
        uiSettings.setZoomControlsEnabled(true);
        //缩放按钮所在的位置
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
        //是否显示默认的定位按钮
        uiSettings.setMyLocationButtonEnabled(true);
        /**是否显示比例尺*/
        uiSettings.setScaleControlsEnabled(true);
        /**显示罗盘, 指南针*/
        uiSettings.setCompassEnabled(true);
        //Logo的位置
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
    }

    /**
     * 地图手势
     */
    protected void initAmapGestures() {
        UiSettings uiSettings = mAMap.getUiSettings();
        //缩放手势
        uiSettings.setZoomGesturesEnabled(true);

        //滑动手势
        uiSettings.setScrollGesturesEnabled(true);

        //旋转手势
        uiSettings.setRotateGesturesEnabled(true);

        //倾斜手势
        uiSettings.setTiltGesturesEnabled(true);

        //所有手势
        uiSettings.setAllGesturesEnabled(true);
    }

    /**
     * Amap定位相关初始化
     */
    protected void initAmapLocation() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_hongbaoguangchang_gerendingwei));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(SkinHelper.getSkin().getThemeSubColor());
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(4);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(SkinHelper.getSkin().getThemeTranColor(80));

        /**
         * http://lbs.amap.com/api/android-sdk/guide/create-map/mylocation
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
         //以下三种模式从5.1.0版本开始提供
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
         * */
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);

        myLocationStyle.interval(700L);

        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);

        // 是否可触发定位并显示定位层
        mAMap.setMyLocationEnabled(true);
    }

    protected void initAmapSource() {
        //高版本的高德地图已经不需要此方式了
        //mAMap.setLocationSource(this);
    }

    protected void initListener() {
        mAMap.setOnMyLocationChangeListener(this);
        mAMap.setOnMapLoadedListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnMapClickListener(this);
        mAMap.setOnCameraChangeListener(this);
    }

    /**
     * 我的位置正确返回
     */
    protected void onMyLocationChange(AmapBean amapBean) {
        onMyLocationChangeCount++;
    }

    @Override
    public void onMyLocationChange(Location location) {
        //latitude=22.542125#longitude=113.953601#province=广东省#city=深圳市#district=南山区#
        // cityCode=0755#adCode=440305#address=广东省深圳市南山区科润路39号靠近华润置地大厦E座#country=中国#road=科润路#
        // poiName=#street=#streetNum=#aoiName=#poiid=#floor=#errorCode=0#errorInfo=success#locationDetail=#csid:e1b55f92341c4b3591b99b2bf4aad887#locationType=2
        //监听我的位置变化
        //L.e(TAG, "call: onMyLocationChange([location])-> " + location);

        if (location != null) {
            myLocationAmapBean = AmapBean.get(location);
            onMyLocationChange(myLocationAmapBean);

            //Log.d(TAG, myLocationAmapBean.toString());
//            Bundle bundle = location.getExtras();
//            /**
//             * Bundle[{Accuracy=29.0, Street=, Country=中国, adcode=440305, citycode=0755, CityCode=0755,
//             * Province=广东省, locationType=2, City=深圳市, desc=广东省 深圳市 南山区 科润路 39号 靠近华润置地大厦E座 ,
//             * Floor=, Speed=0.0, errorCode=0, errorInfo=success, District=南山区,
//             * Address=广东省深圳市南山区科润路39号靠近华润置地大厦E座,
//             * AoiName=, PoiName=, Bearing=0.0, BuildingId=, StreetNum=, AdCode=440305, Altitude=0.0}]*/
//            if (bundle != null) {
//                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
//                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
//
//                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
//                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
//                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
//            } else {
//                Log.e("amap", "定位信息， bundle is null ");
//            }
        } else {
            Log.e(TAG, "onMyLocationChange 定位失败");
        }
    }

    @Override
    public void onMapLoaded() {
        isMapLoaded = true;
        L.i(TAG, "call: onMapLoaded([])-> ");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        L.i(TAG, "call: onMapClick([latLng])-> " + latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        L.i(TAG, "call: onMarkerClick([marker])-> " + marker + " :" + marker.getObject());
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        L.i(TAG, "call: onCameraChange([cameraPosition])-> " + cameraPosition);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        L.i(TAG, "call: onCameraChangeFinish([cameraPosition])-> " + cameraPosition);
    }
}
