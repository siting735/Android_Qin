package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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

    private fun configTabBar() {
        var tabSegment = findViewById<TabSegment>(R.id.teacher_tab_bar)
        val teacherInfo = buildBundleForTeacher()
        buildNavHost()
        TabBarUtil.configTabBar(tabSegment, navController, teacherInfo, TabBarUtil.TEACHER)
        toDefaultPage(teacherInfo)
    }

    private fun toDefaultPage(teacherInfo: Bundle) {
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

    override fun onBackPressed() {
        //invalid back button
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
}