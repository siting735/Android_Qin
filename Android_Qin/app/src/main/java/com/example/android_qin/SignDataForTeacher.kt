package com.example.android_qin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
 * Use the [SignDataForTeacher.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignDataForTeacher : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getClassListForTeacher()
        configSwipeRefresh()
    }
    private fun getClassListForTeacher(){
        val classesInfoString=arguments?.get("classesInfoString").toString()
        val classesInfo = JSONArray(classesInfoString)
        Thread{
            for(index in 0 until classesInfo.length()){
                addClassInfoToLayout(classesInfo[index] as JSONObject)
            }
        }.start()
    }
    private fun configSwipeRefresh(){
        val swipe= view?.findViewById<SwipeRefreshLayout>(R.id.sign_data_swipe_for_teacher)
        swipe?.setOnRefreshListener {
            removeOriginClassInfos()
            getClassInfosForTeacher()
            swipe.isRefreshing=false
        }
    }
    private fun removeOriginClassInfos(){

    }
    private fun getClassInfosForTeacher(){
        //teacherId
        //teacher/teacherClasses?teacherId=2001
    }
    private fun buildBundleForClassInfo(classId:String?,className:String?):Bundle{
        val bundle = Bundle()
        bundle.putString("classId",classId)
        bundle.putString("className",className)
        return bundle
    }
    private fun addClassInfoToLayout(classInfo:JSONObject){
        val classListLayout= view?.findViewById<LinearLayout>(R.id.class_list)
        val classInfoView = SuperTextView(context)
        val className = classInfo?.get("className")?.toString()
        val classId = classInfo?.get("classId")?.toString()
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
        val navController = navHostFragment.navController
        classInfoView.setLeftString(className)
        classInfoView.useShape()
        classInfoView.setShapeCornersRadius(dip2px(10f).toFloat())
        var layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(60f))
        layoutParams.setMargins(dip2px(10f),dip2px(10f),dip2px(10f),0)
        classInfoView.layoutParams = layoutParams
        classInfoView.setRightIcon(R.drawable.get_in)
        classInfoView.setOnClickListener {
            val classInfo = buildBundleForClassInfo(classId,className)
            navController.navigate(R.id.signDataForEachClass,classInfo)
        }
        activity?.runOnUiThread {
            classListLayout?.addView(classInfoView)
        }
    }
    private fun dip2px(dpValue:Float): Int {
        val scale = context?.resources?.displayMetrics?.density;
        return (dpValue * scale!! + 0.5f).toInt()
    }
    private fun px2dip(pxValue:Float):Int{
        val scale = context?.resources?.displayMetrics?.density;
        return (pxValue / scale!! + 0.5f).toInt()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_data_for_teacher, container, false)
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignDataForTeacher().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}