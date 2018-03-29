package com.angcyo.amap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.animation.Interpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.poisearch.PoiSearch;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/11 17:50
 * 修改人员：Robi
 * 修改时间：2017/01/11 17:50
 * 修改备注：
 * Version: 1.0.0
 */
public class AmapControl implements LocationSource, AMapLocationListener {

    //public static final String AMAP_TYPE = "010100|010101|010102|0503|0505|050900|0603|0604|070601|070603|070604|080601|0901|1001|120|140500|150104|150200|150500|1601|060100|060101|060102|060400|120000|150200|150500";
    public static final String AMAP_TYPE = "05|06|07|08|09|10|11|12|13||15|17|19";
    public static final String POI_TYPE = "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|22|97|99";
    //    public static final String POI_TYPE2 = "01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|22|97|99|" +
//            "190101|" +
//            "190102|" +
//            "190103";
    public static final String POI_TYPE2 = "190100|" +
            "190101|" +
            "190102|" +
            "190103|" +
            "190104|" +
            "190600|" +
            "190700";

    /**
     * 头像和背景偏移的距离
     */
    public static final int AVATAR_OFFSET = 8;
    /**
     * 头像的大小
     */
    public static final int AVATAR_SIZE = 70;
    //
//    public Marker addMarks(LatLng latLng, String url) {
//        if (TextUtils.isEmpty(url)) {
//            return null;
//        }
//
//        final Marker marker = map.addMarker(new MarkerOptions().position(latLng)
//                .icon(BitmapDescriptorFactory.fromBitmap(BmpUtil.getRoundedCornerBitmap(mContext, R.drawable.defauit_avatar_contact,
//                        mMarkerWidth, mMarkerHeight,
//                        R.drawable.touxiang_kuang_n, mMarkerWidth))));
//
//        marker.setClickable(true);
//
//        Glide.with(mContext)
//                .asBitmap()
//                .load(OssHelper.getImageThumb(url, mMarkerWidth, mMarkerWidth))
//                //.override(mMarkerWidth, mMarkerWidth)
//                .apply(RequestOptions.centerCropTransform())
////                    .transform(new GlideCircleTransform(mContext))
////                    .override(mMarkerWidth, mMarkerWidth)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        if (resource == null || resource.isRecycled()) {
//                            return;
//                        }
//                        if (marker != null) {
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(
////                                    BmpUtil.getRoundedCornerBitmap(resource, mMarkerWidth)
//                                    BmpUtil.getRoundedCornerBitmap(mContext, resource,
//                                            mMarkerWidth, mMarkerHeight,
//                                            R.drawable.touxiang_kuang_n, mMarkerWidth, AVATAR_OFFSET / 2)
//                            ));
//                            try {
//                                //startJumpAnimation(marker);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//
////            Rx.base(new Func1<String, Object>() {
////                @Override
////                public Object call(String s) {
////                    try {
////                        Bitmap myBitmap = Glide.with(mContext)
////                                .load(info.getAvatar())
////                                .asBitmap() //必须
////                                .centerCrop()
////                                //.transform(new GlideCircleTransform(mContext))...
////                                .into(mMarkerWidth, mMarkerWidth)
////                                .get();
////                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(BmpUtil.getRoundedCornerBitmap(myBitmap, mMarkerWidth)));
////
////                        /**开始动画*/
////                        //startJumpAnimation(marker);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    } catch (ExecutionException e) {
////                        e.printStackTrace();
////                    }
////
////                    return null;
////                }
////            });
//
////            startJumpAnimation(marker);
//        return marker;
//    }
//
//    public Marker addMarks(LatLng latLng) {
//        return addMarks(latLng, R.drawable.pin2);
//    }
//
//    /**
//     * 添加一个用来提示坐标的marks
//     */
//    public Marker addMarks(LatLng latLng, int drawableId) {
//        final Marker marker = map.addMarker(new MarkerOptions().position(latLng)
//                .icon(BitmapDescriptorFactory.fromResource(drawableId)));
//
//        //marker.setClickable(true);
//        return marker;
//    }
//
//    private void clearMarker() {
//        Set<Map.Entry<String, Marker>> entries = mMarkerMap.entrySet();
//        for (Map.Entry<String, Marker> entry : entries) {
//            entry.getValue().destroy();
//        }
//        mMarkerMap.clear();
//    }
//
    public static int DEFAULT_ZOOM_LEVEL = 15;
    private static PoiSearch sPoiSearch;
    OnLocationChangedListener mListener;
    AMapLocationClient mLocationClient;
    List<Marker> mMarkerList;
    /**
     * 保存用户id, 和对应的marker
     */
    Map<String, Marker> mMarkerMap;
    AMap map;
    Activity mContext;
    /**
     * 自己的位置
     */
    Marker myMarker;
    OnMarkerListener mOnMarkListener;
    /**
     * 是否显示自己
     */
    boolean showMyMarker = true;
    /**
     * 当前选中的marker
     */
    Marker selectorMarker;
    //Marker的标准大小, 和选中放大的增量
    private int mMarkerWidth, mMarkerHeight, mMarkerMaxOffset;
    private long lastSaveTime;
    /**
     * 当初次定位成功后, 需要移动到那个位置
     */
    private AmapBean needMoveToLocation;
    private boolean isMapLoadEnd = false;
    /**
     * 定位一次
     */
    private boolean locationOnce = false;

    public AmapControl(Activity context, final AMap map) {
        mContext = context;
        this.map = map;
        mMarkerList = new ArrayList<>();
        mMarkerMap = new HashMap<>();
        mMarkerWidth = (int) ResUtil.dpToPx(mContext, AVATAR_SIZE);
        mMarkerHeight = (int) ResUtil.dpToPx(mContext, AVATAR_SIZE + AVATAR_OFFSET);
        mMarkerMaxOffset = (int) ResUtil.dpToPx(mContext, 10);

        map.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                onMarkerSelector(marker);
                return true;
            }
        });
        map.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMarkerUnSelector();
            }
        });
    }

    public static Point getPointFromLanLng(final AMap aMap, LatLng latLng) {
        return aMap.getProjection().toScreenLocation(latLng);
    }

    public static void test() {
//        FutureTarget<File> future = Glide.with(mContext)
//                .load("url")
//                .downloadOnly(500, 500);
//        try {
//            File cacheFile = future.get();
//            String path = cacheFile.getAbsolutePath();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

//        Bitmap myBitmap = Glide.with(applicationContext)
//                .load(yourUrl)
//                .asBitmap() //必须
//                .centerCrop()
//                .into(500, 500)
//                .get();
    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 开始进行poi搜索(搜索附近的poi信息)
     */
    public static PoiSearch doSearchQuery(LatLng latLng, int currentPage, PoiSearch.OnPoiSearchListener listener) {
        return doSearchQuery(latLng, currentPage, listener, "", AMAP_TYPE);
    }

    public static PoiSearch doSearchQuery(LatLng latLng, int currentPage, PoiSearch.OnPoiSearchListener listener, String queryStr, String type) {
        PoiSearch.Query query = new PoiSearch.Query(queryStr, type, "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (sPoiSearch == null) {
            sPoiSearch = new PoiSearch(RApplication.getApp(), query);
        } else {
            sPoiSearch.setQuery(query);
        }
        sPoiSearch.setOnPoiSearchListener(listener);
        /**center - 该范围的中心点。
         radiusInMeters - 半径，单位：米。
         isDistanceSort - 是否按照距离排序。*/
        if (latLng != null) {
            //周边搜索
            sPoiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLng.latitude, latLng.longitude), 5000, true));//
        } else {
            sPoiSearch.setBound(null);//
        }
        // 设置搜索区域为以lp点为圆心，其周围5000米范围
        sPoiSearch.searchPOIAsyn();// 异步搜索
        return sPoiSearch;
    }

    public static MyLocationStyle createMyLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();

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

        myLocationStyle.interval(2000L);

        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.dingwei_icon));
        // 自定义精度范围的圆形边框颜色
//            myLocationStyle.strokeColor(SkinHelper.getSkin().getThemeTranColor(0x20));
        myLocationStyle.strokeColor(Color.TRANSPARENT);
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(SkinHelper.getSkin().getThemeTranColor(0x20));
        // 将自定义的 myLocationStyle 对象添加到地图上
        //end--------------------
        return myLocationStyle;
    }

    /**
     * 取消选中
     */
    public void unSelectorMarker(final Marker marker) {
        if (marker == null) {
            return;
        }
//        final LikeUserInfoBean userInfoBean = (LikeUserInfoBean) marker.getObject();
//        if (userInfoBean == null) {
//            return;
//        }
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Glide.with(mContext)
//                        .asBitmap()
//                        .load(OssHelper.getImageThumb(userInfoBean.getAvatar(), mMarkerWidth, mMarkerWidth))
//                        .apply(RequestOptions.centerCropTransform())
//                        .into(new SimpleTarget<Bitmap>() {
//                            @Override
//                            public void onLoadStarted(Drawable placeholder) {
//                                super.onLoadStarted(placeholder);
//                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(
//                                        BmpUtil.getRoundedCornerBitmap(mContext, R.drawable.defauit_avatar_contact,
//                                                mMarkerWidth, mMarkerHeight,
//                                                R.drawable.touxiang_kuang_n, mMarkerWidth)
//                                ));
//                            }
//
//                            @Override
//                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                                if (resource == null || resource.isRecycled()) {
//                                    return;
//                                }
//
//                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(
//                                        BmpUtil.getRoundedCornerBitmap(mContext, resource,
//                                                mMarkerWidth, mMarkerHeight,
//                                                R.drawable.touxiang_kuang_n, mMarkerWidth, AVATAR_OFFSET / 2)
//                                ));
//                            }
//                        });
//            }
//        };
//
//        if (RRealm.isMainThread()) {
//            runnable.run();
//        } else {
//            mContext.getWindow().getDecorView().post(runnable);
//        }
    }

    //
    public Marker addMarks(LatLng latLng) {
        final Marker marker = map.addMarker(new MarkerOptions().position(latLng));
//                .icon(BitmapDescriptorFactory.fromBitmap(BmpUtil.getRoundedCornerBitmap(mContext, R.drawable.defauit_avatar_contact,
//                        mMarkerWidth, mMarkerHeight,
//                        R.drawable.touxiang_kuang_n, mMarkerWidth))));
//
//        marker.setClickable(true);
//        marker.setObject(infoBean);
//        unSelectorMarker(marker);
        return marker;
    }

    /**
     * 初始化本地位置
     */
    public void initAmap(final AMap aMap, boolean showZoomControl, final LocationSource locationSource) {
        try {
            //自己的位置 圆点-------------
            // 自定义系统定位蓝点
            initAmap(aMap, showZoomControl, createMyLocationStyle(), locationSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化本地位置
     */
    public void initAmap(final AMap aMap, boolean showZoomControl, MyLocationStyle myLocationStyle, final LocationSource locationSource) {
        try {
            UiSettings uiSettings = aMap.getUiSettings();
            /**放大,缩小按钮,是否激活*/
            uiSettings.setZoomControlsEnabled(showZoomControl);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

            /**是否显示比例尺*/
            uiSettings.setScaleControlsEnabled(true);

            /**显示罗盘*/
            uiSettings.setCompassEnabled(true);

            //aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
            aMap.setLocationSource(locationSource);// 设置定位监听
            aMap.setMyLocationEnabled(true);
            //自己的位置 圆点-------------
            // 自定义系统定位蓝点
            aMap.setMyLocationStyle(myLocationStyle);
            //end--------------------

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMarkerSelector(Marker marker) {
        selectorMarker(marker);
    }

    private void onMarkerUnSelector() {
        if (mOnMarkListener != null) {
            mOnMarkListener.onMarkerUnSelector();
        }
    }

    public void setShowMyMarker(boolean showMyMarker) {
        this.showMyMarker = showMyMarker;
    }

    /**
     * 设置Marker监听事件
     */
    public void setOnMarkListener(OnMarkerListener onMarkListener) {
        mOnMarkListener = onMarkListener;
    }

//    public void addMarks(List<LikeUserInfoBean> userInfos) {
//        clearMarker();
//        onMarkerUnSelector();
//        Rx.from(userInfos)
//                .map(new Func1<LikeUserInfoBean, String>() {
//                    @Override
//                    public String call(LikeUserInfoBean likeUserIfoBean) {
//                        LatLng latLng = new LatLng(Double.valueOf(likeUserIfoBean.getLat()), Double.valueOf(likeUserIfoBean.getLng()));
//                        Marker marker = addMarks(latLng, likeUserIfoBean);
//                        mMarkerMap.put(likeUserIfoBean.getUid(), marker);
//                        return likeUserIfoBean.getUid();
//                    }
//                })
//                .compose(Rx.<String>transformer())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                });
//
//
////        for (final LikeUserInfoBean info : userInfos) {
////            LatLng latLng = new LatLng(Double.valueOf(info.getLat()), Double.valueOf(info.getLng()));
////            Marker marker = addMarks(latLng, info);
//////            marker.setObject(info.getUid());
//////            marker.setObject(info);
////            mMarkerMap.put(info.getUid(), marker);
////        }
//
////        /**开始动画*/
////        Set<Map.Entry<String, Marker>> entries = mMarkerMap.entrySet();
////        for (Map.Entry<String, Marker> entry : entries) {
////            startJumpAnimation(entry.getValue());
////            break;
////        }
//    }
//

    /**
     * 选中Marker, 并且高亮, 并自动滚动到Map位置
     */
    public void selectorMarker(final Marker marker) {
        unSelectorMarker();
        selectorMarker = marker;
        if (marker == null) {
            return;
        }
        final int width, height;
        width = mMarkerWidth + mMarkerMaxOffset;
        height = mMarkerHeight + mMarkerMaxOffset;
//        LikeUserInfoBean userInfoBean = (LikeUserInfoBean) marker.getObject();
//        marker.setToTop();
//        if (userInfoBean == null) {
//            return;
//        }
//        Glide.with(mContext)
//                .asBitmap()
//                .load(OssHelper.getImageThumb(userInfoBean.getAvatar(), width, height))
//                .apply(RequestOptions.centerCropTransform())
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        if (resource == null || resource.isRecycled()) {
//                            return;
//                        }
//                        if (marker == selectorMarker) {
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(
//                                    BmpUtil.getRoundedCornerBitmap(mContext, resource,
//                                            width, height,
//                                            R.drawable.touxiang_kuang_s, width, AVATAR_OFFSET / 2)
//                            ));
//                        }
//                    }
//                });
        moveToLocation(marker.getPosition());
    }

    public void selectorMarker(String uid) {
        selectorMarker(mMarkerMap.get(uid));
    }

    public void unSelectorMarker(String uid) {
        unSelectorMarker(mMarkerMap.get(uid));
    }

    public void unSelectorMarker() {
        unSelectorMarker(selectorMarker);
    }

    /**
     * 自己位置的marker
     */
    public void setMyMarker(LatLng latLng, String url) {
        if (!showMyMarker) {
            return;
        }

        if (myMarker != null) {
            myMarker.setPosition(latLng);
        } else {
            Marker marker = addMarks(latLng);
            //marker.setObject(UserCache.getUserAccount());
            this.myMarker = marker;
        }
    }

    public void moveToLocation(LatLng latlng, boolean anim) {
        moveToLocation(latlng, anim, DEFAULT_ZOOM_LEVEL);
    }

    public void moveToLocation(LatLng latlng, boolean anim, float zoom) {
//        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(
//                new CameraPosition(latlng, Constant.DEFAULT_ZOOM_LEVEL, 0, 0));
//        map.animateCamera(camera);
        if (anim) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
        }

        /**自己位置的marker*/
        //setMyMarker(latlng, UserCache.instance().getAvatar());
    }

    public void moveToLocation(AmapBean bean, boolean anim, float zoom) {
        moveToLocation(new LatLng(bean.latitude, bean.longitude), anim, zoom);
    }

    public void moveToLocation(LatLng latlng) {
        moveToLocation(latlng, true);
    }

    public void moveToLocation(AmapBean bean) {
        moveToLocation(bean, true);
    }

    public void moveToLocation(AmapBean bean, boolean anim) {
        moveToLocation(new LatLng(bean.latitude, bean.longitude), anim);
    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation(final Marker marker) {

        if (marker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = marker.getPosition();
            Point point = map.getProjection().toScreenLocation(latLng);
            if (point == null) {
                return;
            }
            point.y -= dip2px(mContext, 125);
            LatLng target = map.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            marker.setAnimation(animation);
            //开始动画

            T.mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    marker.startAnimation();
                }
            }, 300);

        } else {
            Log.e("ama", "screenMarker is null");
        }
    }

    public void setNeedMoveToLocation(AmapBean needMoveToLocation) {
        this.needMoveToLocation = needMoveToLocation;
    }

    public void setMapLoadEnd(boolean mapLoadEnd) {
        isMapLoadEnd = mapLoadEnd;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
//                setMyMarker(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()),
//                        UserCache.instance().getAvatar());
                if (System.currentTimeMillis() - lastSaveTime > 60 * 1000) {
                    RAmap.saveAmapLocation2(amapLocation);
                    lastSaveTime = System.currentTimeMillis();
                }
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                L.e("call: onLocationChanged([amapLocation])-> " + amapLocation);
            } else {
                String errText = "onLocationChanged 定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                L.e("AmapErr:" + errText);
            }
        }

        if (isMapLoadEnd && needMoveToLocation != null) {
            moveToLocation(needMoveToLocation, false);
            needMoveToLocation = null;
        }

        if (locationOnce) {
            if (mLocationClient != null) {
                mLocationClient.stopLocation();
                mLocationClient.onDestroy();
            }
            mLocationClient = null;
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(mContext);
            AMapLocationClientOption mLocationOption = RAmap.getDefaultOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    public void setLocationOnce(boolean locationOnce) {
        this.locationOnce = locationOnce;
    }

    public interface OnMarkerListener {

        /**
         * 选中用户
         */
        //void onMarkerSelector(LikeUserInfoBean userInfo);

        /**
         * 取消选择
         */
        void onMarkerUnSelector();
    }

}
