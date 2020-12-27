package com.example.android_qin

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.listener.GetInClassListener
import com.example.android_qin.util.TabBarUtil
import com.xuexiang.xui.widget.tabbar.TabSegment
import org.json.JSONArray

class TeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        buildDataForBundle()
        configTabBar()
    }

    override fun onBackPressed() {
        if (GetInClassListener.pageState == CLASS_INFO) {
            super.onBackPressed()
            GetInClassListener.pageState = 0
        }
    }

    private fun configTabBar() {
        var tabSegment = findViewById<TabSegment>(R.id.teacher_tab_bar)
        val teacherInfo = buildBundleForTeacher()
        buildNavHost()
        TabBarUtil.configTabBar(tabSegment, navController, teacherInfo, TabBarUtil.TEACHER)
        toDefaultPage(teacherInfo)
    }

    private fun toDefaultPage(teacherInfo: Bundle) {
        navController?.popBackStack()
        navController?.navigate(R.id.locationFragmentForTeacher, teacherInfo)
    }

    private fun buildNavHost() {
        if (navHostFragment == null) {
            navHostFragment =
                supportFragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
            navController = navHostFragment?.navController
        }
    }

    private fun buildBundleForTeacher(): Bundle {
        val bundle = Bundle()
        bundle.putString("teacherId", teacherId)
        bundle.putString("teacherName", teacherName)
        bundle.putString("classesInfoString", classesInfoString)
        return bundle
    }


    private fun buildDataForBundle() {
        teacherId = intent.getStringExtra("teacherId").toString()
        teacherName = intent.getStringExtra("teacherName").toString()
        classesInfoString = intent.getStringExtra("classesInfo").toString()
        classesInfo = JSONArray(classesInfoString)
    }

    var teacherId = ""
    var teacherName = ""
    var classesInfoString = ""
    var classesInfo: JSONArray? = null
    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null

    companion object {
        const val CLASS_INFO = 1
    }
}