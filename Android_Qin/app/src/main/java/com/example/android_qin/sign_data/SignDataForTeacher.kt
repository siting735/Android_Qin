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
import com.example.android_qin.R
import com.example.android_qin.listener.GetInClassListener
import com.example.android_qin.util.ConnectionUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL

class SignDataForTeacher : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getClassListOnInit()
        configSwipeRefresh()
    }

    private fun getClassListOnInit() {
        Thread {
            val classesInfoString = arguments?.get("classesInfoString").toString()
            classInfos = JSONArray(classesInfoString)
            for (index in 0 until classInfos!!.length()) {
                addClassInfoViewToLayout(classInfos!![index] as JSONObject)
            }
        }.start()
    }

    private fun configSwipeRefresh() {
        buildSwipe()
        swipe?.setOnRefreshListener {
            removeOriginClassInfos()
            getClassListByRequest()
            swipe?.isRefreshing = false
        }
    }

    private fun removeOriginClassInfos() {
        val classList = view?.findViewById<LinearLayout>(R.id.class_list)
        activity?.runOnUiThread {
            classList?.removeAllViews()
        }
    }

    private fun getClassListByRequest() {
        Thread {
            buildRequestForGetClassInfos()
            try {
                response = ConnectionUtil.getDataByUrl(urlForGetClassInfos)
            } catch (e: Exception) {
                buildConnectFailDialog()
                activity?.runOnUiThread {
                    loginFailDialog?.show()
                }
                Thread.currentThread().join()
            }
            dealWithResponse(response)
        }.start()
    }

    private fun addClassInfoViewToLayout(classInfo: JSONObject) {
        val classInfoView = SuperTextView(context)
        buildNavHost()
        buildClassInfoView(classInfoView, classInfo)
        activity?.runOnUiThread {
            classListLayout?.addView(classInfoView)
        }
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density;
        return (dpValue * scale!! + 0.5f).toInt()
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
        return inflater.inflate(R.layout.fragment_sign_data_for_teacher, container, false)
    }

    private fun buildNavHost() {
        if (navHostFragment == null) {
            navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
            navController = navHostFragment?.navController
        }
    }

    @SuppressLint("ResourceType")
    private fun buildClassInfoView(classInfoView: SuperTextView?, classInfo: JSONObject?) {
        if (classListLayout == null) {
            classListLayout = view?.findViewById(R.id.class_list)
        }
        if (layoutParams == null) {
            layoutParams =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(60f))
            layoutParams?.setMargins(dip2px(10f), dip2px(10f), dip2px(10f), 0)
        }
        classInfoView?.id = viewCounter
        viewCounter += 1
        Log.i("build class info view", classInfo.toString())
        className = classInfo?.get("className")?.toString()
        classId = classInfo?.get("classId")?.toString()
        classInfoView?.setLeftIcon(R.drawable.class_icon)
        classInfoView?.setLeftString(className)
        classInfoView?.useShape()
        classInfoView?.setShapeCornersRadius(dip2px(10f).toFloat())
        classInfoView?.setPadding(dip2px(14f), 0, 0, 0)
        classInfoView?.layoutParams = layoutParams
        classInfoView?.setRightIcon(R.drawable.get_in)
        val classInfoViewListener = GetInClassListener(classId, className, navController)
        classInfoView!!.setOnClickListener(classInfoViewListener)
    }

    private fun buildSwipe() {
        if (swipe == null) {
            swipe = view?.findViewById(R.id.sign_data_swipe_for_teacher)
        }
    }

    private fun dealWithResponse(response: StringBuilder?) {
        buildDataForClassList()
        for (index in 0 until classInfos!!.length()) {
            addClassInfoViewToLayout(classInfos!![index] as JSONObject)
        }
    }


    private fun buildDataForClassList() {
        responseJson = JSONObject(response.toString())
        classInfos = responseJson!!["classInfos"] as JSONArray
    }

    private fun buildRequestForGetClassInfos() {
        if (ip == null) {
            ip = getString(R.string.ip)
        }
        teacherId = arguments?.get("teacherId").toString()
        urlForGetClassInfos = URL("http://$ip:8080/teacher/teacherClasses?teacherId=$teacherId")
    }

    private fun buildConnectFailDialog() {
        if (loginFailDialog == null) {
            loginFailDialog = AlertDialog.Builder(this.requireContext())
            loginFailDialog?.setTitle("提示信息")
            loginFailDialog?.setMessage("连接服务器失败")
            loginFailDialog?.setPositiveButton("确定") { dialog, id ->
                {}
            }
        }
    }

    var swipe: SwipeRefreshLayout? = null
    private var classListLayout: LinearLayout? = null
    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null
    var className: String? = null
    var classId: String? = null
    var layoutParams: LinearLayout.LayoutParams? = null
    var viewCounter = 0
    var ip: String? = null
    var teacherId: String? = null
    var urlForGetClassInfos: URL? = null
    var response: StringBuilder? = null
    var loginFailDialog: AlertDialog.Builder? = null
    var responseJson: JSONObject? = null
    var classInfos: JSONArray? = null

    companion object {
        const val REFRESH = 1
        const val INIT = 0

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignDataForTeacher().apply {
                arguments = Bundle().apply {}
            }
    }
}