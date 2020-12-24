package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import com.xuexiang.xui.widget.tabbar.TabSegment
import org.json.JSONArray

class TeacherActivity : AppCompatActivity() {
    var teacherId = ""
    var teacherName = ""
    var classesInfoString = ""
    var classesInfo:JSONArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
        teacherId = intent.getStringExtra("teacherId").toString()
        teacherName = intent.getStringExtra("teacherName").toString()
        classesInfoString = intent.getStringExtra("classesInfo").toString()
        classesInfo = JSONArray(classesInfoString)
        Log.i("classesInfo in teacher",classesInfo.toString())
        configTabBar()
    }
    private fun configTabBar(){
        var tabSegment=findViewById<TabSegment>(R.id.teacher_tab_bar)
        tabSegment.mode = TabSegment.MODE_FIXED
        val locationTab = TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.location),this.applicationContext.getDrawable(R.drawable.location_chosen),"打卡",true)
        val dataTab = TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.data),this.applicationContext.getDrawable(R.drawable.data_chosen),"统计",true)
        val mineTab = TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.mine),this.applicationContext.getDrawable(R.drawable.mine),"我的",true)
        tabSegment.addTab(locationTab)
        tabSegment.addTab(dataTab)
        tabSegment.addTab(mineTab)
        tabSegment.selectTab(0)
        tabSegment.notifyDataChanged()
        val teacherInfo = buildBundleForTeacher()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
        val navController = navHostFragment.navController
        tabSegment.setOnTabClickListener {
            when(it){
                0 -> navController.navigate(R.id.locationFragmentForTeacher,teacherInfo)
                1 -> navController.navigate(R.id.signDataForTeacher,teacherInfo)
                2 -> navController.navigate(R.id.mineForTeacher,teacherInfo)
            }
        }
        navController.navigate(R.id.locationFragmentForTeacher,teacherInfo)
    }
    private fun buildBundleForTeacher():Bundle{
        val bundle = Bundle()
        bundle.putString("teacherId",teacherId)
        bundle.putString("teacherName",teacherName)
        bundle.putString("classesInfoString",classesInfoString)
        return bundle
    }
    override fun onBackPressed() {}
}