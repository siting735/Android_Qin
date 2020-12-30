package com.example.android_qin.sign_data

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.TeacherActivity
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.DpUtil
import com.example.android_qin.util.LayoutUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SignDataForEachClass : Fragment() {
    override fun onStart() {
        configBackButton()
        updateClassName()
        configSwipeRefresh()
        getStudentsSignData()
        super.onStart()
    }

    private fun configBackButton() {
        val backBtn = view?.findViewById<Toolbar>(R.id.tool_bar_for_class_info)
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        backBtn?.setNavigationOnClickListener {
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.signDataForTeacher)
        }
    }

    private fun configSwipeRefresh(){
        val swipe =
            view?.findViewById<SwipeRefreshLayout>(R.id.sign_data_swipe_for_class)
        swipe?.setOnRefreshListener {
            getStudentsSignData()
            swipe.isRefreshing = false
        }
    }

    private fun updateClassName() {
        val className = arguments?.get("className").toString()
        val classNameView = view?.findViewById<SuperTextView>(R.id.class_name)
        activity?.runOnUiThread {
            classNameView?.setLeftString(className)
        }
    }

    private fun getStudentsSignData() {
        Thread {
            if (context != null) {
                removeOriginSignDatas()
                buildRequest()
                ConnectionUtil.getDataByRequest(
                    requireActivity(),
                    requireContext(),
                    urlForGetSignDatas
                )
                dealWithResponse()
            }
        }.start()
    }

    private fun removeOriginSignDatas() {
        val signDataList = view?.findViewById<LinearLayout>(R.id.students_sign_data_list)
        activity?.runOnUiThread {
            signDataList?.removeAllViews()
        }
    }

    private fun dealWithResponse() {
        val signDataList = ConnectionUtil.responseJson!!["signRitoOfStudents"] as JSONArray
        for (index in 0 until signDataList.length()) {
            addSignDataToLayout(signDataList[index] as JSONObject)
        }
    }

    private fun addSignDataToLayout(signData: JSONObject?) {
        val signDataListLayout = view?.findViewById<LinearLayout>(R.id.students_sign_data_list)
        var signDataView = SuperTextView(context)
        val studentId = signData?.get("studentId")?.toString()
        val studentName = signData?.get("studentName")?.toString()
        val signRito = signData?.get("signRito")?.toString()
        signDataView.setLeftBottomString(studentId)
        signDataView.setLeftString(studentName)
        signDataView.setRightString("出勤率：")
        signDataView.setRightBottomString("$signRito%")
        signDataView.setShapeCornersRadius(DpUtil.dip2px(context, 5f).toFloat())
        signDataView.layoutParams = LayoutUtil.layoutParamsForInfoUnit
        signDataView = signDataView.useShape()
        activity?.runOnUiThread {
            signDataListLayout?.addView(signDataView)
        }
    }

    private fun buildRequest() {
        val classId = arguments?.getString("classId")
        urlForGetSignDatas =
            URL("http://${MainActivity.ip}:8080/teacher/signRitoOfStudents?classId=$classId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_data_for_each_class, container, false)
    }

    private var urlForGetSignDatas: URL? = null

}