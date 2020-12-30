package com.example.android_qin.mine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.example.android_qin.R
import com.example.android_qin.util.NavUtil

class About : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configBackButton()
    }

    private fun configBackButton() {
        val backBtn = view?.findViewById<Toolbar>(R.id.about_tool_bar)
        backBtn?.setNavigationOnClickListener {
            NavUtil.navController?.popBackStack()
            if (NavUtil.navType == NavUtil.NAV_STUDENT) {
                NavUtil.navController?.navigate(R.id.mineForStudent)
            } else if (NavUtil.navType == NavUtil.NAV_TEACHER) {
                NavUtil.navController?.navigate(R.id.mineForTeacher)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

}