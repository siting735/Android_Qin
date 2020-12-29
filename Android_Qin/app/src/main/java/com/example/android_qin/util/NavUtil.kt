package com.example.android_qin.util

import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.MainActivity
import com.example.android_qin.R
import com.example.android_qin.StudentActivity

class NavUtil {
    companion object {
        var navHostFragment: NavHostFragment? = null
        var navController: NavController? = null
        fun buildNavHost(fragmentManager: FragmentManager?) {
            if (MainActivity.identity == MainActivity.STUDENT) {
                navHostFragment =
                    fragmentManager?.findFragmentById(R.id.nav_host_for_student) as NavHostFragment
                navController = navHostFragment?.navController
            } else {
                navHostFragment =
                    fragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
                navController = navHostFragment?.navController
            }
        }

    }
}