package com.example.android_qin.mine

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.R
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView

class MineForStudent : Fragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configLogOutButton()
    }
    private fun configLogOutButton(){
        val logOutBtn = view?.findViewById<SuperTextView>(R.id.log_out_btn_for_student)
        logOutBtn?.setOnClickListener {
            logOutConfirm()
        }
    }
    private fun logOutConfirm(){
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_for_student) as NavHostFragment
        val navController = navHostFragment.navController
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("提示")
        dialog.setMessage("确认退出？")
        dialog.setPositiveButton("确定") {
                dialog, id -> navController.navigate(R.id.action_mineForStudent_to_mainActivity2)
        }
        dialog.setNegativeButton("取消") {
                dialog, id ->{}
        }
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine_for_student, container, false)
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MineForStudent().apply {
                    arguments = Bundle().apply {}
                }
    }
}