package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android_qin.util.NavUtil
import com.example.android_qin.util.TabBarUtil
import com.xuexiang.xui.widget.tabbar.TabSegment

class StudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
    }

    override fun onStart() {
        super.onStart()
        NavUtil.buildNavHost(supportFragmentManager)
        configTabBar()
    }

    private fun configTabBar() {
        tabSegment = findViewById(R.id.student_tab_bar)
        if (tabBarState == 0) {
            TabBarUtil.configTabBar(tabSegment, TabBarUtil.STUDENT)
            tabBarState = 1
        }
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