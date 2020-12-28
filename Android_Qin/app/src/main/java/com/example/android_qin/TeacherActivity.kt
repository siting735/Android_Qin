package com.example.android_qin


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android_qin.listener.GetInClassListener
import com.example.android_qin.util.NavUtil
import com.example.android_qin.util.TabBarUtil
import com.xuexiang.xui.widget.tabbar.TabSegment
import org.json.JSONArray
import org.json.JSONObject

class TeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
    }

    override fun onStart() {
        super.onStart()
        NavUtil.buildNavHost(supportFragmentManager)
        configTabBar()
    }

    private fun configTabBar() {
        var tabSegment = findViewById<TabSegment>(R.id.teacher_tab_bar)
        if (tabBarState == 0) {
            TabBarUtil.configTabBar(tabSegment, TabBarUtil.TEACHER)
            tabBarState = 1
            toDefaultPage()
        }

    }

    override fun onBackPressed() {
        if (GetInClassListener.pageState == CLASS_INFO) {
            NavUtil.navController?.popBackStack()
            NavUtil.navController?.navigate(R.id.signDataForTeacher)
            GetInClassListener.pageState = 0
        }
    }

    private fun toDefaultPage() {
        // NavUtil.navController?.popBackStack()
        NavUtil.navController?.navigate(R.id.locationFragmentForTeacher)
    }


    companion object {
        const val CLASS_INFO = 1
        var teacherId: String? = null
        var teacherName: String? = null
        var classesInfoString: String? = null
        var classList: JSONArray? = null
        var tabBarState = 0
    }
}