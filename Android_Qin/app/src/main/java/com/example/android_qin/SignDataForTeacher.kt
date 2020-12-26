package com.example.android_qin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android_qin.listener.GetInClassListener
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SignDataForTeacher : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getClassListForTeacher()
        configSwipeRefresh()
    }

    private fun getClassListForTeacher() {
        Thread {
            val classesInfoString = arguments?.get("classesInfoString").toString()
            val classesInfo = JSONArray(classesInfoString)
            for (index in 0 until classesInfo.length()) {
                addClassInfoViewToLayout(classesInfo[index] as JSONObject)
            }
        }.start()
    }

    private fun configSwipeRefresh() {
        buildSwipe()
        swipe?.setOnRefreshListener {
            removeOriginClassInfos()
            getClassInfosForTeacher()
            swipe?.isRefreshing = false
        }
    }

    private fun removeOriginClassInfos() {

    }

    private fun getClassInfosForTeacher() {
        //teacherId
        //teacher/teacherClasses?teacherId=2001
    }

    private fun addClassInfoViewToLayout(classInfo: JSONObject) {
        val classInfoView = SuperTextView(context)
        buildNavHost()
        buildClassInfoView(classInfoView, classInfo)
        val classInfoViewListener = GetInClassListener(classId,className,navController)
        classInfoView!!.setOnClickListener(classInfoViewListener)
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

    private fun buildBundleForClassInfo(classId: String?, className: String?): Bundle {
        val bundle = Bundle()
        bundle.putString("classId", classId)
        bundle.putString("className", className)
        return bundle
    }
    var viewCounter = 0
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
        className = classInfo?.get("className")?.toString()
        classId = classInfo?.get("classId")?.toString()
        classInfoView?.setLeftIcon(R.drawable.class_icon)
        classInfoView?.setLeftString(className)
        classInfoView?.useShape()
        classInfoView?.setShapeCornersRadius(dip2px(10f).toFloat())
        classInfoView?.setPadding(dip2px(14f), 0, 0, 0)
        classInfoView?.layoutParams = layoutParams
        classInfoView?.setRightIcon(R.drawable.get_in)
    }
    private fun buildSwipe(){
        if (swipe == null){
            swipe = view?.findViewById(R.id.sign_data_swipe_for_teacher)
        }
    }
    var swipe: SwipeRefreshLayout? = null
    private var classListLayout: LinearLayout? = null
    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null
    var className: String? = null
    var classId: String? = null
    var layoutParams: LinearLayout.LayoutParams? = null
    //var classInfoBundle: Bundle? = null
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignDataForTeacher().apply {
                arguments = Bundle().apply {}
            }
    }
}