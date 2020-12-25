package com.example.android_qin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.NavHostFragment
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.xuexiang.xui.widget.edittext.PasswordEditText
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocationFragmentForTeacher : Fragment() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var mMapView = view?.findViewById<MapView>(R.id.map_for_teacher)
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        var aMap: AMap? = null
        if (aMap == null) {
            aMap = mMapView?.map
        }
        var myLocationStyle: MyLocationStyle =
            MyLocationStyle() //初始化定位蓝点样式类//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.showMyLocation(true)
        aMap!!.setMyLocationStyle(myLocationStyle) //设置定位蓝点的Style
        aMap.uiSettings.isMyLocationButtonEnabled = true//设置默认定位按钮是否显示，非必需设置。
        aMap!!.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) //连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        mMapView?.onCreate(savedInstanceState)
        getLocationInfo()
        configSignBtn()
        configSwipeRefresh()
    }

    private fun refreshActivity() {
        val ip = getString(R.string.ip)
        val teacherId = arguments?.get("teacherId").toString()
        Thread {
            val url = "http://$ip:8080/activity/searchActivityInProcess?teacherId=$teacherId"
            val urlForGetActivityInfo = URL(url)
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForGetActivityInfo.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = ConnectionUtil.getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                    Log.i("fail dialog reason in location", e.toString())
                    Log.i("ip in location", ip.toString())
                    Log.i("ip res id", getString(R.string.ip))
                }
                Thread.currentThread().join()
            }
            dealWithResponseForRefreshActivity(response)
        }.start()
    }

    private fun buildConnectFailDialog(): androidx.appcompat.app.AlertDialog.Builder {
        val loginFailDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        loginFailDialog.setTitle("提示信息")
        loginFailDialog.setMessage("连接服务器失败")
        loginFailDialog.setPositiveButton("确定") { dialog, id ->
            {}
        }
        return loginFailDialog
    }

    private fun dealWithResponseForRefreshActivity(response: StringBuilder?) {
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val activityTitle = responseJson["activityTitle"].toString()
        val activityTitleTextView = view?.findViewById<SuperTextView>(R.id.activity_title)
        if (activityTitle == "") {
            activity?.runOnUiThread {
                currentClassId = ""
                activityTitleTextView?.setLeftString("暂无活动")
                activityTitleTextView?.setRightIcon(R.drawable.no_activity)
            }
        } else {
            currentClassId = responseJson["classId"].toString()
            activityId = responseJson["activityId"].toString()
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("$activityTitle")
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.location_swipe_for_teacher)
        swipe?.setOnRefreshListener {
            Log.i("swipe", "location for teacher")
            swipe.isRefreshing = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun configSignBtn() {
        val signBtn = view?.findViewById<Button>(R.id.sign_btn_for_teacher)
        signBtn?.setOnClickListener {
            if (currentClassId == "") {
                launchSignActivityConfirm()
            } else {
                endSignActivityConfirm()
            }
            Log.i("sign in teacher", "i am in")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchSignActivityConfirm() {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("提示")
        dialog.setMessage("确认发起活动？")
        dialog.setPositiveButton("是的") { dialog, id ->
            launchActivity()
        }
        dialog.setNegativeButton("取消") { dialog, id ->
            {}
        }
        dialog.show()
    }

    private fun endSignActivityConfirm() {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("提示")
        dialog.setMessage("确认结束当前活动？")
        dialog.setPositiveButton("是的") { dialog, id ->
            endActivity()
        }
        dialog.setNegativeButton("取消") { dialog, id ->
            {}
        }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchActivity() {
        val ip = getString(R.string.ip)
        Thread {
            val teacherLongitude = locationInfo["teacherLongitude"]
            val teacherLatitude = locationInfo["teacherLatitude"]
            var urlForLogin: URL? = null
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val activityTitle = current.format(formatter)
            urlForLogin =
                URL("http://$ip:8080/activity/launchActivity?classId=$currentClassId&activityTitle=$activityTitle&teacherLongitude=$teacherLatitude&teacherLatitude=$teacherLatitude")
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForLogin.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = ConnectionUtil.getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                    Log.e("error in location", e.toString())
                    Log.i("fail dialog", "i am main")
                }
                Thread.currentThread().join()
            }
            dealWithResponseForLaunchActivity(response)
        }.start()
    }

    private fun endActivity() {
        Thread {
            val ip = getString(R.string.ip)
            var urlForLogin: URL? = null
            urlForLogin =
                URL("http://$ip:8080/sign/changeSignToStop?classId=$currentClassId&activityId=$activityId")
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForLogin.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = ConnectionUtil.getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                    Log.e("error in location", e.toString())
                    Log.i("fail dialog", "i am main")
                }
                Thread.currentThread().join()
            }
            dealWithResponseForEndActivity(response)
        }.start()
    }

    private fun dealWithResponseForLaunchActivity(response: StringBuilder?) {
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val activityState = responseJson["activityState"].toString()
        if (activityState == "0") {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "发布失败", Toast.LENGTH_LONG).show()
            }
        }
        val activityId = responseJson["activityId"].toString()
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), "发布成功", Toast.LENGTH_LONG).show()
        }
        refreshActivity()
    }

    private fun dealWithResponseForEndActivity(response: StringBuilder?) {
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val activityState = responseJson["activityState"].toString()
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
        val navController = navHostFragment.navController
        if (activityState == "0") {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "结束活动失败", Toast.LENGTH_LONG).show()
            }
        } else {
            // need bundle and page is waiting to build
            navController.navigate(R.id.signStateForTeacher)
        }

    }

    override fun onStart() {
        super.onStart()
        refreshActivity()
    }

    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null

    //声明定位回调监听器
    var mLocationListener: AMapLocationListener? = null

    //声明AMapLocationClientOption对象
    var mLocationOption: AMapLocationClientOption? = null
    private val locationInfo = ArrayMap<String, String>()
    private fun getLocationInfo() {
        mLocationClient = AMapLocationClient(context)
        mLocationOption = AMapLocationClientOption()
        mLocationOption!!.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport)
        if (null != mLocationClient) {
            mLocationClient!!.setLocationOption(mLocationOption)
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient!!.stopLocation()
            mLocationClient!!.startLocation()
        }
        //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
        mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption!!.interval = 5000
        //给定位客户端对象设置定位参数
        mLocationClient!!.setLocationOption(mLocationOption)
        mLocationOption!!.isMockEnable = true
        mLocationListener = AMapLocationListener { aMapLocation ->
            if (aMapLocation != null) {
                if (aMapLocation.errorCode == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    Log.d("定位成功", "定位成功")
                    locationInfo["teacherLongitude"] = aMapLocation.longitude.toString()
                    locationInfo["teacherLatitude"] = aMapLocation.latitude.toString()
                    Log.i("locationInfo", locationInfo.toString())
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
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
        //启动定位
        mLocationClient!!.startLocation()
        Log.d("getGPSInfo", "getGPSInfo")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    var currentClassId = ""
    var activityId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_for_teacher, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragmentForTeacher().apply {
                arguments = Bundle().apply {}
            }
    }
}