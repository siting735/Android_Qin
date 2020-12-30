package com.example.android_qin.mine

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.TeacherActivity
import com.example.android_qin.util.AccountInfoUtil
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView

class MineForStudent : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configLogOutButton()
        updateProfile()
    }

    private fun updateProfile(){
        val profileView = view?.findViewById<SuperTextView>(R.id.mine_info_for_student)
        profileView?.setLeftString(StudentActivity.studentName)
    }

    private fun configLogOutButton() {
        val logOutBtn = view?.findViewById<SuperTextView>(R.id.log_out_btn_for_student)
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
            StudentActivity.tabBarState = 0
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.mainActivity)
            AccountInfoUtil.clearAccountInfo()
        }
        dialog.setNegativeButton("取消") { dialog, id -> {} }
        dialog.show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mine_for_student, container, false)
    }
}