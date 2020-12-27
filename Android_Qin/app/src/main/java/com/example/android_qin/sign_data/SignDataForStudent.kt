package com.example.android_qin.sign_data

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.util.ConnectionUtil
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
            removeOriginSignDatas()
            buildRequestForGetSignData()
            try {
                response = ConnectionUtil.getDataByUrl(urlForGetSignData)
            } catch (e: Exception) {
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponse()
        }.start()

    }

    private fun dealWithResponse() {
        buildDataForSignList()
        updateSignRito(responseJson)
        val listLength = signDataJsonList!!.length()
        for (index in listLength - 1 downTo 0) {
            addSignDataToLayout(signDataJsonList!![index] as JSONObject)
        }
    }

    private fun updateSignRito(responseJson: JSONObject?) {
        buildDataForUpdateSignRito()
        activity?.runOnUiThread {
            signRitoTextView?.setCenterBottomString("$signRito%")
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun addSignDataToLayout(signData: JSONObject?) {
        val signDataView = SuperTextView(context)
        buildSignDataView(signDataView, signData)
        activity?.runOnUiThread {
            signDataListLayout?.addView(signDataView)
        }
    }

    private fun configSwipeRefresh() {
        val swipe =
            view?.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.sign_data_swipe)
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

    private fun dip2px(dpValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density;
        return (dpValue * scale!! + 0.5f).toInt()
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
        responseJson = JSONObject(response.toString())
        signDataJsonList = responseJson!!["activityInfo"] as JSONArray
    }

    private fun buildDataForUpdateSignRito() {
        if (signRitoTextView == null) {
            signRitoTextView = view?.findViewById(R.id.sign_rito)
        }
        signRito = responseJson!!["signRito"].toString()
    }

    private fun buildSignDataView(signDataView: SuperTextView?, signData: JSONObject?) {
        if (signDataListLayout == null) {
            signDataListLayout = view?.findViewById<LinearLayout>(R.id.sign_data_list)
        }
        activityTitle = signData?.get("activityTitle")?.toString()
        signState = signData?.get("signState")?.toString()
        signDataView?.setLeftBottomString(activityTitle)
        signDataView?.useShape()
        signDataView?.setShapeCornersRadius(dip2px(10f).toFloat())
        when (signState) {
            SIGN -> {
                signDataView?.setRightString("已打卡")
            }
            UNSIGN -> {
                signDataView?.setRightString("未打卡")
            }
        }
        if (layoutParams == null) {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(100f))
            layoutParams?.setMargins(dip2px(10f), dip2px(10f), dip2px(10f), 0)
        }
        signDataView!!.layoutParams = layoutParams
    }

    var signDataListLayout: LinearLayout? = null
    var activityTitle: String? = null
    var signState: String? = null
    var layoutParams: LinearLayout.LayoutParams? = null
    var signRito: String? = null
    var signRitoTextView: SuperTextView? = null
    var urlForGetSignData: URL? = null
    var response: StringBuilder? = null
    var signDataJsonList: JSONArray? = null
    var responseJson: JSONObject? = null

    companion object {
        const val UNSIGN = "0"
        const val SIGN = "1"
    }
}