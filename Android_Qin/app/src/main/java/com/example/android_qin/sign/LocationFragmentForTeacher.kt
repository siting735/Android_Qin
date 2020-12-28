package com.example.android_qin.sign


import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.TeacherActivity
import com.example.android_qin.listener.OptionListener
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationFragmentForTeacher : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buildMap()
        mMapView?.onCreate(savedInstanceState)
        getLocationInfo()
        configSignBtn()
        configSwipeRefresh()
        refreshActivity()
    }

    private fun refreshActivity() {
        Thread {
            buildRequestForRefreshActivity()
            try {
                response = ConnectionUtil.getDataByUrl(urlForRefreshActivity)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForRefreshActivity(response)
        }.start()
    }

    private fun dealWithResponseForRefreshActivity(response: StringBuilder?) {
        buildDataForRefreshActivity()
        if (currentActivityTitle == "") {
            currentClassId = ""
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("暂无活动")
                activityTitleTextView?.setRightIcon(R.drawable.no_activity)
            }
        } else {
            currentClassId = MainActivity.responseJson!!["classId"].toString()
            currentActivityId = MainActivity.responseJson!!["activityId"].toString()
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString(currentActivityTitle.toString())
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<SwipeRefreshLayout>(R.id.location_swipe_for_teacher)
        swipe?.setOnRefreshListener {
            mLocationClient!!.startLocation()
            refreshActivity()
            swipe.isRefreshing = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configSignBtn() {
        val signBtn = view?.findViewById<Button>(R.id.sign_btn_for_teacher)
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        signBtn?.setOnClickListener {
            // NavUtil.navController?.navigate(R.id.signStateForTeacher)
            if (currentClassId == "") {
                selectClassForLaunch()
            } else {
                buildConfirmDialogForEndActivity()
                confirmDialogForEnd?.show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun endActivity() {
        Thread {
            buildRequestForEndActivity()
            try {
                response = ConnectionUtil.getDataByUrl(urlForEndActivity)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForEndActivity()
        }.start()
    }


    private fun dealWithResponseForEndActivity() {
        buildDataForEndActivity()
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        if (activityState == END_FAIL) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "结束活动失败", Toast.LENGTH_LONG).show()
            }
        } else {
            NavUtil.navController?.navigate(R.id.signStateForTeacher)
        }

    }

    private fun getLocationInfo() {
        mLocationClient = AMapLocationClient(context)
        mLocationOption = AMapLocationClientOption()
        mLocationOption!!.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport)
        if (null != mLocationClient) {
            mLocationClient!!.setLocationOption(mLocationOption)
            mLocationClient!!.stopLocation()
            mLocationClient!!.startLocation()
        }
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption!!.interval = 5000
        mLocationClient!!.setLocationOption(mLocationOption)
        mLocationOption!!.isMockEnable = true
        mLocationListener = AMapLocationListener { aMapLocation ->
            if (aMapLocation != null) {
                if (aMapLocation.errorCode == 0) {
                    mLocationClient!!.stopLocation()
                    Log.d("定位成功", "定位成功")
                    locationInfo["teacherLongitude"] = aMapLocation.longitude.toString()
                    locationInfo["teacherLatitude"] = aMapLocation.latitude.toString()
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

    private fun buildMap() {
        mMapView = view?.findViewById<MapView>(R.id.map_for_teacher)
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

    private fun buildRequestForEndActivity() {
        urlForEndActivity =
            URL("http://${MainActivity.ip}:8080/sign/changeSignToStop?classId=$currentClassId&activityId=$currentActivityId")
    }

    private fun configPickerBuilder() {
        optionsPickerBuilder =
            OptionsPickerBuilder(context, OptionListener(requireActivity(), requireContext()))
        optionsPickerBuilder!!.setTitleText("选择班级")
        optionsPickerBuilder!!.setDividerColor(Color.BLACK)
        optionsPickerBuilder!!.setTextColorCenter(Color.BLACK)
        optionsPickerBuilder!!.setContentTextSize(20)
        optionsPickerBuilder!!.isDialog(true)
    }

    private fun selectClassForLaunch() {
        configPickerBuilder()
        optionsPickerView = optionsPickerBuilder!!.build()
        addClassListToPicker()
        activity?.runOnUiThread {
            optionsPickerView!!.show()
        }
    }

    private fun addClassListToPicker() {
        val classList = ArrayList<String>()
        val classListJsonArray = TeacherActivity.classList
        val classListLength = TeacherActivity.classList!!.length()
        if (classListLength == 0) {
            activity?.runOnUiThread {
                Toast.makeText(context, "暂无班级", Toast.LENGTH_LONG).show()
            }
            return Unit
        }
        for (index in 0 until classListLength) {
            tempClassInfo = classListJsonArray!![index] as JSONObject
            classList.add(tempClassInfo!!["className"].toString())
        }
        optionsPickerView?.setPicker(classList)
    }

    @RequiresApi(Build.VERSION_CODES.O)

    private fun buildConfirmDialogForEndActivity() {
        if (confirmDialogForEnd == null) {
            confirmDialogForEnd = AlertDialog.Builder(context)
            confirmDialogForEnd?.setTitle("提示")
            confirmDialogForEnd?.setMessage("确认结束当前活动？")
            confirmDialogForEnd?.setPositiveButton("确定") { dialog, id ->
                endActivity()
            }
            confirmDialogForEnd?.setNegativeButton("取消") { dialog, id ->
                {}
            }
        }
    }

    private fun buildRequestForRefreshActivity() {
        if (activityTitleTextView == null) {
            activityTitleTextView = view?.findViewById(R.id.activity_title_for_teacher)
        }
        urlForRefreshActivity =
            URL("http://${MainActivity.ip}:8080/activity/searchActivityInProcess?teacherId=${TeacherActivity.teacherId}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_for_teacher, container, false)
    }

    private fun buildDataForRefreshActivity() {
        MainActivity.responseJson = JSONObject(response.toString())
        currentActivityTitle = MainActivity.responseJson!!["activityTitle"].toString()
        if (activityTitleTextView == null) {
            activityTitleTextView = view?.findViewById(R.id.activity_title_for_teacher)
        }
    }

    private fun buildDataForEndActivity() {
        MainActivity.responseJson = JSONObject(response.toString())
        activityState = MainActivity.responseJson!!["activityState"].toString()
    }

    var activityState: String? = null
    var currentClassId: String? = null
    var currentActivityId: String? = null
    var currentActivityTitle: String? = null
    var mMapView: MapView? = null
    var myLocationStyle: MyLocationStyle? = null
    var mLocationClient: AMapLocationClient? = null
    var mLocationListener: AMapLocationListener? = null
    var mLocationOption: AMapLocationClientOption? = null
    var urlForRefreshActivity: URL? = null
    var confirmDialogForEnd: AlertDialog.Builder? = null
    var urlForEndActivity: URL? = null
    var activityTitleTextView: SuperTextView? = null
    var optionsPickerBuilder: OptionsPickerBuilder? = null
    var tempClassInfo: JSONObject? = null

    companion object {
        const val LAUNCH_FAIL = "0"
        const val END_FAIL = "0"
        var response: StringBuilder? = null
        var optionsPickerView: OptionsPickerView<String>? = null
        val locationInfo = ArrayMap<String, String>()
    }
}