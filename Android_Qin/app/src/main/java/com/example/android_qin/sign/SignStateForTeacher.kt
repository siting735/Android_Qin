package com.example.android_qin.sign

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.sign_data.SignDataForStudent
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.DpUtil
import com.example.android_qin.util.LayoutUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView
import org.json.JSONArray
import org.json.JSONObject

class SignStateForTeacher : Fragment() {

    override fun onStart() {
        buildPage()
        configBackButton()
        super.onStart()
    }
    private fun buildPage(){
        val signData = JSONObject(ConnectionUtil.response.toString())
        val signRito = signData["signRito"].toString()
        val unsignedStudentList = JSONArray(signData["unsignedStudentList"].toString())
        updateSignRito(signRito)
        val unsignedStudentListLength = unsignedStudentList.length()
        if (unsignedStudentListLength == 0) {
            showEmptyIcon()
            return Unit
        }
        for (index in 0 until unsignedStudentListLength){
            addStudentInfoToList(unsignedStudentList!![index] as JSONObject)
        }
    }

    private fun showEmptyIcon(){
        if (unsignStudentListLayout == null) {
            unsignStudentListLayout = view?.findViewById(R.id.unsign_students_list)
        }
        val imageView = ImageView(context)
        val layoutParamsForEmptyIcon = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.setImageResource(R.drawable.empty)
        layoutParamsForEmptyIcon?.setMargins(0, DpUtil.dip2px(context,150f), 0, 0)
        layoutParamsForEmptyIcon.gravity = Gravity.CENTER
        imageView.layoutParams = layoutParamsForEmptyIcon
        activity?.runOnUiThread {
            unsignStudentListLayout?.addView(imageView)
        }

    }

    private fun addStudentInfoToList(studentInfo: JSONObject?){
        val studentInfoView = SuperTextView(context)
        buildStudentInfoView(studentInfoView, studentInfo)
        activity?.runOnUiThread {
            unsignStudentListLayout?.addView(studentInfoView)
        }
    }

    private fun buildStudentInfoView(studentInfoView: SuperTextView, studentInfo: JSONObject?){
        if (unsignStudentListLayout == null) {
            unsignStudentListLayout = view?.findViewById(R.id.unsign_students_list)
        }
        studentName = studentInfo?.get("studentName")?.toString()
        studentId = studentInfo?.get("studentId")?.toString()
        studentInfoView?.setRightString(studentId)
        studentInfoView?.setLeftString(studentName)
        studentInfoView?.useShape()
        studentInfoView?.setShapeCornersRadius(DpUtil.dip2px(requireContext(),10f).toFloat())
        studentInfoView!!.layoutParams = LayoutUtil.layoutParamsForInfoUnit
    }

    private fun updateSignRito(signRito: String?){
        val signRitoView = view?.findViewById<SuperTextView>(R.id.sign_rito)
        signRitoView?.setCenterBottomString("$signRito%")
    }

    private fun configBackButton(){
        val backBtn = view?.findViewById<Toolbar>(R.id.activity_result)
        NavUtil.buildNavHost(activity?.supportFragmentManager)
        backBtn?.setNavigationOnClickListener {
            NavUtil.navController?.navigate(R.id.locationFragmentForTeacher)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_state_for_teacher, container, false)
    }

    private var unsignStudentListLayout: LinearLayout? = null
    private var studentId: String? = null
    private var studentName: String? = null
    companion object {

    }
}