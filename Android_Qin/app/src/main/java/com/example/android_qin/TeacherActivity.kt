package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.xuexiang.xui.widget.tabbar.TabSegment

class TeacherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)
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
        tabSegment.notifyDataChanged()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_for_teacher) as NavHostFragment
        val navController = navHostFragment.navController
        tabSegment.setOnTabClickListener {
            when(it){
                0 -> navController.navigate(R.id.locationFragmentForTeacher)
                1 -> navController.navigate(R.id.signDataForTeacher)
                2 -> navController.navigate(R.id.mineForTeacher)
            }
        }
    }
}