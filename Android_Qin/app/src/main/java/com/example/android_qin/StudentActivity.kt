package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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
        addTabsToTabSegment(tabSegment)
        tabSegment.mode = TabSegment.MODE_FIXED
        tabSegment.selectTab(0)
        tabSegment.notifyDataChanged()
        buildNavHost()
        val studentInfo = buildBundleForStudent()
        tabSegment.setOnTabClickListener {
            when (it) {
                SIGN_PAGE -> navController?.navigate(R.id.locationFragmentForStudent, studentInfo)
                SIGN_DATA_PAGE -> navController?.navigate(R.id.signDataForStudent, studentInfo)
                MINE_PAGE -> navController?.navigate(R.id.mineForStudent, studentInfo)
            }
        }
        toDefaultPage(studentInfo)
    }

    private fun toDefaultPage(studentInfo: Bundle) {
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

    override fun onBackPressed() {
        //invalid back button
    }

    private fun buildTabForSign(): TabSegment.Tab {
        return TabSegment.Tab(
            this.applicationContext.getDrawable(R.drawable.location),
            this.applicationContext.getDrawable(R.drawable.location_chosen),
            "打卡",
            true
        )
    }

    private fun buildTabForSignData(): TabSegment.Tab {
        return TabSegment.Tab(
            this.applicationContext.getDrawable(R.drawable.data),
            this.applicationContext.getDrawable(R.drawable.data_chosen),
            "统计",
            true
        )
    }

    private fun buildTabForMine(): TabSegment.Tab {
        return TabSegment.Tab(
            this.applicationContext.getDrawable(R.drawable.mine),
            this.applicationContext.getDrawable(R.drawable.mine),
            "我的",
            true
        )
    }

    private fun addTabsToTabSegment(tabSegment: TabSegment) {
        tabSegment.addTab(locationTab)
        tabSegment.addTab(dataTab)
        tabSegment.addTab(mineTab)
    }

    private fun buildNavHost() {
        if (navHostFragment == null) {
            navHostFragment =
                supportFragmentManager?.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
            navController = navHostFragment?.navController
        }
    }

    var navHostFragment: NavHostFragment? = null
    var navController: NavController? = null
    var locationTab: TabSegment.Tab = buildTabForSign()
    var dataTab: TabSegment.Tab = buildTabForSignData()
    var mineTab: TabSegment.Tab = buildTabForMine()
    var studentId = ""
    var studentName = ""
    var classId = ""
    var className = ""

    companion object {
        const val SIGN_PAGE = 0
        const val SIGN_DATA_PAGE = 1
        const val MINE_PAGE = 2
    }
}