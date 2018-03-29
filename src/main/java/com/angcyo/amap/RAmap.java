package com.angcyo.amap;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.UiThread;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.T_;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：高德定位 封包
 * 创建人员：Robi
 * 创建时间：2016/10/28 14:58
 * 修改人员：Robi
 * 修改时间：2016/10/28 14:58
 * 修改备注：
 * Version: 1.0.0
 */
public class RAmap {

    private static final String TAG = "RAmap";
    private static RAmap amap;
    private static SimpleDateFormat sdf = null;
    private static List<OnAmapLocationListener> listeners = new ArrayList<>();
    boolean mAutoStop = false;//定位成功后, 自动停止
    private GeocodeSearch geocoderSearch;
    private AMapLocationClient locationClient = null;
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(final AMapLocation location) {
            if (null != location) {
                StringBuilder sb = new StringBuilder();
                if (location.getErrorCode() == 0) {
                    //定位成功
                    sb.append(location.getProvince());
                    sb.append(location.getCity());
                    sb.append(location.getDistrict());
                    saveAmapLocation(location);

                    for (OnAmapLocationListener listener : listeners) {
                        listener.onLocationChanged(location);
                    }
                } else {
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");

                    L.e(sb.toString());

                    for (OnAmapLocationListener listener : listeners) {
                        listener.onLocationListener(null);
                    }
                }

                //解析定位结果
                String result = getLocationStr(location);
                L.i(TAG, "onLocationChanged: 定位" + result);
            } else {
                L.i(TAG, "onLocationChanged: 定位失败，location is null");

                for (OnAmapLocationListener listener : listeners) {
                    listener.onLocationListener(null);
                }
            }

            if (mAutoStop) {
                stopLocation();
            }
        }
    };
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private Context mContext;
    private AmapBean lastAmapBean = null;

    private RAmap(Context context) {
        mContext = context;
        geocoderSearch = new GeocodeSearch(context);
        initLocation();
    }

    public static RAmap instance() {
        if (amap == null) {
            throw new NullPointerException("请先调用init方法");
        }
        return amap;
    }

    /**
     * 返回数据库中最后一个位置信息
     */
    @UiThread
    public static AmapBean getLastLocation() {
        AmapBean bean = null;
//        RealmResults<AmapBeanRealm> all = RRealm.realm().where(AmapBeanRealm.class).findAll();
//                //.equalTo("uid", UserCache.getUserAccount()).findAll();
//        if (all.size() > 0) {
//            bean = all.last();
//        }
        return bean;
    }

    @UiThread
    public static AmapBean getLastLocationNotNull() {
        AmapBean bean = getLastLocation();
        if (bean == null) {
            bean = new AmapBean();
            bean.address = "天安门";
            bean.latitude = 116.32715863448607;
            bean.longitude = 39.990912172420714;
        }
        return bean;
    }

    public static AmapBean saveAmapLocation2(final AMapLocation location) {
        AmapBean amapBeanRealm = AmapBean.get(location);
        //RRealm.save(AmapBeanRealm);
        return amapBeanRealm;
    }

    public static void saveAmapLocation(final AMapLocation location) {
        AmapBean lastLocation = getLastLocation();
        boolean needUpload = true;
        if (lastLocation != null /*&& TextUtils.equals(lastLocation.getUid(), UserCache.getUserAccount())*/) {
            //与上一次距离相差太小时, 不上传位置

            //两个经纬度坐标点，计算这两个点间的直线距离，单位为米
            float distance = AMapUtils.calculateLineDistance(
                    new LatLng(location.getLatitude(), location.getLongitude()),
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));

            if (distance > 100f) {
                needUpload = true;
            } else {
                needUpload = false;
            }
        }

//        RRealm.exe(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                AmapBeanRealm AmapBeanRealm = realm.createObject(AmapBeanRealm.class);
////                AmapBeanRealm.setUid(UserCache.getUserAccount());
//                AmapBeanRealm.address = location.getAddress();
//                AmapBeanRealm.city = location.getCity();
//                AmapBeanRealm.country = location.getCountry();
//                AmapBeanRealm.district = location.getDistrict();
//                AmapBeanRealm.latitude = location.getLatitude();
//                AmapBeanRealm.longitude = location.getLongitude();
//                AmapBeanRealm.province = location.getProvince();
//                AmapBeanRealm.cityCode = location.getCityCode();
//
//                for (OnAmapLocationListener listener : listeners) {
//                    listener.onLocationListener(AmapBeanRealm);
//                }
//            }
//        });

//        try {
//            if (Integer.valueOf(UserCache.getUserAccount()) < 1000 ||
//                    TokenControl.INSTANCE.isTokenInvalid(null)) {
//                return;
//            }
//        } catch (Exception e) {
//            return;
//        }
//
//        if (needUpload) {
//            if (location.getLocationType()
//                    == AMapLocation.LOCATION_TYPE_GPS) {
//                return;
//            }
//            /**上报地理位置信息*/
//            Api.Companion.api(UserService.class)
//                    .location(Param.buildMap(
//                            "uid:" + UserCache.getUserAccount(),
//                            "lat:" + location.getLatitude(),
//                            "lng:" + location.getLongitude(),
//                            "province:" + location.getProvince(),
//                            "city:" + location.getCity(),
//                            "town:" + location.getDistrict(),
//                            "street:" + location.getStreet(),
//                            "address:" + location.getAddress(),
//                            "type:" +
//                                    (location.getLocationType()
//                                            == AMapLocation.LOCATION_TYPE_GPS ? "GPS" : "IP")
//                    ))
//                    .compose(Rx.transformer())
//                    .subscribe(new BaseSingleSubscriber<Object>() {
//                        @Override
//                        public void onError(int code, String msg) {
//
//                        }
//                    });
//        }
    }

    public static RAmap init(Context context) {
        if (amap == null) {
            synchronized (RAmap.class) {
                if (amap == null) {
                    amap = new RAmap(context.getApplicationContext());
                    //L.i(getSHA1(context));
                }
            }
        }
        return amap;
    }

    public static void stopLocation() {
        // 停止定位
        if (amap != null) {
            L.d("停止定位...");
            amap.stopLocationInternal();
        }
    }

    //------------------------------------无关内容---------------------------------------

    /**
     * 获取SHA1值
     */
    public static String getSHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据定位结果返回定位信息的字符串
     *
     * @param location
     * @return
     */
    public synchronized static String getLocationStr(AMapLocation location) {
        if (null == location) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            if (location.getProvider().equalsIgnoreCase(
                    android.location.LocationManager.GPS_PROVIDER)) {
                // 以下信息只有提供者是GPS时才会有
                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : "
                        + location.getSatellites() + "\n");
            } else {
                // 提供者是GPS时是没有以下信息的
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
                sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
            }
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        //定位之后的回调时间
        sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
        return sb.toString();
    }

    public synchronized static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    //---------------------------------------------------------------------------

    public static void startLocation(boolean autoStop) {
        if (amap == null) {
            L.e("请先调用init方法.");
            return;
        }
        L.d("开始定位...");
        amap.startLocationInner(autoStop);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public static AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();

        //Battery_Saving  Device_Sensors  Hight_Accuracy
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式

        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        // 设置是否开启缓存
        mOption.setLocationCacheEnable(true);

        return mOption;
    }

    public static void addOnAmapLocationListener(OnAmapLocationListener listener) {
        listeners.add(listener);
    }

    public static void removeOnAmapLocationListener(OnAmapLocationListener listener) {
        listeners.remove(listener);
    }

    public static void release() {
        listeners.clear();
    }

    public static LatLng getLatLngFromBean(AmapBean bean) {
        return new LatLng(bean.getLatitude(), bean.getLongitude());
    }

    public static void setMarkerIcons(Marker marker, int... resId) {
        ArrayList<BitmapDescriptor> icos = new ArrayList<>();
        for (int id : resId) {
            icos.add(BitmapDescriptorFactory.fromResource(id));
        }
        marker.setIcons(icos);
        //设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快。默认是20
        marker.setPeriod(2);
        marker.setZIndex(200);
    }

    public void queryLatLngAddress(final LatLng latlng, final Action1<AmapBean> action1) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latlng.latitude, latlng.longitude),
                200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == AMapException.CODE_AMAP_SUCCESS) {
                    if (regeocodeResult != null) {
                        RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();
                        if (address != null && address.getFormatAddress() != null) {
                            AmapBean bean = new AmapBean();
                            bean.address = address.getFormatAddress();
                            bean.cityCode = address.getCityCode();
                            bean.province = address.getProvince();
                            bean.city = address.getCity();
                            bean.district = address.getDistrict();
                            bean.latitude = latlng.latitude;
                            bean.longitude = latlng.longitude;
                            bean.title = address.getTownship();
                            action1.call(bean);
                        } else {
                            action1.call(null);
                        }
                    } else {
                        action1.call(null);
                    }
                } else {
                    action1.call(null);
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocationInner(boolean autoStop) {
        mAutoStop = autoStop;
        // 设置定位参数
        //locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocationInternal() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    public void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        amap = null;
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(mContext);
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    public AmapBean getLastAmapBean() {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation();
        }
        return lastAmapBean;
    }

    public Double getLatitude() {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation();
        }
        if (lastAmapBean == null) {
            return 116.32715863448607;
        }
        return lastAmapBean.getLatitude();
    }

    public Double getLongitude() {
        if (lastAmapBean == null) {
            lastAmapBean = RAmap.getLastLocation();
        }
        if (lastAmapBean == null) {
            return 39.990912172420714;
        }
        return lastAmapBean.getLongitude();
    }

    public void getLatlogByCity(String cityName, final Action1<LatLng> action) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                if (i == 1000) {
                    if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null &&
                            geocodeResult.getGeocodeAddressList().size() > 0) {

                        GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                        double latitude = geocodeAddress.getLatLonPoint().getLatitude();//纬度
                        double longititude = geocodeAddress.getLatLonPoint().getLongitude();//经度
                        String adcode = geocodeAddress.getAdcode();//区域编码
                        LatLng latLng = new LatLng(latitude, longititude);
                        if (action != null) {
                            action.call(latLng);
                        }
                        L.e("地理编码", geocodeAddress.getAdcode() + "");
                        L.e("纬度latitude", latitude + "");
                        L.e("经度longititude", longititude + "");
                    } else {
                        T_.show("地址名出错");
                    }
                }
            }
        });
        GeocodeQuery geocodeQuery = new GeocodeQuery(cityName.trim(), ""/*"29"*/);
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    public static abstract class OnAmapLocationListener {
        /**
         * @param AmapBeanRealm 定位失败 为null
         */
        public void onLocationListener(AmapBean AmapBeanRealm) {

        }

        public void onLocationChanged(AMapLocation location) {

        }
    }
}
