package com.example.android_qin.mine

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.TeacherActivity
import com.example.android_qin.util.NavUtil
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView

class MineForTeacher : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configLogOutButton()
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
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.mainActivity)
        }
        dialog.setNegativeButton("取消") { dialog, id ->
            {}
        }
        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mine_for_teacher, container, false)
    }

    companion object {

    }
}