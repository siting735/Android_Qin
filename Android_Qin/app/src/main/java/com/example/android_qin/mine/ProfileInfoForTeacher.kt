package com.example.android_qin.mine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.TeacherActivity
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView

class ProfileInfoForTeacher : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateInfo()
        configBackButton()
    }

    private fun updateInfo() {
        val studentName = view?.findViewById<SuperTextView>(R.id.teacher_name)
        val studentId = view?.findViewById<SuperTextView>(R.id.teacher_id)
        activity?.runOnUiThread {
            studentName?.setRightString(TeacherActivity.teacherName)
            studentId?.setRightString(TeacherActivity.teacherId)
        }
    }

    private fun configBackButton() {
        val backBtn = view?.findViewById<Toolbar>(R.id.profile_info_tool_bar)
        backBtn?.setNavigationOnClickListener {
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.mineForTeacher)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_info_for_teacher, container, false)
    }


}