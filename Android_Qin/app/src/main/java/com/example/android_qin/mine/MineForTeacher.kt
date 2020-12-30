package com.example.android_qin.mine

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.TeacherActivity
import com.example.android_qin.util.AccountInfoUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView

class MineForTeacher : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configLogOutButton()
        configProfile()
    }

    private fun configProfile() {
        val profileView = view?.findViewById<SuperTextView>(R.id.mine_info_for_teacher)
        profileView?.setLeftString(TeacherActivity.teacherName)
        profileView?.setOnClickListener {
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.profileInfoForTeacher)
        }
    }

    private fun configLogOutButton() {
        val logOutBtn = view?.findViewById<SuperTextView>(R.id.log_out_btn_for_teacher)
        logOutBtn?.setOnClickListener {
            logOutConfirm()
        }
    }

    private fun logOutConfirm() {
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("提示")
        dialog.setMessage("确认退出？")
        dialog.setPositiveButton("确定") { dialog, id ->
            MainActivity.LOG_OUT = true
            TeacherActivity.tabBarState = 0
            MainActivity.identity = MainActivity.STUDENT
            NavUtil.navController?.navigate(R.id.mainActivity2)
            AccountInfoUtil.clearAccountInfo()
        }
        dialog.setNegativeButton("取消") { dialog, id -> {} }
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mine_for_teacher, container, false)
    }

}