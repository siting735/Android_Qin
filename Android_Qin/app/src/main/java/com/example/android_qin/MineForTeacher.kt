package com.example.android_qin

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
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
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
        val navController = navHostFragment.navController
        var dialog = AlertDialog.Builder(context)
        dialog.setTitle("提示")
        dialog.setMessage("确认退出？")
        dialog.setPositiveButton("是的") { dialog, id ->
            navController.navigate(R.id.action_mineForTeacher_to_mainActivity)
        }
        dialog.setNegativeButton("取消") { dialog, id ->
            {}
        }
        dialog.show()
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine_for_teacher, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MineForTeacher().apply {
                arguments = Bundle().apply {}
            }
    }
}