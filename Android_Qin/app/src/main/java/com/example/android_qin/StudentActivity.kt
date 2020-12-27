package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.util.TabBarUtil
import com.xuexiang.xui.widget.tabbar.TabSegment

class StudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        buildDataForBundle()
        configTabBar()
    }

    private fun configTabBar() {
        var tabSegment = findViewById<TabSegment>(R.id.student_tab_bar)
        buildNavHost()
        val studentInfo = buildBundleForStudent()
        TabBarUtil.configTabBar(tabSegment, navController, studentInfo, TabBarUtil.STUDENT)
        toDefaultPage(studentInfo)
    }

    private fun toDefaultPage(studentInfo: Bundle) {
        navController?.popBackStack()
        navController?.navigate(R.id.locationFragmentForStudent, studentInfo)
    }

    private fun buildBundleForStudent(): Bundle {
        val bundle = Bundle()
        bundle.putString("studentId", studentId)
        bundle.putString("studentName", studentName)
        bundle.putString("classId", classId)
        bundle.putString("className", className)
        return bundle
    }

    private fun buildDataForBundle() {
        studentId = intent.getStringExtra("studentId").toString()
        studentName = intent.getStringExtra("studentName").toString()
        classId = intent.getStringExtra("classId").toString()
        className = intent.getStringExtra("className").toString()
    }


    private fun buildNavHost() {
        if (navHostFragment == null) {
            navHostFragment =
                supportFragmentManager?.findFragmentById(R.id.nav_host_for_student) as NavHostFragment
            navController = navHostFragment?.navController
        }
    }

    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null
    var studentId = ""
    var studentName = ""
    var classId = ""
    var className = ""

}