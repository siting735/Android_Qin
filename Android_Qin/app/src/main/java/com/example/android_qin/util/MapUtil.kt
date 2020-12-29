package com.example.android_qin.util

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.sign.LocationFragmentForTeacher

class MapUtil {
    companion object {
        fun getLocationInfo(context: Context?, activity:FragmentActivity?) {
            mLocationClient = AMapLocationClient(context)
            mLocationOption = AMapLocationClientOption()
            mLocationOption!!.locationPurpose =
                AMapLocationClientOption.AMapLocationPurpose.Transport
            if (null != mLocationClient) {
                mLocationClient!!.setLocationOption(mLocationOption)
                mLocationClient!!.stopLocation()
                mLocationClient!!.startLocation()
            }
            mLocationOption!!.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationOption!!.interval = 9999999
            mLocationClient!!.setLocationOption(mLocationOption)
            mLocationOption!!.isMockEnable = true
            mLocationListener = AMapLocationListener { aMapLocation ->
                if (aMapLocation != null) {
                    if (aMapLocation.errorCode == 0) {
                        mLocationClient!!.stopLocation()
                        Log.d("定位成功", "定位成功")
                        if (MainActivity.identity == MainActivity.STUDENT){
                            locationInfo["studentLongitude"] =
                                aMapLocation.longitude.toString()
                            locationInfo["studentLatitude"] =
                                aMapLocation.latitude.toString()
                        }
                        else{
                            locationInfo["teacherLongitude"] =
                                aMapLocation.longitude.toString()
                            locationInfo["teacherLatitude"] =
                                aMapLocation.latitude.toString()
                        }
                        Log.i("locationInfo", locationInfo.toString())
                    } else {
                        Log.e(
                            "AmapError", "location Error, ErrCode:"
                                    + aMapLocation.errorCode + ", errInfo:"
                                    + aMapLocation.errorInfo
                        )
                        Toast.makeText(context, "定位失败", Toast.LENGTH_LONG)
                    }
                }
            }
            mLocationClient!!.setLocationListener(mLocationListener)
            mLocationClient!!.startLocation()
        }

        fun buildMap(view: View?) {
            mMapView = if(MainActivity.identity == MainActivity.STUDENT){
                view?.findViewById(R.id.map_for_student)
            } else{
                view?.findViewById(R.id.map_for_teacher)
            }
            var aMap: AMap? = null  //创建地图
            if (aMap == null) {
                aMap = mMapView?.map
            }
            myLocationStyle =
                MyLocationStyle()
            myLocationStyle?.interval(2000)
            myLocationStyle?.showMyLocation(true)
            aMap!!.setMyLocationStyle(myLocationStyle)
            aMap.uiSettings.isMyLocationButtonEnabled = true
            aMap!!.isMyLocationEnabled = true
            myLocationStyle?.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW)
        }

        var mMapView: MapView? = null
        var myLocationStyle: MyLocationStyle? = null
        var mLocationClient: AMapLocationClient? = null
        var mLocationListener: AMapLocationListener? = null
        var mLocationOption: AMapLocationClientOption? = null
        val locationInfo = ArrayMap<String, String>()
    }
}