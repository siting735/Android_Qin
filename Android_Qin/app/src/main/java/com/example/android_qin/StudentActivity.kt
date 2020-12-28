package com.example.android_qin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.util.ConnectionUtil
import com.example.android_qin.util.NavUtil
import com.example.android_qin.util.TabBarUtil
import com.xuexiang.xui.widget.tabbar.TabSegment
import kotlin.coroutines.coroutineContext

class StudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
    }

    override fun onStart() {
        super.onStart()
        NavUtil.buildNavHost(supportFragmentManager)
        configTabBar()
        Log.i("student activity","on start")
    }

    private fun configTabBar() {
        tabSegment = findViewById(R.id.student_tab_bar)
        if (tabBarState == 0) {
            TabBarUtil.configTabBar(tabSegment, TabBarUtil.STUDENT)
            tabBarState = 1
            toDefaultPage()
        }
    }

    private fun toDefaultPage() {
        NavUtil.navController?.popBackStack()
        NavUtil.navController?.navigate(R.id.locationFragmentForStudent)
    }

    companion object {
        var studentId: String? = null
        var studentName: String? = null
        var classId: String? = null
        var className: String? = null
        var tabSegment: TabSegment? = null
        var tabBarState = 0
    }
}