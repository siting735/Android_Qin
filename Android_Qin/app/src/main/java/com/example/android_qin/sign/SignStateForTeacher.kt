package com.example.android_qin.sign

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.util.NavUtil

class SignStateForTeacher : Fragment() {

    override fun onStart() {
        configBackButton()
        super.onStart()
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

    companion object {

    }
}