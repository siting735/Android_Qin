package com.example.android_qin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import com.xuexiang.xui.widget.tabbar.TabSegment

class StudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        configTabBar()
    }
    private fun configTabBar(){
        var tabSegment=findViewById<TabSegment>(R.id.student_tab_bar)
        tabSegment.mode = TabSegment.MODE_FIXED
        val locationBtn=tabSegment.addTab(TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.location),this.applicationContext.getDrawable(R.drawable.location_chosen),"打卡",true))
        val dataBtn=tabSegment.addTab(TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.data),this.applicationContext.getDrawable(R.drawable.data_chosen),"统计",true))
        val mineBtn=tabSegment.addTab(TabSegment.Tab(this.applicationContext.getDrawable(R.drawable.mine),this.applicationContext.getDrawable(R.drawable.mine),"我的",true))
        tabSegment.notifyDataChanged()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController
        locationBtn.setOnTabClickListener {

        }
        dataBtn.setOnTabClickListener {
            navController.navigate(R.id.signDataForStudent)
        }
        mineBtn.setOnTabClickListener {

        }
    }
}