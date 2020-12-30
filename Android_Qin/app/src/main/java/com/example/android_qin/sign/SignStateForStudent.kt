package com.example.android_qin.sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.R
import com.example.android_qin.StudentActivity
import com.example.android_qin.util.NavUtil

class SignStateForStudent : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configBackButton()
    }

    private fun configBackButton(){
        val backBtn = view?.findViewById<Toolbar>(R.id.tool_bar_for_student_signState)
        backBtn?.setNavigationOnClickListener {
            StudentActivity.tabSegment?.selectTab(1)
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.signDataForStudent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_state_for_student, container, false)
    }

}