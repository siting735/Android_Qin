package com.example.android_qin.sign_data

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.DpUtil
import com.example.android_qin.util.LayoutUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SignDataForStudent : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSignDataForStudent()
        configSwipeRefresh()
    }

    private fun getSignDataForStudent() {
        Thread {
            if (context != null) {
                removeOriginSignDatas()
                buildRequestForGetSignData()
                ConnectionUtil.getDataByRequest(
                    requireActivity(),
                    requireContext(),
                    urlForGetSignData
                )
                dealWithResponse()
            }
        }.start()
    }

    private fun dealWithResponse() {
        buildDataForSignList()
        Log.i("signDataList",signDataJsonList.toString())
        updateSignRito()
        val listLength = signDataJsonList!!.length()
        if(DISPLAY_TYPE == DISPLAY_UNSIGN_FIRST){
            for (index in 0 until listLength) {
                addSignDataToLayout(signDataJsonList!![index] as JSONObject)
            }
        }
        else if (DISPLAY_TYPE == DISPLAY_SIGN_FIRST){
            DISPLAY_TYPE = DISPLAY_UNSIGN_FIRST
            val signHeadIndex = getSignHeadIndex(listLength)
            Log.i("DISPLAY_SIGN_FIRST","+1")
            Log.i("signHeadIndex", signHeadIndex.toString())
            for (index in signHeadIndex until listLength){
                addSignDataToLayout(signDataJsonList!![index] as JSONObject)
            }
            for (index in 0 until signHeadIndex) {
                addSignDataToLayout(signDataJsonList!![index] as JSONObject)
            }
        }
    }

    private fun getSignHeadIndex(listLength: Int): Int{
        var signData: JSONObject? = null
        for (index in 0 until listLength) {
            signData = signDataJsonList!![index] as JSONObject
            if (signData["signState"].toString().equals(SIGN)){
                return index
            }
        }
        return 0
    }

    private fun updateSignRito() {
        buildDataForUpdateSignRito()
        activity?.runOnUiThread {
            signRitoTextView?.setCenterBottomString("$signRito%")
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun addSignDataToLayout(signData: JSONObject?) {
        var signDataView: SuperTextView? = null
        if (context != null) {
            signDataView = SuperTextView(requireContext())
        } else {
            return Unit
        }
        buildSignDataView(signDataView, signData)
        activity?.runOnUiThread {
            signDataListLayout?.addView(signDataView)
        }
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<SwipeRefreshLayout>(R.id.sign_data_swipe)
        swipe?.setOnRefreshListener {
            getSignDataForStudent()
            swipe.isRefreshing = false
        }
    }

    private fun removeOriginSignDatas() {
        val signDataList = view?.findViewById<LinearLayout>(R.id.sign_data_list)
        activity?.runOnUiThread {
            signDataList?.removeAllViews()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_data_for_student, container, false)
    }

    private fun buildRequestForGetSignData() {
        urlForGetSignData =
            URL("http://${MainActivity.ip}:8080/student/studentSignMessage?studentId=${StudentActivity.studentId}")
    }

    private fun buildDataForSignList() {
        signDataJsonList = ConnectionUtil.responseJson!!["activityInfo"] as JSONArray
    }

    private fun buildDataForUpdateSignRito() {
        if (signRitoTextView == null) {
            signRitoTextView = view?.findViewById(R.id.sign_rito)
        }
        signRito = ConnectionUtil.responseJson!!["signRito"].toString()
    }

    private fun buildSignDataView(
        signDataView: SuperTextView?,
        signData: JSONObject?
    ): SuperTextView {
        if (signDataListLayout == null) {
            signDataListLayout = view?.findViewById(R.id.sign_data_list)
        }
        activityTitle = signData?.get("activityTitle")?.toString()
        signState = signData?.get("signState")?.toString()
        signDataView?.setLeftBottomString(activityTitle)
        if (context != null) {
            signDataView?.setShapeCornersRadius(DpUtil.dip2px(requireContext(), 5f).toFloat())
        }
        when (signState) {
            SIGN -> {
                signDataView?.setRightString("已打卡")
            }
            UNSIGN -> {
                signDataView?.setRightString("未打卡")
            }
        }
        signDataView!!.layoutParams = LayoutUtil.layoutParamsForInfoUnit
        return signDataView?.useShape()
    }

    var signDataListLayout: LinearLayout? = null
    var activityTitle: String? = null
    var signState: String? = null
    var signRito: String? = null
    var signRitoTextView: SuperTextView? = null
    var urlForGetSignData: URL? = null
    var signDataJsonList: JSONArray? = null

    companion object {
        const val UNSIGN = "0"
        const val SIGN = "1"
        const val DISPLAY_SIGN_FIRST = 1
        private const val DISPLAY_UNSIGN_FIRST = 0
        var DISPLAY_TYPE = DISPLAY_UNSIGN_FIRST
    }
}