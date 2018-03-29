package com.angcyo.amap;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/24 10:02
 * 修改人员：Robi
 * 修改时间：2016/12/24 10:02
 * 修改备注：
 * Version: 1.0.0
 */

public class AmapBean {
    //经    度
    public double longitude;
    //纬    度
    public double latitude;

    public String country;//国家
    public String province;//省
    public String city;//市
    public String district;//区
    public String address;//地址
    public String cityCode; // 城市编码
    public String title;
    public String uid = "0";//那个用户的数据

    public boolean result = true;
    private int state = 1;// 1 附近2公里  2 全市 3 全国1

    public AmapBean() {
    }

    public AmapBean(boolean result) {
        this.result = result;
    }

    public static AmapBean get(final AMapLocation location) {
        AmapBean amapBeanRealm = new AmapBean();
        amapBeanRealm.address = location.getAddress();
        amapBeanRealm.city = location.getCity();
        amapBeanRealm.country = location.getCountry();
        amapBeanRealm.district = location.getDistrict();
        amapBeanRealm.latitude = location.getLatitude();
        amapBeanRealm.longitude = location.getLongitude();
        amapBeanRealm.province = location.getProvince();
        amapBeanRealm.cityCode = location.getCityCode();
        return amapBeanRealm;
    }

    public static AmapBean get(PoiItem item) {
        AmapBean amapBean = new AmapBean();
        LatLonPoint latLonPoint = item.getLatLonPoint();

        amapBean.title = item.getTitle();
        amapBean.latitude = latLonPoint.getLatitude();
        amapBean.longitude = latLonPoint.getLongitude();
        amapBean.address = PoiItemAdapter.getAddress(item);
        amapBean.city = item.getCityName();
        amapBean.province = item.getProvinceName();
        amapBean.district = item.getAdName();
        return amapBean;
    }

    public static AmapBean get(Location location) {
        AmapBean amapBeanRealm = new AmapBean();
        Bundle bundle = location.getExtras();
        /**
         * Bundle[{Accuracy=29.0, Street=, Country=中国, adcode=440305, citycode=0755, CityCode=0755,
         * Province=广东省, locationType=2, City=深圳市, desc=广东省 深圳市 南山区 科润路 39号 靠近华润置地大厦E座 ,
         * Floor=, Speed=0.0, errorCode=0, errorInfo=success, District=南山区,
         * Address=广东省深圳市南山区科润路39号靠近华润置地大厦E座,
         * AoiName=, PoiName=, Bearing=0.0, BuildingId=, StreetNum=, AdCode=440305, Altitude=0.0}]*/

        amapBeanRealm.address = bundle.getString("Address");
        amapBeanRealm.city = bundle.getString("City");
        amapBeanRealm.country = bundle.getString("Country");
        amapBeanRealm.district = bundle.getString("District");
        amapBeanRealm.latitude = location.getLatitude();
        amapBeanRealm.longitude = location.getLongitude();
        amapBeanRealm.province = bundle.getString("Province");
        amapBeanRealm.cityCode = bundle.getString("CityCode");
        return amapBeanRealm;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longitude);
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        if (TextUtils.isEmpty(title)) {
            return address;
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getString() {
        StringBuilder builder = new StringBuilder();
        builder.append("经度:");
        builder.append(longitude);
        builder.append(" ");
        builder.append("纬度:");
        builder.append(latitude);
        builder.append(" ");
        builder.append(country);
        builder.append(" ");
        builder.append(province);
        builder.append(" ");
        builder.append(city);
        builder.append(" ");
        builder.append(district);
        builder.append(" ");
        builder.append(address);
        builder.append(" ");
        return builder.toString();
    }

    @Override
    public String toString() {
        return getString();
    }
}
