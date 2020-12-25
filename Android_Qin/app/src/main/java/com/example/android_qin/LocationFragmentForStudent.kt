package com.example.android_qin

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.ArrayMap
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragmentForStudent : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var mMapView = view?.findViewById<MapView>(R.id.map_for_student)
        var aMap: AMap?=null  //创建地图
        if (aMap == null) {
            aMap = mMapView?.map
        }
        var myLocationStyle: MyLocationStyle = MyLocationStyle() //初始化定位蓝点样式类//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
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
    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null
    //声明定位回调监听器
    var mLocationListener: AMapLocationListener? = null
    //声明AMapLocationClientOption对象
    var mLocationOption: AMapLocationClientOption? = null
    private val locationInfo = ArrayMap<String,String>()
    private fun getLocationInfo(){
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
                    locationInfo["studentLongitude"] = aMapLocation.longitude.toString()
                    locationInfo["studentLatitude"] = aMapLocation.latitude.toString()
                    Log.i("locationInfo",locationInfo.toString())
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.errorCode + ", errInfo:"
                            + aMapLocation.errorInfo)
                    Toast.makeText(context, "定位失败", Toast.LENGTH_LONG)
                }
            }
        }
        mLocationClient!!.setLocationListener(mLocationListener)
        //启动定位
        mLocationClient!!.startLocation()
        Log.d("getGPSInfo", "getGPSInfo")
    }
    private fun configSwipeRefresh(){
        val swipe= view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.location_swipe_for_student)
        swipe?.setOnRefreshListener {
            Log.i("swipe","location")
            refreshActivity()
            swipe.isRefreshing=false
        }
    }
    private fun refreshActivity(){
        val ip = getString(R.string.ip)
        val classId = arguments?.get("classId").toString()
        Thread{
            val url = "http://$ip:8080/activity/activityInProgress?classId=$classId"
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
                    Log.i("fail dialog reason in location",e.toString())
                    Log.i("ip in location",ip.toString())
                    Log.i("ip res id",getString(R.string.ip))
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
        loginFailDialog.setPositiveButton("确定") {
                dialog, id ->{}
        }
        return loginFailDialog
    }
    private fun dealWithResponseForRefreshActivity(response: StringBuilder?){
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val activityTitle = responseJson["activityTitle"].toString()
        val activityTitleTextView = view?.findViewById<SuperTextView>(R.id.activity_title)
        if(activityTitle == ""){
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("当前活动：无")
            }
        }
        else{
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("当前活动：$activityTitle")
            }
        }
    }
    private fun configSignBtn(){
        val signBtn= view?.findViewById<Button>(R.id.sign_btn_for_student)
        signBtn?.setOnClickListener {
            sign()
        }
    }
    private fun sign(){
        val ip = getString(R.string.ip)
        val studentId = arguments?.get("studentId").toString()
        val deviceId = getDeviceId()
        Log.i("deviceId",deviceId)
        Thread{
            val studentLongitude = locationInfo["studentLongitude"].toString()
            val studentLatitude = locationInfo["studentLatitude"].toString()
            Log.i("locationInfo in sign",studentLatitude)
            val url = "http://$ip:8080/sign/studentSign?studentId=$studentId&studentLongitude=$studentLongitude&studentLatitude=$studentLatitude&deviceId=$deviceId"
            val urlForGetSignData = URL(url)
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForGetSignData.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = ConnectionUtil.getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                Log.e("error in sign",e.toString())
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForSign(response)
        }.start()
    }
    private fun dealWithResponseForSign(response:StringBuilder?){
        val jsonString = response.toString()
        val responseJson = JSONObject(jsonString)
        val signState = responseJson["signState"] as Int
        if(signState == 0){
            activity?.runOnUiThread {
                Toast.makeText(context,"签到失败",Toast.LENGTH_LONG).show()
            }
        }
        else{
            activity?.runOnUiThread {
                Toast.makeText(context,"签到成功",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun getDeviceId():String{
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED){
            activity?.runOnUiThread {
                Toast.makeText(requireContext(),"no permission\nplease setting for the app",Toast.LENGTH_LONG).show()
            }
            return null.toString()
        }
        var deviceId= Settings.System.getString(activity?.contentResolver, Settings.System.ANDROID_ID);
        if(deviceId==null){
            Toast.makeText(requireContext(),"error",Toast.LENGTH_SHORT).show()
            return null.toString()
        }
        return deviceId.toString()
    }
    fun show_dialog(view: View) {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("Delete Dialog")
        dialog.setMessage("Do you really want to delete?")
        dialog.setPositiveButton("Yes") {
            dialog, id ->
            Toast.makeText(context, "The file has been deleted.",
                    Toast.LENGTH_LONG).show()
        }
        dialog.setNegativeButton("No") {
            dialog, id ->
            Toast.makeText(context, "not deleted.", Toast.LENGTH_LONG).show()
        }
        dialog.show()
    }
    override fun onStart() {
        super.onStart()
        refreshActivity()
    }

    override fun onResume() {
        super.onResume()
    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_for_student, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragmentForStudent().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}