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
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.MapUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class LocationFragmentForStudent : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        MapUtil.buildMap(view)
        MapUtil.mMapView?.onCreate(savedInstanceState)
        configSignBtn()
        configSwipeRefresh()
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.location_swipe_for_student)
        swipe?.setOnRefreshListener {
            MapUtil.mLocationClient?.startLocation()
            refreshActivity()
            swipe.isRefreshing = false
        }
    }

    private fun refreshActivity() {
        Thread {
            buildRequestForRefreshActivity()
            try {
                ConnectionUtil.getDataByUrl(urlForRefreshActivity)
            } catch (e: Exception) {
                e.printStackTrace()
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForRefreshActivity()
        }.start()
    }

    private fun dealWithResponseForRefreshActivity() {
        buildDataForRefreshActivity()
        if (activityTitle.toString() == "") {
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString("暂无活动")
                activityTitleTextView?.setRightIcon(R.drawable.no_activity)
            }
        } else {
            activity?.runOnUiThread {
                activityTitleTextView?.setLeftString(activityTitle.toString())
                activityTitleTextView?.setRightIcon(R.drawable.activity_running)
            }
        }
    }

    private fun configSignBtn() {
        val signBtn = view?.findViewById<Button>(R.id.sign_btn_for_student)
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        signBtn?.setOnClickListener {
            sign()
            NavUtil.navController?.navigate(R.id.signStateForStudent)
        }
    }

    private fun sign() {
        Thread {
            buildRequestForSign()
            try {
                ConnectionUtil.getDataByUrl(urlForSign)
            } catch (e: Exception) {
                Log.e("error in sign", e.toString())
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponseForSign()
        }.start()
    }

    private fun dealWithResponseForSign() {
        val signState = ConnectionUtil.responseJson!!["signState"] as Int
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        if (signState == 0) {
            activity?.runOnUiThread {
                Toast.makeText(context, "签到失败", Toast.LENGTH_LONG).show()
            }
        } else {
            activity?.runOnUiThread {
                // page is waiting to build
                NavUtil.navController?.navigate(R.id.signStateForStudent)
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
        MapUtil.getLocationInfo(requireContext(),activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location_for_student, container, false)
    }

    private fun buildRequestForRefreshActivity() {
        urlForRefreshActivity =
            URL("http://${MainActivity.ip}:8080/activity/activityInProgress?classId=${StudentActivity.classId}")
    }

    private fun buildDataForRefreshActivity() {
        activityTitle = ConnectionUtil.responseJson!!["activityTitle"].toString()
        if (activityTitleTextView == null) {
            activityTitleTextView = view?.findViewById(R.id.activity_title_for_student)
        }

    }

    private fun buildRequestForSign() {
        if (deviceId == null) {
            deviceId = getDeviceId()
        }
        studentLongitude = locationInfo["studentLongitude"].toString()
        studentLatitude = locationInfo["studentLatitude"].toString()
        studentId = arguments?.get("studentId").toString()
        urlForSign =
            URL("http://${MainActivity.ip}:8080/sign/studentSign?studentId=$studentId&studentLongitude=$studentLongitude&studentLatitude=$studentLatitude&deviceId=$deviceId")
    }

    private var deviceId: String? = null
    var activityTitle: String? = null
    var activityTitleTextView: SuperTextView? = null
    var studentLongitude: String? = null
    var studentLatitude: String? = null
    var studentId: String? = null
    var urlForRefreshActivity: URL? = null
    var urlForSign: URL? = null
    private val locationInfo = ArrayMap<String, String>()

    companion object {

    }
}