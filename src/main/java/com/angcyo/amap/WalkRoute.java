package com.angcyo.amap;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.angcyo.amap.overlay.AMapUtil;
import com.angcyo.amap.overlay.WalkRouteOverlay;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：步行路线
 * 创建人员：Robi
 * 创建时间：2018/03/29 15:26
 * 修改人员：Robi
 * 修改时间：2018/03/29 15:26
 * 修改备注：
 * Version: 1.0.0
 */
public class WalkRoute {
    Context mContext;
    AMap mAMap;
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
                                mContext, mAMap, walkPath,
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

    public WalkRoute(Context context, AMap AMap) {
        mContext = context;
        mAMap = AMap;
    }

    /**
     * 显示步行路线
     */
    public void showRoute(LatLng startPoint, LatLng targetLatLng /*目标地*/) {
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(startPoint.latitude, startPoint.longitude),
                new LatLonPoint(targetLatLng.latitude, targetLatLng.longitude));

        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
        RouteSearch mRouteSearch = new RouteSearch(mContext);
        mRouteSearch.setRouteSearchListener(mOnRouteSearchListener);
        mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
    }
}
