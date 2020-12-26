package com.example.android_qin.sign

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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps2d.AMap
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.MyLocationStyle
import com.example.android_qin.R
import com.example.android_qin.util.ConnectionUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class LocationFragmentForStudent : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buildMap()
        mMapView?.onCreate(savedInstanceState)
        getLocationInfo()
        configSignBtn()
        configSwipeRefresh()
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.location_swipe_for_student)
        swipe?.setOnRefreshListener {
            refreshActivity()
            swipe.isRefreshing = false
        }
    }

    private fun refreshActivity() {
        Thread {
            buildRequestForRefreshActivity()
            try {
                response = ConnectionUtil.getDataByUrl(urlForRefreshActivity)
            } catch (e: Exception) {
                buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForRefreshActivity(response)
        }.start()
    }

    private fun dealWithResponseForRefreshActivity(response: StringBuilder?) {
        buildDataForRefreshActivity()
        if (activityTitle.toString() == "") {
            activity?.runOnUiThread {
                Log.i("refresh","no activity")
                activityTitleTextView?.setLeftString("暂无活动")
                activityTitleTextView?.setRightIcon(R.drawable.no_activity)
            }
        } else {
            Log.i("refresh","activity running")
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString(activityTitle.toString())
                Log.i("in refresh", activityTitle.toString())
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun configSignBtn() {
        val signBtn = view?.findViewById<Button>(R.id.sign_btn_for_student)
        signBtn?.setOnClickListener {
            sign()
        }
    }

    private fun sign() {
        Thread {
            buildRequestForSign()
            try {
                response = ConnectionUtil.getDataByUrl(urlForSign)
            } catch (e: Exception) {
                Log.e("error in sign", e.toString())
                buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForSign(response)
        }.start()
    }

    private fun dealWithResponseForSign(response: StringBuilder?) {
        val responseJson = JSONObject(response.toString())
        val signState = responseJson["signState"] as Int
        buildNavHost()
        if (signState == 0) {
            activity?.runOnUiThread {
                Toast.makeText(context, "签到失败", Toast.LENGTH_LONG).show()
            }
        } else {
            activity?.runOnUiThread {
                // page is waiting to build
                navController?.navigate(R.id.signStateForStudent)
            }
        }
    }

    private fun permissionCheck(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "no permission\nplease setting for the app",
                    Toast.LENGTH_LONG
                ).show()
            }
            return false
        }
        return true
    }

    private fun getDeviceId(): String? {
        if (!permissionCheck()) {
            return null
        }
        deviceId =
            Settings.System.getString(activity?.contentResolver, Settings.System.ANDROID_ID);
        if (deviceId == null) {
            Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
            return null
        }
        return deviceId.toString()
    }

    fun show_dialog(view: View) {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("Delete Dialog")
        dialog.setMessage("Do you really want to delete?")
        dialog.setPositiveButton("Yes") { dialog, id ->
            Toast.makeText(
                context, "The file has been deleted.",
                Toast.LENGTH_LONG
            ).show()
        }
        dialog.setNegativeButton("No") { dialog, id ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_for_student, container, false)
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
                    Log.d("定位成功", "定位成功")
                    locationInfo["studentLongitude"] = aMapLocation.longitude.toString()
                    locationInfo["studentLatitude"] = aMapLocation.latitude.toString()
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

    private fun buildConnectFailDialog() {
        if (loginFailDialog == null) {
            val loginFailDialog = AlertDialog.Builder(requireContext())
            loginFailDialog.setTitle("提示信息")
            loginFailDialog.setMessage("连接服务器失败")
            loginFailDialog.setPositiveButton("确定") { dialog, id ->
                {}
            }
        }
    }

    private fun buildMap() {
        mMapView = view?.findViewById<MapView>(R.id.map_for_student)
        var aMap: AMap? = null
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

    private fun buildRequestForRefreshActivity() {
        if (ip == null) {
            ip = getString(R.string.ip)
        }
        classId = arguments?.get("classId").toString()
        urlForRefreshActivity = URL("http://$ip:8080/activity/activityInProgress?classId=$classId")
    }

    private fun buildDataForRefreshActivity() {
        responseJson = JSONObject(response.toString())
        activityTitle = responseJson!!["activityTitle"].toString()
        if (activityTitleTextView == null) {
            activityTitleTextView = view?.findViewById(R.id.activity_title_for_student)
        }

    }

    private fun buildRequestForSign() {
        if (ip == null) {
            ip = getString(R.string.ip)
        }
        if (deviceId == null) {
            deviceId = getDeviceId()
        }
        classId = arguments?.get("classId").toString()
        studentLongitude = locationInfo["studentLongitude"].toString()
        studentLatitude = locationInfo["studentLatitude"].toString()
        studentId = arguments?.get("studentId").toString()
        urlForSign =
            URL("http://$ip:8080/sign/studentSign?studentId=$studentId&studentLongitude=$studentLongitude&studentLatitude=$studentLatitude&deviceId=$deviceId")
    }

    private fun buildNavHost() {
        if (navHostFragment == null) {
            navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_for_student) as NavHostFragment
            navController = navHostFragment?.navController
        }
    }

    var ip: String? = null
    private var deviceId: String? = null
    var responseJson: JSONObject? = null
    var activityTitle: String? = null
    var activityTitleTextView: SuperTextView? = null
    var classId: String? = null
    var studentLongitude: String? = null
    var studentLatitude: String? = null
    var studentId: String? = null
    var urlForRefreshActivity: URL? = null
    var urlForSign: URL? = null
    var connection: HttpURLConnection? = null
    var response: StringBuilder? = null
    var loginFailDialog: AlertDialog.Builder? = null
    var mMapView: MapView? = null
    var myLocationStyle: MyLocationStyle? = null
    var mLocationClient: AMapLocationClient? = null
    var mLocationListener: AMapLocationListener? = null
    var mLocationOption: AMapLocationClientOption? = null
    private val locationInfo = ArrayMap<String, String>()
    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LocationFragmentForStudent().apply {
                arguments = Bundle().apply {}
            }
    }
}