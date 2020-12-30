package com.example.android_qin.sign_data

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.TeacherActivity
import com.example.android_qin.listener.GetInClassListener
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.DpUtil
import com.example.android_qin.util.LayoutUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL

class SignDataForTeacher : Fragment() {

    override fun onStart() {
        configSwipeRefresh()
        getClassListByRequest()
        super.onStart()
    }

    private fun configSwipeRefresh() {
        buildSwipe()
        swipe?.setOnRefreshListener {
            getClassListByRequest()
            swipe?.isRefreshing = false
        }
    }

    private fun getClassListByRequest() {
        Thread {
            buildRequestForGetClassInfos()
            try {
                ConnectionUtil.getDataByUrl(urlForGetClassInfos)
            } catch (e: Exception) {
                e.printStackTrace()
                ConnectionUtil.buildConnectFailDialog(requireContext())
                activity?.runOnUiThread {
                    ConnectionUtil.connectFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponse()
        }.start()
    }

    private fun addClassInfoViewToLayout(classInfo: JSONObject) {
        var classInfoView = SuperTextView(context)
        classInfoView = buildClassInfoView(classInfoView, classInfo)
        activity?.runOnUiThread {
            classListLayout?.addView(classInfoView)
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
        return inflater.inflate(R.layout.fragment_sign_data_for_teacher, container, false)
    }

    @SuppressLint("ResourceType")
    private fun buildClassInfoView(
        classInfoView: SuperTextView?,
        classInfo: JSONObject?
    ): SuperTextView {
        if (classListLayout == null) {
            classListLayout = view?.findViewById(R.id.class_list)
        }
        classInfoView?.id = viewCounter
        viewCounter += 1
        tempClassName = classInfo?.get("className")?.toString()
        tempClassId = classInfo?.get("classId")?.toString()
        classInfoView?.setLeftIcon(R.drawable.class_icon)
        classInfoView?.setLeftString(tempClassName)
        classInfoView?.setShapeCornersRadius(DpUtil.dip2px(requireContext(),5f).toFloat())
        classInfoView?.setPadding(dip2px(14f), 0, 0, 0)
        classInfoView?.layoutParams = LayoutUtil.layoutParamsForInfoUnit
        classInfoView?.setRightIcon(R.drawable.get_in)
        val classInfoViewListener = GetInClassListener(tempClassId, tempClassName)
        classInfoView!!.setOnClickListener(classInfoViewListener)
        return classInfoView?.useShape()
    }

    private fun buildSwipe() {
        if (swipe == null) {
            swipe = view?.findViewById(R.id.sign_data_swipe_for_teacher)
        }
    }

    private fun dealWithResponse() {
        buildDataForClassList()
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        removeOriginClassInfos()
        for (index in 0 until classInfos!!.length()) {
            addClassInfoViewToLayout(classInfos!![index] as JSONObject)
        }
    }

    private fun buildDataForClassList() {
        responseJson = JSONObject(ConnectionUtil.response.toString())
        classInfos = responseJson!!["classInfos"] as JSONArray
    }

    private fun buildRequestForGetClassInfos() {
        urlForGetClassInfos =
            URL("http://${MainActivity.ip}:8080/teacher/teacherClasses?teacherId=${TeacherActivity.teacherId}")
    }

    private fun removeOriginClassInfos() {
        val classList = view?.findViewById<LinearLayout>(R.id.class_list)
        activity?.runOnUiThread {
            classList?.removeAllViews()
        }
    }

    var swipe: SwipeRefreshLayout? = null
    private var classListLayout: LinearLayout? = null
    var viewCounter = 0
    var urlForGetClassInfos: URL? = null
    var responseJson: JSONObject? = null
    var classInfos: JSONArray? = null
    var tempClassId: String? = null
    var tempClassName: String? = null

    companion object {

    }
}