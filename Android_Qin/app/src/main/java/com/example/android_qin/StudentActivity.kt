package com.example.android_qin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
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
        configTabBar()
    }

    private fun configTabBar() {
        tabSegment = findViewById<TabSegment>(R.id.student_tab_bar)
        NavUtil.buildNavHost(supportFragmentManager)
        TabBarUtil.configTabBar(tabSegment, TabBarUtil.STUDENT)
        toDefaultPage()
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

    }
}