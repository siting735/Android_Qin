package com.example.android_qin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var mMapView = view?.findViewById<MapView>(R.id.map_for_student)
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        var aMap: AMap?=null
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
        configSignBtn()
        configSwipeRefresh()
    }

    override fun onStart() {
        super.onStart()
        refreshActivity()
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
        val classId = arguments?.get("classId").toString()
        Log.i("classId in location1",classId)
        Thread{
            val url = "http://10.60.0.13:8080/activity/activityInProgress?classId="+ classId
            val urlForGetActivityInfo = URL(url)
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForGetActivityInfo.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                    Log.i("fail dialog reason",e.toString())
                    Log.i("fail dialog","i am location")
                }
                Thread.currentThread().join()
            }
            dealWithResponse(response)
        }.start()
    }
    private fun dealWithResponse(response: StringBuilder?){
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
    private fun buildConnectFailDialog(): androidx.appcompat.app.AlertDialog.Builder {
        val loginFailDialog = androidx.appcompat.app.AlertDialog.Builder(this.requireContext())
        loginFailDialog.setTitle("提示信息")
        loginFailDialog.setMessage("连接服务器失败")
        loginFailDialog.setPositiveButton("确定") {
                dialog, id ->{}
        }
        return loginFailDialog
    }
    private fun getDataFromConnection(connection: HttpURLConnection): StringBuilder {
        val inputStream = connection?.inputStream
        val reader = inputStream?.bufferedReader()
        val response = StringBuilder()
        while (true) {
            val line = reader?.readLine() ?: break
            response.append(line)
        }
        reader?.close()
        return response
    }
    private fun configSignBtn(){
        val signBtn= view?.findViewById<Button>(R.id.sign_btn_for_student)
        signBtn?.setOnClickListener {
            sign()
        }
    }
    private fun sign(){
        val classId = arguments?.get("classId").toString()
        Thread{
            val url = "http://10.60.0.13:8080/activity/activityInProgress?classId=$classId"
            val urlForGetSignData = URL(url)
            var connection: HttpURLConnection? = null
            var response: StringBuilder? = null
            try {
                connection = urlForGetSignData.openConnection() as HttpURLConnection
                connection?.requestMethod = "GET"
                response = getDataFromConnection(connection)
                connection?.disconnect()
            } catch (e: Exception) {
                var loginFailDialog = buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponse(response)
        }.start()
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
}