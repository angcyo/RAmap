package com.angcyo.amap;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

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
public class AmapBean extends RealmObject {
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
    @Ignore
    public boolean result = true;
    private int state = 1;// 1 附近2公里  2 全市 3 全国1

    public AmapBean() {
    }

    public AmapBean(boolean result) {
        this.result = result;
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
}
